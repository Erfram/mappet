package mchorse.mappet.items;

import mchorse.mappet.Mappet;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemNpcSoulStoneEmpty extends Item {
    public ItemNpcSoulStoneEmpty() {
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(IKey.lang("item.mappet.npc_soul_stone.tooltip").toString());
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (player.world.isRemote || !(target instanceof EntityNpc)) {
            return super.itemInteractionForEntity(stack, player, target, hand);
        }

        final EntityNpc npc = (EntityNpc) target;
        final NBTTagCompound npcState = npc.getState().serializeNBT();
        final String id = npcState.getString("Id");

        ItemStack npcSoulstoneFilled = new ItemStack(MappetItems.npcSoulStoneFilled);

        npcSoulstoneFilled.setTagCompound(npcState);

        player.setHeldItem(hand, npcSoulstoneFilled);

        return true;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isCreative() && playerIn.isSneaking()) {
            if (worldIn.isRemote) {
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            if (this.replaceItem(playerIn, playerIn.getHeldItem(handIn), handIn)) {
                return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private boolean replaceItem(EntityPlayer player, ItemStack stack, EnumHand hand) {
        ItemStack npcPicker = new ItemStack(MappetItems.npcPicker);

        npcPicker.setTagCompound(stack.getTagCompound());
        player.setHeldItem(hand, npcPicker);
        return true;
    }
}