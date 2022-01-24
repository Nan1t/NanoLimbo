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
import ru.nanit.limbo.world.BlockEntity;
import ru.nanit.limbo.world.BlockMap;
import ru.nanit.limbo.world.Location;
import ru.nanit.limbo.world.schematic.Schematic;

import java.util.List;
import java.util.Map;

/**
 * Modern schematic format (Sponge second specification)
 */
public class SpongeSchematic implements Schematic {

    private int dataVersion;
    private int width;
    private int height;
    private int length;
    private int paletteMax;
    private Map<Integer, String> palette;
    private byte[] blockData;
    private List<BlockEntity> blockEntities;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public List<BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    @Override
    public BlockData getBlock(Location loc, Version version, BlockMap mappings) {
        int index = loc.getBlockX() + loc.getBlockZ() * width + loc.getBlockY() * width * length;
        int id = blockData[index];
        String state = palette.get(id);
        return mappings.convert(state, version);
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLength(int length) {
        this.length = length;
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

    public void setBlockEntities(List<BlockEntity> blockEntities) {
        this.blockEntities = blockEntities;
    }
}
