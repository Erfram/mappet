package mchorse.mappet.blocks;

import mchorse.mappet.Mappet;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.tile.TileTrigger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MappetBlocks {
    public static BlockEmitter emitterBlock = new BlockEmitter();
    public static BlockTrigger triggerBlock = new BlockTrigger();
    public static BlockRegion regionBlock = new BlockRegion();
    public static BlockConditionModel conditionModelBlock = new BlockConditionModel();

    public static void registerItems(RegistryEvent.Register<Item> event) {
        registerItem(event, emitterBlock, "emitter");
        registerItem(event, triggerBlock, "trigger");
        registerItem(event, regionBlock, "region");
        registerItem(event, conditionModelBlock, "condition_model");
    }

    private static void registerItem(RegistryEvent.Register<Item> event, Block block, String id) {
        event.getRegistry().register(new ItemBlock(block)
                .setRegistryName(new ResourceLocation(Mappet.MOD_ID, id))
                .setUnlocalizedName(Mappet.MOD_ID + "." + id));
    }

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registerBlock(event, emitterBlock);
        registerBlock(event, triggerBlock);
        registerBlock(event, regionBlock);
        registerBlock(event, conditionModelBlock);
    }

    private static void registerBlock(RegistryEvent.Register<Block> event, Block block) {
        event.getRegistry().register(block);
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEmitter.class, Mappet.MOD_ID + ":emitter");
        GameRegistry.registerTileEntity(TileTrigger.class, Mappet.MOD_ID + ":trigger");
        GameRegistry.registerTileEntity(TileRegion.class, Mappet.MOD_ID + ":region");
        GameRegistry.registerTileEntity(TileConditionModel.class, Mappet.MOD_ID + ":condition_model");
    }
}
