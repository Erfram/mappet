package mchorse.mappet.client;

import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.server.events.PacketKeyboardEvent;
import mchorse.mappet.network.server.events.PacketMouseEvent;
import mchorse.mappet.network.server.events.PacketUpdateGuiEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.getGui() != null) {
            Dispatcher.sendToServer(new PacketUpdateGuiEvent(this.processText(event.getGui().toString())));
        }
    }

    @SubscribeEvent
    public void onKeyboard(InputEvent.KeyInputEvent event) {
        Dispatcher.sendToServer(new PacketKeyboardEvent(Keyboard.getEventKey(), Keyboard.getEventKeyState()));
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.getButton() != -1) {
            Dispatcher.sendToServer(new PacketMouseEvent(event.getButton(), event.getX(), event.getY(), event.isButtonstate()));
        }
    }

    public String processText(String text){
        int lastDot = text.lastIndexOf('.');
        text = text.substring(lastDot + 1);

        int indexOfAt = text.indexOf('@');
        if(indexOfAt != -1) {
            text = text.substring(0, indexOfAt);
        }

        text = text.trim();
        if(text.toLowerCase().startsWith("gui")) {
            text = text.substring(3);
        }

        if(!text.isEmpty()) {
            text = Character.toLowerCase(text.charAt(0)) + text.substring(1);
        }

        return text;
    }
}
