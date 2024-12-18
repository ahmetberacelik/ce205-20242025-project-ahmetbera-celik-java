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

}
