package ua.nanit.limbo.server;

import ua.nanit.limbo.server.commands.CmdConn;
import ua.nanit.limbo.server.commands.CmdHelp;
import ua.nanit.limbo.server.commands.CmdMem;
import ua.nanit.limbo.server.commands.CmdStop;
import ua.nanit.limbo.server.commands.client.ClientCommand;

import java.util.*;

public final class CommandManager extends Thread {

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, ClientCommand> clientCommands = new HashMap<>();

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public Map<String, ClientCommand> getClientCommands() {
        return Collections.unmodifiableMap(clientCommands);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    public ClientCommand getClientCommand(String name) {
        return clientCommands.get(name.toLowerCase());
    }

    public void register(String name, Command cmd) {
        commands.put(name.toLowerCase(), cmd);
    }
    public void register(String name, ClientCommand cmd) {
        clientCommands.put(name.toLowerCase(), cmd);
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            try {
                command = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                break;
            }

            Command handler = getCommand(command);

            if (handler != null) {
                try {
                    handler.execute();
                } catch (Throwable t) {
                    Logger.error("Cannot execute command:", t);
                }
                continue;
            }

            Logger.info("Unknown command. Type \"help\" to get commands list");
        }
    }

    public void registerAll(LimboServer server) {
        register("help", new CmdHelp(server));
        register("conn", new CmdConn(server));
        register("mem", new CmdMem());
        register("stop", new CmdStop());
        register("help", new ua.nanit.limbo.server.commands.client.CmdHelp());
    }
}
