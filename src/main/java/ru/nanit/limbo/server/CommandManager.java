package ru.nanit.limbo.server;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class CommandManager extends Thread {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String line;

        while (true) {
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }

            if (line.equalsIgnoreCase("stop")) {
                System.exit(0);
                continue;
            }

            Logger.info("Unknown command");
        }
    }

}
