package ru.nanit.limbo.server.commands;

import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.Logger;

public class CmdMem implements Command {

    @Override
    public void execute() {
        Runtime runtime = Runtime.getRuntime();
        long mb = 1024 * 1024;
        long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;
        long total = runtime.totalMemory() / mb;
        long free = runtime.freeMemory() / mb;
        long max = runtime.maxMemory() / mb;

        Logger.info("Memory usage:");
        Logger.info("Used: %d MB", used);
        Logger.info("Total: %d MB", total);
        Logger.info("Free: %d MB", free);
        Logger.info("Max: %d MB", max);
    }

    @Override
    public String description() {
        return "Display memory usage";
    }
}
