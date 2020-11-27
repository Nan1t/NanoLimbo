## NanoLimbo

This is lightweight minecraft limbo server, written on Java with Netty.
The main goal of the project is maximum simplicity with a minimum number of sent and processed packets.
This limbo is empty, there are no ability to set schematic building since 
this is not necessary. You can send useful information in chat or BossBar.

The general features is:
* High performance. The server not saves and not cached any useless (for limbo) data.
* Support for BungeeCord and Velocity info forwarding.
* All necessary data are configurable.
* Lightweight. App size is around 1.5 MB.

### Commands

There are no commands. To close server just run Ctrl+C in terminal. It will be closed correctly.

### Installation

The installation process is simple.

1. Put jar file folder you want.
2. Create start script as you did it with bukkit/spigot/etc. with command like this:
`java -jar <FileName>.jar`
3. The server will create `settings.yml` file. It's a server configuration.
4. Configure it as you want and restart server.

I recommend you set parameter `debugLevel` to `0` when you finish test server and run it into production.
This will disable some useless for production information in the console.

### Contributing

You can create pull request, if you found some bug, optimization ability, or you wanna add some functional, 
which will not significantly load the server.

### Building

The app use `Gradle` to build and minimize .jar file.
Run `shadowJar` task to build minimized executable jar file.
