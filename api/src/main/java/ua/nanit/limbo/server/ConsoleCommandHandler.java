package ua.nanit.limbo.server;

import ua.nanit.limbo.server.commands.CmdConn;
import ua.nanit.limbo.server.commands.CmdHelp;
import ua.nanit.limbo.server.commands.CmdMem;
import ua.nanit.limbo.server.commands.CmdStop;

import java.util.*;

public final class ConsoleCommandHandler extends Thread implements CommandHandler<Command> {

    private final Map<String, Command> commands = new HashMap<>();

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    @Override
    public void register(Command cmd) {
        commands.put(cmd.name().toLowerCase(), cmd);
    }

    @Override
    public boolean executeCommand(String input) {
        Command handler = getCommand(input);

        if (handler != null) {
            try {
                handler.execute();
            } catch(Throwable t) {
                Logger.error("Cannot execute command:", t);
            }
            return true;
        }

        Logger.info("Unknown command. Type \"help\" to get commands list");
        return false;
    }

    @Override
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while(true) {
            try {
                command = scanner.nextLine().trim();
            } catch(NoSuchElementException e) {
                break;
            }

            executeCommand(command);
        }
    }

    public ConsoleCommandHandler registerAll(LimboServer server) {
        register(new CmdHelp(server));
        register(new CmdConn(server));
        register(new CmdMem());
        register(new CmdStop());
        return this;
    }
}
