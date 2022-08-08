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

import net.md_5.bungee.api.plugin.Plugin;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.server.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NanoLimbo extends Plugin {


    public void start() {
        try {
            Path path = Paths.get(this.getDataFolder().getAbsolutePath());
            getLogger().info(path.toString());
            new LimboServer(path, true).start();
            getLogger().info("NanoLimbo successfully loaded");
        } catch (Exception e) {
            getLogger().warning("Error loading Nanolimbo"+ e);
        }
    }
    public static void main(String[] args) {
        try {
            Path path = Paths.get("./");
            new LimboServer(path, false).start();
        } catch (Exception e) {
            Logger.error("Cannot start server: ", e);
        }
    }

}
