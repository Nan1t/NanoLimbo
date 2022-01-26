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

import ru.nanit.limbo.world.BlockEntity;
import ru.nanit.limbo.world.BlockMappings;

import java.util.List;

public abstract class AbstractSchematic implements Schematic {

    protected final BlockMappings mappings;

    private int width;
    private int height;
    private int length;
    private List<BlockEntity> blockEntities;

    public AbstractSchematic(BlockMappings mappings) {
        this.mappings = mappings;
    }

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

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setBlockEntities(List<BlockEntity> blockEntities) {
        this.blockEntities = blockEntities;
    }
}
