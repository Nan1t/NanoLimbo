package ru.nanit.limbo.server.data;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import napi.configurate.serializing.NodeSerializingException;
import ru.nanit.limbo.util.Colors;

public class BossBar {

    private String text;
    private float health;
    private Color color;
    private Division division;

    public String getText() {
        return text;
    }

    public float getHealth() {
        return health;
    }

    public Color getColor() {
        return color;
    }

    public Division getDivision() {
        return division;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public enum Color {

        PINK(0),
        BLUE(1),
        RED(2),
        GREEN(3),
        YELLOW(4),
        PURPLE(5),
        WHITE(6);

        private final int index;

        Color(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum Division {

        SOLID(0),
        DASHES_6(1),
        DASHES_10(2),
        DASHES_12(3),
        DASHES_20(4);

        private final int index;

        Division(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Serializer implements NodeSerializer<BossBar>{

        @Override
        public BossBar deserialize(ConfigNode node) throws NodeSerializingException {
            BossBar bossBar = new BossBar();

            bossBar.setText(Colors.of(node.getNode("text").getString()));
            bossBar.setHealth(node.getNode("health").getFloat());

            if (bossBar.getHealth() < 0 || bossBar.getHealth() > 1)
                throw new NodeSerializingException("BossBar health value must be between 0.0 and 1.0");

            try {
                bossBar.setColor(Color.valueOf(node.getNode("color").getString().toUpperCase()));
            } catch (IllegalArgumentException e){
                throw new NodeSerializingException("Invalid bossbar color");
            }

            try {
                bossBar.setDivision(Division.valueOf(node.getNode("division").getString().toUpperCase()));
            } catch (IllegalArgumentException e){
                throw new NodeSerializingException("Invalid bossbar division");
            }

            return bossBar;
        }

        @Override
        public void serialize(BossBar bossBar, ConfigNode configNode) {

        }
    }
}
