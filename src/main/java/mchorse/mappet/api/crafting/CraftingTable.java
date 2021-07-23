package mchorse.mappet.api.crafting;

import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CraftingTable extends AbstractData
{
    public String title = "";
    public String action = "";
    public List<CraftingRecipe> recipes = new ArrayList<CraftingRecipe>();

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList recipes = new NBTTagList();

        for (CraftingRecipe recipe : this.recipes)
        {
            NBTTagCompound recipeTag = recipe.serializeNBT();

            if (recipeTag.getSize() > 0)
            {
                recipes.appendTag(recipeTag);
            }
        }

        if (!this.title.isEmpty())
        {
            tag.setString("Title", this.title);
        }

        if (!this.action.isEmpty())
        {
            tag.setString("Action", this.action);
        }

        if (recipes.tagCount() > 0)
        {
            tag.setTag("Recipes", recipes);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Title"))
        {
            this.title = tag.getString("Title");
        }

        if (tag.hasKey("Action"))
        {
            this.action = tag.getString("Action");
        }

        if (tag.hasKey("Recipes"))
        {
            NBTTagList recipes = tag.getTagList("Recipes", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < recipes.tagCount(); i++)
            {
                CraftingRecipe recipe = new CraftingRecipe();

                recipe.deserializeNBT(recipes.getCompoundTagAt(i));
                this.recipes.add(recipe);
            }
        }
    }

    public void filter(EntityPlayerMP player)
    {
        this.recipes.removeIf(recipe -> !recipe.isAvailable(player));
    }
}