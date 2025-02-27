package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.tile.TileRegion;
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

public class PacketEditRegion implements IMessage
{
    public boolean open;
    public BlockPos pos;
    public NBTTagCompound tag;

    public PacketEditRegion()
    {}

    public PacketEditRegion(TileRegion tile)
    {
        this(tile.getPos(), tile.region.serializeNBT());
    }

    public PacketEditRegion(BlockPos pos, NBTTagCompound tag)
    {
        this.pos = pos;
        this.tag = tag;
    }

    public PacketEditRegion open()
    {
        this.open = true;

        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.open = buf.readBoolean();
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.tag = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(this.open);
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketEditRegion> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketEditRegion message) {
            TileEntity tile = player.world.getTileEntity(message.pos);

            if (tile instanceof TileRegion) {
                TileRegion region = (TileRegion) tile;

                region.set(message.tag);

                if (message.open) {
                    GuiMappetDashboard dashboard = GuiMappetDashboard.get(Minecraft.getMinecraft());

                    dashboard.panels.setPanel(dashboard.region);
                    dashboard.region.fill(region, true);

                    Minecraft.getMinecraft().displayGuiScreen(dashboard);
                }
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketEditRegion> {
        @Override
        public void run(EntityPlayerMP player, PacketEditRegion message) {
            if (!player.isCreative()) {
                return;
            }

            TileEntity tile = WorldUtils.getTileEntity(player.world, message.pos);

            if (tile instanceof TileRegion) {
                ((TileRegion) tile).set(message.tag);
            }
        }
    }
}