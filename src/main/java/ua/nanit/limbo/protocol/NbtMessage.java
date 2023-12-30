package ua.nanit.limbo.protocol;

import net.kyori.adventure.nbt.BinaryTag;

public class NbtMessage {

    private String json;
    private BinaryTag tag;

    public NbtMessage(String json, BinaryTag tag) {
        this.json = json;
        this.tag = tag;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public BinaryTag getTag() {
        return tag;
    }

    public void setTag(BinaryTag tag) {
        this.tag = tag;
    }
}
