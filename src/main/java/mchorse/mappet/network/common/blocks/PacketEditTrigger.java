package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.blocks.BlockTrigger;
import mchorse.mappet.client.gui.GuiTriggerBlockScreen;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEditTrigger implements IMessage
{
    public BlockPos pos;
    public NBTTagCompound left = new NBTTagCompound();
    public NBTTagCompound right = new NBTTagCompound();
    public boolean collidable;
    public Vec3d boundingBoxPos1;
    public Vec3d boundingBoxPos2;

    public PacketEditTrigger()
    {}

    public PacketEditTrigger(TileTrigger tile)
    {
        this(
                tile.getPos(),
                tile.leftClick.serializeNBT(),
                tile.rightClick.serializeNBT(),
                tile.getWorld().getBlockState(tile.getPos()).getValue(BlockTrigger.COLLIDABLE),
                new Vec3d(tile.boundingBoxPos1.x, tile.boundingBoxPos1.y, tile.boundingBoxPos1.z),
                new Vec3d(tile.boundingBoxPos2.x, tile.boundingBoxPos2.y, tile.boundingBoxPos2.z)
        );
    }

    public PacketEditTrigger(BlockPos pos, NBTTagCompound left, NBTTagCompound right, boolean collidable, Vec3d boundingBoxPos1, Vec3d boundingBoxPos2)
    {
        this.pos = pos;
        this.left = left;
        this.right = right;
        this.collidable = collidable;
        this.boundingBoxPos1 = boundingBoxPos1;
        this.boundingBoxPos2 = boundingBoxPos2;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.left = NBTUtils.readInfiniteTag(buf);
        this.right = NBTUtils.readInfiniteTag(buf);
        this.collidable = buf.readBoolean();
        this.boundingBoxPos1 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.boundingBoxPos2 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        ByteBufUtils.writeTag(buf, this.left);
        ByteBufUtils.writeTag(buf, this.right);
        buf.writeBoolean(this.collidable);
        buf.writeDouble(this.boundingBoxPos1.x);
        buf.writeDouble(this.boundingBoxPos1.y);
        buf.writeDouble(this.boundingBoxPos1.z);
        buf.writeDouble(this.boundingBoxPos2.x);
        buf.writeDouble(this.boundingBoxPos2.y);
        buf.writeDouble(this.boundingBoxPos2.z);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketEditTrigger> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketEditTrigger message) {
            Trigger left = new Trigger();
            Trigger right = new Trigger();

            left.deserializeNBT(message.left);
            right.deserializeNBT(message.right);

            Minecraft.getMinecraft().displayGuiScreen(new GuiTriggerBlockScreen(message.pos, left, right, message.collidable, message.boundingBoxPos1, message.boundingBoxPos2));
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketEditTrigger> {
        @Override
        public void run(EntityPlayerMP player, PacketEditTrigger message) {
            if (!player.isCreative()) {
                return;
            }

            TileEntity tile = WorldUtils.getTileEntity(player.world, message.pos);

            if (tile instanceof TileTrigger) {
                ((TileTrigger) tile).set(message.left, message.right, message.collidable, message.boundingBoxPos1, message.boundingBoxPos2);
            }
        }
    }
}