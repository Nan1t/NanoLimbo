package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

public final class DefaultDimension {

    private static CompoundBinaryTag CODEC;
    private static CompoundBinaryTag DIMENSION;

    public static void init(){
        DIMENSION = CompoundBinaryTag.builder()
                .putString("name", "minecraft:the_end")
                .putByte("piglin_safe", (byte) 0)
                .putByte("natural", (byte) 0)
                .putFloat("ambient_light", 0.0F)
                .putString("infiniburn", "minecraft:infiniburn_end")
                .putByte("respawn_anchor_works", (byte) 0)
                .putByte("has_skylight", (byte) 0)
                .putByte("bed_works", (byte) 0)
                .putString("effects", "minecraft:the_end")
                .putLong("fixed_time", 6000L)
                .putByte("has_raids", (byte) 1)
                .putInt("logical_height", 256)
                .putDouble("coordinate_scale", 1.0)
                .putByte("ultrawarm", (byte) 0)
                .putByte("has_ceiling", (byte) 0)
                .build();

        CompoundBinaryTag dimensionData = CompoundBinaryTag.builder()
                .putString("name", "minecraft:the_end")
                .putInt("id", 2)
                .put("element", DIMENSION)
                .build();

        CompoundBinaryTag plains = CompoundBinaryTag.builder()
                .putString("name", "minecraft:plains")
                .putInt("id", 1)
                .put("element", CompoundBinaryTag.builder()
                        .putString("precipitation", "rain")
                        .putFloat("depth", 0.125F)
                        .putFloat("temperature", 0.8F)
                        .putFloat("scale", 0.05F)
                        .putFloat("downfall", 0.4F)
                        .putString("category", "plains")
                        .put("effects", CompoundBinaryTag.builder()
                                .putInt("sky_color", 7907327)
                                .putInt("water_fog_color", 329011)
                                .putInt("fog_color", 12638463)
                                .putInt("water_color", 4159204)
                                .put("mood_sound", CompoundBinaryTag.builder()
                                        .putInt("tick_delay", 6000)
                                        .putFloat("offset", 2.0F)
                                        .putString("sound", "minecraft:ambient.cave")
                                        .putInt("block_search_extent", 8)
                                        .build())
                                .build())
                        .build())
                .build();

        CODEC = CompoundBinaryTag.builder()
                .put("minecraft:dimension_type", CompoundBinaryTag.builder()
                        .putString("type", "minecraft:dimension_type")
                        .put("value", ListBinaryTag.builder()
                                .add(dimensionData)
                                .build())
                        .build())
                .put("minecraft:worldgen/biome", CompoundBinaryTag.builder()
                        .putString("type", "minecraft:worldgen/biome")
                        .put("value", ListBinaryTag.builder()
                                .add(plains)
                                .build())
                        .build())
                .build();
    }

    public static CompoundBinaryTag getCodec(){
        return CODEC;
    }

    public static CompoundBinaryTag getDimension(){
        return DIMENSION;
    }

}
