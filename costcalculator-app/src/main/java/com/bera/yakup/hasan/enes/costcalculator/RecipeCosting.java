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
    /**
     * @brief Edits an existing recipe.
     *
     * Allows the user to modify the name, category, or ingredients of a selected recipe.
     * Updates the recipe data in the specified file after editing.
     *
     * @param recipes The list of existing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @param pathFileRecipes Path to the file where recipes are saved.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void editRecipe(List<Recipe> recipes, String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        userAuth.clearScreen();
        if (recipes.isEmpty()) {
            out.println("No recipes available to edit.");
            userAuth.enterToContinue();
            return;
        }

        out.println("Available Recipes:");
        for (int i = 0; i < recipes.size(); i++) {
            out.printf("%d) %s%n", i + 1, recipes.get(i).getName());
        }

        out.println("Enter the ID of the recipe you want to edit: ");
        int recipeId = userAuth.getInput();

        if (recipeId < 1 || recipeId > recipes.size()) {
            userAuth.handleInputError();
            userAuth.enterToContinue();
            return;
        }

        Recipe selectedRecipe = recipes.get(recipeId - 1);
        userAuth.clearScreen();

        out.println("Editing Recipe: " + selectedRecipe.getName());
        out.println("What would you like to edit?");
        out.println("1) Name\n2) Category\n3) Ingredients");
        int editChoice = userAuth.getInput();

        switch (editChoice) {
            case 1:
                userAuth.clearScreen();
                out.println("Enter new name: ");
                String newName = scanner.nextLine();
                selectedRecipe.setName(newName);
                break;
            case 2:
                userAuth.clearScreen();
                out.println("Select new category:");
                out.println("1) Soup\n2) Appetizer\n3) Main Course\n4) Dessert");
                int newCategory = userAuth.getInput();
                if (newCategory < 1 || newCategory > 4) {
                    userAuth.handleInputError();
                    userAuth.enterToContinue();
                    return;
                }
                selectedRecipe.setCategory(newCategory);
                break;
            case 3:
                userAuth.clearScreen();
                List<Ingredient> ingredientList = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients);

                System.out.println("Current Ingredients in Recipe:");
                for (int ingredientId : selectedRecipe.getIngredients()) {
                    // Find ingredient name by ID
                    Ingredient ingredient = ingredientList.stream()
                            .filter(ing -> ing.getId() == ingredientId)
                            .findFirst()
                            .orElse(null);
                    if (ingredient != null) {
                        System.out.printf("ID: %d - Name: %s%n", ingredient.getId(), ingredient.getName());
                    } else {
                        System.out.printf("ID: %d - Name: Not Found%n", ingredientId);
                    }
                }
                System.out.println("Would you like to:\n1) Add Ingredients\n2) Remove Ingredients");
                int ingredientChoice = userAuth.getInput();

                if (ingredientChoice == -2) {
                    userAuth.handleInputError();
                    userAuth.enterToContinue();
                    break;
                }

                if (ingredientChoice == 1) {
                    userAuth.clearScreen();
                    System.out.println("Available Ingredients:");
                    priceAdjustment.printIngredientsToConsole(pathFileIngredients);

                    System.out.println("Enter the ingredient ID to add to the recipe. Type 'done' when finished:");
                    while (true) {
                        System.out.println("Ingredient ID (or 'done'): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("done")) {
                            break;
                        }
                        try {
                            int ingredientId = Integer.parseInt(input);

                            // Validate if the entered ingredient ID exists in the available ingredients
                            boolean exists = ingredientList.stream().anyMatch(ingredient -> ingredient.getId() == ingredientId);
                            if (exists) {
                                selectedRecipe.getIngredients().add(ingredientId);
                                System.out.println("Ingredient added successfully.");
                            } else {
                                System.out.println("Invalid ingredient ID. Please enter a valid ID from the available ingredients.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid ingredient ID or 'done' to finish.");
                        }
                    }
                } else if (ingredientChoice == 2) {
                    userAuth.clearScreen();
                    ingredientList = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients);

                    System.out.println("Current Ingredients in Recipe:");
                    for (int ingredientId : selectedRecipe.getIngredients()) {
                        // Find ingredient name by ID
                        Ingredient ingredient = ingredientList.stream()
                                .filter(ing -> ing.getId() == ingredientId)
                                .findFirst()
                                .orElse(null);
                        if (ingredient != null) {
                            System.out.printf("ID: %d - Name: %s%n", ingredient.getId(), ingredient.getName());
                        } else {
                            System.out.printf("ID: %d - Name: Not Found%n", ingredientId);
                        }
                    }
                    System.out.println("Enter the ingredient ID to remove from the recipe. Type 'done' when finished:");
                    while (true) {
                        System.out.println("Ingredient ID (or 'done'): ");
                        String input = scanner.nextLine();
                        if (input.equalsIgnoreCase("done")) {
                            break;
                        }
                        try {
                            int ingredientId = Integer.parseInt(input);
                            if (selectedRecipe.getIngredients().remove(Integer.valueOf(ingredientId))) {
                                System.out.println("Ingredient " + ingredientId + " removed.");
                            } else {
                                System.out.println("Ingredient " + ingredientId + " not found in the recipe.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid ingredient ID or 'done' to finish.");
                        }
                    }
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            default:
                System.out.println("Invalid choice. Returning to menu.");
                break;
        }

        saveRecipesToFile(pathFileRecipes, recipes);
        System.out.println("Recipe updated successfully!");
    }
    /**
     * @brief Calculates the total cost of a specific recipe.
     *
     * The method uses a sparse matrix to store ingredient prices, calculates the total cost
     * of the recipe by summing the prices of its ingredients, and prints the sparse matrix
     * representation for debugging purposes.
     *
     * @param recipes The list of existing recipes.
     * @param pathFileIngredients Path to the file containing ingredients.
     * @param pathFileRecipes Path to the file where recipes are saved.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void calculateRecipeCost(List<Recipe> recipes, String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        if (recipes.isEmpty()) {
            out.println("No recipes available to calculate cost.");
            userAuth.enterToContinue(); // Wait for user to continue
            return;
        }

        userAuth.clearScreen(); // Clear the console
        out.println("Available Recipes:");
        for (int i = 0; i < recipes.size(); i++) {
            out.printf("%d) %s%n", i + 1, recipes.get(i).getName());
        }

        out.println("Enter the ID of the recipe to calculate cost: ");
        int recipeId = userAuth.getInput(); // Get user input for recipe ID

        if (recipeId == -2) { // Invalid input check
            userAuth.handleInputError(); // Display error message for invalid input
            userAuth.enterToContinue(); // Wait for user to continue
            return;
        }

        if (recipeId < 1 || recipeId > recipes.size()) { // Check if the recipe ID is valid
            out.println("Invalid recipe ID.");
            userAuth.enterToContinue(); // Wait for user to continue
            return;
        }

        Recipe selectedRecipe = recipes.get(recipeId - 1);

        // Sparse matrix to store ingredients and their prices
        SparseMatrix sparseMatrix = new SparseMatrix();
        List<Ingredient> ingredients = priceAdjustment.convertDoubleLinkToArray(pathFileIngredients); // Load ingredients

        double totalCost = 0.0;

        // Populate the sparse matrix with ingredient prices
        for (int ingredientId : selectedRecipe.getIngredients()) {
            Optional<Ingredient> ingredient = ingredients.stream()
                    .filter(ing -> ing.getId() == ingredientId)
                    .findFirst();

            if (ingredient.isPresent()) {
                Ingredient ing = ingredient.get();
                sparseMatrix.insert(selectedRecipe.getIngredients().indexOf(ingredientId), ing.getId(), ing.getPrice());
                totalCost += ing.getPrice();
            } else {
                out.printf("Warning: Ingredient ID %d not found.%n", ingredientId);
            }
        }

        // Print sparse matrix for debugging purposes
        out.println("Sparse Matrix Representation of Recipe Ingredients:");
        sparseMatrix.printMatrix();

        out.printf("The total cost of the recipe '%s' is: %.2f TL%n", selectedRecipe.getName(), totalCost);
        userAuth.enterToContinue(); // Wait for user to continue
    }


    /**
     * @param pathFileRecipes File path to save the recipes.
     * @param recipes         List of recipes to be saved.
     * @throws IOException If an I/O error occurs during file writing.
     * @brief Saves all recipes to a file.
     */
    public void saveRecipesToFile(String pathFileRecipes, List<Recipe> recipes) throws IOException {
        File file = new File(pathFileRecipes);

        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(file, true))) {
            // Write each Recipe object to the file
            for (Recipe recipe : recipes) {
                writer.writeUTF(recipe.getName());
                writer.writeInt(recipe.getCategory());
                List<Integer> ingredients = recipe.getIngredients();
                writer.writeInt(ingredients.size());
                for (int ingredientId : ingredients) {
                    writer.writeInt(ingredientId);
                }
            }
        }
    }
    /**
     * @brief Traverses the recipes using Breadth-First Search (BFS) to analyze ingredient usage.
     *
     * The method traverses all recipes in BFS order and computes the frequency of each ingredient
     * used in the recipes.
     *
     * @param recipes The list of existing recipes.
     */
    public void traverseRecipesBFS(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            out.println("No recipes available.");
            userAuth.enterToContinue();
            return;
        }

        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Map<Integer, Double>> ingredientUsage = new HashMap<>();

        for (int i = 0; i < recipes.size(); i++) {
            queue.add(i);
        }

        while (!queue.isEmpty()) {
            int recipeId = queue.poll();
            Recipe currentRecipe = recipes.get(recipeId);

            for (int ingredientId : currentRecipe.getIngredients()) {
                ingredientUsage
                        .computeIfAbsent(recipeId, k -> new HashMap<>())
                        .merge(ingredientId, 1.0, Double::sum);
            }
        }

        ingredientUsage.forEach((recipeId, ingredients) -> {
            ingredients.forEach((ingredientId, count) -> {
                out.println("-------------------------------------------");
                out.printf("Recipe %d uses Ingredient %d - %.2f times%n", recipeId + 1, ingredientId, count);
                out.println("-------------------------------------------");
            });
        });
    }
    /**
     * @brief Traverses the recipes using Depth-First Search (DFS) to analyze ingredient usage.
     *
     * The method traverses all recipes in DFS order and computes the frequency of each ingredient
     * used in the recipes.
     *
     * @param recipes The list of existing recipes.
     */
    public void traverseRecipesDFS(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            out.println("No recipes available.");
            userAuth.enterToContinue();
            return;
        }

        Stack<Integer> stack = new Stack<>();
        Map<Integer, Map<Integer, Double>> ingredientUsage = new HashMap<>();

        for (int i = 0; i < recipes.size(); i++) {
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            int recipeId = stack.pop();
            Recipe currentRecipe = recipes.get(recipeId);

            for (int ingredientId : currentRecipe.getIngredients()) {
                ingredientUsage
                        .computeIfAbsent(recipeId, k -> new HashMap<>())
                        .merge(ingredientId, 1.0, Double::sum);
            }
        }

        ingredientUsage.forEach((recipeId, ingredients) -> {
            ingredients.forEach((ingredientId, count) -> {
                out.println("-------------------------------------------");
                out.printf("Recipe %d uses Ingredient %d - %.2f times%n", recipeId + 1, ingredientId, count);
                out.println("-------------------------------------------");
            });
        });
    }
    /**
     * @brief Provides an analysis of ingredient usage across all recipes.
     *
     * Prompts the user to choose a traversal method (BFS or DFS) to analyze ingredient usage
     * in all recipes.
     *
     * @param recipes The list of existing recipes.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void analyzeIngredientUsage(List<Recipe> recipes) throws InterruptedException, IOException {
        out.println("How would you like to analyze the ingredients used in all recipes?");
        out.println("1) BFS (Breadth-First Search)");
        out.println("2) DFS (Depth-First Search)");
        out.print("Enter your choice (1-2): ");
        int choice = userAuth.getInput();

        switch (choice) {
            case 1:
                traverseRecipesBFS(recipes);
                break;
            case 2:
                traverseRecipesDFS(recipes);
                break;
            default:
                out.println("Invalid choice. Returning to menu.");
                break;
        }
        userAuth.enterToContinue();
    }

    /**
     * @param pathFileRecipes File path to load the recipes from.
     * @return A list of loaded recipes.
     * @brief Loads recipes from a file.
     */
    public List<Recipe> loadRecipesFromFile(String pathFileRecipes) throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        File file = new File(pathFileRecipes);

        if (!file.exists()) {
            out.println("Error opening recipe file.");
            return recipes;
        }

        try (DataInputStream reader = new DataInputStream(new FileInputStream(file))) {
            while (reader.available() > 0) {
                Recipe recipe = new Recipe();
                // Read recipe name
                recipe.setName(reader.readUTF());
                // Read recipe category
                recipe.setCategory(reader.readInt());
                // Read ingredients
                int ingredientCount = reader.readInt();
                List<Integer> ingredients = new ArrayList<>();
                for (int i = 0; i < ingredientCount; i++) {
                    ingredients.add(reader.readInt());
                }
                recipe.setIngredients(ingredients);
                recipes.add(recipe);
            }
        }

        return recipes;
    }
    /**
     * @brief Searches for recipes by category using a B+ tree.
     *
     * Prompts the user to input a category and searches the B+ tree for recipes in the
     * specified category.
     *
     * @param bPlusTree The B+ tree containing recipes indexed by category.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    private void searchRecipeByCategory(BPlusTree bPlusTree) throws IOException, InterruptedException {
        out.println("Enter category to search (1: Soup, 2: Appetizer, 3: Main Course, 4: Dessert): ");
        int category = userAuth.getInput();

        if (category < 1 || category > 4) {
            out.println("Invalid category choice.");
            userAuth.enterToContinue();
            return;
        }

        out.println("Recipes in selected category:");
        bPlusTree.search(category);

        userAuth.enterToContinue();
    }
    /**
     * @brief Analyzes the recipes for Strongly Connected Components (SCC).
     *
     * Constructs a recipe graph, resets its state, and uses Tarjan's algorithm to find and
     * display SCCs within the recipe graph.
     *
     * @param recipes The list of existing recipes.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    private void analyzeSCC(List<Recipe> recipes) throws IOException, InterruptedException {
        RecipeGraph recipeGraph = RecipeGraph.buildGraphFromRecipes(recipes, scanner);

        // Reset graph state before running SCC
        recipeGraph.resetGraphState();

        out.println("+--------------------------------------+\n"
                + "|   STRONGLY CONNECTED COMPONENTS      |\n"
                + "+--------------------------------------+\n");
        recipeGraph.tarjanSCC(recipes);
        userAuth.enterToContinue();
    }
    /**
     * @brief Displays and handles the Recipe Costing Menu.
     *
     * The method provides a menu for users to manage recipes, calculate costs, search by category,
     * analyze ingredient usage, and analyze Strongly Connected Components (SCC) in the recipe graph.
     * It dynamically updates the B+ tree and recipe graph based on user actions.
     *
     * @param pathFileIngredients Path to the file containing ingredients.
     * @param pathFileRecipes Path to the file containing recipes.
     * @return Returns `true` when the user exits the menu.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public boolean recipeCostingMenu(String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        userAuth.clearScreen();
        List<Recipe> recipes = loadRecipesFromFile(pathFileRecipes);
        BPlusTree bPlusTree = new BPlusTree();
        RecipeGraph recipeGraph = RecipeGraph.buildGraphFromRecipes(recipes, scanner);

        for (Recipe recipe : recipes) {
            bPlusTree.insert(recipe.getCategory(), recipe);
        }
        while (true) {
            userAuth.clearScreen();
            out.println("+--------------------------------------+\n"
                    + "|           RECIPE COSTING MENU        |\n"
                    + "+--------------------------------------+\n"
                    + "| 1. Create Recipe                     |\n"
                    + "| 2. Edit Recipe                       |\n"
                    + "| 3. Calculate Recipe Cost             |\n"
                    + "| 4. Search Recipe by Category         |\n"
                    + "| 5. Analyze Ingredient Usage          |\n"
                    + "| 6. Analyze SCC in Recipe Graph       |\n"
                    + "| 7. Exit                              |\n"
                    + "+--------------------------------------+\n");
            out.print("Enter your choice: ");
            int choice = userAuth.getInput();

            if (choice == -2) {
                userAuth.handleInputError();
                userAuth.enterToContinue();
                continue;
            }

            switch (choice) {
                case 1:
                    createRecipe(recipes, pathFileIngredients, pathFileRecipes);
                    bPlusTree = new BPlusTree();
                    for (Recipe recipe : recipes) {
                        bPlusTree.insert(recipe.getCategory(), recipe);
                    }
                    break;
                case 2:
                    editRecipe(recipes, pathFileIngredients, pathFileRecipes);
                    bPlusTree = new BPlusTree();
                    for (Recipe recipe : recipes) {
                        bPlusTree.insert(recipe.getCategory(), recipe);
                    }
                    break;
                case 3:
                    calculateRecipeCost(recipes, pathFileIngredients, pathFileRecipes);
                    break;
                case 4:
                    searchRecipeByCategory(bPlusTree);
                    break;
                case 5:
                    analyzeIngredientUsage(recipes);
                    break;
                case 6:
                    analyzeSCC(recipes);
                    break;
                case 7:
                    return true;
                default:
                    out.println("Invalid choice. Please try again.");
                    userAuth.enterToContinue();
                    break;
            }
        }
    }

}
