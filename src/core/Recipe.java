package core;

import java.util.Map;
import java.util.StringJoiner;

public class Recipe {
    private final Item result;
    private final Map<Resource, Integer> ingredients;

    public Recipe(Item result, Map<Resource, Integer> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public Item getResult() { return result; }
    public Map<Resource, Integer> getIngredients() { return ingredients; }

    // FIX: StringJoiner replaces the manual sb.substring(0, sb.length() - 3) hack
    public String describe() {
        StringJoiner sj = new StringJoiner(" + ");
        ingredients.forEach((res, amt) -> sj.add(amt + " " + res.name()));
        return result.getName() + " = " + sj;
    }
}