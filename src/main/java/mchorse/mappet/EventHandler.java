package mchorse.mappet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptEntityItem;
import mchorse.mappet.api.scripts.code.entities.ScriptPlayer;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.EntityAIRepeatingCommand;
import mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand.RepeatingCommandDataStorage;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.EntityAIRotations;
import mchorse.mappet.api.scripts.code.entities.ai.rotations.RotationDataStorage;
import mchorse.mappet.api.scripts.code.items.ScriptInventory;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptPlayer;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.IExecutable;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.CharacterProvider;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.client.KeyboardHandler;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.SoundPack;
import mchorse.mappet.commands.data.CommandDataClear;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.entities.utils.MappetNpcRespawnManager;
import mchorse.mappet.events.StateChangedEvent;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.events.PacketEventHotkeys;
import mchorse.mappet.network.common.huds.PacketHUDScene;
import mchorse.mappet.network.common.npc.PacketNpcJump;
import mchorse.mappet.network.common.quests.PacketQuest;
import mchorse.mappet.network.common.quests.PacketQuests;
import mchorse.mappet.network.common.scripts.PacketClick;
import mchorse.mappet.utils.RunnableExecutionFork;
import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventHandler
{
    /**
     * Resource location for cosmetic capability
     */
    public static final ResourceLocation CAPABILITY = new ResourceLocation(Mappet.MOD_ID, "character");

    private static Boolean isMohist;

    /**
     * Players that must be checked
     */
    private Set<EntityPlayer> playersToCheck = new HashSet<EntityPlayer>();

    /**
     * Delayed executions
     */
    private List<IExecutable> executables = new ArrayList<IExecutable>();

    /**
     * Second executables list to avoid concurrent modification
     * exceptions when adding consequent delayed executions
     */
    private List<IExecutable> secondList = new ArrayList<IExecutable>();

    /**
     * Server data context which is used by server tick global trigger
     */
    private DataContext context;

    /**
     * Set that keeps track of players that just joined (it is needed to avoid
     * triggering the player respawn trigger when player logs in)
     */
    private Set<UUID> loggedInPlayers = new HashSet<UUID>();

    private static Set<Class<? extends Event>> registeredEvents = new HashSet<>();

    private int skinCounter;

    private static boolean isMohist()
    {
        if (isMohist != null)
        {
            return isMohist;
        }

        try
        {
            Class.forName("com.mohistmc.MohistMC");

            isMohist = true;
        }
        catch (Exception e)
        {
            isMohist = false;
        }

        return isMohist;
    }

    public List<String> getIds()
    {
        List<String> ids = new ArrayList<String>();

        for (IExecutable executable : this.executables)
        {
            ids.add(executable.getId());
        }

        return Lists.newArrayList(Sets.newLinkedHashSet(ids));
    }

    public void addExecutables(List<IExecutable> executionForks)
    {
        this.executables.addAll(executionForks);
    }

    public void addExecutable(IExecutable executable)
    {
        this.executables.add(executable);
    }

    public int removeExecutables(String id)
    {
        int size = this.executables.size();

        this.executables.removeIf((e) -> e.getId().equals(id));

        return size - this.executables.size();
    }

    public void reset()
    {
        this.playersToCheck.clear();
        this.executables.clear();
        this.secondList.clear();
        this.context = null;
    }

    public void trigger(Event event, Trigger trigger, DataContext context)
    {
        context.set("event", event);

        trigger.trigger(context);

        if (event.isCancelable() && context.isCanceled())
        {
            if (event instanceof LivingEquipmentChangeEvent || event instanceof TickEvent.PlayerTickEvent)
            {
                return; //otherwise game crashes
            }
            event.setCanceled(true);
        }
    }

    /* Universal forge event handler */
    @SubscribeEvent
    public void onAnyEvent(Event event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server == null || Mappet.settings == null)
        {
            return;
        }

        if (!Mappet.enableForgeTriggers.get())
        {
            return;
        }

        if (event instanceof TickEvent && ((TickEvent) event).side == Side.CLIENT)
        {
            return;
        }

        if (event instanceof EntityEvent && (((EntityEvent) event).getEntity() == null || ((EntityEvent) event).getEntity().world.isRemote))
        {
            return;
        }

        if (event instanceof WorldEvent && ((WorldEvent) event).getWorld().isRemote)
        {
            return;
        }


        String name = getEventClassName(event.getClass());
        Trigger trigger = Mappet.settings.registeredForgeTriggers.get(name);

        if (shouldCancelTrigger(trigger))
        {
            return;
        }

        this.trigger(event, trigger, new DataContext(server));
    }

    public static String getEventClassName(Class<? extends Event> clazz)
    {
        return clazz.getName().replace("$", ".");
    }

    public static Set<Class<? extends Event>> getRegisteredEvents()
    {
        if (Mappet.enableForgeTriggers.get() && (registeredEvents == null || registeredEvents.isEmpty()))
        {
            Reflections reflections = new Reflections();
            registeredEvents = reflections.getSubTypesOf(Event.class).stream()
                    .filter(clazz -> !(FMLNetworkEvent.class.isAssignableFrom(clazz)))
                    .filter(clazz -> clazz != Event.class)
                    .filter(clazz -> clazz != CommandEvent.class)
                    .filter(clazz -> !(TextureStitchEvent.class.isAssignableFrom(clazz)))
                    .collect(Collectors.toSet());
        }

        return registeredEvents;
    }

    /* Server trigger handlers */

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event)
    {
        if (!Mappet.settings.playerChat.isEmpty())
        {
            DataContext context = new DataContext(event.getPlayer())
                    .set("message", event.getMessage());

            this.trigger(event, Mappet.settings.playerChat, context);
        }
    }

    @SubscribeEvent
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event)
    {
        if (!Mappet.settings.blockBreak.isEmpty())
        {
            IBlockState state = event.getState();
            DataContext context = new DataContext(event.getPlayer())
                    .set("block", state.getBlock().getRegistryName().toString())
                    .set("meta", state.getBlock().getMetaFromState(state))
                    .set("x", event.getPos().getX())
                    .set("y", event.getPos().getY())
                    .set("z", event.getPos().getZ());

            this.trigger(event, Mappet.settings.blockBreak, context);
        }
    }

    @SubscribeEvent
    public void onPlayerPlaceBlock(BlockEvent.PlaceEvent event)
    {
        if (!Mappet.settings.blockPlace.isEmpty())
        {
            IBlockState state = event.getPlacedBlock();
            DataContext context = new DataContext(event.getPlayer())
                    .set("block", state.getBlock().getRegistryName().toString())
                    .set("meta", state.getBlock().getMetaFromState(state))
                    .set("x", event.getPos().getX())
                    .set("y", event.getPos().getY())
                    .set("z", event.getPos().getZ());

            this.trigger(event, Mappet.settings.blockPlace, context);
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent event)
    {
        DamageSource source = event.getSource();
        EntityLivingBase attacker = source.getTrueSource() instanceof EntityLivingBase ? (EntityLivingBase) source.getTrueSource() : null;

        //potential compability with zettasword
        if (
                Mappet.settings == null ||
                Mappet.settings.entityDamaged == null ||
                Mappet.settings.entityDamaged.isEmpty()
        )
        {
            return;
        }

        DataContext context = new DataContext(event.getEntityLiving(), source.getTrueSource())
                .set("damage", event.getAmount());
        context.set("damageType", source.getDamageType());
        context.set("attacker", ScriptEntity.create(attacker));

        this.trigger(event, Mappet.settings.entityDamaged, context);
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();

        //potential compability with zettasword
        if (
                event.getEntity() == null ||
                event.getEntity().world.isRemote ||
                Mappet.settings == null ||
                Mappet.settings.entityAttacked == null ||
                Mappet.settings.entityAttacked.isEmpty()
        )
        {
            return;
        }

        DataContext context = new DataContext(event.getEntityLiving(), source.getTrueSource())
                .set("damage", event.getAmount());
        context.set("damageType", source.getDamageType());

        this.trigger(event, Mappet.settings.entityAttacked, context);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerOpenOrCloseContainer(PlayerContainerEvent event)
    {
        Trigger trigger = (event instanceof PlayerContainerEvent.Close) ? Mappet.settings.playerCloseContainer : Mappet.settings.playerOpenContainer;

        this.playersToCheck.add(event.getEntityPlayer());

        if (trigger.isEmpty())
        {
            return;
        }

        Container container = event.getContainer();
        DataContext context = new DataContext(event.getEntityPlayer());
        IInventory inventory = null;

        if (container instanceof ContainerChest)
        {
            ContainerChest chest = (ContainerChest) container;

            if (chest.getLowerChestInventory() instanceof TileEntity)
            {
                BlockPos pos = ((TileEntity) chest.getLowerChestInventory()).getPos();

                context.set("x", pos.getX());
                context.set("y", pos.getY());
                context.set("z", pos.getZ());
            }

            inventory = chest.getLowerChestInventory();
        }
        else if (container instanceof ContainerPlayer)
        {
            inventory = event.getEntityPlayer().inventory;
        }
        else
        {
            Field[] fields = container.getClass().getDeclaredFields();

            for (Field field : fields)
            {
                if (field.getType().isAssignableFrom(IInventory.class))
                {
                    try
                    {
                        field.setAccessible(true);

                        inventory = (IInventory) field.get(container);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }

        if (inventory != null)
        {
            context.set("inventory", new ScriptInventory(inventory));
        }

        trigger.trigger(context);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event)
    {
        if (!event.getEntityPlayer().world.isRemote)
        {
            return;
        }

        Dispatcher.sendToServer(new PacketClick(EnumHand.MAIN_HAND));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if (!event.getEntityPlayer().world.isRemote || event.getHand() == EnumHand.OFF_HAND)
        {
            return;
        }

        Dispatcher.sendToServer(new PacketClick(EnumHand.OFF_HAND));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote || Mappet.settings.playerItemInteract.isEmpty())
        {
            return;
        }

        DataContext context = new DataContext(player)
                .set("x", event.getPos().getX())
                .set("y", event.getPos().getY())
                .set("z", event.getPos().getZ())
                .set("hand", event.getHand() == EnumHand.MAIN_HAND ? "main" : "off");

        this.trigger(event, Mappet.settings.playerItemInteract, context);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote || Mappet.settings.blockClick.isEmpty())
        {
            return;
        }

        DataContext context = new DataContext(player)
                .set("x", event.getPos().getX())
                .set("y", event.getPos().getY())
                .set("z", event.getPos().getZ())
                .set("hand", event.getHand() == EnumHand.MAIN_HAND ? "main" : "off");

        this.trigger(event, Mappet.settings.blockClick, context);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote || Mappet.settings.blockInteract.isEmpty())
        {
            return;
        }

        IBlockState state = event.getWorld().getBlockState(event.getPos());
        DataContext context = new DataContext(player)
                .set("block", state.getBlock().getRegistryName().toString())
                .set("meta", state.getBlock().getMetaFromState(state))
                .set("x", event.getPos().getX())
                .set("y", event.getPos().getY())
                .set("z", event.getPos().getZ())
                .set("hand", event.getHand() == EnumHand.MAIN_HAND ? "main" : "off");

        this.trigger(event, Mappet.settings.blockInteract, context);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteractWithEntity(PlayerInteractEvent.EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote || Mappet.settings.playerEntityInteract.isEmpty())
        {
            return;
        }

        DataContext context = new DataContext(player, event.getTarget())
                .set("hand", event.getHand() == EnumHand.MAIN_HAND ? "main" : "off");

        this.trigger(event, Mappet.settings.playerEntityInteract, context);
    }

    /* Other cool stuff */

    /**
     * Attach player capabilities
     */
    @SubscribeEvent
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(CAPABILITY, new CharacterProvider());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerClientLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        // play sounds for the player if he has config "reload all sounds on log in" set to true
        if (Mappet.loadCustomSoundsOnLogin.get())
        {
            IScriptPlayer scriptPlayer = new ScriptPlayer(player);
            for (String sound : SoundPack.getCustomSoundEvents())
            {
                scriptPlayer.playStaticSound(sound, 0.000000001f, 1);
                CommonProxy.eventHandler.addExecutable(new RunnableExecutionFork(1, () ->
                {
                    scriptPlayer.stopSound(sound);
                }));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ICharacter character = Character.get(player);
        Instant lastClear = Mappet.data.getLastClear();

        if (character != null)
        {
            if (character.getLastClear().isBefore(lastClear))
            {
                CommandDataClear.clear(player, Mappet.data.getLastInventory());

                character.updateLastClear(lastClear);
            }

            this.syncData(player, character);
        }
        /// restore the displayed HUDs
        Map<String, List<HUDScene>> displayedHUDs = character.getDisplayedHUDs();
        for (Map.Entry<String, List<HUDScene>> entry : displayedHUDs.entrySet())
        {
            String id = entry.getKey();
            List<HUDScene> scenes = entry.getValue();

            for (HUDScene scene : scenes)
            {
                // Send the PacketHUDScene for each HUDScene
                Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), player);
            }
        }

        // display present global HUDs player on any player that has a global HUD in his displayed HUDs scenes list
        for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            ICharacter c = Character.get(p);
            if (c != null)
            {
                Map<String, List<HUDScene>> displayed = c.getDisplayedHUDs();
                for (Map.Entry<String, List<HUDScene>> entry : displayed.entrySet())
                {
                    String id = entry.getKey();
                    List<HUDScene> scenes = entry.getValue();

                    for (HUDScene scene : scenes)
                    {
                        if (scene.global)
                        {
                            Dispatcher.sendTo(new PacketHUDScene(id, scene.serializeNBT()), player);
                        }
                    }
                }
            }
        }

        if (!Mappet.settings.playerLogIn.isEmpty())
        {
            DataContext context = new DataContext(event.player);

            Mappet.settings.playerLogIn.trigger(context);
        }

        this.loggedInPlayers.add(player.getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerLogsOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (!Mappet.settings.playerLogOut.isEmpty())
        {
            DataContext context = new DataContext(event.player);

            Mappet.settings.playerLogOut.trigger(context);
        }

        this.loggedInPlayers.remove(event.player.getUniqueID());
    }

    /**
     * Copy data from dead player (or player returning from end) to the new player
     */
    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ICharacter character = Character.get(player);
        ICharacter oldCharacter = Character.get(event.getOriginal());

        if (!isMohist())
        {
            character.copy(oldCharacter, player);
        }
    }

    @SubscribeEvent
    public void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (!event.player.world.isRemote)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            ICharacter character = Character.get(player);

            this.syncData(player, character);

            if (this.loggedInPlayers.contains(player.getUniqueID()) && !Mappet.settings.playerRespawn.isEmpty())
            {
                Mappet.settings.playerRespawn.trigger(new DataContext(player));
            }
        }
    }

    private void syncData(EntityPlayerMP player, ICharacter character)
    {
        if (!character.getQuests().quests.isEmpty())
        {
            character.getQuests().initiate(player);

            Dispatcher.sendTo(new PacketQuests(character.getQuests()), player);
        }

        if (!Mappet.settings.hotkeys.hotkeys.isEmpty())
        {
            Dispatcher.sendTo(new PacketEventHotkeys(Mappet.settings), player);
        }
    }

    @SubscribeEvent
    public void onPlayerPickUp(EntityItemPickupEvent event)
    {
        this.playersToCheck.add(event.getEntityPlayer());

        if (!Mappet.settings.playerItemPickup.isEmpty())
        {
            DataContext context = new DataContext(event.getEntityPlayer());

            context.set("item", ScriptItemStack.create(event.getItem().getItem()));
            context.set("entityItem", ScriptEntityItem.create(event.getItem()));
            this.trigger(event, Mappet.settings.playerItemPickup, context);
        }
    }

    @SubscribeEvent
    public void onPlayerToss(ItemTossEvent event)
    {
        if (event.getPlayer().world.isRemote)
        {
            return;
        }

        if (!Mappet.settings.playerItemToss.isEmpty())
        {
            DataContext context = new DataContext(event.getPlayer());

            context.set("entityItem", ScriptEntityItem.create(event.getEntityItem()));
            this.trigger(event, Mappet.settings.playerItemToss, context);
        }
    }

    @SubscribeEvent
    public void onMobKilled(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote)
        {
            return;
        }

        Entity source = event.getSource().getTrueSource();
        String damageType = event.getSource().getDamageType();
        Trigger trigger = event.getEntity() instanceof EntityPlayer
                ? Mappet.settings.playerDeath
                : Mappet.settings.entityDeath;

        if (!trigger.isEmpty())
        {
            DataContext context = new DataContext(event.getEntityLiving(), source);

            context.set("damageType", damageType);

            if (source != null)
            {
                context.set("killer", ScriptEntity.create(source));
            }

            Entity thrower = null;

            if (source instanceof EntityThrowable)
            {
                thrower = ((EntityThrowable) event.getEntity()).getThrower();
            }

            if (thrower != null)
            {
                context.set("thrower", ScriptEntity.create(thrower));
            }

            this.trigger(event, trigger, context);
        }

        if (source instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) source;
            ICharacter character = Character.get(player);

            if (character != null)
            {
                for (Quest quest : character.getQuests().quests.values())
                {
                    quest.mobWasKilled(player, event.getEntity());
                }

                this.playersToCheck.add(player);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityLiving)
        {
            // Handle load AI rotation data
            EntityLiving entityLiving = (EntityLiving) event.getEntity();
            RotationDataStorage rotationDataStorage = RotationDataStorage.getRotationDataStorage(event.getWorld());
            RotationDataStorage.RotationData rotationData = rotationDataStorage.getRotationData(entityLiving.getUniqueID());
            if (rotationData != null)
            {
                float yaw = rotationData.yaw;
                float pitch = rotationData.pitch;
                float yawHead = rotationData.yawHead;
                entityLiving.tasks.addTask(0, new EntityAIRotations(entityLiving, yaw, pitch, yawHead, 1.0F));
            }

            // Handle load AI repeating command data
            RepeatingCommandDataStorage repeatingCommandDataStorage = RepeatingCommandDataStorage.getRepeatingCommandDataStorage(event.getWorld());
            List<RepeatingCommandDataStorage.RepeatingCommandData> repeatingCommandDataList = repeatingCommandDataStorage.getRepeatingCommandData(entityLiving.getUniqueID());
            if (repeatingCommandDataList != null)
            {
                for (RepeatingCommandDataStorage.RepeatingCommandData repeatingCommandData : repeatingCommandDataList)
                {
                    String command = repeatingCommandData.command;
                    int frequency = repeatingCommandData.frequency;
                    entityLiving.tasks.addTask(10, new EntityAIRepeatingCommand(entityLiving, command, frequency));
                }
            }
        }
    }

    List<Entity> getAllEntities()
    {
        List<Entity> entities = new ArrayList<Entity>();
        try
        {
            for (Entity entity : EntitySelector.matchEntities(FMLCommonHandler.instance().getMinecraftServerInstance(), "@e", Entity.class))
            {
                entities.add(entity);
            }
        }
        catch (Exception e)
        {
        }
        return entities;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            return;
        }

        //lock entity if they should be locked
        for (Entity entity : getAllEntities())
        {
            if (entity == null)
            {
                continue;
            }
            //lock position if it should be locked
            if (entity.getEntityData().getBoolean("positionLocked"))
            {
                IScriptEntity scriptEntity = (ScriptEntity.create(entity));
                scriptEntity.setPosition(
                        entity.getEntityData().getDouble("lockX"),
                        entity.getEntityData().getDouble("lockY"),
                        entity.getEntityData().getDouble("lockZ")
                );
                scriptEntity.setMotion(0.0, 0.0, 0.0);
            }
            //lock rotation if it should be locked
            if (entity.getEntityData().getBoolean("rotationLocked"))
            {
                IScriptEntity scriptEntity = (ScriptEntity.create(entity));
                scriptEntity.setRotations(
                        entity.getEntityData().getFloat("lockPitch"),
                        entity.getEntityData().getFloat("lockYaw"),
                        entity.getEntityData().getFloat("lockYawHead")
                );
            }
        }

        for (EntityPlayer player : this.playersToCheck)
        {
            ICharacter character = Character.get(player);

            if (character != null)
            {
                Quests quests = character.getQuests();
                Iterator<Map.Entry<String, Quest>> it = quests.quests.entrySet().iterator();

                quests.iterating = true;

                while (it.hasNext())
                {
                    Map.Entry<String, Quest> entry = it.next();
                    Quest quest = entry.getValue();

                    if (quest.instant && quest.rewardIfComplete(player))
                    {
                        it.remove();

                        Dispatcher.sendTo(new PacketQuest(entry.getKey(), null), (EntityPlayerMP) player);
                    }
                    else
                    {
                        Dispatcher.sendTo(new PacketQuest(entry.getKey(), entry.getValue()), (EntityPlayerMP) player);
                    }
                }

                quests.flush(player);
            }
        }

        this.playersToCheck.clear();

        /* This block of code might be a bit confusing, but essentially
         * what it does is prevents concurrent modification when timer nodes
         * add consequent execution forks, this way I can reliably keep track
         * of order of both the old executions which are not yet executed and
         * of new forks that were added by new timer nodes */
        if (!this.executables.isEmpty())
        {
            /* Copy original event forks to another list and clear them
             * to be ready for new forks */
            this.secondList.addAll(this.executables);
            this.executables.clear();

            /* Execute event forks (and remove those which were finished) */
            this.secondList.removeIf(IExecutable::update);

            /* Add back to the original list the remaining forks and
             * new forks that were added by consequent timer nodes */
            this.secondList.addAll(this.executables);
            this.executables.clear();
            this.executables.addAll(this.secondList);
            this.secondList.clear();
        }

        /* Execute a server tick trigger */
        if (!Mappet.settings.serverTick.isEmpty())
        {
            if (this.context == null)
            {
                this.context = new DataContext(FMLCommonHandler.instance().getMinecraftServerInstance());
            }

            Mappet.settings.serverTick.trigger(this.context);
            this.context.cancel(false);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            return;
        }

        ICharacter character = Character.get(event.player);

        if (character != null && !event.player.world.isRemote)
        {
            character.getPositionCache().updatePlayer(event.player);
            ((Character) character).updateDisplayedHUDsList();
        }

        if (event.player.world.isRemote && event.player == Minecraft.getMinecraft().player)
        {
            this.onPlayerTickClient(event);
        }
    }

    @SideOnly(Side.CLIENT)
    private void onPlayerTickClient(TickEvent.PlayerTickEvent event)
    {
        RenderingHandler.update();

        KeyboardHandler.updateHeldKeys();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (this.skinCounter >= 50)
            {
                this.updateSkins();

                this.skinCounter = 0;
            }

            this.skinCounter += 1;
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateSkins()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null)
        {
            return;
        }

        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(mc.renderEngine);

        for (EntityPlayer player : mc.world.playerEntities)
        {
            if (player instanceof EntityOtherPlayerMP)
            {
                map.put(AbstractClientPlayer.getLocationSkin(player.getName()), map.get(((EntityOtherPlayerMP)player).getLocationSkin()));
            }
            else if (player instanceof EntityPlayerSP) {
                map.put(AbstractClientPlayer.getLocationSkin(player.getName()), map.get(((EntityPlayerSP)player).getLocationSkin()));
            }
        }
    }

    @SubscribeEvent
    public void onStateChange(StateChangedEvent event)
    {
        Trigger trigger = Mappet.settings.stateChanged;
        if (!trigger.isEmpty())
        {
            handleStateChangedEvent(event, trigger);
        }

        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            ICharacter character = Character.get(player);

            if (character != null && (event.isGlobal() || character.getStates() == event.states))
            {
                int i = 0;

                for (Quest quest : character.getQuests().quests.values())
                {
                    i += quest.stateWasUpdated(player) ? 1 : 0;
                }

                if (i > 0)
                {
                    this.playersToCheck.add(player);
                }
            }
        }
    }

    private void handleStateChangedEvent(StateChangedEvent event, Trigger trigger)
    {
        if (event.isGlobal())
        {
            handleGlobalStateChangedEvent(event, trigger);
        }
        else
        {
            handlePlayerStateChangedEvent(event, trigger);
            handleNpcStateChangedEvent(event, trigger);
        }
    }

    private void handleGlobalStateChangedEvent(StateChangedEvent event, Trigger trigger)
    {
        this.context = new DataContext(FMLCommonHandler.instance().getMinecraftServerInstance());

        setStateChangedEventValues(context, event);
        trigger.trigger(context);
    }

    private void handlePlayerStateChangedEvent(StateChangedEvent event, Trigger trigger)
    {
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            ICharacter character = Character.get(player);
            if (character != null && character.getStates() == event.states)
            {
                this.context = new DataContext(player);

                setStateChangedEventValues(context, event);
                context.set("entity", ScriptEntity.create(player));
                trigger.trigger(context);
            }
        }
    }

    private void handleNpcStateChangedEvent(StateChangedEvent event, Trigger trigger)
    {
        for (EntityNpc npc : getAllNpcs())
        {
            if (npc != null && npc.getStates() == event.states)
            {
                this.context = new DataContext(npc);

                setStateChangedEventValues(context, event);
                context.set("entity", ScriptEntity.create(npc));
                trigger.trigger(context);
            }
        }
    }

    private void setStateChangedEventValues(DataContext context, StateChangedEvent event)
    {
        context.set("key", event.key);
        context.set("current", event.current);
        context.set("previous", event.previous);
    }

    private List<EntityNpc> getAllNpcs()
    {
        List<EntityNpc> npcs = new ArrayList<EntityNpc>();
        try
        {
            for (World world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
            {
                npcs.addAll(world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityNpc)
                        .map(entity -> (EntityNpc) entity)
                        .collect(Collectors.toList()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return npcs;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        MappetNpcRespawnManager respawnManager = MappetNpcRespawnManager.get(event.world);

        respawnManager.onTick();
    }

    @SubscribeEvent
    public void onLivingKnockBack(LivingKnockBackEvent event)
    {
        EntityLivingBase target = event.getEntityLiving();
        Entity attacker = event.getAttacker();

        if (target != null && target.getEntityData().getBoolean("positionLocked"))
        {
            event.setCanceled(true);
        }

        if (target.world.isRemote || Mappet.settings.livingKnockBack.isEmpty())
        {
            return;
        }

        DataContext context = new DataContext(target, attacker)
                .set("strength", event.getStrength())
                .set("ratioX", event.getRatioX())
                .set("ratioZ", event.getRatioZ());
        context.set("attacker", ScriptEntity.create(attacker));

        this.trigger(event, Mappet.settings.livingKnockBack, context);
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event)
    {
        if (event.getEntity().world.isRemote)
        {
            return;
        }

        Trigger trigger = Mappet.settings.projectileImpact;
        if (!trigger.isEmpty())
        {
            Entity hitEntity = event.getRayTraceResult().entityHit;
            DataContext context = new DataContext(event.getEntity(), hitEntity);

            context.getValues().put("pos", new ScriptVector(event.getRayTraceResult().hitVec));
            context.getValues().put("projectile", ScriptEntity.create(event.getEntity()));

            if (hitEntity != null && !context.getValues().containsKey("entity"))
            {
                context.getValues().put("entity", ScriptEntity.create(hitEntity));
            }

            Entity thrower = null;

            if (event.getEntity() instanceof EntityThrowable)
            {
                thrower = ((EntityThrowable) event.getEntity()).getThrower();
            }

            if (thrower != null)
            {
                context.getValues().put("thrower", ScriptEntity.create(thrower));
            }

            this.trigger(event, trigger, context);
        }
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event)
    {
        if (event.getEntity().world.isRemote)
        {
            return;
        }

        Trigger trigger = Mappet.settings.onLivingEquipmentChange;

        if (!trigger.isEmpty())
        {
            DataContext context = new DataContext(event.getEntity());

            context.getValues().put("item", ScriptItemStack.create(event.getTo()));

            context.getValues().put("previous", ScriptItemStack.create(event.getFrom()));

            if (event.getEntity() instanceof EntityPlayerMP)
            {
                ScriptPlayer player = new ScriptPlayer((EntityPlayerMP) event.getEntity());

                context.getValues().put("slot", player.getHotbarIndex());
            }
            else
            {
                context.getValues().put("slot", event.getSlot().getIndex());
            }

            this.trigger(event, trigger, context);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed())
        {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (
                    player.isRiding() &&
                            player.getRidingEntity() instanceof EntityNpc &&
                            ((EntityNpc) player.getRidingEntity()).getState().canBeSteered.get()
            ) {
                float jumpPower = ((EntityNpc) player.getRidingEntity()).getState().jumpPower.get();

                Dispatcher.sendToServer(new PacketNpcJump(player.getRidingEntity().getEntityId(), jumpPower));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLeashEntity(PlayerInteractEvent.EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = player.getHeldItem(event.getHand());

        // Check if the player is on the server, has a leash, and if there's an interaction event set
        if (player.world.isRemote || item.getItem() != Items.LEAD || Mappet.settings.playerEntityLeash.isEmpty())
        {
            return;
        }

        Entity target = event.getTarget();

        // Check if the target entity can be leashed
        if (!(target instanceof EntityLiving) || ((EntityLiving) target).getLeashed() || !((EntityLiving) target).canBeLeashedTo(player))
        {
            return;
        }

        DataContext context = new DataContext(player, target)
                .set("hand", event.getHand() == EnumHand.MAIN_HAND ? "main" : "off");

        this.trigger(event, Mappet.settings.playerEntityLeash, context);
    }

    @SubscribeEvent
    public void onPlayerWalking(TickEvent.PlayerTickEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        Trigger trigger = Mappet.settings.playerMove;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        if (player.prevDistanceWalkedModified - player.distanceWalkedModified == 0) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("onGround", player.onGround);

        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.livingJumping;

        EntityLivingBase entity = event.getEntityLiving();

        if (shouldCancelTrigger(trigger) || entity.world.isRemote) {
            return;
        }

        this.trigger(event, trigger, new DataContext(entity));
    }

    @SubscribeEvent
    public void onLivingFallEvent(LivingFallEvent event) {
        if (Mappet.settings == null) {
            return;
        }
        Trigger trigger = Mappet.settings.livingFalling;

        EntityLivingBase entity = event.getEntityLiving();

        if (shouldCancelTrigger(trigger) || entity.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(entity);
        context.set("distance", event.getDistance());
        context.set("damageMultiplier", event.getDamageMultiplier());

        this.trigger(event, trigger, context);
    }


    public void onPlayerOpenOrCloseGui(EntityPlayer player, String gui) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerOpenGui;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);

        context.set("gui", gui);

        trigger.trigger(context);
    }

    @SubscribeEvent
    public void onPlayerEatEvent(TickEvent.PlayerTickEvent event){
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerEat;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        boolean isEat = player.getItemInUseCount() > 0 && player.getActiveItemStack().getItem() instanceof ItemFood;

        if(!isEat){
            return;
        }

        DataContext context = new DataContext(player);

        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onPlayerDrinkPotionEvent(TickEvent.PlayerTickEvent event){
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerDrinkPotion;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        boolean isDrink = player.getItemInUseCount() > 0 && player.getActiveItemStack().getItem() instanceof ItemPotion;

        if(!isDrink){
            return;
        }

        this.trigger(event, trigger, new DataContext(player));
    }

    @SubscribeEvent
    public void onDimensionChangeEvent(PlayerEvent.PlayerChangedDimensionEvent event){
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerDimensionChange;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("fromDim", event.fromDim);
        context.set("toDim", event.toDim);

        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onPlayerItemCraft(PlayerEvent.ItemCraftedEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerItemCraft;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("stack", ScriptItemStack.create(event.crafting));
        context.set("inventory", new ScriptInventory(event.craftMatrix));
        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onPlayerItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerItemSmelted;

        EntityPlayer player = event.player;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("stack", ScriptItemStack.create(event.smelting));

        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onPlayerCriticalHit(CriticalHitEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerCriticalHit;

        EntityPlayer player = event.getEntityPlayer();

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player, event.getTarget());
        context.set("damageModifier", event.getDamageModifier());

        this.trigger(event, trigger, context);
    }

    @SubscribeEvent
    public void onAnimalTame(AnimalTameEvent event) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.animalTame;

        EntityPlayer player = event.getTamer();

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player, event.getAnimal());

        this.trigger(event, trigger, context);
    }

    public void onKeyboardInput(EntityPlayerMP player, int key, boolean state) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerKeyboardInput;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("key", key);
        context.set("state", state);
        trigger.trigger(context);
    }

    public void onMouseInput(EntityPlayerMP player, int buttonId, int x, int y, boolean state) {
        if (Mappet.settings == null) {
            return;
        }

        Trigger trigger = Mappet.settings.playerMouseInput;

        if (shouldCancelTrigger(trigger) || player.world.isRemote) {
            return;
        }

        DataContext context = new DataContext(player);
        context.set("id", buttonId);
        context.set("state", state);
        context.set("x", x);
        context.set("y", y);
        trigger.trigger(context);
    }

    private boolean shouldCancelTrigger(Trigger trigger) {
        return trigger == null || trigger.isEmpty();
    }
}