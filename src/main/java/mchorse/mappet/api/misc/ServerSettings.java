package mchorse.mappet.api.misc;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkeys;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.events.RegisterServerTriggerEvent;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global server settings
 */
public class ServerSettings implements INBTSerializable<NBTTagCompound>
{
    private File file;

    private Map<String, String> keyToAlias = new HashMap<String, String>();

    public final Map<String, Trigger> registered = new LinkedHashMap<String, Trigger>();

    public final Map<String, Trigger> registeredForgeTriggers = new LinkedHashMap<String, Trigger>();

    public final TriggerHotkeys hotkeys = new TriggerHotkeys();

    /* Block triggers */

    public final Trigger blockBreak;

    public final Trigger blockPlace;

    public final Trigger blockInteract;

    public final Trigger blockClick;

    /* Server triggers */

    public final Trigger serverLoad;

    public final Trigger serverTick;

    /* Entity triggers */

    public final Trigger entityDamaged;

    public final Trigger entityAttacked;

    public final Trigger entityDeath;

    /* Living triggers */

    public final Trigger livingKnockBack;

    public final Trigger livingFalling;

    public final Trigger livingJumping;

    public final Trigger onLivingEquipmentChange;

    /* Animal triggers */

    public final Trigger animalTame;

    /* Projectile triggers */

    public final Trigger projectileImpact;

    /* Player triggers */

    public final Trigger playerChat;

    public final Trigger playerLogIn;

    public final Trigger playerLogOut;

    public final Trigger playerLeftClick;

    public final Trigger playerRightClick;

    public final Trigger playerRespawn;

    public final Trigger playerDeath;

    public final Trigger playerItemPickup;

    public final Trigger playerItemToss;

    public final Trigger playerItemInteract;

    public final Trigger playerEntityInteract;

    public final Trigger playerCloseContainer;

    public final Trigger playerOpenContainer;

    public final Trigger playerJournal;

    public final Trigger playerEntityLeash;

    public final Trigger playerEat;

    public final Trigger playerDrinkPotion;

    public final Trigger playerMove;

    public final Trigger playerDimensionChange;

    public final Trigger playerItemCraft;

    public final Trigger playerItemSmelted;

    public final Trigger playerCriticalHit;

    public final Trigger playerOpenGui;

    public final Trigger playerKeyboardInput;

    public final Trigger playerMouseInput;

    /* State triggers */

    public final Trigger stateChanged;



    public Trigger register(String key)
    {
        return this.register(key, null, new Trigger());
    }

    public Trigger register(String key, Trigger trigger)
    {
        return this.register(key, null, trigger);
    }

    public Trigger register(String key, String alias)
    {
        Trigger trigger = new Trigger();

        if (this.registered.containsKey(key))
        {
            throw new IllegalStateException("Server trigger '" + key + "' is already registered!");
        }

        if (alias != null)
        {
            this.keyToAlias.put(key, alias);
        }

        this.registered.put(key, trigger);

        return trigger;
    }

    public Trigger register(String key, String alias, Trigger trigger)
    {
        if (this.registered.containsKey(key))
        {
            throw new IllegalStateException("Server trigger '" + key + "' is already registered!");
        }

        if (alias != null)
        {
            this.keyToAlias.put(key, alias);
        }

        this.registered.put(key, trigger);

        return trigger;
    }

    public ServerSettings(File file)
    {
        this.file = file;

        this.blockBreak = this.register("block_break", "break_block");
        this.blockPlace = this.register("block_place", "place_block");
        this.blockInteract = this.register("block_interact", "interact_block");
        this.blockClick = this.register("block_click");
        this.entityDamaged = this.register("entity_damaged", "damage_entity");
        this.entityAttacked = this.register("entity_attacked", "attack_entity");
        this.entityDeath = this.register("entity_death");
        this.serverLoad = this.register("server_load");
        this.serverTick = this.register("server_tick");
        this.playerChat = this.register("player_chat", "chat");
        this.playerLogIn = this.register("player_login");
        this.playerLogOut = this.register("player_logout");
        this.playerLeftClick = this.register("player_lmb");
        this.playerRightClick = this.register("player_rmb");
        this.playerRespawn = this.register("player_respawn");
        this.playerDeath = this.register("player_death");
        this.playerItemPickup = this.register("player_item_pickup");
        this.playerItemToss = this.register("player_item_toss");
        this.playerItemInteract = this.register("player_item_interact");
        this.playerEntityInteract = this.register("player_entity_interact");
        this.playerCloseContainer = this.register("player_close_container");
        this.playerOpenContainer = this.register("player_open_container");
        this.playerJournal = this.register("player_journal");
        this.livingKnockBack = this.register("living_knockback");
        this.projectileImpact = this.register("projectile_impact");
        this.onLivingEquipmentChange = this.register("living_equipment_change");
        this.playerEntityLeash = this.register("player_entity_leash");
        this.stateChanged = this.register("state_changed");

        this.playerEat = this.register("player_eat");
        this.playerDrinkPotion = this.register("player_drink_potion");
        this.livingFalling = this.register("living_fall");
        this.livingJumping = this.register("living_jump");
        this.playerMove = this.register("player_move");
        this.playerDimensionChange = this.register("player_dimension_change");

        this.playerItemCraft = this.register("player_item_craft");
        this.playerItemSmelted = this.register("player_item_smelted");
        this.animalTame = this.register("animal_tame");
        this.playerCriticalHit = this.register("player_critical_hit");
        this.playerOpenGui = this.register("player_open_gui");
        this.playerKeyboardInput = this.register("player_keyboard_input");
        this.playerMouseInput = this.register("player_mouse_input");

        Mappet.EVENT_BUS.post(new RegisterServerTriggerEvent(this));
    }

