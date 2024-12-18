/**
 * @file RecipeCosting.java
 * @brief Handles operations related to creating, editing, and managing recipes.
 */
package com.bera.yakup.hasan.enes.costcalculator;

import java.io.*;
import java.util.*;
/**
 * @class RecipeCosting
 * @brief Provides functionality for managing recipes, including creation, editing, and saving to files.
 *
 * The RecipeCosting class enables users to create new recipes, edit existing ones,
 * and manage recipe ingredients and categories. It integrates with other modules
 * such as UserAuthentication and PriceAdjustment to streamline recipe management.
 */
public class RecipeCosting {

    private Scanner scanner; ///< Scanner for user input.
    private PrintStream out; ///< PrintStream for displaying output.
    private UserAuthentication userAuth; ///< Instance of UserAuthentication for shared utilities.
    private PriceAdjustment priceAdjustment; ///< Instance of PriceAdjustment for ingredient operations.
    /**
     * @brief Constructor for the RecipeCosting class.
     *
     * @param userAuth Instance of UserAuthentication for shared utilities.
     * @param priceAdjustment Instance of PriceAdjustment for ingredient operations.
     * @param scanner Scanner for user input.
     * @param out PrintStream for displaying output.
     */
    public RecipeCosting(UserAuthentication userAuth, PriceAdjustment priceAdjustment, Scanner scanner, PrintStream out) {
        this.userAuth = userAuth;
        this.scanner = scanner;
        this.out = out;
        this.priceAdjustment = priceAdjustment;
    }
    /**
     * @brief Creates a new recipe.
     *
     * Prompts the user for recipe details, including the name, category, and ingredients.
     * Saves the created recipe to the specified file.
     *
     * @param recipes The list of existing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @param pathFileRecipes Path to the file where recipes are saved.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void createRecipe(List<Recipe> recipes, String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        userAuth.clearScreen();
        out.println("Enter the name of the new recipe: ");
        String recipeName = scanner.nextLine();

        userAuth.clearScreen();
        out.println("Select the category of the recipe:");
        out.println("1) Soup\n2) Appetizer\n3) Main Course\n4) Dessert");
        int categoryChoice = userAuth.getInput();

        if (categoryChoice < 1 || categoryChoice > 4) {
            userAuth.handleInputError();
            userAuth.enterToContinue();
            return;
        }

        List<Ingredient> ingredients = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients);
        if (ingredients.isEmpty()) {
            out.println("No ingredients available to add.");
            userAuth.enterToContinue();
            return;
        }

        out.println("Available Ingredients:");
        for (Ingredient ingredient : ingredients) {
            out.printf("ID: %d | Name: %s | Price: %.2f TL%n", ingredient.getId(), ingredient.getName(), ingredient.getPrice());
        }

        out.println("Enter the ingredient ID to add to the recipe. Type 'done' when finished:");
        List<Integer> selectedIngredients = new ArrayList<>();
        while (true) {
            out.println("Ingredient ID (or 'done'): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            try {
                int ingredientId = Integer.parseInt(input);
                boolean exists = ingredients.stream().anyMatch(ingredient -> ingredient.getId() == ingredientId);
                if (exists) {
                    selectedIngredients.add(ingredientId);
                } else {
                    out.println("Invalid ingredient ID. Please enter a valid ID from the available ingredients.");
                }
            } catch (NumberFormatException e) {
                userAuth.handleInputError();
            }
        }

        if (selectedIngredients.isEmpty()) {
            out.println("No ingredients selected. Recipe creation canceled.");
            userAuth.enterToContinue();
            return;
        }

        Recipe newRecipe = new Recipe(recipeName, categoryChoice);
        newRecipe.setIngredients(selectedIngredients);
        recipes.add(newRecipe);

        saveRecipesToFile(pathFileRecipes, recipes);
        out.println("Recipe created successfully!");
        userAuth.enterToContinue();
    }