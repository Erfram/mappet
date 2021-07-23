package mchorse.mappet.client.gui.crafting;

import mchorse.mappet.api.crafting.CraftingRecipe;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class GuiCraftingRecipeList extends GuiListElement<CraftingRecipe>
{
    public GuiCraftingRecipeList(Minecraft mc, Consumer<List<CraftingRecipe>> callback)
    {
        super(mc, callback);
    }

    @Override
    protected String elementToString(CraftingRecipe element)
    {
        return element.title;
    }
}