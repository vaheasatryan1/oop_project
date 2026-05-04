package cli;

import java.util.Scanner;

public class CliInputHandler {
    private final Scanner scanner;

    public CliInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String readRaw() {
        return scanner.nextLine().trim();
    }

    public boolean isQuit(String input) {
        return input.equalsIgnoreCase("q");
    }
}