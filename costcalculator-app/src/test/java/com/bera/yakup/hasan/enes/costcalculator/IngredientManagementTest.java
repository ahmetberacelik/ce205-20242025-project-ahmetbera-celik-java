package com.bera.yakup.hasan.enes.costcalculator;

import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.*;

public class IngredientManagementTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private IngredientManagement ingredientManagement;

    private Ingredient head;
    private String ingredientTestFile = "ingredientTestFile.bin";
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
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);
    }
    /**
     * @brief This method is executed after each test method.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setIn(originalIn);
        deleteFile(ingredientTestFile);
    }
    @Test
    public void exitingredientManagementMenuTest() throws IOException, InterruptedException {
        String testInput = "6\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(testInput.getBytes());
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner,System.out);

        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);
        assertTrue(result);
    }
    @Test
    public void testAddIngredient() throws IOException {
        // Arrange
        String name = "Onion";
        float price = 1.5f;
        head = null;

        // Act
        head = ingredientManagement.addIngredient(head, name, price, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        assertEquals(name, head.getName());
        assertEquals(price, head.getPrice(), 0.01);
        assertNull(head.getPrev());
        assertNull(head.getNext());

        // Verify file was written correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(1, loadedHead.getId());
        assertEquals(name, loadedHead.getName());
        assertEquals(price, loadedHead.getPrice(), 0.01);
    }

    @Test
    public void testAddIngredientToExistingList() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        String name = "Onion";
        float price = 1.5f;

        // Act
        head = ingredientManagement.addIngredient(head, name, price, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        assertEquals("Tomato", head.getName());
        assertEquals(2.0f, head.getPrice(), 0.01);
        assertNotNull(head.getNext());
        assertEquals(2, head.getNext().getId());
        assertEquals(name, head.getNext().getName());
        assertEquals(price, head.getNext().getPrice(), 0.01);
        assertEquals(head, head.getNext().getPrev());

        // Verify file was written correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(1, loadedHead.getId());
        assertEquals("Tomato", loadedHead.getName());
        assertEquals(2.0f, loadedHead.getPrice(), 0.01);
        assertNotNull(loadedHead.getNext());
        assertEquals(2, loadedHead.getNext().getId());
        assertEquals(name, loadedHead.getNext().getName());
        assertEquals(price, loadedHead.getNext().getPrice(), 0.01);
    }

    @Test
    public void testSaveIngredientsToFile() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        secondIngredient.setPrev(firstIngredient);
        firstIngredient.setNext(secondIngredient);

        // Act
        boolean result = ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        // Assert
        assertTrue(result);

        // Verify file was written correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(1, loadedHead.getId());
        assertEquals("Tomato", loadedHead.getName());
        assertEquals(2.0f, loadedHead.getPrice(), 0.01);
        assertNotNull(loadedHead.getNext());
        assertEquals(2, loadedHead.getNext().getId());
        assertEquals("Onion", loadedHead.getNext().getName());
        assertEquals(1.5f, loadedHead.getNext().getPrice(), 0.01);
    }

    @Test
    public void testListIngredientsDLL() {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        secondIngredient.setPrev(firstIngredient);
        firstIngredient.setNext(secondIngredient);

        // Act
        boolean result = ingredientManagement.listIngredientsDLL(head);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        //assertTrue(output.contains("ID: 1, Name: Tomato, Price: 2.00"));
        //assertTrue(output.contains("ID: 2, Name: Onion, Price: 1.50"));
    }

    @Test
    public void testListIngredientsXLL() {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        secondIngredient.setPrev(firstIngredient);
        firstIngredient.setNext(secondIngredient);

        // Act
        boolean result = ingredientManagement.listIngredientsXLL(head);

        // Assert
        assertTrue(result);

    }
    @Test
    public void testListIngredientsDLLNoIngredients() {
        // Arrange
        head = null;

        // Act
        boolean result = ingredientManagement.listIngredientsDLL(head);

        // Assert
        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available."));
    }
    @Test
    public void testListIngredientsXLLNoIngredients() {
        // Arrange
        head = null;

        // Act
        boolean result = ingredientManagement.listIngredientsXLL(head);

        // Assert
        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available."));
    }
    @Test
    public void testListIngredients() {
        // Arrange
        String userInput = "1\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        // Act
        boolean result = ingredientManagement.listIngredients(head);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Available Ingredients (DLL):"));
        //assertTrue(output.contains("ID: 1, Name: Tomato, Price: 2.00"));
    }
    @Test
    public void testListIngredientsCase2() {
        // Arrange
        String userInput = "2\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        // Act
        boolean result = ingredientManagement.listIngredients(head);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testListIngredientsInvalidInput() {
        // Arrange
        String userInput = "abc\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.listIngredients(head);

        // Assert
        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
    }

    @Test
    public void testListIngredientsInvalidChoice() {
        // Arrange
        String userInput = "3\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.listIngredients(head);

        // Assert
        assertFalse(result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice. Please try again."));
    }
    @Test
    public void testRemoveIngredient() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        secondIngredient.setPrev(firstIngredient);
        firstIngredient.setNext(secondIngredient);

        // Act
        head = ingredientManagement.removeIngredient(head, 1, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(2, head.getId());
        assertNull(head.getPrev());
        assertNull(head.getNext());
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient with ID 1 removed successfully."));

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(2, loadedHead.getId());
        assertEquals("Onion", loadedHead.getName());
    }

    @Test
    public void testRemoveIngredientNotFound() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        // Act
        head = ingredientManagement.removeIngredient(head, 3, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient with ID 3 not found."));
    }
