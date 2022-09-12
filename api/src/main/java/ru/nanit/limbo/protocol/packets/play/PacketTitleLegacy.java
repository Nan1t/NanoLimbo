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

package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.server.data.Title;

public class PacketTitleLegacy implements PacketOut {

    private Action action;
    private final PacketTitleSetTitle title;
    private final PacketTitleSetSubTitle subtitle;
    private final PacketTitleTimes times;

    public PacketTitleLegacy() {
        this.title = new PacketTitleSetTitle();
        this.subtitle = new PacketTitleSetSubTitle();
        this.times = new PacketTitleTimes();
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setTitle(Title title) {
        this.title.setTitle(title.getTitle());
        this.subtitle.setSubtitle(title.getSubtitle());
        this.times.setFadeIn(title.getFadeIn());
        this.times.setStay(title.getStay());
        this.times.setFadeOut(title.getFadeOut());
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(action.getId(version));

        switch (action) {
            case SET_TITLE:
                title.encode(msg, version);
                break;
            case SET_SUBTITLE:
                subtitle.encode(msg, version);
                break;
            case SET_TIMES_AND_DISPLAY:
                times.encode(msg, version);
                break;
            default:
                throw new IllegalArgumentException("Invalid title action: " + action);
        }
    }

    public enum Action {
        SET_TITLE(0),
        SET_SUBTITLE(1),
        SET_TIMES_AND_DISPLAY(3, 2);

        private final int id;
        private final int legacyId;

        Action(int id, int legacyId) {
            this.id = id;
            this.legacyId = legacyId;
        }

        Action(int id) {
            this(id, id);
        }

        public int getId(Version version) {
            return version.less(Version.V1_11) ? legacyId : id;
        }
    }
}
