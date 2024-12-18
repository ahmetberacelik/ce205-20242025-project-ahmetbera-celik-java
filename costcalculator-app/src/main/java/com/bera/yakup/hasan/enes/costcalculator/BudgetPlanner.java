/**
 * @file BudgetPlanner.java
 * @brief This file contains the implementation of the BudgetPlanner class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @class BudgetPlanner
 * @brief Manages budget-related operations, including meal planning and budget tracking.
 *
 * The BudgetPlanner class allows users to manage their budgets by planning meals,
 * viewing the current budget, and listing recipes with their costs. It integrates
 * with RecipeCosting, IngredientManagement, and PriceAdjustment classes.
 */
public class BudgetPlanner {
    private Scanner scanner; ///< Scanner for user input.
    private PrintStream out; ///< PrintStream for displaying output.
    private UserAuthentication userAuth; ///< Instance of UserAuthentication for shared utilities.
    private RecipeCosting recipeCosting; ///< Instance of RecipeCosting for recipe operations.
    private IngredientManagement ingredientManagement; ///< Instance of IngredientManagement for ingredient operations.
    private PriceAdjustment priceAdjustment; ///< Instance of PriceAdjustment for price-related operations.

    /**
     * @brief Constructor for the BudgetPlanner class.
     *
     * @param userAuth Instance of UserAuthentication for shared utilities.
     * @param recipeCosting Instance of RecipeCosting for recipe operations.
     * @param ingredientManagement Instance of IngredientManagement for ingredient operations.
     * @param priceAdjustment Instance of PriceAdjustment for price-related operations.
     * @param scanner Scanner for user input.
     * @param out PrintStream for displaying output.
     */
    public BudgetPlanner(UserAuthentication userAuth, RecipeCosting recipeCosting, IngredientManagement ingredientManagement, PriceAdjustment priceAdjustment, Scanner scanner, PrintStream out) {
        this.userAuth = userAuth;
        this.recipeCosting = recipeCosting;
        this.ingredientManagement = ingredientManagement;
        this.priceAdjustment = priceAdjustment;
        this.scanner = scanner;
        this.out = out;
    }

    /**
     * @brief Lists all recipes with their prices.
     *
     * @param pathFileRecipes Path to the file containing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @return Returns 1 if successful, 0 if no recipes found.
     * @throws IOException If an I/O error occurs.
     */
    public int listRecipesWithPrices(String pathFileRecipes, String pathFileIngredients) throws IOException {
        List<Recipe> recipes = recipeCosting.loadRecipesFromFile(pathFileRecipes);
        List<Ingredient> ingredientList = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients);

        if (recipes.isEmpty()) {
            out.println("\nNo recipes found.\n");
            return 0;
        }

        for (Recipe recipe : recipes) {
            double recipeCost = 0;
            for (int ingredientId : recipe.getIngredients()) {
                for (Ingredient ingredient : ingredientList) {
                    if (ingredient.getId() == ingredientId) {
                        recipeCost += ingredient.getPrice();
                        break;
                    }
                }
            }
            out.printf("ID: %d | Name: %s | Price: %.2f TL\n", recipes.indexOf(recipe) + 1, recipe.getName(), recipeCost);
        }
        out.println();

        return 1;
    }
