package es.angelillo15.limbo;

import ru.nanit.limbo.NanoLimbo;

public class NanoLimboLoader extends NanoLimbo {
    @Override
    public void onEnable(){
        getLogger().info("Loading NanoLimbo");
        start();
    }
}
