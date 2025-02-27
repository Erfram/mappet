package mchorse.mappet;

import mchorse.mappet.api.crafting.CraftingManager;
import mchorse.mappet.api.dialogues.DialogueManager;
import mchorse.mappet.api.events.EventManager;
import mchorse.mappet.api.factions.FactionManager;
import mchorse.mappet.api.huds.HUDManager;
import mchorse.mappet.api.npcs.NpcManager;
import mchorse.mappet.api.quests.QuestManager;
import mchorse.mappet.api.quests.chains.QuestChainManager;
import mchorse.mappet.api.schematics.SchematicManager;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.blocks.*;
import mchorse.mappet.client.KeyboardHandler;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.items.*;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.tile.TileTrigger;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Calendar;

public class RegisterHandler
{
    public static final Calendar calendar = Calendar.getInstance();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        if (!event.isLocal())
        {
            Mappet.quests = new QuestManager(null);
            Mappet.schematics = new SchematicManager(null);
            Mappet.crafting = new CraftingManager(null);
            Mappet.events = new EventManager(null);
            Mappet.dialogues = new DialogueManager(null);
            Mappet.npcs = new NpcManager(null);
            Mappet.factions = new FactionManager(null);
            Mappet.chains = new QuestChainManager(null);
            Mappet.scripts = new ScriptManager(null);
            Mappet.huds = new HUDManager(null);
        }

        KeyboardHandler.clientPlayerJournal = true;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        Mappet.quests = null;
        Mappet.crafting = null;
        Mappet.events = null;
        Mappet.dialogues = null;
        Mappet.npcs = null;
        Mappet.factions = null;
        Mappet.chains = null;
        Mappet.scripts = null;
        Mappet.huds = null;

        KeyboardHandler.hotkeys.clear();
        RenderingHandler.reset();
    }

    @SubscribeEvent
    public void onBlocksRegister(RegistryEvent.Register<Block> event)
    {
        MappetBlocks.registerBlocks(event);
    }

    @SubscribeEvent
    public void onItemsRegister(RegistryEvent.Register<Item> event)
    {
        MappetItems.register(event);
        MappetBlocks.registerItems(event);
    }

    @SubscribeEvent
    public void onEntityRegister(RegistryEvent.Register<EntityEntry> event)
    {
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityNpc.class)
                .name(Mappet.MOD_ID + ".npc")
                .id(new ResourceLocation(Mappet.MOD_ID, "npc"), 0)
                .tracker(EntityNpc.RENDER_DISTANCE, 3, false)
                .build());

        MappetBlocks.registerTileEntities();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(MappetItems.npcTool, 0, this.getNpcToolTexture("npc_tool"));
        ModelLoader.setCustomModelResourceLocation(MappetItems.npcPicker, 0, this.getNpcToolTexture("npc_picker"));
        ModelLoader.setCustomModelResourceLocation(MappetItems.npcSoulStoneEmpty, 0, this.getNpcToolTexture("npc_soulstone_empty"));
        ModelLoader.setCustomModelResourceLocation(MappetItems.npcSoulStoneFilled, 0, this.getNpcToolTexture("npc_soulstone_filled"));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MappetBlocks.emitterBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":emitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MappetBlocks.triggerBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":trigger", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MappetBlocks.regionBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":region", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MappetBlocks.conditionModelBlock), 0, new ModelResourceLocation(Mappet.MOD_ID + ":condition_model", "inventory"));
    }

    public ModelResourceLocation getNpcToolTexture(String toolName) {
        String postfix = "";

        if (isWinter()) {
            postfix = "_winter";
        }

        if (isChristmas()) {
            postfix = "_christmas";
        } else if (isEaster()) {
            postfix = "_easter";
        } else if (isAprilFoolsDay()) {
            postfix = "_april";
        } else if (isHalloween()) {
            postfix = "_halloween";
        }

        if (isMappetBirthday()) {
            postfix = "_mchorse";
        } else if (isLlamaBirthday()) {
            postfix = "_llama";
        } else if (isDyamoBirthday()) {
            postfix = "_dyamo";
        }

        return new ModelResourceLocation(Mappet.MOD_ID + ":" + toolName + "/" + toolName + postfix, "inventory");
    }

    public static boolean isChristmas()
    {
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26;
    }

    public static boolean isAprilFoolsDay()
    {
        return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) <= 2;
    }

    public static boolean isWinter()
    {
        int month = calendar.get(Calendar.MONTH);

        return month == Calendar.DECEMBER || month == Calendar.JANUARY || month == Calendar.FEBRUARY;
    }

    public static boolean isEaster()
    {
        Calendar easterDate = getEasterDate(calendar.get(Calendar.YEAR));

        return calendar.get(Calendar.MONTH) == easterDate.get(Calendar.MONTH) && calendar.get(Calendar.DATE) == easterDate.get(Calendar.DATE);
    }

    public static boolean isHalloween()
    {
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DATE) >= 24;
    }

    public static boolean isLlamaBirthday() {
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) == 1;
    }

    public static boolean isDyamoBirthday() {
        return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 21;
    }

    public static boolean isMappetBirthday() {
        return calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER && calendar.get(Calendar.DATE) >= 13 && calendar.get(Calendar.DATE) <= 15;
    }

    public static Calendar getEasterDate(int year)
    {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int month = (h + l + 114) / 31;
        int day = (h + l + 114) % 31;

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month - 1, day + 1);

        return calendar;
    }
}