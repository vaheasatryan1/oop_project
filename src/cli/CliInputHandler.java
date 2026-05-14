package cli;

import core.Game;
import core.Recipe;

import java.util.List;
import java.util.Scanner;

public class CliInputHandler {

    private final Scanner scanner = new Scanner(System.in);

    public String readRaw() {
        return scanner.nextLine().trim();
    }

    public boolean isQuit(String input) {
        return input.equalsIgnoreCase("q");
    }


    public void openCrafting(Game game) {
        List<Recipe> recipes = game.getCraftingRecipes();

        System.out.println("\n--- CRAFTING ---");
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            String canCraft = game.canCraft(r) ? " [can craft]" : " [need more resources]";
            System.out.println("[" + (i + 1) + "] " + r.describe() + canCraft);
        }
        System.out.print("Enter number to craft, Q to close: ");
        String input = readRaw();
        if (input.equalsIgnoreCase("q")) return;

        try {
            int choice = Integer.parseInt(input) - 1;
            if (game.craft(choice)) {
                System.out.println("Crafted: " + recipes.get(choice).getResult().getName());
            } else {
                System.out.println("Not enough resources.");
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice.");
        }
    }
}