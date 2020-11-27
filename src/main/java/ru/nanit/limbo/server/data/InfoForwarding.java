package ru.nanit.limbo.server.data;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import napi.configurate.serializing.NodeSerializingException;

import java.util.Optional;

public class InfoForwarding {

    private Type type;
    private String secret;

    public Type getType() {
        return type;
    }

    public Optional<String> getSecret() {
        return Optional.ofNullable(secret);
    }

    public enum Type {
        NONE,
        LEGACY,
        MODERN
    }

    public static class Serializer implements NodeSerializer<InfoForwarding> {

        @Override
        public InfoForwarding deserialize(ConfigNode node) throws NodeSerializingException {
            InfoForwarding forwarding = new InfoForwarding();

            try {
                forwarding.type = Type.valueOf(node.getNode("type").getString().toUpperCase());
            } catch (IllegalArgumentException e){
                throw new NodeSerializingException("Undefined info forwarding type");
            }

            if (forwarding.type == Type.MODERN){
                forwarding.secret = node.getNode("secret").getString();
            }

            return forwarding;
        }

        @Override
        public void serialize(InfoForwarding infoForwarding, ConfigNode configNode) throws NodeSerializingException {

        }
    }

}
