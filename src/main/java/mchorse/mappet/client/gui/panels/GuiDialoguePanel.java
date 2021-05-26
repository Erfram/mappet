package mchorse.mappet.client.gui.panels;

import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueManager;
import mchorse.mappet.api.dialogues.nodes.CraftingNode;
import mchorse.mappet.api.dialogues.nodes.DialogueNode;
import mchorse.mappet.api.dialogues.nodes.QuestChainNode;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.api.events.nodes.CommandNode;
import mchorse.mappet.api.events.nodes.ConditionNode;
import mchorse.mappet.api.events.nodes.EventNode;
import mchorse.mappet.api.events.nodes.SwitchNode;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.nodes.GuiEventNodeGraph;
import mchorse.mappet.client.gui.nodes.GuiEventNodePanel;
import mchorse.mappet.client.gui.nodes.dialogues.GuiCraftingNodePanel;
import mchorse.mappet.client.gui.nodes.dialogues.GuiDialogueNodePanel;
import mchorse.mappet.client.gui.nodes.dialogues.GuiQuestChainNodePanel;
import mchorse.mappet.client.gui.nodes.dialogues.GuiReactionNodePanel;
import mchorse.mappet.client.gui.nodes.events.GuiCommandNodePanel;
import mchorse.mappet.client.gui.nodes.events.GuiConditionNodePanel;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiDialoguePanel extends GuiMappetDashboardPanel<Dialogue>
{
    public GuiEventNodeGraph graph;
    public GuiEventNodePanel panel;
    public GuiToggleElement closable;

    public GuiDialoguePanel(Minecraft mc, GuiMappetDashboard dashboard)
    {
        super(mc, dashboard);

        this.graph = new GuiEventNodeGraph(mc, DialogueManager.FACTORY, this::pickNode);
        this.graph.notifyAboutMain().flex().relative(this.editor).wh(1F, 1F);

        this.closable = new GuiToggleElement(mc, IKey.lang("mappet.gui.nodes.dialogue.closable"), (b) -> this.data.closable = b.isToggled());
        this.closable.flex().relative(this.sidebar).x(10).y(1F, -10).w(1F, -20).anchorY(1F);

        this.names.flex().hTo(this.closable.area, -5);

        this.editor.add(this.graph);
        this.sidebar.prepend(this.closable);

        this.fill(null);
    }

    private void pickNode(EventNode node)
    {
        if (this.panel != null)
        {
            this.panel.removeFromParent();
            this.panel = null;
        }

        if (node != null)
        {
            GuiEventNodePanel panel = null;

            if (node instanceof CommandNode)
            {
                panel = new GuiCommandNodePanel(this.mc);
                panel.set(node);
            }
            else if (node instanceof ConditionNode)
            {
                panel = new GuiConditionNodePanel(this.mc);
                panel.set(node);

                if (node instanceof SwitchNode)
                {
                    panel.binary.removeFromParent();
                }
            }
            else if (node instanceof ReactionNode)
            {
                panel = new GuiReactionNodePanel(this.mc, this.dashboard::getMorphMenu);
                panel.set(node);
            }
            else if (node instanceof DialogueNode)
            {
                panel = new GuiDialogueNodePanel(this.mc);
                panel.set(node);
            }
            else if (node instanceof CraftingNode)
            {
                panel = new GuiCraftingNodePanel(this.mc);
                panel.set(node);
            }
            else if (node instanceof QuestChainNode)
            {
                panel = new GuiQuestChainNodePanel(this.mc);
                panel.set(node);
            }

            if (panel != null)
            {
                panel.flex().relative(this).y(1F).w(220).anchorY(1F);

                this.panel = panel;
                this.panel.resize();

                this.editor.add(panel);
            }
        }
    }

    @Override
    public ContentType getType()
    {
        return ContentType.DIALOGUE;
    }

    @Override
    public String getTitle()
    {
        return "mappet.gui.panels.dialogues";
    }

    @Override
    public void fill(Dialogue data, boolean allowed)
    {
        super.fill(data, allowed);

        this.graph.setVisible(data != null);
        this.closable.setVisible(data != null && allowed);
        this.pickNode(null);

        if (data != null)
        {
            this.graph.set(data);
            this.closable.toggled(data.closable);
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        if (!this.graph.isVisible())
        {
            int w = this.editor.area.w / 2;
            int x = this.editor.area.mx() - w / 2;

            GuiDraw.drawMultiText(this.font, I18n.format("mappet.gui.nodes.info.empty_dialogue"), x, this.area.my(), 0xffffff, w, 12, 0.5F, 0.5F);
        }
    }
}