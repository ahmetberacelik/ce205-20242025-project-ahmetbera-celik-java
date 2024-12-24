package com.bera.yakup.hasan.enes.costcalculator;

import org.junit.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class CostcalculatorAppTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

  private String ingredientsTestFile = "test_ingredients.bin";
  private String recipesTestFile = "test_recipes.bin";
  private String usersTestFile = "test_users.bin";

  @Before
  public void setUp() throws Exception {
    System.setOut(new PrintStream(outContent));
    System.setIn(originalIn);
  }

  @After
  public void tearDown() throws Exception {
    System.setOut(originalOut);
    System.setIn(originalIn);
    deleteFile(ingredientsTestFile);
    deleteFile(recipesTestFile);
    deleteFile(usersTestFile);
  }

  private void deleteFile(String filePath) throws IOException {
    Files.deleteIfExists(Paths.get(filePath));
  }

  @Test
  public void testCostCalculatorAppMainMenu() throws IOException, InterruptedException {
    // Arrange: Simulate user input to navigate the main menu and exit
    String userInput = "4\n"; // Exit the main menu
    ByteArrayInputStream inContent = new ByteArrayInputStream(userInput.getBytes());
    System.setIn(inContent);

    // Act: Call the main method
    CostcalculatorApp.main(new String[]{});

    // Assert: Verify console output
    String output = outContent.toString();
    assertTrue(output.contains("MAIN MENU"));
    assertTrue(output.contains("Exit Program"));
  }

}
