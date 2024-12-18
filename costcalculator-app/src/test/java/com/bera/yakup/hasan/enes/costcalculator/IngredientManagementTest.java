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