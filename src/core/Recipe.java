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


    public String describe() {
        StringJoiner sj = new StringJoiner(" + ");
        ingredients.forEach((res, amt) -> sj.add(amt + " " + res.name()));
        return result.getName() + " = " + sj;
    }
}