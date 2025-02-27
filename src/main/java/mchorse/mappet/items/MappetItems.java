package mchorse.mappet.items;

import mchorse.mappet.Mappet;
import mchorse.mappet.RegisterHandler;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public class MappetItems {
    public static Item npcTool = new ItemNpcTool();
    public static Item npcPicker = new ItemNpcPicker();
    public static Item npcSoulStoneEmpty;
    public static Item npcSoulStoneFilled;

    static String april = RegisterHandler.isAprilFoolsDay() ? ".april" : "";

    public static void register(RegistryEvent.Register<Item> event) {
        registerItem(event, npcTool, "npc_tool");
        registerItem(event, npcPicker, "npc_picker");

        event.getRegistry().register(npcSoulStoneEmpty = new ItemNpcSoulStoneEmpty()
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "npc_soul_stone_empty"))
                .setUnlocalizedName(Mappet.MOD_ID + april + ".npc_soul_stone"));

        event.getRegistry().register(npcSoulStoneFilled = new ItemNpcSoulStoneFilled()
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, "npc_soul_stone_filled"))
                .setUnlocalizedName(Mappet.MOD_ID + april + ".npc_soul_stone"));
    }

    private static void registerItem(RegistryEvent.Register<Item> event, Item item, String id) {
        event.getRegistry().register(item
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, id))
                .setUnlocalizedName(Mappet.MOD_ID + april + "." + id));
    }
}
