package mchorse.mappet.network.common.crafting;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.client.gui.GuiCraftingTableScreen;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCraftingTable implements IMessage {
    public CraftingTable table;

    public PacketCraftingTable() {}

    public PacketCraftingTable(CraftingTable table) {
        this.table = table;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            NBTTagCompound tag = NBTUtils.readInfiniteTag(buf);

            this.table = new CraftingTable();
            this.table.deserializeNBT(tag);
            this.table.setId(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.table != null);

        if (this.table != null) {
            ByteBufUtils.writeTag(buf, this.table.serializeNBT());
            ByteBufUtils.writeUTF8String(buf, this.table.getId());
        }
    }

    public static class ClientHandler extends ClientMessageHandler<PacketCraftingTable> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketCraftingTable message) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCraftingTableScreen(message.table));
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketCraftingTable> {
        @Override
        public void run(EntityPlayerMP player, PacketCraftingTable message) {
            ICharacter character = Character.get(player);

            if (character != null) {
                character.setCraftingTable(null);

                if (character.getDialogueContext() != null) {
                    ReactionNode node = character.getDialogueContext().reactionNode;

                    if (node != null && !node.sound.isEmpty()) {
                        WorldUtils.stopSound(player, node.sound);
                    }

                    character.setDialogue(null, null);
                }
            }
        }
    }
}