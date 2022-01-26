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

package ru.nanit.limbo.world.schematic;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.world.BlockEntity;
import ru.nanit.limbo.world.BlockMappings;
import ru.nanit.limbo.world.Location;
import ru.nanit.limbo.world.schematic.versions.LegacySchematic;
import ru.nanit.limbo.world.schematic.versions.SpongeSchematic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SchematicLoader {

    private final BlockMappings mappings;

    public SchematicLoader(BlockMappings mappings) {
        this.mappings = mappings;
    }

    public Schematic load(Path file) throws IOException {
        return load(Files.newInputStream(file));
    }

    public Schematic load(InputStream stream) throws IOException {
        CompoundBinaryTag nbt = BinaryTagIO.unlimitedReader().read(stream, BinaryTagIO.Compression.GZIP);

        if (nbt.keySet().contains("BlockData")) {
            return loadSponge(nbt);
        } else {
            return loadLegacy(nbt);
        }
    }

    // Specification: https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md
    private Schematic loadSponge(CompoundBinaryTag nbt) {
        SpongeSchematic schematic = new SpongeSchematic(mappings);

        schematic.setDataVersion(nbt.getInt("DataVersion"));

        schematic.setWidth(nbt.getShort("Width"));
        schematic.setHeight(nbt.getShort("Height"));
        schematic.setLength(nbt.getShort("Length"));

        schematic.setPaletteMax(nbt.getInt("PaletteMax"));

        Map<Integer, String> palette = new HashMap<>();
        CompoundBinaryTag paletteNbt = nbt.getCompound("Palette");

        for (String key : paletteNbt.keySet()) {
            palette.put(paletteNbt.getInt(key), key);
        }

        schematic.setPalette(palette);
        schematic.setBlockData(nbt.getByteArray("BlockData"));

        List<BlockEntity> blockEntities = new LinkedList<>();

        for (BinaryTag tag : nbt.getList("BlockEntities")) {
            if (tag instanceof CompoundBinaryTag) {
                CompoundBinaryTag data = (CompoundBinaryTag) tag;

                int[] posArr = data.getIntArray("Pos");
                Location pos = Location.pos(posArr[0], posArr[1], posArr[2]);
                String id = data.getString("Id");
                CompoundBinaryTag extra = data.getCompound("Extra");

                blockEntities.add(new BlockEntity(pos, id, extra));
            }
        }

        schematic.setBlockEntities(blockEntities);

        return schematic;
    }

    private Schematic loadLegacy(CompoundBinaryTag nbt) {
        LegacySchematic schematic = new LegacySchematic(mappings);

        schematic.setWidth(nbt.getShort("Width"));
        schematic.setHeight(nbt.getShort("Height"));
        schematic.setLength(nbt.getShort("Length"));

        schematic.setMaterials(LegacySchematic.Materials.from(nbt.getString("Materials")));

        schematic.setBlocks(nbt.getByteArray("Blocks"));
        schematic.setAddBlocks(nbt.getByteArray("AddBlocks"));
        schematic.setData(nbt.getByteArray("Data"));

        List<BlockEntity> blockEntities = new LinkedList<>();

        for (BinaryTag tag : nbt.getList("TileEntities")) {
            if (tag instanceof CompoundBinaryTag) {
                CompoundBinaryTag data = (CompoundBinaryTag) tag;

                String id = data.getString("id");
                int x = data.getInt("x");
                int y = data.getInt("y");
                int z = data.getInt("z");

                data.remove("id");
                data.remove("x");
                data.remove("y");
                data.remove("z");

                blockEntities.add(new BlockEntity(Location.pos(x, y, z), id, data));
            }
        }

        schematic.setBlockEntities(blockEntities);

        return schematic;
    }
}
