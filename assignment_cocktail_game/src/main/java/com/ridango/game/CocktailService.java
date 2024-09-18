package com.ridango.game;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CocktailService {

    private static final String COCKTAIL_API_URL = "https://www.thecocktaildb.com/api/json/v1/1/random.php";

    // Fetch a random cocktail from the API and map it to a Cocktail object
    public Cocktail getRandomCocktail() {
        RestTemplate restTemplate = new RestTemplate();

        // Fetch the JSON response from the API
        Map<String, Object> response = restTemplate.getForObject(COCKTAIL_API_URL, Map.class);

        // Extract the first drink from the list
        List<Map<String, String>> drinks = (List<Map<String, String>>) response.get("drinks");
        Map<String, String> drink = drinks.get(0);

        // Create a new Cocktail object and populate its fields
        Cocktail cocktail = new Cocktail();
        cocktail.setStrDrink(drink.get("strDrink"));
        cocktail.setStrInstructions(drink.get("strInstructions"));
        cocktail.setStrCategory(drink.get("strCategory"));
        cocktail.setStrGlass(drink.get("strGlass"));
        cocktail.setStrIngredients(getIngredients(drink));

        return cocktail;
    }

    // Method to extract ingredients and measures as a concatenated string
    private String getIngredients(Map<String, String> drink) {
        StringBuilder ingredients = new StringBuilder();

        for (int i = 1; i <= 15; i++) {
            String ingredient = drink.get("strIngredient" + i);
            String measure = drink.get("strMeasure" + i);

            if (ingredient != null && !ingredient.isEmpty()) {
                ingredients.append(ingredient);
                if (measure != null && !measure.isEmpty()) {
                    ingredients.append(" (").append(measure).append(")");
                }
                ingredients.append(", ");
            }
        }

        // Remove the trailing comma and space, if present
        if (ingredients.length() > 0) {
            ingredients.setLength(ingredients.length() - 2);
        }

        return ingredients.toString();
    }
}
