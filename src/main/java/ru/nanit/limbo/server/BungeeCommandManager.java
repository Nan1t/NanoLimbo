package ru.nanit.limbo.server;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ru.nanit.limbo.server.commands.BungeeHelp;
import ru.nanit.limbo.server.commands.CmdConn;
import ru.nanit.limbo.server.commands.CmdMem;
import ru.nanit.limbo.server.commands.CmdStop;

import javax.swing.*;
import java.util.ArrayList;

public class BungeeCommandManager extends Command {
    private ArrayList<ru.nanit.limbo.server.Command> commands = new ArrayList<>();
    private LimboServer server;
    public BungeeCommandManager(LimboServer server){
        super("limbo");
        this.server = server;

        this.commands.add(new CmdStop());
        this.commands.add(new BungeeHelp(this));
        this.commands.add(new CmdMem());
        this.commands.add(new CmdConn(server));
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if (args.length > 0) {
                for (int i = 0; i < getConsoleSubcommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getConsoleSubcommands().get(i).name())) {
                        getConsoleSubcommands().get(i).execute();
                    }
                }
            } else {
                help();
            }
        }else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            player.sendMessage("Command not available as a player");
        }
    }
    public void help(){
        java.util.logging.Logger logger = ProxyServer.getInstance().getLogger();
        logger.info(ChatColor.translateAlternateColorCodes('&', "&6----------------BungeeNanoLimbo----------------"));
        logger.info(ChatColor.translateAlternateColorCodes('&', "&bAvailable Commands:"));
        for(ru.nanit.limbo.server.Command csb : getConsoleSubcommands()){
            Logger.info(ChatColor.translateAlternateColorCodes('&',"&b limbo " + csb.name() + " &8&l» &f " + csb.description()));
        }
    }
    public ArrayList<ru.nanit.limbo.server.Command> getConsoleSubcommands(){
        return this.commands;
    }
}
