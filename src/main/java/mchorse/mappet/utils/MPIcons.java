package mchorse.mappet.utils;

import mchorse.mappet.Mappet;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import net.minecraft.util.ResourceLocation;

public class MPIcons
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Mappet.MOD_ID, "textures/gui/icons.png");

    public static final Icon REPL = new Icon(TEXTURE, 0, 0);
    public static final Icon IN = new Icon(TEXTURE, 16, 0);
    public static final Icon OUT = new Icon(TEXTURE, 32, 0);
    public static final Icon BAG = new Icon(TEXTURE, 48, 0);
    public static final Icon MED_KIT = new Icon(TEXTURE, 64, 0);
    public static final Icon WALLET = new Icon(TEXTURE, 80, 0);
    public static final Icon HANGER = new Icon(TEXTURE, 96, 0);
    public static final Icon STATISTICS = new Icon(TEXTURE, 112, 0);
    public static final Icon BANNER = new Icon(TEXTURE, 128, 0);
    public static final Icon OPEN_LOCK = new Icon(TEXTURE, 144, 0);
    public static final Icon LOCK = new Icon(TEXTURE, 160, 0);
    public static final Icon FLAG = new Icon(TEXTURE, 176, 0);
    public static final Icon FLAG_WIN = new Icon(TEXTURE, 192, 0);
    public static final Icon UMBRELLA = new Icon(TEXTURE, 208, 0);
    public static final Icon STAR = new Icon(TEXTURE, 224, 0);
    public static final Icon STAR_2 = new Icon(TEXTURE, 240, 0);

    public static final Icon KEY = new Icon(TEXTURE, 0, 16);
    public static final Icon KEY_2 = new Icon(TEXTURE, 16, 16);
    public static final Icon KEY_3 = new Icon(TEXTURE, 32, 16);
    public static final Icon X = new Icon(TEXTURE, 48, 16);
    public static final Icon BACKPACK = new Icon(TEXTURE, 64, 16);
    public static final Icon BOOK = new Icon(TEXTURE, 80, 16);
    public static final Icon BOOK_2 = new Icon(TEXTURE, 96, 16);
    public static final Icon DOCUMENT = new Icon(TEXTURE, 112, 16);
    public static final Icon CANDLE = new Icon(TEXTURE, 128, 16);
    public static final Icon HOOK = new Icon(TEXTURE, 144, 16);
    public static final Icon USER = new Icon(TEXTURE, 160, 16);
    public static final Icon USER_CROSS = new Icon(TEXTURE, 176, 16);
    public static final Icon USER_RED_CROSS = new Icon(TEXTURE, 192, 16);
    public static final Icon MESSAGE = new Icon(TEXTURE, 208, 16);
    public static final Icon MESSAGE_CROSS = new Icon(TEXTURE, 224, 16);
    public static final Icon MESSAGE_RED_CROSS = new Icon(TEXTURE, 240, 16);

    public static final Icon MICROPHONE = new Icon(TEXTURE, 0, 32);
    public static final Icon MICROPHONE_CROSS = new Icon(TEXTURE, 16, 32);
    public static final Icon MICROPHONE_RED_CROSS = new Icon(TEXTURE, 32, 32);
    public static final Icon SPEAKER = new Icon(TEXTURE, 48, 32);
    public static final Icon SPEAKER_CROSS = new Icon(TEXTURE, 64, 32);
    public static final Icon SPEAKER_RED_CROSS = new Icon(TEXTURE, 80, 32);
    public static final Icon SPEAKER_OFF = new Icon(TEXTURE, 96, 32);
    public static final Icon SPEAKER_SILENT = new Icon(TEXTURE, 112, 32);
    public static final Icon SPEAKER_FULL = new Icon(TEXTURE, 128, 32);
    public static final Icon SWORD = new Icon(TEXTURE, 144, 32);
    public static final Icon DAGGER = new Icon(TEXTURE, 160, 32);
    public static final Icon DAGGER_2 = new Icon(TEXTURE, 176, 32);
    public static final Icon DAGGER_3 = new Icon(TEXTURE, 192, 32);
    public static final Icon SWORD_2 = new Icon(TEXTURE, 208, 32);
    public static final Icon HAMMER = new Icon(TEXTURE, 224, 32);
    public static final Icon CHAMPAGNE = new Icon(TEXTURE, 240, 32);

    public static void register()
    {
        IconRegistry.register("repl", REPL);
        IconRegistry.register("in", IN);
        IconRegistry.register("out", OUT);
        IconRegistry.register("bag", BAG);
        IconRegistry.register("med_kit", MED_KIT);
        IconRegistry.register("wallet", WALLET);
        IconRegistry.register("hanger", HANGER);
        IconRegistry.register("statistics", STATISTICS);
        IconRegistry.register("banner", BANNER);
        IconRegistry.register("open_lock", OPEN_LOCK);
        IconRegistry.register("lock", LOCK);
        IconRegistry.register("flag", FLAG);
        IconRegistry.register("flag_win", FLAG_WIN);
        IconRegistry.register("umbrella", UMBRELLA);
        IconRegistry.register("star", STAR);
        IconRegistry.register("star_2", STAR_2);

        IconRegistry.register("key", KEY);
        IconRegistry.register("key_2", KEY_2);
        IconRegistry.register("key_3", KEY_3);
        IconRegistry.register("X", X);
        IconRegistry.register("backpack", BACKPACK);
        IconRegistry.register("book", BOOK);
        IconRegistry.register("book_2", BOOK_2);
        IconRegistry.register("document", DOCUMENT);
        IconRegistry.register("candle", CANDLE);
        IconRegistry.register("hook", HOOK);
        IconRegistry.register("user", USER);
        IconRegistry.register("user_cross", USER_CROSS);
        IconRegistry.register("user_red_cross", USER_RED_CROSS);
        IconRegistry.register("message", MESSAGE);
        IconRegistry.register("message_cross", MESSAGE_CROSS);
        IconRegistry.register("message_red_cross", MESSAGE_RED_CROSS);

        IconRegistry.register("microphone", MICROPHONE);
        IconRegistry.register("microphone_cross", MICROPHONE_CROSS);
        IconRegistry.register("microphone_red_cross", MICROPHONE_RED_CROSS);
        IconRegistry.register("speaker", SPEAKER);
        IconRegistry.register("speaker_cross", SPEAKER_CROSS);
        IconRegistry.register("speaker_red_cross", SPEAKER_RED_CROSS);
        IconRegistry.register("speaker_off", SPEAKER_OFF);
        IconRegistry.register("speaker_silent", SPEAKER_SILENT);
        IconRegistry.register("speaker_full", SPEAKER_FULL);
        IconRegistry.register("sword", SWORD);
        IconRegistry.register("dagger", DAGGER);
        IconRegistry.register("dagger_2", DAGGER_2);
        IconRegistry.register("dagger_3", DAGGER_3);
        IconRegistry.register("sword_2", SWORD_2);
        IconRegistry.register("hammer", HAMMER);
        IconRegistry.register("champagne", CHAMPAGNE);
    }
}