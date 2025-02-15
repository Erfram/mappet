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
    public static final Icon KEY_1 = new Icon(TEXTURE, 16, 16);
    public static final Icon KEY_2 = new Icon(TEXTURE, 32, 16);

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
        IconRegistry.register("key_1", KEY_1);
        IconRegistry.register("key_2", KEY_2);
    }
}