package mchorse.mappet.client.gui;

import mchorse.mappet.client.gui.panels.GuiCraftingTablePanel;
import mchorse.mappet.client.gui.panels.GuiDialoguePanel;
import mchorse.mappet.client.gui.panels.GuiEventPanel;
import mchorse.mappet.client.gui.panels.GuiFactionPanel;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mappet.client.gui.panels.GuiNpcPanel;
import mchorse.mappet.client.gui.panels.GuiQuestChainPanel;
import mchorse.mappet.client.gui.panels.GuiQuestPanel;
import mchorse.mappet.client.gui.panels.GuiRegionPanel;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentExit;
import mchorse.mclib.client.gui.mclib.GuiAbstractDashboard;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanels;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;

public class GuiMappetDashboard extends GuiAbstractDashboard
{
    public static GuiMappetDashboard dashboard;

    public GuiCraftingTablePanel crafting;
    public GuiQuestPanel quest;
    public GuiEventPanel event;
    public GuiDialoguePanel dialogue;
    public GuiRegionPanel region;
    public GuiNpcPanel npc;
    public GuiFactionPanel faction;
    public GuiQuestChainPanel chain;
    public GuiScriptPanel script;

    public GuiCreativeMorphsMenu morphs;

    public static GuiMappetDashboard get(Minecraft mc)
    {
        if (dashboard == null)
        {
            dashboard = new GuiMappetDashboard(mc);
        }

        return dashboard;
    }

    public GuiMappetDashboard(Minecraft mc)
    {
        super(mc);
    }

    @Override
    protected GuiDashboardPanels createDashboardPanels(Minecraft mc)
    {
        return new GuiDashboardPanels(mc);
    }

    public GuiCreativeMorphsMenu getMorphMenu()
    {
        if (this.morphs == null)
        {
            this.morphs = new GuiCreativeMorphsMenu(Minecraft.getMinecraft(), null).pickUponExit();
        }

        return this.morphs;
    }

    @Override
    protected void registerPanels(Minecraft mc)
    {
        this.crafting = new GuiCraftingTablePanel(mc, this);
        this.quest = new GuiQuestPanel(mc, this);
        this.event = new GuiEventPanel(mc, this);
        this.dialogue = new GuiDialoguePanel(mc, this);
        this.region = new GuiRegionPanel(mc, this);
        this.npc = new GuiNpcPanel(mc, this);
        this.faction = new GuiFactionPanel(mc, this);
        this.chain = new GuiQuestChainPanel(mc, this);
        this.script = new GuiScriptPanel(mc, this);

        this.panels.registerPanel(this.crafting, IKey.lang("mappet.gui.panels.crafting"), Icons.WRENCH);
        this.panels.registerPanel(this.quest, IKey.lang("mappet.gui.panels.quests"), Icons.EXCLAMATION);
        this.panels.registerPanel(this.event, IKey.lang("mappet.gui.panels.events"), Icons.FILE);
        this.panels.registerPanel(this.dialogue, IKey.lang("mappet.gui.panels.dialogues"), Icons.BUBBLE);
        this.panels.registerPanel(this.region, IKey.lang("mappet.gui.panels.regions"), Icons.FULLSCREEN);
        this.panels.registerPanel(this.npc, IKey.lang("mappet.gui.panels.npcs"), Icons.PROCESSOR);
        this.panels.registerPanel(this.faction, IKey.lang("mappet.gui.panels.factions"), Icons.BOOKMARK);
        this.panels.registerPanel(this.chain, IKey.lang("mappet.gui.panels.chains"), Icons.FOLDER);
        this.panels.registerPanel(this.script, IKey.lang("mappet.gui.panels.scripts"), MMIcons.PROPERTIES);

        this.panels.setPanel(this.crafting);
    }

    @Override
    protected void closeScreen()
    {
        super.closeScreen();

        Dispatcher.sendToServer(new PacketContentExit());
    }
}