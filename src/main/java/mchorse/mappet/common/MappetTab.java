package mchorse.mappet.common;

import mchorse.mappet.blocks.MappetBlocks;
import mchorse.mappet.items.MappetItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MappetTab extends CreativeTabs {
    public MappetTab() {
        super("mappet");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(MappetItems.npcTool);
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> items) {
        items.add(new ItemStack(MappetItems.npcTool));
        items.add(new ItemStack(MappetItems.npcPicker));
        items.add(new ItemStack(MappetBlocks.emitterBlock));
        items.add(new ItemStack(MappetBlocks.regionBlock));
        items.add(new ItemStack(MappetBlocks.triggerBlock));
        items.add(new ItemStack(MappetBlocks.conditionModelBlock));
    }
}
