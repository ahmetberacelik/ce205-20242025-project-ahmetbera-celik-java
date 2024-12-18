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

    /**
     * @brief Displays the current budget.
     *
     * @param budget Current budget value.
     * @return Returns 1 after displaying the budget.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public int viewBudget(double budget) throws IOException, InterruptedException {
        userAuth.clearScreen();
        out.println("\n=== Current Budget ===\n");
        out.printf("Your current budget: %.2f TL\n\n", budget);
        userAuth.enterToContinue();
        return 1;
    }

    /**
     * @brief Plans meals and updates the budget.
     *
     * Allows the user to select recipes to plan meals and calculates the total cost,
     * updating the budget accordingly.
     *
     * @param pathFileRecipes Path to the file containing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @param budget The current budget (passed as a reference).
     * @return Returns the updated budget.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public double planMeals(String pathFileRecipes, String pathFileIngredients, double budget) throws IOException, InterruptedException {
        List<Recipe> recipes = recipeCosting.loadRecipesFromFile(pathFileRecipes);
        List<Ingredient> ingredientList = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients);

        if (recipes.isEmpty()) {
            out.println("\nNo recipes found for meal planning.\n");
            userAuth.enterToContinue();
            return budget; // Return unchanged budget
        }

        double totalCost = 0;
        List<Integer> selectedRecipeIds = new ArrayList<>();

        // Step 1: List available recipes
        userAuth.clearScreen();
        out.println("\n=== Available Recipes ===\n");
        listRecipesWithPrices(pathFileRecipes, pathFileIngredients);

        // Step 2: Allow user to select recipes by ID
        out.println("\n=== Recipe Selection ===\n");
        out.println("Enter recipe ID to add to meal plan. Type 'done' to finish:\n");

        while (true) {
            out.print("Recipe ID (or 'done'): ");
            String input = scanner.next();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            int recipeId;
            try {
                recipeId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                out.println("Invalid input. Please enter a valid recipe ID or 'done'.\n");
                continue;
            }

            if (recipeId <= 0 || recipeId > recipes.size()) {
                out.println("Invalid recipe ID. Please select a recipe from the list.\n");
                continue;
            }

            Recipe selectedRecipe = recipes.get(recipeId - 1); // Adjusting for 0-based indexing

            // Calculate the cost of the selected recipe
            double recipeCost = 0;
            for (int ingredientId : selectedRecipe.getIngredients()) {
                for (Ingredient ingredient : ingredientList) {
                    if (ingredient.getId() == ingredientId) {
                        recipeCost += ingredient.getPrice();
                        break;
                    }
                }
            }

            // Check if the recipe can be added to the budget
            if (recipeCost > budget) {
                out.printf("\nCannot add '%s'. Insufficient funds.\n\n", selectedRecipe.getName());
                continue;
            }

            selectedRecipeIds.add(recipeId);
            totalCost += recipeCost;
            budget -= recipeCost; // Deduct from budget
            out.printf("\n'%s' added to meal plan.\n", selectedRecipe.getName());
            out.printf("Current total cost: %.2f TL\n", totalCost);
            out.printf("Remaining budget: %.2f TL\n\n", budget);
        }

        // Display the total cost and remaining budget
        out.println("\n=== Meal Plan Summary ===\n");
        out.printf("Total cost of selected recipes: %.2f TL\n", totalCost);
        out.printf("Remaining budget: %.2f TL\n\n", budget);

        userAuth.enterToContinue();
        return budget; // Return updated budget
    }

    /**
     * @brief Displays and handles the budget planner menu.
     *
     * Allows the user to interact with budget planning features, such as meal planning
     * and viewing the current budget.
     *
     * @param pathFileRecipes Path to the file containing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @return Returns 1 when exiting the menu.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public int budgetPlannerMenu(String pathFileRecipes, String pathFileIngredients) throws IOException, InterruptedException {
        // Ask user for their current budget
        double budget;
        out.print("\nEnter your current budget: ");
        do {
            budget = userAuth.getInput();
        } while (budget == -2);

        int choice;
        while (true) {
            userAuth.clearScreen();
            out.println("\n=== Budget Planner Menu ===\n");
            out.println("1. Plan Meals");
            out.println("2. View Budget");
            out.println("3. Exit\n");
            out.print("Your choice: ");

            choice = userAuth.getInput();
            if (choice == -2) {
                userAuth.handleInputError();
                userAuth.enterToContinue();
                continue;
            }

            switch (choice) {
                case 1:
                    budget = planMeals(pathFileRecipes, pathFileIngredients, budget); // Update budget
                    break;
                case 2:
                    viewBudget(budget); // Show updated budget
                    break;
                case 3:
                    return 1;
                default:
                    out.println("\nInvalid choice. Please try again.\n");
                    userAuth.enterToContinue();
                    break;
            }
        }
    }
}
