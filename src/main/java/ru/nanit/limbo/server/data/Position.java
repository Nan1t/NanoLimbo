package ru.nanit.limbo.server.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class Position {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public static class Serializer implements TypeSerializer<Position> {

        @Override
        public Position deserialize(Type type, ConfigurationNode node) {
            Position position = new Position();
            position.setX(node.node("x").getDouble());
            position.setY(node.node("y").getDouble());
            position.setZ(node.node("z").getDouble());
            position.setYaw(node.node("yaw").getFloat());
            position.setPitch(node.node("pitch").getFloat());
            return position;
        }

        @Override
        public void serialize(Type type, @Nullable Position obj, ConfigurationNode node) {

        }
    }
}
