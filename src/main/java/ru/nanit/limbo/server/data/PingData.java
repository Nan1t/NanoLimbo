package ru.nanit.limbo.server.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.nanit.limbo.util.Colors;

import java.lang.reflect.Type;

public class PingData {

    private String version;
    private String description;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Serializer implements TypeSerializer<PingData> {

        @Override
        public PingData deserialize(Type type, ConfigurationNode node) {
            PingData pingData = new PingData();
            pingData.setDescription(Colors.of(node.node("description").getString("")));
            pingData.setVersion(Colors.of(node.node("version").getString("")));
            return pingData;
        }

        @Override
        public void serialize(Type type, @Nullable PingData obj, ConfigurationNode node) {

        }
    }
}