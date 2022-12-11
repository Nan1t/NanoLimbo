package ru.nanit.limbo.server.commands;

import ru.nanit.limbo.server.BungeeCommandManager;
import ru.nanit.limbo.server.Command;

public class BungeeHelp implements Command {
    BungeeCommandManager manager;
    public BungeeHelp(BungeeCommandManager manager){
        this.manager = manager;
    }
    @Override
    public void execute() {
        manager.help();
    }

    @Override
    public String description() {
        return "Command to show the help of the plugin";
    }

    @Override
    public String name() {
        return "help";
    }
}
