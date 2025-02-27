package mchorse.mappet.network.common.items;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketScriptedItemInfo implements IMessage
{
    public NBTTagCompound tag;
    public NBTTagCompound stackTag;
    public int entity;

    public PacketScriptedItemInfo()
    {
        this.tag = new NBTTagCompound();
    }

    public PacketScriptedItemInfo(NBTTagCompound tag, NBTTagCompound stackTag, int entity)
    {
        this.tag = tag;
        this.stackTag = stackTag;
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.tag = NBTUtils.readInfiniteTag(buf);

        if (buf.readBoolean())
        {
            this.stackTag = NBTUtils.readInfiniteTag(buf);
        }

        this.entity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, this.tag);

        buf.writeBoolean(this.stackTag != null);

        if (this.stackTag != null)
        {
            ByteBufUtils.writeTag(buf, this.stackTag);
        }

        buf.writeInt(this.entity);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketScriptedItemInfo> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketScriptedItemInfo message) {
            Entity entity = player.world.getEntityByID(message.entity);

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase base = (EntityLivingBase) entity;
                ItemStack stack = base.getHeldItemMainhand();

                if (stack.isEmpty()) {
                    return;
                }

                if (message.stackTag != null) {
                    stack.setTagCompound(message.stackTag);
                }

                mchorse.mappet.utils.NBTUtils.saveScriptedItemProps(stack, message.tag);
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketScriptedItemInfo> {
        @Override
        public void run(EntityPlayerMP player, PacketScriptedItemInfo message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            ItemStack stack = player.getHeldItemMainhand();

            if (message.stackTag != null) {
                stack.setTagCompound(message.stackTag);
            }

            if (mchorse.mappet.utils.NBTUtils.saveScriptedItemProps(stack, message.tag)) {
                IMessage packet = new PacketScriptedItemInfo(message.tag, message.stackTag, player.getEntityId());
                Dispatcher.sendTo(packet, player);
                Dispatcher.sendToTracked(player, packet);
            }
        }
    }
}
