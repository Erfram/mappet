package mchorse.mappet.api.scripts.code;

import mchorse.mappet.api.scripts.code.blocks.ScriptBlockState;
import mchorse.mappet.api.scripts.user.IScriptBiome;
import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;
import net.minecraft.world.biome.Biome;

public class ScriptBiome implements IScriptBiome {
    Biome biome;

    public ScriptBiome(Biome biome) {
        this.biome = biome;
    }

    @Override
    public boolean canRain() {
        return this.biome.canRain();
    }

    @Override
    public float getSpawningChance() {
        return this.biome.getSpawningChance();
    }

    @Override
    public String getName() {
        return this.biome.getBiomeName();
    }

    @Override
    public IScriptBlockState getFilterBlock() {
        return ScriptBlockState.create(this.biome.fillerBlock);
    }

    @Override
    public IScriptBlockState getTopBlock() {
        return ScriptBlockState.create(this.biome.topBlock);
    }

    @Override
    public boolean isSnowy() {
        return this.biome.isSnowyBiome();
    }

    @Override
    public boolean isMutation() {
        return this.biome.isMutation();
    }

    @Override
    public boolean isHighHumidity() {
        return this.biome.isHighHumidity();
    }
}
