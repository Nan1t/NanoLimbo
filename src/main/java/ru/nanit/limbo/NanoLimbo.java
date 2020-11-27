package ru.nanit.limbo;

import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;

public final class NanoLimbo {

    public static void main(String[] args){
        try {
            new LimboServer().start();
        } catch (Exception e){
            Logger.error("Cannot start server: ", e);
        }
    }

}
