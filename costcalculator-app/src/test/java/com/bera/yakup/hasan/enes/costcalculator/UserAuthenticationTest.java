//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bera.yakup.hasan.enes.costcalculator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserAuthenticationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut;
    private final InputStream originalIn;
    private UserAuthentication userAuthentication;
    private String usersTestFile;
    private String usersTestFile2;
    private String usersTestFile3;
    private String ingredientTestFile;
    private String recipesTestFile;
    private String budgetPlannerTestFile;
    private String priceAdjustmentTestFile;

    public UserAuthenticationTest() {
        this.originalOut = System.out;
        this.originalIn = System.in;
        this.usersTestFile = "usersTestFile.bin";
        this.usersTestFile2 = "usersTestFile2.bin";
        this.usersTestFile3 = "usersTestFile3.bin";
        this.ingredientTestFile = "ingredientTestFile.bin";
        this.recipesTestFile = "recipesTestFile.bin";
        this.budgetPlannerTestFile = "budgetPlannerTestFile.bin";
        this.priceAdjustmentTestFile = "priceAdjustmentTestFile.bin";
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(this.outContent));
        Scanner testScanner = new Scanner(System.in);
        this.userAuthentication = new UserAuthentication(testScanner, System.out);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(this.originalOut);
        System.setIn(this.originalIn);
        this.deleteFile(this.usersTestFile);
        this.deleteFile(this.usersTestFile2);
        this.deleteFile(this.usersTestFile3);
        this.deleteFile(this.ingredientTestFile);
        this.deleteFile(this.recipesTestFile);
        this.deleteFile(this.budgetPlannerTestFile);
        this.deleteFile(this.priceAdjustmentTestFile);
    }

    private UserAuthentication simulateUserInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inContent);
        this.userAuthentication = new UserAuthentication(testScanner, System.out);
        return this.userAuthentication;
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

    private void createRecipeFile() throws IOException {
        createIngredientFile();
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(recipesTestFile))) {
            for (int i = 1; i <= 5; i++) {
                // Create a mock Recipe
                Recipe recipe = new Recipe("Recipe" + i, i); // Recipe name and category
                recipe.addIngredient(1); // Add the first ingredient
                recipe.addIngredient(2); // Add the second ingredient

                // Write recipe details to the file
                writer.writeUTF(recipe.getName()); // Recipe name
                writer.writeInt(recipe.getCategory()); // Recipe category
                writer.writeInt(recipe.getIngredientCount()); // Number of ingredients

                for (int ingredientId : recipe.getIngredients()) {
                    writer.writeInt(ingredientId); // Write ingredient IDs
                }
            }
        }
    }

    @Test
    public void exitMainMenuTest() throws IOException, InterruptedException {
        UserAuthentication userAuthentication = this.simulateUserInput("4\n");
        boolean result = userAuthentication.mainMenu(this.usersTestFile, this.ingredientTestFile, this.recipesTestFile);
        Assert.assertTrue(result);
    }

    @Test
    public void testRegisterUserMenu() throws IOException, InterruptedException {
        UserAuthentication userAuthentication = this.simulateUserInput("John\nDoe\njohn.doe@example.com\npassword123\n\n");
        boolean result = userAuthentication.registerUserMenu(this.usersTestFile);
        Assert.assertTrue(result);
        String output = this.outContent.toString();
        Assert.assertTrue(output.contains("User registered successfully"));
        List<User> users = userAuthentication.loadUsers(this.usersTestFile);
        Assert.assertEquals(1L, (long) users.size());
        Assert.assertEquals("John", ((User) users.get(0)).getName());
        Assert.assertEquals("Doe", ((User) users.get(0)).getSurname());
        Assert.assertEquals("john.doe@example.com", ((User) users.get(0)).getEmail());
        Assert.assertEquals("password123", ((User) users.get(0)).getPassword());
        userAuthentication = this.simulateUserInput("John\nDoe\njohn.doe@example.com\npassword123\n\n");
        boolean result2 = userAuthentication.registerUserMenu(this.usersTestFile);
        Assert.assertFalse(result2);
    }

    @Test
    public void testRegisterUser() throws IOException, InterruptedException {
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        UserAuthentication userAuthentication = this.simulateUserInput("\n\n");
        boolean result1 = userAuthentication.registerUser(user, this.usersTestFile);
        Assert.assertTrue(result1);
        String output1 = this.outContent.toString();
        Assert.assertTrue(output1.contains("User registered successfully"));
        this.outContent.reset();
        boolean result2 = userAuthentication.registerUser(user, this.usersTestFile);
        Assert.assertFalse(result2);
        String output2 = this.outContent.toString();
        Assert.assertTrue(output2.contains("User already exists."));
    }

    @Test
    public void testLoginUserMenu() throws IOException, InterruptedException {
        User registeredUser = new User();
        registeredUser.setName("Jane");
        registeredUser.setSurname("Doe");
        registeredUser.setEmail("jane.doe@example.com");
        registeredUser.setPassword("securepassword");
        UserAuthentication userAuthenticationWithEnterInput = this.simulateUserInput("\n");
        userAuthenticationWithEnterInput.registerUser(registeredUser, this.usersTestFile2);
        UserAuthentication userAuthenticationWithInput = this.simulateUserInput("jane.doe@example.com\nsecurepassword\n\n");
        boolean loginMenuSuccess = userAuthenticationWithInput.loginUserMenu(this.usersTestFile2);
        Assert.assertTrue(loginMenuSuccess);
        String outputSuccess = this.outContent.toString();
        Assert.assertTrue(outputSuccess.contains("Login successful."));
        this.outContent.reset();
        UserAuthentication userAuthenticationWithWrongInput = this.simulateUserInput("jane.doe@example.com\nwrongpassword\n\n");
        boolean loginMenuFail = userAuthenticationWithWrongInput.loginUserMenu(this.usersTestFile2);
        Assert.assertFalse(loginMenuFail);
        String outputFail = this.outContent.toString();
        Assert.assertTrue(outputFail.contains("Incorrect email or password."));
    }

    @Test
    public void testLoginUser() throws IOException, InterruptedException {
        User registeredUser = new User();
        registeredUser.setName("Jane");
        registeredUser.setSurname("Doe");
        registeredUser.setEmail("jane.doe@example.com");
        registeredUser.setPassword("securepassword");
        UserAuthentication userAuthenticationWithEnterInput = this.simulateUserInput("\n");
        userAuthenticationWithEnterInput.registerUser(registeredUser, this.usersTestFile3);
        User loginUser = new User();
        loginUser.setEmail("jane.doe@example.com");
        loginUser.setPassword("securepassword");
        UserAuthentication userAuthenticationWithEnterInput2 = this.simulateUserInput("\n");
        boolean loginSuccess = userAuthenticationWithEnterInput2.loginUser(loginUser, this.usersTestFile3);
        Assert.assertTrue(loginSuccess);
        String output = this.outContent.toString();
        Assert.assertTrue(output.contains("Login successful."));
        this.outContent.reset();
        loginUser.setPassword("wrongpassword");
        UserAuthentication userAuthenticationWithEnterInput3 = this.simulateUserInput("\n");
        boolean loginFail = userAuthenticationWithEnterInput3.loginUser(loginUser, this.usersTestFile3);
        Assert.assertFalse(loginFail);
        String outputFail = this.outContent.toString();
        Assert.assertTrue(outputFail.contains("Incorrect email or password."));
    }

    @Test
    public void testUserOperationsExit() throws IOException, InterruptedException {
        UserAuthentication userAuthenticationWithInput = this.simulateUserInput("5\n");
        boolean result = userAuthenticationWithInput.userOperations(this.ingredientTestFile, this.recipesTestFile);
        Assert.assertTrue(result);
    }

    @Test
    public void testUserOperationsCase1() throws IOException, InterruptedException {
        UserAuthentication userAuthenticationWithInput = this.simulateUserInput("1\n6\n5\n");
        boolean result = userAuthenticationWithInput.userOperations(this.ingredientTestFile, this.recipesTestFile);
        Assert.assertTrue(result);
    }

}
