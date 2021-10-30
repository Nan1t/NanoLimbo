package ru.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;

public class Dimension {

    private final int id;
    private final String name;
    private final CompoundBinaryTag data;

    public Dimension(int id, String name, CompoundBinaryTag data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CompoundBinaryTag getData() {
        return data;
    }
}
