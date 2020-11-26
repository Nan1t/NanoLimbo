package ru.nanit.limbo;

import ru.nanit.limbo.protocol.packets.play.PacketBossBar;

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
    private static JoinMessages joinMessages;

    public static void load(Path file) throws IOException {
        if (!Files.exists(file)){
            Files.copy(NanoLimbo.getResource("/settings.properties"), file);
        }

        Properties props = new Properties();
        props.load(Files.newInputStream(file));

        host = props.getProperty("host");
        port = Integer.parseInt(props.getProperty("port"));

        maxPlayers = Integer.parseInt(props.getProperty("max-players"));
        ipForwardingType = IpForwardingType.valueOf(props.getProperty("ip-forwarding").toUpperCase());
        readTimeout = Long.parseLong(props.getProperty("read-timeout"));

        pingData = new PingData();
        pingData.setVersion(props.getProperty("ping-version"));
        pingData.setDescription(props.getProperty("ping-description"));

        debugLevel = Integer.parseInt(props.getProperty("debug-level"));

        joinMessages = new JoinMessages();

        if(props.containsKey("join-message")){
            joinMessages.setChatMessage(props.getProperty("join-message"));
        }

        if(props.containsKey("join-bossbar-text")){
            joinMessages.setBossBarText(props.getProperty("join-bossbar-text"));
            joinMessages.setBossBarHealth(Float.parseFloat(props.getProperty("join-bossbar-health")));
            joinMessages.setBossBarColor(PacketBossBar.Color.valueOf(
                    props.getProperty("join-bossbar-color").toUpperCase()));
            joinMessages.setBossBarDivision(PacketBossBar.Division.valueOf(
                    props.getProperty("join-bossbar-division").toUpperCase()));
        }
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

    public static JoinMessages getJoinMessages() {
        return joinMessages;
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

    public static class JoinMessages {

        private String chatMessage;
        private String bossBarText;
        private float bossBarHealth;
        private PacketBossBar.Color bossBarColor;
        private PacketBossBar.Division bossBarDivision;

        public String getChatMessage() {
            return chatMessage;
        }

        public void setChatMessage(String chatMessage) {
            this.chatMessage = chatMessage;
        }

        public String getBossBarText() {
            return bossBarText;
        }

        public void setBossBarText(String bossBarText) {
            this.bossBarText = bossBarText;
        }

        public float getBossBarHealth() {
            return bossBarHealth;
        }

        public void setBossBarHealth(float bossBarHealth) {
            this.bossBarHealth = bossBarHealth;
        }

        public PacketBossBar.Color getBossBarColor() {
            return bossBarColor;
        }

        public void setBossBarColor(PacketBossBar.Color bossBarColor) {
            this.bossBarColor = bossBarColor;
        }

        public PacketBossBar.Division getBossBarDivision() {
            return bossBarDivision;
        }

        public void setBossBarDivision(PacketBossBar.Division bossBarDivision) {
            this.bossBarDivision = bossBarDivision;
        }
    }

}
