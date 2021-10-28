package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class DimensionRegistry {

    private CompoundBinaryTag defaultDimension;

    private CompoundBinaryTag codec;

    public CompoundBinaryTag getCodec() {
        return codec;
    }

    public CompoundBinaryTag getDefaultDimension() {
        return defaultDimension;
    }

    public void load(LimboServer server, String def) throws IOException {
        InputStream in = server.getClass().getResourceAsStream("/dimension_registry.nbt");

        if(in == null)
            throw new FileNotFoundException("Cannot find dimension registry file");

        codec = BinaryTagIO.readCompressedInputStream(in);
        ListBinaryTag dimensions = codec.getCompound("minecraft:dimension_type").getList("value");

        CompoundBinaryTag overWorld = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(0)).get("element");
        CompoundBinaryTag nether = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(2)).get("element");
        CompoundBinaryTag theEnd = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(3)).get("element");

        switch (def.toLowerCase()) {
            case "overworld":
                defaultDimension = overWorld;
                break;
            case "nether":
                defaultDimension = nether;
                break;
            case "the_end":
                defaultDimension = theEnd;
                break;
            default:
                defaultDimension = theEnd;
                Logger.warning("Undefined dimension type: '%s'. Using THE_END as default", def);
                break;
        }
    }
}
