package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.client.gui.GuiEmitterBlockScreen;
import mchorse.mappet.tile.TileEmitter;
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
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEditEmitter implements IMessage
{
    public BlockPos pos;
    public NBTTagCompound checker;
    public float radius;
    public int update;
    public boolean disable;

    public PacketEditEmitter()
    {}

    public PacketEditEmitter(TileEmitter tile)
    {
        this(tile.getPos(), tile.getChecker().toNBT(), tile.getRadius(), tile.getUpdate(), tile.getDisable());
    }

    public PacketEditEmitter(BlockPos pos, NBTTagCompound checker, float radius, int update, boolean disable)
    {
        this.pos = pos;
        this.checker = checker;
        this.radius = radius;
        this.update = update;
        this.disable = disable;
    }

    public Checker createChecker()
    {
        Checker checker = new Checker();

        checker.deserializeNBT(this.checker);

        return checker;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.checker = NBTUtils.readInfiniteTag(buf);
        this.radius = buf.readFloat();
        this.update = buf.readInt();
        this.disable = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        ByteBufUtils.writeTag(buf, this.checker);
        buf.writeFloat(this.radius);
        buf.writeInt(this.update);
        buf.writeBoolean(this.disable);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketEditEmitter> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketEditEmitter message) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEmitterBlockScreen(message));
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketEditEmitter> {
        @Override
        public void run(EntityPlayerMP player, PacketEditEmitter message) {
            if (!player.isCreative()) {
                return;
            }

            TileEntity tile = WorldUtils.getTileEntity(player.world, message.pos);

            if (tile instanceof TileEmitter) {
                ((TileEmitter) tile).setExpression(message);
            }
        }
    }
}