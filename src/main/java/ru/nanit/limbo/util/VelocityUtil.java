package ru.nanit.limbo.util;

import io.netty.buffer.ByteBuf;
import ru.nanit.limbo.configuration.LimboConfig;
import ru.nanit.limbo.protocol.ByteMessage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

public final class VelocityUtil {

    private static byte[] secretKey;

    private VelocityUtil(){}

    public static void init(LimboConfig config){
        secretKey = config.getInfoForwarding().getSecretKey();
    }

    public static boolean checkIntegrity(ByteMessage buf) {
        byte[] signature = new byte[32];
        buf.readBytes(signature);
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature))
                return false;
        } catch (InvalidKeyException |java.security.NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        int version = buf.readVarInt();
        if (version != 1)
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');
        return true;
    }

}
