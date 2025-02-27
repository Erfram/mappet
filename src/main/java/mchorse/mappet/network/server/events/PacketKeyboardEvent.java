package mchorse.mappet.network.server.events;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.CommonProxy;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketKeyboardEvent implements IMessage {
    int key;
    boolean state;

    public PacketKeyboardEvent() {

    }

    public PacketKeyboardEvent(int key, boolean state) {
        this.key = key;
        this.state = state;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.key);
        buf.writeBoolean(this.state);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = buf.readInt();
        this.state = buf.readBoolean();
    }

    public static class ServerHandler extends ServerMessageHandler<PacketKeyboardEvent> {
        @Override
        public void run(EntityPlayerMP entityPlayerMP, PacketKeyboardEvent packet) {
            CommonProxy.eventHandler.onKeyboardInput(entityPlayerMP, packet.key, packet.state);
        }
    }
}
