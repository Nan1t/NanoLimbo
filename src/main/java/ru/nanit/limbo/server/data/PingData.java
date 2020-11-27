package ru.nanit.limbo.server.data;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import ru.nanit.limbo.util.Colors;

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

    public static class Serializer implements NodeSerializer<PingData> {

        @Override
        public PingData deserialize(ConfigNode node) {
            PingData pingData = new PingData();
            pingData.setDescription(Colors.of(node.getNode("description").getString()));
            pingData.setVersion(Colors.of(node.getNode("version").getString()));
            return pingData;
        }

        @Override
        public void serialize(PingData pingData, ConfigNode configNode) {

        }
    }
}