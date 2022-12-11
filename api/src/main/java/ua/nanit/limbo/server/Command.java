package ua.nanit.limbo.server;

public interface Command {

    void execute();

    String name();

    String description();

}
