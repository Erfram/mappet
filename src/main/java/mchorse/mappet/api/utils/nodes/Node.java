package mchorse.mappet.api.utils.nodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public abstract class Node implements INBTSerializable<NBTTagCompound>
{
    public String title = "";

    private UUID id;

    /* Visual properties */
    public int x;
    public int y;

    public UUID getId()
    {
        return this.id;
    }

    public void setId(UUID id)
    {
        if (this.id == null)
        {
            this.id = id;
        }
    }

    @SideOnly(Side.CLIENT)
    public String getTitle()
    {
        if (this.title.isEmpty())
        {
            return this.getDisplayTitle();
        }

        return this.title;
    }

    @SideOnly(Side.CLIENT)
    protected String getDisplayTitle()
    {
        return "";
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if (this.id != null)
        {
            tag.setString("Id", this.id.toString());
        }

        tag.setString("Title", this.title);
        tag.setInteger("X", this.x);
        tag.setInteger("Y", this.y);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Id"))
        {
            this.id = UUID.fromString(tag.getString("Id"));
        }

        this.title = tag.getString("Title");
        this.x = tag.getInteger("X");
        this.y = tag.getInteger("Y");
    }
}