## NanoLimbo

This is a lightweight Minecraft limbo server, written in Java with Netty.
The main goal of this project is maximum simplicity with a minimum number of sent and processed packets.
The limbo is empty; there is no ability to set a schematic building since this is not necessary.
You can send useful information via chat or boss bar.

The server is fully clear. It is only able to keep a lot of players while the main server is down.

General features:
* High performance. The server doesn't save or cache any useless (for limbo) data.
* Doesn't spawn threads per player. Use a fixed thread pool.
* Support for **BungeeCord** and **Velocity** info forwarding.
* Support for [BungeeGuard](https://www.spigotmc.org/resources/79601/) handshake format.
* Support for [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/) text format.
* Multiple versions support.
* Fully configurable.
* Lightweight. App size around **5MB**.

![](https://i.imgur.com/sT8p1Gz.png)

### Versions support

Symbol `X` means all minor versions.

- [x] 1.7.X
- [x] 1.8.X
- [x] 1.9.X
- [x] 1.10.X
- [x] 1.11.X
- [x] 1.12.X
- [x] 1.13.X
- [x] 1.14.X
- [x] 1.15.X
- [x] 1.16.X
- [x] 1.17.X
- [x] 1.18.X
- [x] 1.19.X
- [x] 1.20.X
- [x] 1.21.X &nbsp; *(incl. 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11)*
- [x] 26.1.X
- [x] 26.2

The server **doesn't** support snapshots.

### Commands

* `help` - Show help message
* `conn` - Display number of connections
* `mem` - Display memory usage stats
* `version` - Display limbo version
* `stop` - Stop the server

Note that the server also will be closed correctly if you just press `Ctrl+C`.

### Installation

Required software: JRE 21+

The installation process is simple.

1. Download the latest version of the program [**here**](https://github.com/Nan1t/NanoLimbo/releases).
2. Put the jar file in the folder you want.
3. Create a start script as you did for Bukkit or BungeeCord, with a command like this:
   `java -jar NanoLimbo.jar`
4. The server will create `settings.yml` file, which is the server configuration. 
5. Configure it as you want and restart the server.

### Player info forwarding

The server supports player info forwarding from the proxy. There are several types of info forwarding:

* `LEGACY` - The **BungeeCord** IP forwarding.
* `MODERN` - **Velocity** native info forwarding type.
* `BUNGEE_GUARD` - **BungeeGuard** forwarding type.

If you use BungeeCord, or Velocity with `LEGACY` forwarding, just set this type in the config.  
If you use Velocity with `MODERN` info forwarding, set this type and paste the secret key from
Velocity config into `secret` field.
If you installed BungeeGuard on your proxy, then use `BUNGEE_GUARD` forwarding type.
Then add your tokens to `tokens` list.

### Credits

This release is built on top of community contributions across multiple forks.
Huge thanks to everyone listed below — expand each section to see what they contributed.

<details>
<summary><b>Nan1t</b> — original author &amp; maintainer</summary>

The entire foundation of NanoLimbo:

- Netty pipeline, packet system, multi-version protocol skeleton up to 1.21
- BungeeCord and Velocity info forwarding
- Configuration framework, command system, dimension registry
- Project structure, build setup, release process

Source: https://github.com/Nan1t/NanoLimbo
</details>

<details>
<summary><b>BoomEaro</b> (Valentine) — Minecraft 1.21.2 → 26.1, modernization</summary>

The bulk of post-1.21 protocol work and toolchain modernization:

- Protocol support for **1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11** and **26.1**
- Rewrote the login → configuration phase for the post-1.20.5 known-packs handshake: `PacketKnownPacks`, `PacketUpdateTags`, per-version `PacketRegistryData`
- Rewrote play packets for the 1.21.x line: `PacketLogin` (formerly `PacketJoinGame`), `PacketChunkWithLight` (real heightmaps + biome palette + light update), `PacketPlayerPositionAndLook` for the 1.21.2 teleport-flags redesign, `PacketGameEvent` with `start_waiting_for_chunks`
- Build modernization: Gradle Kotlin DSL, version catalog, **Java 17**, Lombok, GitHub Actions for build & release
- Adventure stack (api / gson / legacy / plain / json / nbt) + **MiniMessage** support in all text fields
- Netty 4.2 split modules with native transports: epoll, io_uring (Linux x86_64 / aarch64) and kqueue (macOS x86_64 / aarch64); new `TransportType` enum
- Per-connection traffic rate limiting in `ChannelTrafficHandler`
- Refactored configuration serializers, `VersionedDimension`, `version` command

Source: https://github.com/BoomEaro/NanoLimbo &nbsp;·&nbsp; upstream PR: [#98](https://github.com/Nan1t/NanoLimbo/pull/98)
</details>

<details>
<summary><b>YueMi-Development</b> — external secret files</summary>

- `@`-prefix support in `infoForwarding.secret` and `infoForwarding.tokens`: values starting with `@` are read from a file relative to the working directory. Lets you keep credentials out of `settings.yml` (Docker / Kubernetes secrets, SOPS, etc.).

Source: https://github.com/YueMi-Development/NanoLimbo
</details>

<details>
<summary><b>Biquaternions</b> — IP logging privacy switch</summary>

- New `logPlayersIp` config flag. When `false`, player IP addresses are redacted in connection logs (shown as `<redacted>`). Useful for GDPR / privacy-compliant deployments.

Source: https://github.com/Biquaternions/NanoLimbo &nbsp;·&nbsp; upstream PR: [#96](https://github.com/Nan1t/NanoLimbo/pull/96)
</details>

### Contributing

Feel free to create a pull request if you find some bug or optimization opportunity, or if you want
to add some functionality that is suitable for a limbo server and won't significantly load the server.

### Building

Required software:

* JDK 21+
* Gradle 9+ (optional)

To build a minimized jar, go to the project root directory and run in the terminal:

```
./gradlew build
```

### Contacts

If you have any questions or suggestions, join our [Discord server](https://discord.gg/4VGP3Gv)!
