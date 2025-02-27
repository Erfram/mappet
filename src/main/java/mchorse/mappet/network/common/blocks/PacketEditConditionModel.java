package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketEditConditionModel implements IMessage {
    public BlockPos pos;
    public boolean isEdit;
    public NBTTagCompound tag;

    public PacketEditConditionModel() {}

    public PacketEditConditionModel(BlockPos pos, NBTTagCompound tag) {
        this.isEdit = true;
        this.pos = pos;
        this.tag = tag;
    }

    public PacketEditConditionModel setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.isEdit = buf.readBoolean();
        this.tag = NBTUtils.readInfiniteTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeBoolean(this.isEdit);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketEditConditionModel> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketEditConditionModel message) {
            TileEntity tile = WorldUtils.getTileEntity(player.world, message.pos);

            if (!(tile instanceof TileConditionModel)) {
                return;
            }

            TileConditionModel tileConditionModel = (TileConditionModel) tile;

            GuiMappetDashboard dashboard = GuiMappetDashboard.get(Minecraft.getMinecraft());
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (message.isEdit) {
                tileConditionModel.fill(message.tag);

                dashboard.panels.setPanel(dashboard.conditionModel);
                dashboard.conditionModel.fill(tileConditionModel, true);

                Minecraft.getMinecraft().displayGuiScreen(dashboard);
            } else if (!dashboard.equals(screen) || !dashboard.panels.view.delegate.equals(dashboard.conditionModel)) {
                AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(message.tag.getCompoundTag("morph"));

                tileConditionModel.isGlobal = message.tag.getBoolean("global");
                tileConditionModel.isShadow = message.tag.getBoolean("shadow");

                if (tileConditionModel.entity == null) {
                    tileConditionModel.createEntity(player.world);
                }

                if (tileConditionModel.entity.morph.get() == null || !tileConditionModel.entity.morph.get().equals(morph)) {
                    tileConditionModel.entity.morph.set(null);
                    tileConditionModel.entity.morph(morph, true);
                    tileConditionModel.entity.ticksExisted = 0;
                }
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketEditConditionModel> {
        @Override
        public void run(EntityPlayerMP player, PacketEditConditionModel message) {
            if (!player.isCreative()) {
                return;
            }

            TileEntity tile = WorldUtils.getTileEntity(player.world, message.pos);

            if (tile instanceof TileConditionModel) {
                tile.readFromNBT(message.tag);
            }
        }
    }
}
