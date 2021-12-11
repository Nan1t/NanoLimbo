package ru.nanit.limbo.server.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.nanit.limbo.util.Colors;

import java.lang.reflect.Type;

public class Title {

    private String title;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public static class Serializer implements TypeSerializer<Title> {

        @Override
        public Title deserialize(Type type, ConfigurationNode node) {
            Title title = new Title();
            title.setTitle(Colors.of(node.node("title").getString("")));
            title.setSubtitle(Colors.of(node.node("subtitle").getString("")));
            title.setFadeIn(node.node("fadeIn").getInt(10));
            title.setStay(node.node("stay").getInt(100));
            title.setFadeOut(node.node("fadeOut").getInt(10));
            return title;
        }

        @Override
        public void serialize(Type type, @Nullable Title obj, ConfigurationNode node) {
            // Not used
        }
    }
}
