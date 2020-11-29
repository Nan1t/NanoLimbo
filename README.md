## NanoLimbo

This is lightweight minecraft limbo server, written on Java with Netty.
The main goal of the project is maximum simplicity with a minimum number of sent and processed packets.
This limbo is empty, there are no ability to set schematic building since 
this is not necessary. You can send useful information in chat or BossBar.

No plugins, no logs. The server is fully clear. It only able keep a lot of players while the main server is down.

The general features:
* High performance. The server not saves and not cached any useless (for limbo) data.
* Support for **BungeeCord** and **Velocity** info forwarding.
* All necessary data are configurable.
* Lightweight. App size less than **2MB.**

![](https://i.imgur.com/sT8p1Gz.png)

### Protocol support

The server written based on `1.16.4` minecraft protocol. You can use `ViaVersion` 
or another protocol hack on your **proxy server** to achieve compatibility with other versions.
However, compatibility with other versions is in the plans, but not the nearest ones, since there 
is a way to do this using a plugin that is installed on most networks.

### Commands

There are no commands. To close server just run `Ctrl+C` in the terminal. It will be closed correctly.

### Installation

The installation process is simple.

1. Download the latest version of program **[here](https://github.com/Nan1t/NanoLimbo/releases)**
2. Put jar file in the folder you want.
3. Create a start script as you did it for Bukkit/Spigot/etc. with command like this:
`java -jar <FileName>.jar`
4. The server will create `settings.yml` file. It's a server configuration.
5. Configure it as you want and restart server.

I recommend you to set parameter `debugLevel` to `0` when you finish testing server and run it into production.
This will disable some useless for production information in the console.

### About player info forwarding

The server supports player info forwarding from the proxy. There are two type of info forwarding:

* LEGACY - The BungeeCord IP forwarding
* MODERN - Velocity native info forwarding type

If you use **BungeeCord**, or Velocity with LEGACY forwarding, just set this type in the config.
If you use **Velocity** with modern info forwarding, set this type and paste secret key from Velocity 
config into `secret` field.

### Contributing

You can create pull request, if you found some bug, optimization ability, or you wanna add some functional, 
which will not significantly load the server.

### Building

The project uses `Gradle` to build and minimize output .jar file.
Run `shadowJar` task to build minimized executable jar file.

### Contacts

If you have any question or suggestion, join to [Discord server](https://discord.gg/4VGP3Gv)
