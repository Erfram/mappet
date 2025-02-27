package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPlayAnimation implements IMessage {
    public String animation;
    public String uuid;

    public PacketPlayAnimation(String animation, String uuid) {
        this.animation = animation;
        this.uuid = uuid;
    }

    public PacketPlayAnimation() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.animation = ByteBufUtils.readUTF8String(buf);
        this.uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.animation);
        ByteBufUtils.writeUTF8String(buf, this.uuid);
    }
}