    /* Deserialization / Serialization */

    public void load()
    {
        if (this.file == null || !this.file.isFile())
        {
            return;
        }

        try
        {
            NBTTagCompound tag = NBTToJsonLike.read(this.file);

            if (!tag.hasKey("Hotkeys"))
            {
                /* Backward compatibility with beta */
                File hotkeys = new File(this.file.getParentFile(), "hotkeys.json");

                if (hotkeys.isFile())
                {
                    try
                    {
                        NBTTagCompound hotkeysTag = NBTToJsonLike.read(hotkeys);

                        tag.setTag("Hotkeys", hotkeysTag);
                        hotkeys.delete();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }

            this.deserializeNBT(tag);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        try
        {
            NBTToJsonLike.write(this.file, this.serializeNBT());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* NBT */

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound triggers = new NBTTagCompound();

        for (Map.Entry<String, Trigger> entry : this.registered.entrySet())
        {
            this.writeTrigger(triggers, entry.getKey(), entry.getValue());
        }

        if (!triggers.hasNoTags())
        {
            tag.setTag("Triggers", triggers);
        }

        NBTTagCompound forgeTriggers = new NBTTagCompound();

        for (Map.Entry<String, Trigger> entry : this.registeredForgeTriggers.entrySet())
        {
            this.writeTrigger(forgeTriggers, entry.getKey(), entry.getValue());
        }

        if (!forgeTriggers.hasNoTags())
        {
            tag.setTag("ForgeTriggers", forgeTriggers);
        }

        tag.setTag("Hotkeys", this.hotkeys.serializeNBT());

        return tag;
    }

    private void writeTrigger(NBTTagCompound tag, String key, Trigger trigger)
    {
        if (trigger != null)
        {
            NBTTagCompound triggerTag = trigger.serializeNBT();

            if (!triggerTag.hasNoTags())
            {
                tag.setTag(key, triggerTag);
            }
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Triggers"))
        {
            NBTTagCompound triggers = tag.getCompoundTag("Triggers");

            for (Map.Entry<String, Trigger> entry : this.registered.entrySet())
            {
                String oldAlias = this.keyToAlias.get(entry.getKey());

                if (triggers.hasKey(oldAlias, Constants.NBT.TAG_COMPOUND))
                {
                    this.readTrigger(triggers, oldAlias, entry.getValue());
                }
                else
                {
                    this.readTrigger(triggers, entry.getKey(), entry.getValue());
                }
            }
        }

        this.registeredForgeTriggers.clear();

        if (tag.hasKey("ForgeTriggers"))
        {
            NBTTagCompound forgeTriggers = tag.getCompoundTag("ForgeTriggers");

            for (String key : forgeTriggers.getKeySet())
            {
                Trigger trigger = new Trigger();
                trigger.deserializeNBT(forgeTriggers.getCompoundTag(key));
                this.registeredForgeTriggers.put(key, trigger);
            }
        }

        if (tag.hasKey("Hotkeys"))
        {
            this.hotkeys.deserializeNBT(tag.getCompoundTag("Hotkeys"));
        }
    }

    private void readTrigger(NBTTagCompound tag, String key, Trigger trigger)
    {
        if (tag.hasKey(key, Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound triggerTag = tag.getCompoundTag(key);

            if (!triggerTag.hasNoTags())
            {
                trigger.deserializeNBT(triggerTag);
            }
        }
    }
}