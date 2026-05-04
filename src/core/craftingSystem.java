package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingSystem {
    private final List<Recipe> recipes = new ArrayList<>();

    public CraftingSystem() {
        // PICKAXE = 2 ROCK + 1 STICK
        recipes.add(new Recipe(Item.PICKAXE, Map.of(
                Resource.ROCK, 2,
                Resource.STICK, 1
        )));

        // BOMB = 1 ROCK + 2 STICK
        recipes.add(new Recipe(Item.BOMB, Map.of(
                Resource.ROCK, 1,
                Resource.STICK, 2
        )));
    }

    public List<Recipe> getRecipes() { return recipes; }

    public boolean canCraft(Recipe recipe, Inventory inventory) {
        for (Map.Entry<Resource, Integer> entry : recipe.getIngredients().entrySet()) {
            if (inventory.getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean craft(Recipe recipe, Inventory inventory) {
        if (!canCraft(recipe, inventory)) return false;

        recipe.getIngredients().forEach((res, amt) ->
                inventory.removeResource(res, amt)
        );
        inventory.addItem(recipe.getResult());
        return true;
    }
}