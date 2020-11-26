package ru.nanit.limbo;

import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.world.DefaultDimension;

import java.io.InputStream;
import java.nio.file.Paths;

public final class NanoLimbo {

    private LimboServer server;

    public void start() throws Exception {
        LimboConfig.load(Paths.get("./settings.properties"));
        DefaultDimension.init();

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

    public static InputStream getResource(String path){
        return NanoLimbo.class.getResourceAsStream(path);
    }
}
