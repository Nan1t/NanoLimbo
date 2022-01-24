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

package ru.nanit.limbo.world.schematic.versions;

import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.world.BlockData;
import ru.nanit.limbo.world.BlockMappings;
import ru.nanit.limbo.world.Location;
import ru.nanit.limbo.world.schematic.AbstractSchematic;

import java.util.Map;

/**
 * Modern schematic format (Sponge second specification)
 */
public class SpongeSchematic extends AbstractSchematic {

    private int dataVersion;
    private int paletteMax;
    private Map<Integer, String> palette;
    private byte[] blockData;

    public SpongeSchematic(BlockMappings mappings) {
        super(mappings);
    }

    @Override
    public BlockData getBlock(Location loc, Version version) {
        int index = loc.getBlockX() + loc.getBlockZ() * getWidth() + loc.getBlockY() * getWidth() * getLength();
        int id = blockData[index];
        String state = palette.get(id);
        return mappings.convert(state, version);
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public void setPaletteMax(int paletteMax) {
        this.paletteMax = paletteMax;
    }

    public void setPalette(Map<Integer, String> palette) {
        this.palette = palette;
    }

    public void setBlockData(byte[] blockData) {
        this.blockData = blockData;
    }

    @Override
    public String toString() {
        return "SpongeSchematic{" +
                "dataVersion=" + dataVersion +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", length=" + getLength() +
                ", paletteMax=" + paletteMax +
                ", palette=" + palette +
                ", blockData bytes=" + blockData.length +
                ", blockEntities=" + getBlockEntities() +
                '}';
    }
}
