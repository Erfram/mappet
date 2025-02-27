package mchorse.mappet.network.server.logs;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketLogs implements IMessage {
    public String text;

    public PacketLogs() {
    }

    public PacketLogs(String line) {
        this.text = line;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.text = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.text);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketLogs> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketLogs message) {
            GuiMappetDashboard.get(Minecraft.getMinecraft()).logs.update(message.text);
        }
    }
}