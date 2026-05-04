package core;

import java.util.Map;

public class Recipe {
    private final Item result;
    private final Map<Resource, Integer> ingredients;

    public Recipe(Item result, Map<Resource, Integer> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public Item getResult() { return result; }
    public Map<Resource, Integer> getIngredients() { return ingredients; }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(result.getName()).append(" = ");
        ingredients.forEach((res, amt) ->
                sb.append(amt).append(" ").append(res.name()).append(" + ")
        );
        // remove trailing " + "
        return sb.substring(0, sb.length() - 3);
    }
}