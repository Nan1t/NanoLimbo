/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.util;

import com.google.gson.*;
import io.netty.util.NetUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.UUID;

@UtilityClass
public class ForwardingUtils {

    private static final String ALGORITHM = "HmacSHA256";

    public static final byte VELOCITY_MAX_SUPPORTED_FORWARDING_VERSION = 1;

    @Nullable
    public static LegacyForwarding parseLegacyForwarding(@NonNull String handshake) {
        String[] split = handshake.split("\00", -1);
        if (split.length < 3) {
            return null;
        }

        int addressIndex = split[1].startsWith("^Floodgate^") ? 2 : 1;
        if (split.length != addressIndex + 2 && split.length != addressIndex + 3) {
            return null;
        }

        String address = split[addressIndex];
        // Proxies always forward an IP literal; anything else is a forged handshake.
        if (!NetUtil.isValidIpV4Address(address) && !NetUtil.isValidIpV6Address(address)) {
            return null;
        }

        try {
            UUID uuid = UUIDUtils.fromString(split[addressIndex + 1]);
            String properties = split.length == addressIndex + 3 ? split[addressIndex + 2] : null;
            return new LegacyForwarding(address, uuid, properties);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public record LegacyForwarding(String address, UUID uuid, @Nullable String properties) {
    }

    public static boolean checkVelocityKeyIntegrity(@NonNull ClientConnection conn,
                                                    @NonNull ByteMessage buf) {
        if (buf.readableBytes() < 32) {
            return false;
        }
        byte[] signature = new byte[32];
        buf.readBytes(signature);

        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(conn.getServer().getConfig().getInfoForwarding().getSecretKey(), ALGORITHM));
            byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (InvalidKeyException | java.security.NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        return true;
    }

    public static boolean checkBungeeGuardHandshake(@NonNull ClientConnection conn,
                                                    @NonNull String handshake) {
        LegacyForwarding forwarding = parseLegacyForwarding(handshake);
        if (forwarding == null || forwarding.properties() == null) {
            return false;
        }

        String socketAddressHostname = forwarding.address();
        UUID uuid = forwarding.uuid();

        String token = null;

        try {
            JsonElement rootElement = JsonParser.parseString(forwarding.properties());
            if (!rootElement.isJsonArray()) {
                return false;
            }

            JsonArray jsonArray = rootElement.getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    JsonElement nameElement = jsonObject.get("name");
                    if (nameElement != null && nameElement.isJsonPrimitive()) {
                        if (nameElement.getAsString().equals("bungeeguard-token")) {
                            JsonElement valueElement = jsonObject.get("value");
                            if (valueElement != null && valueElement.isJsonPrimitive()) {
                                token = valueElement.getAsString();
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JsonParseException e) {
            return false;
        }

        if (!conn.getServer().getConfig().getInfoForwarding().hasToken(token)) {
            return false;
        }

        conn.setAddress(socketAddressHostname);
        conn.getGameProfile().setUuid(uuid);

        return true;
    }

}
