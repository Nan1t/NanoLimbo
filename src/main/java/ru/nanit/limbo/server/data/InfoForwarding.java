package ru.nanit.limbo.server.data;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import napi.configurate.serializing.NodeSerializingException;

import java.nio.charset.StandardCharsets;

public class InfoForwarding {

    private Type type;
    private byte[] secretKey;

    public Type getType() {
        return type;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public boolean isNone(){
        return type == Type.NONE;
    }

    public boolean isLegacy(){
        return type == Type.LEGACY;
    }

    public boolean isModern(){
        return type == Type.MODERN;
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
                forwarding.secretKey = node.getNode("secret").getString().getBytes(StandardCharsets.UTF_8);
            }

            return forwarding;
        }

        @Override
        public void serialize(InfoForwarding infoForwarding, ConfigNode configNode) {

        }
    }

}
