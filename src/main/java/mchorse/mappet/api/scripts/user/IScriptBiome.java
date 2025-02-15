package mchorse.mappet.api.scripts.user;

import mchorse.mappet.api.scripts.user.blocks.IScriptBlockState;

public interface IScriptBiome {
    boolean canRain();
    float getSpawningChance();
    String getName();
    IScriptBlockState getFilterBlock();
    IScriptBlockState getTopBlock();
    boolean isSnowy();
    boolean isMutation();
    boolean isHighHumidity();
}
