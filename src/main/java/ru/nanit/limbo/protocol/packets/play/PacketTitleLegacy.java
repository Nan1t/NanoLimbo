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
        }
    }

    public enum Action {
        SET_TITLE(0),
        SET_SUBTITLE(1),
        SET_TIMES_AND_DISPLAY(3);

        private final int id;

        Action(int id) {
            this.id = id;
        }

        public int getId(Version version) {
            return version.moreOrEqual(Version.V1_11) && id > 2 ? id - 1 : id;
        }
    }
}
