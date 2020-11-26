package ru.nanit.limbo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class LimboConfig {

    private static String host;
    private static int port;
    private static int maxPlayers;
    private static IpForwardingType ipForwardingType;
    private static long readTimeout;
    private static PingData pingData;
    private static int debugLevel = 3;

    public static void load(Path file) throws IOException {
        if (!Files.exists(file)){
            Files.copy(NanoLimbo.getResource("/settings.properties"), file);
        }

        Properties properties = new Properties();
        properties.load(Files.newInputStream(file));

        host = properties.getProperty("host");
        port = Integer.parseInt(properties.getProperty("port"));

        maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
        ipForwardingType = IpForwardingType.valueOf(properties.getProperty("ip-forwarding").toUpperCase());
        readTimeout = Long.parseLong(properties.getProperty("read-timeout"));

        pingData = new PingData();
        pingData.setVersion(properties.getProperty("ping-version"));
        pingData.setDescription(properties.getProperty("ping-description"));

        debugLevel = Integer.parseInt(properties.getProperty("debug-level"));
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static int getMaxPlayers() {
        return maxPlayers;
    }

    public static IpForwardingType getIpForwardingType() {
        return ipForwardingType;
    }

    public static long getReadTimeout() {
        return readTimeout;
    }

    public static PingData getPingData() {
        return pingData;
    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public enum IpForwardingType {
        NONE,
        LEGACY,
        MODERN
    }

    public static class PingData {

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
    }

}
