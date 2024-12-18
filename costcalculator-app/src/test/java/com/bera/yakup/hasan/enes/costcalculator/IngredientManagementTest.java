package com.bera.yakup.hasan.enes.costcalculator;

import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class BudgetPlannerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private BudgetPlanner budgetPlanner;
    private String budgetPlannerTestFile = "budgetPlannerTestFile.bin";
    private String ingredientTestFile = "ingredientTestFile.bin";
    private String recipeCostingTestFile = "recipeCostingTestFile.bin";
    /**
     * @brief This method is executed once before all test methods.
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }
    /**
     * @brief This method is executed once after all test methods.
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    /**
     * @brief This method is executed before each test method.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        Scanner testScanner = new Scanner(System.in);
        budgetPlanner = getBudgetPlanner(testScanner);
    }
    /**
     * @brief This method is executed after each test method.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        deleteFile(budgetPlannerTestFile);
        deleteFile(ingredientTestFile);
        deleteFile(recipeCostingTestFile);

    }
    private static BudgetPlanner getBudgetPlanner(Scanner testScanner) {
        UserAuthentication userAuthentication = new UserAuthentication(testScanner,System.out);
        IngredientManagement ingredientManagement = new IngredientManagement(userAuthentication, testScanner,System.out);
        PriceAdjustment priceAdjustment = new PriceAdjustment(userAuthentication,ingredientManagement, testScanner,System.out);
        RecipeCosting recipeCosting = new RecipeCosting(userAuthentication,priceAdjustment, testScanner,System.out);
        BudgetPlanner budgetPlanner = new BudgetPlanner(userAuthentication,recipeCosting,ingredientManagement,priceAdjustment, testScanner,System.out);
        return budgetPlanner;
    }
    private BudgetPlanner simulateUserInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inContent);
        return getBudgetPlanner(testScanner);
    }
    private void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }
    @Test
    public void exitBudgetPlannerMenuTest() throws IOException, InterruptedException {
        BudgetPlanner budgetPlanner = simulateUserInput("100\n3\n");
        int result = budgetPlanner.budgetPlannerMenu(recipeCostingTestFile, ingredientTestFile);
        Assert.assertEquals(result,1);
    }
    @Test
    public void listRecipesWithPricesTest() throws IOException {
        // Simulate recipes and ingredients files
        List<Recipe> recipes = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();

        // Add mock data
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1);
        ingredient1.setName("Tomato");
        ingredient1.setPrice(2.5f);
        ingredients.add(ingredient1);

        Recipe recipe1 = new Recipe("Tomato Soup", 1);
        recipe1.setIngredients(Collections.singletonList(1));
        recipes.add(recipe1);

        // Write mock data to files
        RecipeCosting recipeCosting = new RecipeCosting(null, null, null, System.out);
        recipeCosting.saveRecipesToFile(recipeCostingTestFile, recipes);

        PriceAdjustment priceAdjustment = new PriceAdjustment(null, null, null, System.out);
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(ingredientTestFile))) {
            for (Ingredient ing : ingredients) {
                writer.writeInt(ing.getId());
                writer.writeUTF(ing.getName());
                writer.writeFloat(ing.getPrice());
            }
        }

        // Test
        BudgetPlanner budgetPlanner = simulateUserInput("");
        int result = budgetPlanner.listRecipesWithPrices(recipeCostingTestFile, ingredientTestFile);
        Assert.assertEquals(1, result);
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Tomato Soup"));
    }
    @Test
    public void listRecipesWithPricesEmptyRecipesTest() throws IOException {
        // Write empty recipes file
        RecipeCosting recipeCosting = new RecipeCosting(null, null, null, System.out);
        recipeCosting.saveRecipesToFile(recipeCostingTestFile, new ArrayList<>());

        // Test
        BudgetPlanner budgetPlanner = simulateUserInput("\n");
        int result = budgetPlanner.listRecipesWithPrices(recipeCostingTestFile, ingredientTestFile);
        Assert.assertEquals(0, result);
        String output = outContent.toString();
        Assert.assertTrue(output.contains("No recipes found."));
    }
    @Test
    public void testViewBudget() throws IOException, InterruptedException {
        // Setup a budget value
        double budget = 150;
        BudgetPlanner budgetPlanner = simulateUserInput("\n");
        // Execute test
        int result = budgetPlanner.viewBudget(budget);

        // Verify
        Assert.assertEquals(1, result);
    }
    @Test
    public void planMealsTest() throws IOException, InterruptedException {
        // Simulate recipes and ingredients files
        List<Recipe> recipes = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();

        // Add mock data
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1);
        ingredient1.setName("Tomato");
        ingredient1.setPrice(2.5f);
        ingredients.add(ingredient1);

        Recipe recipe1 = new Recipe("Tomato Soup", 1);
        recipe1.setIngredients(Collections.singletonList(1));
        recipes.add(recipe1);

        // Write mock data to files
        RecipeCosting recipeCosting = new RecipeCosting(null, null, null, System.out);
        recipeCosting.saveRecipesToFile(recipeCostingTestFile, recipes);

        PriceAdjustment priceAdjustment = new PriceAdjustment(null, null, null, System.out);
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(ingredientTestFile))) {
            for (Ingredient ing : ingredients) {
                writer.writeInt(ing.getId());
                writer.writeUTF(ing.getName());
                writer.writeFloat(ing.getPrice());
            }
        }

        // Simulate user input
        BudgetPlanner budgetPlanner = simulateUserInput("500\n1\ndone\n");

        double updatedBudget = budgetPlanner.planMeals(recipeCostingTestFile, ingredientTestFile, 500.0);
        Assert.assertEquals(497.5, updatedBudget, 0.01);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Tomato Soup"));
        Assert.assertTrue(output.contains("Remaining budget: 497.50"));
    }