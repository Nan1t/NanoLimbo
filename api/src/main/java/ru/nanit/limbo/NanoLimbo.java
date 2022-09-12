/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.nanit.limbo;

import java.nio.file.Paths;

import ru.nanit.limbo.configuration.YamlLimboConfig;
import ru.nanit.limbo.server.ConsoleCommandHandler;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.server.Logger;

public final class NanoLimbo {

    public static void main(String[] args) {
        try {
            ConsoleCommandHandler consoleCommandHandler = new ConsoleCommandHandler();
            ClassLoader classLoader = LimboServer.class.getClassLoader();
            LimboServer server = new LimboServer(new YamlLimboConfig(Paths.get("./"), classLoader).load(), consoleCommandHandler, classLoader);
            consoleCommandHandler.registerAll(server).start();
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop, "NanoLimbo shutdown thread"));
        } catch(Exception e) {
            Logger.error("Cannot start server: ", e);
        }
    }

}
