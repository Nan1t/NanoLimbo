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

public class BlockData {

    private final String state;
    private final int id;
    private final byte data;

    public BlockData(String state, int id, byte data) {
        this.state = state;
        this.id = id;
        this.data = data;
    }

    public BlockData(String state) {
        this(state, 0, (byte) 0);
    }

    public BlockData(int id, byte data) {
        this(null, id, data);
    }

    public String getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }
}
