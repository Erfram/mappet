package mchorse.mappet.network.server.events;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.CommonProxy;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketMouseEvent implements IMessage {
    public int buttonId;
    public int x;
    public int y;
    public boolean state;

    public PacketMouseEvent() {

    }

    public PacketMouseEvent(int buttonId, int x, int y, boolean state) {
        this.buttonId = buttonId;
        this.x = x;
        this.y = y;
        this.state = state;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.buttonId);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeBoolean(this.state);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.buttonId = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.state = buf.readBoolean();
    }

    public static class ServerHandler extends ServerMessageHandler<PacketMouseEvent> {
        @Override
        public void run(EntityPlayerMP entityPlayerMP, PacketMouseEvent packet) {
            CommonProxy.eventHandler.onMouseInput(entityPlayerMP, packet.buttonId, packet.x, packet.y, packet.state);
        }
    }
}
