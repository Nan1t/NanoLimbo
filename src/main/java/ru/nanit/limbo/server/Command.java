package ru.nanit.limbo.server;

public interface Command {

    void execute();

    String description();
    String name();

}
