package com.bera.yakup.hasan.enes.costcalculator;

import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class PriceAdjustmentTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private PriceAdjustment priceAdjustment;
    private String ingredientTestFile = "ingredientTestFile.bin";

    /**
     * @throws Exception
     * @brief This method is executed once before all test methods.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws Exception
     * @brief This method is executed once after all test methods.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws Exception
     * @brief This method is executed before each test method.
     */
    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        Scanner testScanner = new Scanner(System.in);
        priceAdjustment = getPriceAdjustment(testScanner);
    }

    /**
     * @throws Exception
     * @brief This method is executed after each test method.
     */
    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        deleteFile(ingredientTestFile);
    }

    private static PriceAdjustment getPriceAdjustment(Scanner testScanner) {
        UserAuthentication userAuthentication = new UserAuthentication(testScanner, System.out);
        IngredientManagement ingredientManagement = new IngredientManagement(userAuthentication, testScanner, System.out);
        PriceAdjustment priceAdjustment = new PriceAdjustment(userAuthentication, ingredientManagement, testScanner, System.out);
        return priceAdjustment;
    }

    private PriceAdjustment simulateUserInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inContent);
        return getPriceAdjustment(testScanner);
    }

    private void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    private void createIngredientFile() throws IOException {
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(ingredientTestFile))) {
            for (int i = 1; i <= 5; i++) {
                writer.writeInt(i);  // Ingredient ID
                writer.writeUTF("Ingredient" + i);  // Ingredient Name
                writer.writeFloat(15.0f * i);  // Ingredient Price
            }
        }
    }

    @Test
    public void adjustIngredientPriceSuccessTest() throws IOException, InterruptedException {
        // Simulate user input for ingredient ID, algorithm choice, and new price
        String simulatedInput = "1\n1\n20\n";  // Ingredient ID 1, Linear Probing, new price 20
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);

        // Create a sample ingredient file with one ingredient
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(ingredientTestFile))) {
            writer.writeInt(1);  // Ingredient ID
            writer.writeUTF("Tomato");  // Ingredient Name
            writer.writeFloat(15.0f);  // Ingredient Price
        }

        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);

        // Verify the new price in the file
        try (DataInputStream reader = new DataInputStream(new FileInputStream(ingredientTestFile))) {
            int id = reader.readInt();
            String name = reader.readUTF();
            float price = reader.readFloat();

            Assert.assertEquals(1, id);
            Assert.assertEquals("Tomato", name);
            //Assert.assertEquals(20.5f, price, 0.01);
        }
    }

    @Test
    public void adjustIngredientPriceInvalidIdTest() throws IOException, InterruptedException {
        // Simulate user input for an invalid ingredient ID
        String simulatedInput = "-1\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);

        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(0, result);
    }

    @Test
    public void adjustIngredientPriceMenuTest() throws IOException, InterruptedException {
        // Simulate user input for an invalid ingredient ID
        //Remove the ingredient file
        deleteFile(ingredientTestFile);
        String simulatedInput = "qwe\n\n1\n\n2\n\n4\n\n3\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);

        int result = priceAdjustment.adjustIngredientPriceMenu(ingredientTestFile);
        Assert.assertEquals(1, result);
    }

    @Test
    public void printIngredientsToConsole_NoIngredient_Test() throws IOException, InterruptedException {
        //Remove the ingredient file
        deleteFile(ingredientTestFile);

        String simulatedInput = "\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.printIngredientsToConsole(ingredientTestFile);
        Assert.assertEquals(0, result);
    }

    @Test
    public void printIngredientsToConsole_ValidIngredients_Test() throws IOException, InterruptedException {
        //Remove the ingredient file
        createIngredientFile();
        int result = priceAdjustment.printIngredientsToConsole(ingredientTestFile);
        Assert.assertEquals(5, result);
    }

    @Test
    public void adjustIngredientPriceValidUpdateTest() throws IOException, InterruptedException {
        // Simulate user input for ingredient ID, algorithm choice, and new price
        String simulatedInput = "1\n1\n20.5\n";  // Ingredient ID 1, Linear Probing, new price 20.5
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        // Create a sample ingredient file with one ingredient
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(ingredientTestFile))) {
            writer.writeInt(1);  // Ingredient ID
            writer.writeUTF("Tomato");  // Ingredient Name
            writer.writeFloat(15.0f);  // Ingredient Price
        }
        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);
        Assert.assertTrue(outContent.toString().contains("The ingredient was successfully updated"));
        // Verify the new price in the file
        try (DataInputStream reader = new DataInputStream(new FileInputStream(ingredientTestFile))) {
            int id = reader.readInt();
            String name = reader.readUTF();
            float price = reader.readFloat();
            Assert.assertEquals(1, id);
            Assert.assertEquals("Tomato", name);
            Assert.assertEquals(20.5f, price, 0.01);
        }
    }
    @Test
    public void resetPrice_InvalidIngredientId_Test() throws IOException, InterruptedException {
        createIngredientFile();

        String simulatedInput = "qwe\n\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.resetIngredientPrice(ingredientTestFile);
        Assert.assertEquals(-1, result);
    }
    @Test
    public void resetPrice_ValidIngredientId_Test() throws IOException, InterruptedException {
        createIngredientFile();

        String simulatedInput = "1\n\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.resetIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);
    }

    @Test
    public void adjustIngredientPrice_LineerProbing_Test() throws IOException, InterruptedException {
        createIngredientFile();

        String simulatedInput = "qwe\n\n1\n1\n111\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);
    }
    @Test
    public void adjustIngredientPrice_QuadraticProbingSearch_Test() throws IOException, InterruptedException {
        createIngredientFile();

        String simulatedInput = "1\n2\n222\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);
    }
    @Test
    public void adjustIngredientPrice_DoubleHashingSearch_Test() throws IOException, InterruptedException {
        createIngredientFile();

        String simulatedInput = "1\n3\n333\n";  // Invalid Ingredient ID
        PriceAdjustment priceAdjustment = simulateUserInput(simulatedInput);
        int result = priceAdjustment.adjustIngredientPrice(ingredientTestFile);
        Assert.assertEquals(1, result);
    }
