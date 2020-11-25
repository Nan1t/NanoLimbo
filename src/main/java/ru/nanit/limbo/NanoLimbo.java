package ru.nanit.limbo;

import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;

import java.nio.file.Paths;

public final class NanoLimbo {

    private LimboServer server;

    public void start() throws Exception {
        LimboConfig.load(Paths.get("./settings.properties"));

        server = new LimboServer();
        server.start();
    }

    public static void main(String[] args){
        try {
            new NanoLimbo().start();
        } catch (Exception e){
            Logger.error("Cannot start server: ", e);
        }
    }
}
