package ru.nanit.limbo.server.data;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;

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

    public static class Serializer implements NodeSerializer<Position> {

        @Override
        public Position deserialize(ConfigNode node) {
            Position position = new Position();
            position.setX(node.getNode("x").getDouble());
            position.setY(node.getNode("y").getDouble());
            position.setZ(node.getNode("z").getDouble());
            position.setYaw(node.getNode("yaw").getFloat());
            position.setPitch(node.getNode("pitch").getFloat());
            return position;
        }

        @Override
        public void serialize(Position position, ConfigNode configNode) {

        }
    }
}
