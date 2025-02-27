package mchorse.mappet.network.common.crafting;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.client.gui.crafting.ICraftingScreen;
import mchorse.mappet.network.Dispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCraft implements IMessage
{
    public int index;

    public PacketCraft() {}

    public PacketCraft(int index) {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.index);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketCraft> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketCraft message) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof ICraftingScreen) {
                ((ICraftingScreen) screen).refresh();
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketCraft> {
        @Override
        public void run(EntityPlayerMP player, PacketCraft message) {
            ICharacter character = Character.get(player);

            if (character != null && character.getCraftingTable() != null) {
                DataContext context = null;

                if (character.getDialogueContext() != null) {
                    context = character.getDialogueContext().data;
                }

                character.getCraftingTable().recipes.get(message.index).craft(player, context);

                Dispatcher.sendTo(message, player);
            }
        }
    }
}
