package ru.nanit.limbo.configuration;

import napi.configurate.data.ConfigNode;
import napi.configurate.serializing.NodeSerializer;
import napi.configurate.serializing.NodeSerializingException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SocketAddressSerializer implements NodeSerializer<SocketAddress> {

    @Override
    public SocketAddress deserialize(ConfigNode node) {
        String ip = node.getNode("ip").getString();
        int port = node.getNode("port").getInt();
        SocketAddress address;

        if (ip == null || ip.isEmpty()){
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        return address;
    }

    @Override
    public void serialize(SocketAddress socketAddress, ConfigNode configNode) throws NodeSerializingException {

    }
}
