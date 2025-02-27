package mchorse.mappet.network.server.events;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.CommonProxy;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUpdateGuiEvent implements IMessage {
    public String gui = "";

    public PacketUpdateGuiEvent() {

    }

    public PacketUpdateGuiEvent(String gui) {
        this.gui = gui;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.gui);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gui = ByteBufUtils.readUTF8String(buf);
    }


    public static class ServerHandler extends ServerMessageHandler<PacketUpdateGuiEvent> {
        @Override
        public void run(EntityPlayerMP entityPlayerMP, PacketUpdateGuiEvent packet) {
            CommonProxy.eventHandler.onPlayerOpenOrCloseGui(entityPlayerMP, packet.gui);
        }
    }
}
