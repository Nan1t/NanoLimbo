package ru.nanit.limbo.server;

import java.util.Scanner;

public final class CommandManager extends Thread {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("stop")) {
                System.exit(0);
                continue;
            }

            Logger.info("Unknown command");
        }
    }
}
