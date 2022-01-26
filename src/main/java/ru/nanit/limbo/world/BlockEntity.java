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

package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;

public class BlockEntity {

    private final Location pos;
    private final String id;
    private final CompoundBinaryTag data;

    public BlockEntity(Location pos, String id, CompoundBinaryTag data) {
        this.pos = pos;
        this.id = id;
        this.data = data;
    }

    public Location getPos() {
        return pos;
    }

    public String getId() {
        return id;
    }

    public CompoundBinaryTag getData() {
        return data;
    }
}
