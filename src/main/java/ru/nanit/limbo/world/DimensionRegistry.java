package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class DimensionRegistry {

    private final LimboServer server;

    private Dimension defaultDimension;

    private CompoundBinaryTag codec;
    private CompoundBinaryTag oldCodec;

    public DimensionRegistry(LimboServer server) {
        this.server = server;
    }

    public CompoundBinaryTag getCodec() {
        return codec;
    }

    public CompoundBinaryTag getOldCodec() {
        return oldCodec;
    }

    public Dimension getDefaultDimension() {
        return defaultDimension;
    }

    public void load(String def) throws IOException {
        codec = readCodecFile("/dimension/codec_new.snbt");
        // On 1.16-1.16.1 different codec format
        oldCodec = readCodecFile("/dimension/codec_old.snbt");

        ListBinaryTag dimensions = codec.getCompound("minecraft:dimension_type").getList("value");

        CompoundBinaryTag overWorld = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(0)).get("element");
        CompoundBinaryTag nether = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(2)).get("element");
        CompoundBinaryTag theEnd = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(3)).get("element");

        switch (def.toLowerCase()) {
            case "overworld" -> defaultDimension = new Dimension(0, "minecraft:overworld", overWorld);
            case "nether" -> defaultDimension = new Dimension(-1, "minecraft:nether", nether);
            case "the_end" -> defaultDimension = new Dimension(1, "minecraft:the_end", theEnd);
            default -> {
                defaultDimension = new Dimension(1, "minecraft:the_end", theEnd);
                Logger.warning("Undefined dimension type: '%s'. Using THE_END as default", def);
            }
        }
    }

    private CompoundBinaryTag readCodecFile(String resPath) throws IOException {
        InputStream in = server.getClass().getResourceAsStream(resPath);

        if(in == null)
            throw new FileNotFoundException("Cannot find dimension registry file");

        return TagStringIO.get().asCompound(streamToString(in));
    }

    private String streamToString(InputStream in) {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
