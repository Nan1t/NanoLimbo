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

/**
 * Legacy schematic format (1.12-)
 */
public class LegacySchematic implements Schematic {

    private short width;
    private short height;
    private short length;
    private Materials materials;
    private byte[] blocks;
    private byte[] addBlocks;
    private byte[] data;
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
        int index = (loc.getBlockY() * length + loc.getBlockZ()) * width + loc.getBlockX();
        byte id = this.blocks[index];
        byte data = this.data[index];

        return mappings.convert(id, data, version);
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public void setMaterials(Materials materials) {
        this.materials = materials;
    }

    public void setBlocks(byte[] blocks) {
        this.blocks = blocks;
    }

    public void setAddBlocks(byte[] addBlocks) {
        this.addBlocks = addBlocks;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setBlockEntities(List<BlockEntity> blockEntities) {
        this.blockEntities = blockEntities;
    }

    @Override
    public String toString() {
        return "Schematic{" +
                "width=" + width +
                ", height=" + height +
                ", length=" + length +
                ", materials=" + materials +
                ", blocks length=" + blocks.length +
                ", addBlocks length=" + addBlocks.length +
                ", data length=" + data.length +
                ", blockEntities=" + blockEntities +
                '}';
    }

    public enum Materials {

        CLASSIC,
        ALPHA,
        POCKET;

        public static Materials from(String value) {
            switch (value.toLowerCase()) {
                case "classic": return CLASSIC;
                case "alpha": return ALPHA;
                case "pocket": return POCKET;
                default: throw new IllegalArgumentException("Invalid materials type");
            }
        }
    }
}
