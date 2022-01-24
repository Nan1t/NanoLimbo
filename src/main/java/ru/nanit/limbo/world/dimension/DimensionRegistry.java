/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.nanit.limbo.world.dimension;

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
            case "overworld":
                defaultDimension = new Dimension(0, "minecraft:overworld", overWorld);
                break;
            case "nether":
                defaultDimension = new Dimension(-1, "minecraft:nether", nether);
                break;
            case "the_end":
                defaultDimension = new Dimension(1, "minecraft:the_end", theEnd);
                break;
            default:
                defaultDimension = new Dimension(1, "minecraft:the_end", theEnd);
                Logger.warning("Undefined dimension type: '%s'. Using THE_END as default", def);
                break;
        }
    }

    private CompoundBinaryTag readCodecFile(String resPath) throws IOException {
        InputStream in = server.getClass().getResourceAsStream(resPath);

        if(in == null)
            throw new FileNotFoundException("Cannot find dimension registry file");

        return TagStringIO.get().asCompound(streamToString(in));
    }

    private String streamToString(InputStream in) throws IOException {
        InputStreamReader isReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader bufReader = new BufferedReader(isReader);
        String content = bufReader.lines()
                .collect(Collectors.joining("\n"));

        isReader.close();
        bufReader.close();

        return content;
    }
}
