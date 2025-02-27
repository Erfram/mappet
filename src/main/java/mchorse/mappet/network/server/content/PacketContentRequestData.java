package mchorse.mappet.network.server.content;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentBase;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketContentRequestData extends PacketContentBase {
    public String name = "";

    public PacketContentRequestData() {
        super();
    }

    public PacketContentRequestData(IContentType type, String name) {
        super(type);

        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        this.name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        ByteBufUtils.writeUTF8String(buf, this.name);
    }

    public static class ServerHandler extends ServerMessageHandler<PacketContentRequestData> {
        public static boolean isOtherPlayerEdits(EntityPlayerMP except, IContentType type, String id) {
            for (EntityPlayerMP player : except.getServer().getPlayerList().getPlayers()) {
                if (player == except) {
                    continue;
                }

                if (mchorse.mappet.capabilities.character.Character.get(player).getCurrentSession().isActive(type, id)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void run(EntityPlayerMP player, PacketContentRequestData message) {
            if (!OpHelper.isPlayerOp(player)) {
                return;
            }

            boolean otherEdit = isOtherPlayerEdits(player, message.type, message.name);

            NBTTagCompound tag = message.type.getManager().load(message.name).serializeNBT();
            PacketContentData packet = new PacketContentData(message.type, message.name, tag);
            CurrentSession session = Character.get(player).getCurrentSession();

            if (otherEdit) {
                packet.disallow();
            }

            Dispatcher.sendTo(packet, player);

            if (!session.isEditing(message.type, message.name)) {
                ServerHandlerContentExit.syncData(player);
            }

            if (!otherEdit) {
                session.set(message.type, message.name);
            }

            session.setActive(message.type, message.name);
        }
    }
}