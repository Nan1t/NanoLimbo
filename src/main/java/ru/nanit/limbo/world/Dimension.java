package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;

public class Dimension {

    private final int id;
    private final CompoundBinaryTag data;

    public Dimension(int id, CompoundBinaryTag data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public CompoundBinaryTag getData() {
        return data;
    }
}
