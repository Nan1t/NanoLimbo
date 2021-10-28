package ru.nanit.limbo.configuration;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SocketAddressSerializer implements TypeSerializer<SocketAddress> {

    @Override
    public SocketAddress deserialize(Type type, ConfigurationNode node) {
        String ip = node.node("ip").getString();
        int port = node.node("port").getInt();
        SocketAddress address;

        if (ip == null || ip.isEmpty()){
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        return address;
    }

    @Override
    public void serialize(Type type, @Nullable SocketAddress obj, ConfigurationNode node) {

    }
}
