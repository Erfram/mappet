package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiNpcDamagePanel extends GuiNpcPanel
{
    public GuiTrackpadElement damage;
    public GuiTrackpadElement damageDelay;
    public GuiTrackpadElement fallback;
    public GuiToggleElement canFallDamage;
    public GuiToggleElement canGetBurned;
    public GuiToggleElement invincible;
    public GuiToggleElement killable;

    public GuiNpcDamagePanel(Minecraft mc)
    {
        super(mc);

        this.damage = new GuiTrackpadElement(mc, (v) -> this.state.damage = v.floatValue());
        this.damage.limit(0);
        this.damageDelay = new GuiTrackpadElement(mc, (v) -> this.state.damageDelay = v.intValue());
        this.damageDelay.limit(0, 200);
        this.fallback = new GuiTrackpadElement(mc, (v) -> this.state.fallback = v.floatValue());
        this.fallback.limit(0, 64);
        this.canFallDamage = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.fall"), (b) -> this.state.canFallDamage = b.isToggled());
        this.canGetBurned = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.fire"), (b) -> this.state.canGetBurned = b.isToggled());
        this.invincible = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.invincible"), (b) -> this.state.invincible = b.isToggled());
        this.killable = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.killable"), (b) -> this.state.killable = b.isToggled());

        this.add(Elements.label(IKey.lang("mappet.gui.npcs.damage.damage")), this.damage);
        this.add(Elements.label(IKey.lang("mappet.gui.npcs.damage.damage_delay")), this.damageDelay);
        this.add(Elements.label(IKey.lang("mappet.gui.npcs.damage.fallback")), this.fallback);
        this.add(this.canFallDamage.marginTop(12), this.canGetBurned, this.invincible, this.killable);
    }

    @Override
    public void set(NpcState state)
    {
        super.set(state);

        this.damageDelay.setValue(state.damageDelay);
        this.damage.setValue(state.damage);
        this.canFallDamage.toggled(state.canFallDamage);
        this.canGetBurned.toggled(state.canGetBurned);
        this.invincible.toggled(state.invincible);
        this.killable.toggled(state.killable);
        this.fallback.setValue(state.fallback);
    }
}