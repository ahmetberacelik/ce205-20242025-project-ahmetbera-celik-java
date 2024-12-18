package com.bera.yakup.hasan.enes.costcalculator;

import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class RecipeCostingTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private RecipeCosting recipeCosting;
    private String ingredientTestFile = "ingredientTestFile.bin";
    private String recipeTestFile = "recipeTestFile.bin";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        Scanner testScanner = new Scanner(System.in);
        recipeCosting = getRecipeCosting(testScanner);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        deleteFile(ingredientTestFile);
        deleteFile(recipeTestFile);
    }

    private static RecipeCosting getRecipeCosting(Scanner testScanner) {
        UserAuthentication userAuthentication = new UserAuthentication(testScanner, System.out);
        IngredientManagement ingredientManagement = new IngredientManagement(userAuthentication, testScanner, System.out);
        PriceAdjustment priceAdjustment = new PriceAdjustment(userAuthentication, ingredientManagement, testScanner, System.out);
        return new RecipeCosting(userAuthentication, priceAdjustment, testScanner, System.out);
    }

    private RecipeCosting simulateUserInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inContent);
        return getRecipeCosting(testScanner);
    }

    private void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    private Ingredient createTestIngredientFile(String filePath) throws IOException {
        IngredientManagement ingredientManagement = new IngredientManagement(null, null, System.out);

        Ingredient head = null;

        // Add predefined ingredients
        head = ingredientManagement.addIngredient(head, "Salt", 1.5f, filePath);
        head = ingredientManagement.addIngredient(head, "Sugar", 2.0f, filePath);
        head = ingredientManagement.addIngredient(head, "Flour", 1.2f, filePath);
        head = ingredientManagement.addIngredient(head, "Butter", 3.5f, filePath);
        head = ingredientManagement.addIngredient(head, "Milk", 2.5f, filePath);

        // Save the list to the specified file
        ingredientManagement.saveIngredientsToFile(head, filePath);

        return head;
    }

    private List<Recipe> createTestRecipeFile(String filePath) throws IOException {
        List<Recipe> recipes = new ArrayList<>();

        // Create predefined recipes
        Recipe recipe1 = new Recipe("Tomato Soup", 1); // Category: Soup
        recipe1.addIngredient(1); // Ingredient ID: 1
        recipe1.addIngredient(2); // Ingredient ID: 2

        Recipe recipe2 = new Recipe("Caesar Salad", 2); // Category: Appetizer
        recipe2.addIngredient(3); // Ingredient ID: 3
        recipe2.addIngredient(4); // Ingredient ID: 4

        Recipe recipe3 = new Recipe("Grilled Chicken", 3); // Category: Main Course
        recipe3.addIngredient(5); // Ingredient ID: 5
        recipe3.addIngredient(6); // Ingredient ID: 6

        Recipe recipe4 = new Recipe("Chocolate Cake", 4); // Category: Dessert
        recipe4.addIngredient(7); // Ingredient ID: 7
        recipe4.addIngredient(8); // Ingredient ID: 8

        // Add recipes to the list
        recipes.add(recipe1);
        recipes.add(recipe2);
        recipes.add(recipe3);
        recipes.add(recipe4);

        // Save recipes to file
        saveRecipesToFile(recipes, filePath);

        return recipes;
    }

    private void saveRecipesToFile(List<Recipe> recipes, String filePath) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath))) {
            for (Recipe recipe : recipes) {
                out.writeUTF(recipe.getName());
                out.writeInt(recipe.getCategory());
                out.writeInt(recipe.getIngredients().size());
                for (int ingredientId : recipe.getIngredients()) {
                    out.writeInt(ingredientId);
                }
            }
        }
    }

    private List<Recipe> loadRecipesFromFile(String filePath) throws IOException {
        List<Recipe> recipes = new ArrayList<>();

        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            while (in.available() > 0) {
                String name = in.readUTF();
                int category = in.readInt();
                int ingredientCount = in.readInt();
                List<Integer> ingredients = new ArrayList<>();
                for (int i = 0; i < ingredientCount; i++) {
                    ingredients.add(in.readInt());
                }

                Recipe recipe = new Recipe(name, category);
                recipe.setIngredients(ingredients);
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    @Test
    public void testCreateTestRecipeFile() throws IOException {
        String testFilePath = "RecipeTestFile.bin";

        // Create the test file with predefined recipes
        List<Recipe> recipes = createTestRecipeFile(testFilePath);

        // Assert the file exists
        File file = new File(testFilePath);
        assertTrue(file.exists());

        // Load the recipes back from the file and verify
        List<Recipe> loadedRecipes = loadRecipesFromFile(testFilePath);

        // Verify the number of recipes loaded
        assertEquals(recipes.size(), loadedRecipes.size());

        // Verify the details of the first recipe
        Recipe loadedRecipe1 = loadedRecipes.get(0);
        assertEquals("Tomato Soup", loadedRecipe1.getName());
        assertEquals(1, loadedRecipe1.getCategory());
        assertEquals(2, loadedRecipe1.getIngredientCount());
        assertEquals(1, (int) loadedRecipe1.getIngredients().get(0));
        assertEquals(2, (int) loadedRecipe1.getIngredients().get(1));
    }

    @Test
    public void testCreateTestIngredientFile() throws IOException {
        String testFilePath = "ingredientTestFile.bin";

        // Create the test file with predefined ingredients
        Ingredient head = createTestIngredientFile(testFilePath);

        // Assert the file exists
        File file = new File(testFilePath);
        assertTrue(file.exists());

        // Load the ingredients back from the file and verify
        IngredientManagement ingredientManagement = new IngredientManagement(null, null, System.out);
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(testFilePath);

        // Verify the loaded ingredients
        assertNotNull(loadedHead);
        assertEquals("Salt", loadedHead.getName());
        assertEquals(1.5f, loadedHead.getPrice(), 0.01);

        // Check the second ingredient
        assertNotNull(loadedHead.getNext());
        assertEquals("Sugar", loadedHead.getNext().getName());
        assertEquals(2.0f, loadedHead.getNext().getPrice(), 0.01);
    }

    @Test
    public void calculateRecipeCostNoRecipesTest() throws IOException, InterruptedException {
        // Simulate user input for calculating recipe cost
        String simulatedInput = "1\n";  // Recipe ID 1
        RecipeCosting recipeCosting = simulateUserInput(simulatedInput);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.calculateRecipeCost(recipes, ingredientTestFile, recipeTestFile);

        Assert.assertTrue(outContent.toString().contains("No recipes available to calculate cost."));
    }

    @Test
    public void testCreateRecipeInvalidCategory() throws IOException, InterruptedException {
        String input = "Test Recipe\n5\n\n"; // Invalid category choice (5)
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Only enter numerical value"));
        assertTrue(output.contains("Press any key to continue..."));
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void testCreateRecipeNoIngredients() throws IOException, InterruptedException {
        String input = "Test Recipe\n2\n\n\n"; // Valid category (Appetizer)
        RecipeCosting recipeCosting = simulateUserInput(input);

        // Ensure ingredient list is empty
        recipeCosting.createRecipe(new ArrayList<>(), ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available to add."));
        assertTrue(output.contains("Press any key to continue..."));
    }

    @Test
    public void testCreateRecipeNoIngredientSelected() throws IOException, InterruptedException {
        String input = "No Ingredient Recipe\n1\ndone\n\n"; // No ingredient selected
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available to add."));
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void testRecipeCostingNoIngredientAll1() throws IOException, InterruptedException {
        String input = "1\nasd\n\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Only enter numerical value"));
    }

    @Test
    public void testRecipeCostingEditRecipeNoRecipe() throws IOException, InterruptedException {
        String input = "2\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available to edit."));
    }

    @Test
    public void testRecipeCostingNoRecipeToCalculateCost() throws IOException, InterruptedException {
        String input = "3\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available to calculate cost."));
    }

    @Test
    public void testRecipeCostingNoRecipeToSearchRecipeByCategory() throws IOException, InterruptedException {
        String input = "4\n1\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe not found"));
    }

    @Test
    public void testRecipeCostingToSearchRecipeByCategoryWithRecipe() throws IOException, InterruptedException {
        String input = "4\n1\n\n4\n2\n\n4\n3\n\n4\n4\n\n7\n";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe found: Tomato Soup"));
    }

    @Test
    public void testRecipeCostingNoRecipeToAnalyzeIngredientUsageWithBfs() throws IOException, InterruptedException {
        String input = "5\n1\n\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available."));
    }

    @Test
    public void testRecipeCostingWithRecipeToAnalyzeIngredientUsageWithBfs() throws IOException, InterruptedException {
        String input = "5\n1\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe 4 uses Ingredient 8 - 1,00 times"));
    }

    @Test
    public void testRecipeCostingNoRecipeToAnalyzeIngredientUsageWithDfs() throws IOException, InterruptedException {
        String input = "5\n2\n\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available."));
    }

    @Test
    public void testRecipeCostingWithRecipeToAnalyzeIngredientUsageWithDfs() throws IOException, InterruptedException {
        String input = "5\n2\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe 4 uses Ingredient 8 - 1,00 times"));
    }

    @Test
    public void testRecipeCostingWithRecipeToAnalyzeTarjan() throws IOException, InterruptedException {
        String input = "6\n\n7\n";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe 4: Chocolate Cake (Category: 4, Ingredients: [7, 8])"));
    }

    @Test
    public void testRecipeCostingNoRecipeToAnalyzeSccInRecipeGraph() throws IOException, InterruptedException {
        String input = "6\n\n7\n5\n4\n";
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("STRONGLY CONNECTED COMPONENTS"));
    }

    @Test
    public void testCreateRecipeInvalidCategoryChoice() throws IOException, InterruptedException {
        // Simulate user input for invalid category choice
        String input = "Test Recipe\n5\n\n"; // Invalid category (5)
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Only enter numerical value"));
        assertTrue(output.contains("Press any key to continue..."));
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void testCreateRecipeWithIngredients() throws IOException, InterruptedException {
        // Create a test ingredient file with ingredients
        createTestIngredientFile(ingredientTestFile);

        // Simulate user input for valid recipe creation
        String input = "My Recipe\n4\n1\ndone\n\n"; // Valid category (Dessert) with one ingredient selected
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe is created successfully
        String output = outContent.toString();
        assertTrue(output.contains("Recipe created successfully!"));
        assertEquals(1, recipes.size());
        Recipe createdRecipe = recipes.get(0);
        assertEquals("My Recipe", createdRecipe.getName());
        assertEquals(4, createdRecipe.getCategory());
        assertEquals(1, createdRecipe.getIngredients().size());
        assertTrue(createdRecipe.getIngredients().contains(1)); // Ingredient ID 1
    }

    @Test
    public void testCreateRecipeWithIngredientsButInwalidIngredientId() throws IOException, InterruptedException {
        // Create a test ingredient file with ingredients
        createTestIngredientFile(ingredientTestFile);

        // Simulate user input for valid recipe creation
        String input = "My Recipe\n4\n9\nasd\n1\ndone\n\n"; // Valid category (Dessert) with one ingredient selected
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe is created successfully
        String output = outContent.toString();
        assertTrue(output.contains("Recipe created successfully!"));
        Recipe createdRecipe = recipes.get(0);
        assertEquals("My Recipe", createdRecipe.getName());
        assertEquals(4, createdRecipe.getCategory());
        assertEquals(1, createdRecipe.getIngredients().size());
        assertTrue(createdRecipe.getIngredients().contains(1)); // Ingredient ID 1
    }

    @Test
    public void testCreateRecipeWithIngredientsButNoAddIngredients() throws IOException, InterruptedException {
        // Create a test ingredient file with ingredients
        createTestIngredientFile(ingredientTestFile);

        // Simulate user input for valid recipe creation
        String input = "My Recipe\n4\ndone\n\n"; // Valid category (Dessert) with one ingredient selected
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe is created successfully
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients selected. Recipe creation canceled."));
    }

    @Test
    public void testCreateRecipeWithMultipleIngredients() throws IOException, InterruptedException {
        // Create a test ingredient file with ingredients
        createTestIngredientFile(ingredientTestFile);

        // Simulate user input for valid recipe creation with multiple ingredients
        String input = "Multi Ingredient Recipe\n2\n1\n2\ndone\n\n"; // Valid category (Appetizer) with two ingredients
        RecipeCosting recipeCosting = simulateUserInput(input);

        List<Recipe> recipes = new ArrayList<>();
        recipeCosting.createRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe is created successfully
        String output = outContent.toString();
        assertTrue(output.contains("Recipe created successfully!"));
        assertEquals(1, recipes.size());
        Recipe createdRecipe = recipes.get(0);
        assertEquals("Multi Ingredient Recipe", createdRecipe.getName());
        assertEquals(2, createdRecipe.getCategory());
        assertEquals(2, createdRecipe.getIngredients().size());
        assertTrue(createdRecipe.getIngredients().contains(1)); // Ingredient ID 1
        assertTrue(createdRecipe.getIngredients().contains(2)); // Ingredient ID 2
    }

    @Test
    public void testEditRecipeNoRecipesAvailable() throws IOException, InterruptedException {
        // Simulate empty recipe list
        List<Recipe> recipes = new ArrayList<>();

        RecipeCosting recipeCosting = simulateUserInput("\n\n");
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available to edit."));
    }

    @Test
    public void testEditRecipeInvalidRecipeId() throws IOException, InterruptedException {
        // Create a test recipe file with one recipe
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate invalid recipe ID input
        String input = "5\n\n\n"; // Invalid ID (no recipe with ID 5)
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Only enter numerical value"));
        assertTrue(output.contains("Press any key to continue..."));
    }

    @Test
    public void testEditRecipeChangeName() throws IOException, InterruptedException {
        // Create a test recipe file
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate changing the name of a recipe
        String input = "1\n1\nNew Recipe Name\n\n"; // Edit recipe ID 1, change name
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe name is updated
        assertEquals("New Recipe Name", recipes.get(0).getName());
        assertTrue(outContent.toString().contains("Recipe updated successfully!"));
    }

    @Test
    public void testEditRecipeChangeCategory() throws IOException, InterruptedException {
        // Create a test recipe file
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate changing the category of a recipe
        String input = "1\n2\n3\n\n"; // Edit recipe ID 1, change category to 3 (Main Course)
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe category is updated
        assertEquals(3, recipes.get(0).getCategory());
        assertTrue(outContent.toString().contains("Recipe updated successfully!"));
    }

    @Test
    public void testEditRecipeChangeCategoryInputError() throws IOException, InterruptedException {
        // Create a test recipe file
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate changing the category of a recipe
        String input = "1\n2\n7\n\n"; // Edit recipe ID 1, change category to 3 (Main Course)
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the recipe category is updated
        assertEquals(1, recipes.get(0).getCategory());
        assertTrue(outContent.toString().contains("Only enter numerical value"));
    }

    @Test
    public void testEditRecipeAddIngredient() throws IOException, InterruptedException {
        // Create test ingredient and recipe files
        createTestIngredientFile(ingredientTestFile);
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate adding an ingredient to a recipe
        String input = "1\n3\n1\n1\ndone\n\n"; // Edit recipe ID 1, add ingredient ID 1
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        // Verify the ingredient is added to the recipe
        assertTrue(recipes.get(0).getIngredients().contains(1));
        assertTrue(outContent.toString().contains("Ingredient added successfully."));
        assertTrue(outContent.toString().contains("Recipe updated successfully!"));
    }

    @Test
    public void testEditRecipeInvalidEditChoice() throws IOException, InterruptedException {
        // Create test recipe file
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate invalid edit choice
        String input = "1\n5\n\n"; // Invalid edit choice
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice. Returning to menu."));
    }

    @Test
    public void testEditRecipeInvalidIngredientIdToAdd() throws IOException, InterruptedException {
        // Create test ingredient and recipe files
        createTestIngredientFile(ingredientTestFile);
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate adding an invalid ingredient ID
        String input = "1\n3\n1\n999\ndone\n\n"; // Invalid ingredient ID 999
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid ingredient ID. Please enter a valid ID from the available ingredients."));
    }

    @Test
    public void testEditRecipeInvalidIngredientIdToRemove() throws IOException, InterruptedException {
        // Create test ingredient and recipe files
        createTestIngredientFile(ingredientTestFile);
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate removing an invalid ingredient ID
        String input = "1\n3\n2\n999\ndone\n\n"; // Invalid ingredient ID 999
        RecipeCosting recipeCosting = simulateUserInput(input);
        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Ingredient 999 not found in the recipe."));
    }

    @Test
    public void testCalculateRecipeCostNoRecipes() throws IOException, InterruptedException {
        // Simulate empty recipe list
        List<Recipe> recipes = new ArrayList<>();
        RecipeCosting recipeCosting = simulateUserInput("\n");

        recipeCosting.calculateRecipeCost(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("No recipes available to calculate cost."));
    }

    @Test
    public void testCalculateRecipeCostInvalidRecipeId() throws IOException, InterruptedException {
        // Create test recipe and ingredient files
        createTestIngredientFile(ingredientTestFile);
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate invalid recipe ID
        String input = "5\n\n"; // Invalid recipe ID
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.calculateRecipeCost(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid recipe ID."));
    }

    @Test
    public void testCalculateRecipeCostWithValidRecipe() throws IOException, InterruptedException {
        // Create test ingredient and recipe files
        createTestIngredientFile(ingredientTestFile);
        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);

        // Simulate valid recipe cost calculation
        String input = "1\n\n"; // Recipe ID 1
        RecipeCosting recipeCosting = simulateUserInput(input);

        recipeCosting.calculateRecipeCost(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("The total cost of the recipe 'Tomato Soup' is:"));
        assertTrue(output.contains("$"));
    }

    @Test
    public void testCalculateRecipeCostIngredientNotFound() throws IOException, InterruptedException {
        // Create test recipe with invalid ingredient ID
        List<Recipe> recipes = new ArrayList<>();
        Recipe recipe = new Recipe("Test Recipe", 1);
        recipe.setIngredients(Arrays.asList(999)); // Non-existent ingredient ID
        recipes.add(recipe);

        RecipeCosting recipeCosting = simulateUserInput("1\n\n\n");
        recipeCosting.calculateRecipeCost(recipes, ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Warning: Ingredient ID 999 not found."));
        assertTrue(output.contains("The total cost of the recipe 'Test Recipe' is: $0,00"));
    }

    @Test
    public void testRecipeCostingBplussTree1() throws IOException, InterruptedException {
        String input = "1\nkgg\n1\n1\n2\n1\n1\ndone\n\n2\n5\n1\ncvb\n2\n5\n2\n2\n2\n5\n3\n1\n1\ndone\n\n2\n7";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe updated successfully!"));
    }

    @Test
    public void testRecipeCostingBplussTree2() throws IOException, InterruptedException {
        String input =
                "1\nsandwich\n1\n1\n2\n1\n1\ndone\n\n1\npasta\n1\n1\n2\n1\n1\ndone\n\n" +
                        "1\nomlet\n1\n1\n2\n1\n1\ndone\n\n1\nerman\n1\n1\n2\n1\n1\ndone\n\n" +
                        "1\nhasan\n1\n1\n2\n1\n1\ndone\n\n1\nelma\n1\n1\n2\n1\n1\ndone\n\n" +
                        "2\n5\n1\ncvb\n2\n5\n2\n2\n2\n5\n3\n1\n1\ndone\n\n2\n7";
        RecipeCosting recipeCosting = simulateUserInput(input);
        createTestIngredientFile(ingredientTestFile);
        createTestRecipeFile(recipeTestFile);

        recipeCosting.recipeCostingMenu(ingredientTestFile, recipeTestFile);

        String output = outContent.toString();
        assertTrue(output.contains("Recipe updated successfully!"));
    }

//    @Test
//    public void testRecipeCostingEditRecipeNoIngredientFound() throws IOException, InterruptedException {
//        String input = "3\n3\n";
//        RecipeCosting recipeCosting = simulateUserInput(input);
//        List<Recipe> recipes = createTestRecipeFile(recipeTestFile);
//
//        recipeCosting.editRecipe(recipes, ingredientTestFile, recipeTestFile);
//
//        String output = outContent.toString();
//        assertTrue(output.contains("No recipes available to edit."));
//    }


}
