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

