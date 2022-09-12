package ru.nanit.limbo.server;

import java.util.Collection;

public interface CommandHandler<T> {
    Collection<Command> getCommands();

    void register(T command);

    boolean executeCommand(String input);
}
