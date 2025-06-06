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

    @Test
    public void testEditIngredient() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        String userInput = "1\n1\nOnion\n\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        head = ingredientManagement.editIngredient(head, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        assertEquals("Onion", head.getName());
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient name updated successfully."));

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(1, loadedHead.getId());
        assertEquals("Onion", loadedHead.getName());
    }

    @Test
    public void testEditIngredientInvalidInput() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        String userInput = "1\nabc\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        head = ingredientManagement.editIngredient(head, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        assertEquals("Tomato", head.getName());
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a valid ID."));
    }

    @Test
    public void testEditIngredientNotFound() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;

        String userInput = "1\n2\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        head = ingredientManagement.editIngredient(head, ingredientTestFile);

        // Assert
        assertNotNull(head);
        assertEquals(1, head.getId());
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient with ID 2 not found."));
    }
    @Test
    public void testEditIngredientNoIngredients() throws IOException {
        // Arrange
        head = null;

        String userInput = "1\nOnion\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        head = ingredientManagement.editIngredient(head, ingredientTestFile);

        // Assert
        assertNull(head);
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available to edit."));
    }
    @Test
    public void testRemoveIngredientNoIngredients() throws IOException {
        // Arrange
        head = null;

        // Act
        head = ingredientManagement.removeIngredient(head, 1, ingredientTestFile);

        // Assert
        assertNull(head);
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients to remove."));
    }
    @Test
    public void testIngredientManagementMenuViewIngredients() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "1\n3\n6\n"; // View ingredient and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Current Ingredient:"));
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Name: Tomato"));
        //assertTrue(output.contains("Price: 2.00"));
    }

    @Test
    public void testIngredientManagementMenuRemoveIngredient() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "3\nabc\n\n3\n8\n\n3\n1\n1\n\n6\n"; // Remove ingredient and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient removed successfully."));

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNull(loadedHead);
    }
    @Test
    public void testIngredientManagementMenuRemoveIngredient2XLL() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "3\nabc\n\n3\n8\n\n3\n2\n1\n\n3\n2\n8\n\n6\n"; // Remove ingredient and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient removed successfully."));

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNull(loadedHead);
    }
    @Test
    public void testIngredientManagementMenuRemoveIngredientNoIngredient() throws IOException, InterruptedException {
        String userInput = "3\n\n6\n"; // Remove ingredient and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNull(loadedHead);
    }
    @Test
    public void testIngredientManagementMenuEditIngredient() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "4\n1\n1\nOnion\n\n6\n"; // Edit ingredient and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient name updated successfully."));

        // Verify file was updated correctly
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals(1, loadedHead.getId());
        assertEquals("Onion", loadedHead.getName());
    }

    @Test
    public void testIngredientManagementMenuInvalidChoice() throws IOException, InterruptedException {
        // Arrange
        String userInput = "9\n\nabc\n\n6\n"; // Invalid choice and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);

    }
    @Test
    public void testIngredientManagementMenuViewIngredientsEmpty() throws IOException, InterruptedException {
        // Arrange
        head = null;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "1\n\n6\n"; // Attempt to view ingredients and then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available."));
    }

    @Test
    public void testIngredientManagementMenuNavigateIngredients() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        firstIngredient.setNext(secondIngredient);
        secondIngredient.setPrev(firstIngredient);
        secondIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "1\n1\n2\n3\n6\n"; // View first ingredient, navigate to next, then previous, then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Current Ingredient:"));
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Name: Tomato"));
        //assertTrue(output.contains("Price: 2.00"));
        assertTrue(output.contains("ID: 2"));
        assertTrue(output.contains("Name: Onion"));
        //assertTrue(output.contains("Price: 1.50"));
    }
    @Test
    public void testComputeLPSArray() {
        // Arrange
        String pattern = "ABABAC";

        // Act
        int[] lps = IngredientManagement.computeLPSArray(pattern);

        // Assert
        int[] expectedLPS = {0, 0, 1, 2, 3, 0};
        assertArrayEquals(expectedLPS, lps);
    }

    @Test
    public void testKMPSearchPatternFound() {
        // Arrange
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";

        // Act
        boolean result = IngredientManagement.KMPSearch(text, pattern);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testKMPSearchPatternNotFound() {
        // Arrange
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABAC";

        // Act
        boolean result = IngredientManagement.KMPSearch(text, pattern);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testSearchIngredientByKMPIngredientFound() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        // Act
        ingredientManagement.searchIngredientByKMP(head, "Tomato");

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient found:"));
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Name: Tomato"));
        //assertTrue(output.contains("Price: 2.00"));
    }

    @Test
    public void testSearchIngredientByKMPIngredientNotFound() throws IOException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        firstIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        // Act
        ingredientManagement.searchIngredientByKMP(head, "Onion");

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient 'Onion' not found in the list."));
    }

    @Test
    public void testSearchIngredientByKMPNoIngredients() throws IOException {
        // Arrange
        head = null;

        // Act
        ingredientManagement.searchIngredientByKMP(head, "Tomato");

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("No ingredients available to search."));
    }
    @Test
    public void testAddIngredientValidInput() throws IOException, InterruptedException {
        // Arrange
        String userInput = "2\nOnion\n1.5\n\n6\n"; // Add valid ingredient and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient added successfully."));
        // Verify the ingredient was saved
        Ingredient loadedHead = ingredientManagement.loadIngredientsFromFile(ingredientTestFile);
        assertNotNull(loadedHead);
        assertEquals("Onion", loadedHead.getName());
        assertEquals(1.5f, loadedHead.getPrice(), 0.01);
    }

    @Test
    public void testAddIngredientEmptyName() throws IOException, InterruptedException {
        // Arrange
        String userInput = "2\n\n\nOnion\n1.5\n\n6\n"; // Enter empty name, then valid inputs and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient name cannot be empty. Please try again."));
    }

    @Test
    public void testAddIngredientInvalidPrice() throws IOException, InterruptedException {
        // Arrange
        String userInput = "2\nOnion\n-1.5\n\nOnion\n1.5\n\n6\n"; // Enter invalid price, then valid price and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Price must be greater than zero. Please try again."));
    }

    @Test
    public void testAddIngredientNonNumericPrice() throws IOException, InterruptedException {
        // Arrange
        String userInput = "2\nOnion\nabc\n\nOnion\n1.5\n\n6\n"; // Enter non-numeric price, then valid price and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input. Please enter a valid number for the price."));
    }
    @Test
    public void testIngredientNavigationValidChoices() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        firstIngredient.setNext(secondIngredient);
        secondIngredient.setPrev(firstIngredient);
        secondIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "1\nabc\n\n1\n2\n3\n6\n"; // View first ingredient, navigate to next, then previous, then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Current Ingredient:"));
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Name: Tomato"));
        assertTrue(output.contains("ID: 2"));
        assertTrue(output.contains("Name: Onion"));
    }

    @Test
    public void testIngredientNavigationInvalidChoice() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        Ingredient secondIngredient = new Ingredient();
        secondIngredient.setId(2);
        secondIngredient.setName("Onion");
        secondIngredient.setPrice(1.5f);
        firstIngredient.setNext(secondIngredient);
        secondIngredient.setPrev(firstIngredient);
        secondIngredient.setNext(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "1\n9\n\n3\n6\n"; // Invalid choice, then exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice or no more ingredients in that direction."));
    }
    @Test
    public void testSearchIngredientByValidName() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "5\nTomato\n\n6\n"; // Search for "Tomato" and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient found:"));
        assertTrue(output.contains("Name: Tomato"));
    }

    @Test
    public void testSearchIngredientByEmptyName() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "5\n\n\n6\n"; // Leave search name empty and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Search name cannot be empty."));
    }

    @Test
    public void testSearchIngredientByInvalidName() throws IOException, InterruptedException {
        // Arrange
        Ingredient firstIngredient = new Ingredient();
        firstIngredient.setId(1);
        firstIngredient.setName("Tomato");
        firstIngredient.setPrice(2.0f);
        firstIngredient.setPrev(null);
        head = firstIngredient;
        ingredientManagement.saveIngredientsToFile(head, ingredientTestFile);

        String userInput = "5\nOnion\n\n6\n"; // Search for "Onion" (not in list) and exit
        ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inContent);
        Scanner testScanner = new Scanner(inContent);
        ingredientManagement = new IngredientManagement(new UserAuthentication(testScanner, System.out), testScanner, System.out);

        // Act
        boolean result = ingredientManagement.ingredientManagementMenu(ingredientTestFile);

        // Assert
        assertTrue(result);
        String output = outContent.toString();
        assertTrue(output.contains("Ingredient 'Onion' not found in the list."));
    }


    private void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }
}
