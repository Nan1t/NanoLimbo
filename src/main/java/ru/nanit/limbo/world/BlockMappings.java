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

import ru.nanit.limbo.protocol.registry.Version;

import java.util.HashMap;
import java.util.Map;

public final class BlockMappings {

    private final Map<String, String> idToState = new HashMap<>();
    private final Map<String, String> stateToId = new HashMap<>();

    public BlockData convert(int id, byte data, Version version) {
        if (version.less(Version.V1_13))
            return new BlockData(id, data);

        String state = idToState.get(toId(id, data));

        return state != null ? new BlockData(state) : null;
    }

    public BlockData convert(String state, Version version) {
        if (state == null) return null;

        if (version.moreOrEqual(Version.V1_13)) {
            return new BlockData(state);
        }

        String id = stateToId.get(state);

        if (id != null) {
            String[] arr = id.split(":");
            int blockId = Integer.parseInt(arr[0]);
            byte data = Byte.parseByte(arr[1]);

            return new BlockData(blockId, data);
        }

        return null;
    }

    public void register(int id, byte data, String state) {
        String strId = toId(id, data);
        idToState.put(strId, state);
        stateToId.put(state, strId);
    }

    private String toId(int id, byte data) {
        return id + ":" + data;
    }
}
