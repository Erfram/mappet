package mchorse.mappet.network.client.utils;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketChangedBoundingBox implements IMessage {
    public BlockPos pos;
    public Vec3d boundingBoxPos1;
    public Vec3d boundingBoxPos2;

    public PacketChangedBoundingBox() {

    }

    public PacketChangedBoundingBox(BlockPos pos, Vec3d boundingBoxPos1, Vec3d boundingBoxPos2) {
        this.pos = pos;
        this.boundingBoxPos1 = boundingBoxPos1;
        this.boundingBoxPos2 = boundingBoxPos2;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.boundingBoxPos1 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.boundingBoxPos2 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeDouble(boundingBoxPos1.x);
        buf.writeDouble(boundingBoxPos1.y);
        buf.writeDouble(boundingBoxPos1.z);
        buf.writeDouble(boundingBoxPos2.x);
        buf.writeDouble(boundingBoxPos2.y);
        buf.writeDouble(boundingBoxPos2.z);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketChangedBoundingBox> {
        @Override
        public void run(EntityPlayerSP entityPlayerSP, PacketChangedBoundingBox message) {
            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(message.pos);

            if (tile instanceof TileTrigger) {
                ((TileTrigger) tile).boundingBoxPos1 = message.boundingBoxPos1;
                ((TileTrigger) tile).boundingBoxPos2 = message.boundingBoxPos2;
            }
        }
    }
}
