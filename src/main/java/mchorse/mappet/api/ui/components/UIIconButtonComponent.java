package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UIIconButtonComponent extends UIComponent
{
    public String icon = "";

    public UIIconButtonComponent()
    {}

    public UIIconButtonComponent icon(String icon)
    {
        this.change("Icon");

        this.icon = icon;

        return this;
    }

    @Override
    @DiscardMethod
    @SideOnly(Side.CLIENT)
    protected boolean isDataReserved()
    {
        return true;
    }

    @Override
    @DiscardMethod
    @SideOnly(Side.CLIENT)
    protected void applyProperty(UIContext context, String key, GuiElement element)
    {
        super.applyProperty(context, key, element);

        GuiIconElement button = (GuiIconElement) element;

        if (key.equals("Icon"))
        {
            button.both(this.getIcon());
        }
    }

    @Override
    @DiscardMethod
    @SideOnly(Side.CLIENT)
    public GuiElement create(Minecraft mc, UIContext context)
    {
        GuiIconElement button = new GuiIconElement(mc, this.getIcon(), (b) ->
        {
            if (!this.id.isEmpty())
            {
                this.populateData(context.data);
                context.dirty(this.id, this.updateDelay);
            }
        });

        return this.apply(button, context);
    }

    @DiscardMethod
    @SideOnly(Side.CLIENT)
    private Icon getIcon()
    {
        Icon icon = IconRegistry.icons.get(this.icon);

        if (icon == null)
        {
            icon = Icons.NONE;
        }

        return icon;
    }

    @Override
    @DiscardMethod
    public void populateData(NBTTagCompound tag)
    {
        super.populateData(tag);

        if (!this.id.isEmpty())
        {
            tag.setInteger(this.id, tag.getInteger(this.id) + 1);
        }
    }

    @Override
    @DiscardMethod
    public void serializeNBT(NBTTagCompound tag)
    {
        super.serializeNBT(tag);

        tag.setString("Icon", this.icon);
    }

    @Override
    @DiscardMethod
    public void deserializeNBT(NBTTagCompound tag)
    {
        super.deserializeNBT(tag);

        if (tag.hasKey("Icon"))
        {
            this.icon = tag.getString("Icon");
        }
    }
}