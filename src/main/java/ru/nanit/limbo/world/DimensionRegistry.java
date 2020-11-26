package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import ru.nanit.limbo.util.Logger;

public final class DimensionRegistry {

    private static CompoundBinaryTag codec;
    private static CompoundBinaryTag defaultDimension;

    public static void init(String defaultDimensionName){
        CompoundBinaryTag overworld = CompoundBinaryTag.builder()
                .putString("name", "minecraft:overworld")
                .putByte("piglin_safe", (byte) 0)
                .putByte("natural", (byte) 0)
                .putFloat("ambient_light", 0.0F)
                .putString("infiniburn", "minecraft:infiniburn_overworld")
                .putByte("respawn_anchor_works", (byte) 0)
                .putByte("has_skylight", (byte) 0)
                .putByte("bed_works", (byte) 0)
                .putString("effects", "minecraft:overworld")
                .putLong("fixed_time", 6000L)
                .putByte("has_raids", (byte) 1)
                .putInt("logical_height", 256)
                .putDouble("coordinate_scale", 1.0)
                .putByte("ultrawarm", (byte) 0)
                .putByte("has_ceiling", (byte) 0)
                .build();

        CompoundBinaryTag nether = CompoundBinaryTag.builder()
                .putString("name", "minecraft:the_nether")
                .putByte("piglin_safe", (byte) 0)
                .putByte("natural", (byte) 0)
                .putFloat("ambient_light", 0.0F)
                .putString("infiniburn", "minecraft:infiniburn_nether")
                .putByte("respawn_anchor_works", (byte) 0)
                .putByte("has_skylight", (byte) 0)
                .putByte("bed_works", (byte) 0)
                .putString("effects", "minecraft:the_nether")
                .putLong("fixed_time", 18000L)
                .putByte("has_raids", (byte) 1)
                .putInt("logical_height", 128)
                .putDouble("coordinate_scale", 1.0)
                .putByte("ultrawarm", (byte) 0)
                .putByte("has_ceiling", (byte) 0)
                .build();

        CompoundBinaryTag theEnd = CompoundBinaryTag.builder()
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

        CompoundBinaryTag overworldData = CompoundBinaryTag.builder()
                .putString("name", "minecraft:overworld")
                .putInt("id", 2)
                .put("element", overworld)
                .build();

        CompoundBinaryTag netherData = CompoundBinaryTag.builder()
                .putString("name", "minecraft:the_nether")
                .putInt("id", 2)
                .put("element", nether)
                .build();

        CompoundBinaryTag endData = CompoundBinaryTag.builder()
                .putString("name", "minecraft:the_end")
                .putInt("id", 2)
                .put("element", theEnd)
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

        codec = CompoundBinaryTag.builder()
                .put("minecraft:dimension_type", CompoundBinaryTag.builder()
                        .putString("type", "minecraft:dimension_type")
                        .put("value", ListBinaryTag.builder()
                                .add(overworldData)
                                .add(netherData)
                                .add(endData)
                                .build())
                        .build())
                .put("minecraft:worldgen/biome", CompoundBinaryTag.builder()
                        .putString("type", "minecraft:worldgen/biome")
                        .put("value", ListBinaryTag.builder()
                                .add(plains)
                                .build())
                        .build())
                .build();

        switch (defaultDimensionName.toLowerCase()){
            case "overworld":
                defaultDimension = overworld;
                break;
            case "nether":
                defaultDimension = nether;
                break;
            case "the_end":
                defaultDimension = theEnd;
                break;
            default:
                defaultDimension = theEnd;
                Logger.error("Undefined dimension type: '%s'. Using THE_END as default", defaultDimensionName);
                break;
        }
    }

    public static CompoundBinaryTag getCodec(){
        return codec;
    }

    public static CompoundBinaryTag getDefaultDimension() {
        return defaultDimension;
    }
}
