package ru.nanit.limbo.protocol.packets.login;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketEncryptionRequest implements PacketOut {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeString(serverId);
        msg.writeBytesArray(publicKey);
        msg.writeBytesArray(verifyToken);
    }
}
