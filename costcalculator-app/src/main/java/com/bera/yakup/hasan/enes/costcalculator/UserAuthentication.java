/**
 * @file UserAuthentication.java
 * @brief This file contains the definition of the UserAuthentication class and its methods.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.io.*;
import java.util.*;

/**
 * @class UserAuthentication
 * @brief Manages user authentication, registration, and menu navigation for various operations.
 *
 * The UserAuthentication class provides functionality to handle user input, manage menus,
 * and facilitate operations such as login, registration, and guest/user-specific actions.
 */
public class UserAuthentication {

    private Scanner scanner; ///< Scanner object for user input.
    private PrintStream out; ///< PrintStream object for output.
    private IngredientManagement ingredientManagement; ///< Manages ingredient-related operations.
    private RecipeCosting recipeCosting; ///< Handles recipe costing operations.
    private BudgetPlanner budgetPlanner; ///< Manages budget planning functionalities.
    private PriceAdjustment priceAdjustment; ///< Adjusts ingredient prices.
    private XORNode head; // Add this as a class field

    /**
     * @brief Constructor for UserAuthentication.
     * @param scanner Scanner object for user input.
     * @param out PrintStream object for output.
     */
    public UserAuthentication(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
        this.head = null;
        this.ingredientManagement = new IngredientManagement(this, scanner, out);
        this.priceAdjustment = new PriceAdjustment(this, ingredientManagement, scanner, out);
        this.recipeCosting = new RecipeCosting(this, priceAdjustment, scanner, out);
        this.budgetPlanner = new BudgetPlanner(this, recipeCosting, ingredientManagement, priceAdjustment, scanner, out);
    }

    /**
     * @brief Clears the console screen.
     * @throws InterruptedException If the thread is interrupted while waiting.
     * @throws IOException If an I/O error occurs.
     */
    public void clearScreen() throws InterruptedException, IOException {
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem.contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            out.print("\033[H\033[2J");
            out.flush();
        }
    }

    /**
     * @brief Waits for user input to continue the program.
     * @details Displays a message prompting the user to press any key to continue.
     */
    public void enterToContinue() {
        out.println("Press any key to continue...");
        scanner.nextLine();
    }

    /**
     * @brief Handles input errors by displaying an error message.
     * @return Always returns false to indicate an input error.
     */
    public boolean handleInputError() {
        out.println("Only enter numerical value");
        return false;
    }

    /**
     * @brief Gets input from the user as an integer.
     * @return The integer input by the user, or -2 if the input is invalid.
     * @throws InterruptedException If interrupted while processing.
     * @throws IOException If an I/O error occurs.
     */
    public int getInput() throws InterruptedException, IOException {
        String input = scanner.nextLine().trim();
        if (input.matches("-?\\d+")) {
            return Integer.parseInt(input);
        } else {
            return -2;
        }
    }

    /**
     * @brief Prints the main menu options to the console.
     */
    public void printMainMenu() {
        out.println("\n+---------------------------------------+");
        out.println("|           MAIN MENU                   |");
        out.println("+---------------------------------------+");
        out.println("| 1. Login                              |");
        out.println("| 2. Register                           |");
        out.println("| 3. Guest Operations                   |");
        out.println("| 4. Exit Program                       |");
        out.println("| 5. View Users (ForAdmins)             |");
        out.println("+---------------------------------------+");
        out.print("\nPlease enter a number to select: ");
    }

    /**
     * @brief Prints the user operations menu options.
     * @throws InterruptedException If interrupted while processing.
     * @throws IOException If an I/O error occurs.
     */
    public void printUserMenu() throws InterruptedException, IOException {
        clearScreen();
        out.println("\n+---------------------------------------+");
        out.println("|         USER OPERATIONS MENU          |");
        out.println("+---------------------------------------+");
        out.println("| 1. Ingredient Management              |");
        out.println("| 2. Recipe Costing                     |");
        out.println("| 3. Price Adjustment                   |");
        out.println("| 4. Budget Planner                     |");
        out.println("| 5. Exit                               |");
        out.println("+---------------------------------------+");
        out.print("\nPlease enter a number to select: ");
    }

    /**
     * @brief Prints the guest operations menu options.
     */
    public void printGuestMenu() {
        out.println("\n+---------------------------------------+");
        out.println("|         GUEST OPERATIONS MENU         |");
        out.println("+---------------------------------------+");
        out.println("| 1. View Ingredients                   |");
        out.println("| 2. View Recipes                       |");
        out.println("| 3. Exit                               |");
        out.println("+---------------------------------------+");
        out.print("\nPlease enter a number to select: ");
    }

    /**
     * @brief Loads a list of users from a binary file.
     * @param pathFileUsers The path to the binary file containing user data.
     * @return A list of `User` objects loaded from the file.
     * @throws FileNotFoundException If the file is not found.
     * @throws IOException If an I/O error occurs during reading.
     */
    public List<User> loadUsers(String pathFileUsers) throws FileNotFoundException, IOException {
        List<User> users = new ArrayList<>();
        if (new File(pathFileUsers).exists()) {
            try (DataInputStream reader = new DataInputStream(new FileInputStream(pathFileUsers))) {
                while (reader.available() > 0) {
                    User user = new User();
                    user.setId(reader.readInt());
                    user.setName(reader.readUTF());
                    user.setSurname(reader.readUTF());
                    user.setEmail(reader.readUTF());
                    user.setPassword(reader.readUTF());
                    users.add(user);
                }
            }
        }
        return users;
    }

    /**
     * @brief Generates a new unique user ID.
     * @param users List of existing users.
     * @return A new unique user ID.
     */
    public int getNewUserId(List<User> users) {
        return users.size() + 1;
    }

    /**
     * @brief Registers a new user and saves the data to a file.
     * @param user The user to register.
     * @param pathFileUser The file path to save the user data.
     * @return True if registration is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean registerUser(User user, String pathFileUser) throws IOException, InterruptedException {
        List<User> users = loadUsers(pathFileUser);
        for (User existingUser : users) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                out.println("User already exists.");
                enterToContinue();
                return false;
            }
        }
        user.setId(getNewUserId(users));
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File(pathFileUser), true))) {
            writer.writeInt(user.getId());
            writer.writeUTF(user.getName());
            writer.writeUTF(user.getSurname());
            writer.writeUTF(user.getEmail());
            writer.writeUTF(user.getPassword());
        }
        out.println("User registered successfully");
        enterToContinue();
        return true;
    }

    /**
     * @brief Displays the registration menu and prompts for user information.
     * @param pathFileUsers The file path to save the registered user data.
     * @return True if registration menu is successful.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean registerUserMenu(String pathFileUsers) throws IOException, InterruptedException {
        clearScreen();
        User newUser = new User();
        out.print("Enter Name: ");
        newUser.setName(scanner.nextLine());
        out.print("Enter Surname: ");
        newUser.setSurname(scanner.nextLine());
        out.print("Enter email: ");
        newUser.setEmail(scanner.nextLine());
        out.print("Enter password: ");
        newUser.setPassword(scanner.nextLine());
        return registerUser(newUser, pathFileUsers);
    }

    /**
     * @brief Attempts to log in a user with the provided credentials.
     * @param loginUser The user attempting to log in.
     * @param pathFileUsers The file path to read the user data from.
     * @return True if login is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean loginUser(User loginUser, String pathFileUsers) throws IOException, InterruptedException {
        List<User> users = loadUsers(pathFileUsers);
        for (User user : users) {
            if (user.getEmail().equals(loginUser.getEmail()) && user.getPassword().equals(loginUser.getPassword())) {
                out.println("Login successful.");
                enterToContinue();
                return true;
            }
        }
        out.println("Incorrect email or password.");
        enterToContinue();
        return false;
    }

    /**
     * @brief Displays the login menu and prompts for user credentials.
     * @param pathFileUsers The file path to read the user data from.
     * @return True if login is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean loginUserMenu(String pathFileUsers) throws IOException, InterruptedException {
        clearScreen();
        User userLogin = new User();
        out.print("Enter email: ");
        userLogin.setEmail(scanner.nextLine());
        out.print("Enter password: ");
        userLogin.setPassword(scanner.nextLine());
        return loginUser(userLogin, pathFileUsers);
    }

    /**
     * @brief Prints available recipes to the console.
     * @param pathFileIngredients The file path to the ingredients data.
     * @param pathFileRecipes The file path to the recipes data.
     * @return 1 if successful, -1 if no recipes exist.
     * @throws IOException If an I/O error occurs.
     */
    public int printRecipesToConsole(String pathFileIngredients, String pathFileRecipes) throws IOException {
        if (new File(pathFileRecipes).exists()) {
            try (DataInputStream fileReader = new DataInputStream(new FileInputStream(pathFileRecipes))) {
                Ingredient ingredients = ingredientManagement.loadIngredientsFromFile(pathFileIngredients);
                out.println("\nAvailable Recipes:");
                int id = 1;

                while (fileReader.available() > 0) {
                    String recipeName = fileReader.readUTF();
                    out.println(id++ + ") " + recipeName);

                    int category = fileReader.readInt();
                    switch (category) {
                        case 1:
                            out.println("   Category: Soup");
                            break;
                        case 2:
                            out.println("   Category: Appetizer");
                            break;
                        case 3:
                            out.println("   Category: Main Course");
                            break;
                        case 4:
                            out.println("   Category: Dessert");
                            break;
                        default:
                            out.println("   Category: Unknown");
                            break;
                    }

                    int ingredientCount = fileReader.readInt();
                    out.print("   Ingredients: ");
                    for (int i = 0; i < ingredientCount; i++) {
                        int ingredientId = fileReader.readInt();
                        Ingredient current = ingredients;
                        Ingredient ingredient = null;
                        while (current != null) {
                            if (current.getId() == ingredientId) {
                                ingredient = current;
                                break;
                            }
                            current = current.getNext();
                        }
                        if (ingredient != null) {
                            out.print(ingredient.getName() + ", ");
                        }
                    }
                    out.println();
                }
            }
        } else {
            out.println("There are no recipes.");
            enterToContinue();
            return -1;
        }

        return 1;
    }

    /**
     * @brief Handles guest operations menu and actions.
     * @param pathFileIngredients The file path to the ingredients data.
     * @param pathFileRecipes The file path to the recipes data.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public void guestOperations(String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        int choice;
        while (true) {
            clearScreen();
            printGuestMenu();
            choice = getInput();
            if (choice == -2) {
                handleInputError();
                enterToContinue();
                continue;
            }
            switch (choice) {
                case 1:
                    clearScreen();
                    int size = priceAdjustment.printIngredientsToConsole(pathFileIngredients);
                    if (size > 0) {
                        enterToContinue();
                    }
                    break;
                case 2:
                    clearScreen();
                    int res = printRecipesToConsole(pathFileIngredients, pathFileRecipes);
                    if (res == 1) {
                        enterToContinue();
                    }
                    break;
                case 3:
                    out.println("Exiting Guest Operations...");
                    return;
                default:
                    out.println("Invalid choice. Please try again.");
                    enterToContinue();
                    break;
            }
        }
    }

    /**
     * @brief Handles user operations menu and actions.
     * @param pathFileIngredients The file path to the ingredients data.
     * @param pathFileRecipes The file path to the recipes data.
     * @return True if user exits the operations menu.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean userOperations(String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        int choice;
        while (true) {
            printUserMenu();
            choice = getInput();
            if (choice == -2) {
                handleInputError();
                enterToContinue();
                continue;
            }
            switch (choice) {
                case 1:
                    ingredientManagement.ingredientManagementMenu(pathFileIngredients);
                    break;
                case 2:
                    recipeCosting.recipeCostingMenu(pathFileIngredients, pathFileRecipes);
                    break;
                case 3:
                    priceAdjustment.adjustIngredientPriceMenu(pathFileIngredients);
                    break;
                case 4:
                    budgetPlanner.budgetPlannerMenu(pathFileRecipes, pathFileIngredients);
                    break;
                case 5:
                    out.println("Exiting User Operations...");
                    return true;
                default:
                    clearScreen();
                    out.println("Invalid choice. Please try again.");
                    enterToContinue();
                    break;
            }
        }
    }
    /**
     * @brief Displays the main menu and handles menu actions.
     * @param pathFileUsers The file path to user data.
     * @param pathFileIngredients The file path to ingredient data.
     * @param pathFileRecipes The file path to recipe data.
     * @return True if the program should exit.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public boolean mainMenu(String pathFileUsers, String pathFileIngredients, String pathFileRecipes) throws IOException, InterruptedException {
        int choice;
        while (true) {
            clearScreen();
            printMainMenu();
            choice = getInput();
            if (choice == -2) {
                handleInputError();
                enterToContinue();
                continue;
            }
            switch (choice) {
                case 1:
                    clearScreen();
                    if (loginUserMenu(pathFileUsers)) {
                        userOperations(pathFileIngredients, pathFileRecipes);
                    }
                    break;
                case 2:
                    clearScreen();
                    registerUserMenu(pathFileUsers);
                    break;
                case 3:
                    clearScreen();
                    guestOperations(pathFileIngredients, pathFileRecipes);
                    break;
                case 4:
                    out.println("Exit Program");
                    return true;
                case 5:
                    clearScreen();
                    XORNode userList = loadUsersIntoXORList(pathFileUsers);
                    if (userList == null) {
                        out.println("No users found to display.");
                        enterToContinue();
                    } else {
                        viewUsers(userList);
                    }
                    break;
                default:
                    out.println("Invalid choice. Please try again.");
                    enterToContinue();
                    break;
            }
        }
    }

    /**
     * @brief Inserts a user into the XOR doubly linked list.
     * @param user User data to insert.
     */
    public void insertXORNode(User user) {
        XORNode newNode = new XORNode(user);
        if (head == null) {
            head = newNode;
            return;
        }

        XORNode current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }

        current.setNext(newNode);
        newNode.setPrev(current);
    }

    /**
     * @brief Displays all users stored in the linked list with navigation options.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If interrupted during processing.
     */
    public void viewUsers(XORNode head) throws IOException, InterruptedException {
        if (head == null) {
            out.println("No users available to display.");
            enterToContinue();
            return;
        }

        XORNode current = head;
        while (true) {
            clearScreen();
            out.println("+---------------------------------------+");
            out.println("|               USERS                   |");
            out.println("+---------------------------------------+");
            out.println("ID: " + current.getUser().getId());
            out.println("Name: " + current.getUser().getName() + " " + current.getUser().getSurname());
            out.println("Email: " + current.getUser().getEmail());
            out.println("+---------------------------------------+");
            out.println("1. Next");
            out.println("2. Previous");
            out.println("3. Exit");
            out.print("Enter your choice: ");

            int choice = getInput();
            if (choice == -2) {
                handleInputError();
                enterToContinue();
                continue;
            }

            switch (choice) {
                case 1: // Move to next node
                    if (current.getNext() != null) {
                        current = current.getNext();
                    } else {
                        out.println("You are at the last user.");
                        enterToContinue();
                    }
                    break;

                case 2: // Move to previous node
                    if (current.getPrev() != null) {
                        current = current.getPrev();
                    } else {
                        out.println("You are at the first user.");
                        enterToContinue();
                    }
                    break;

                case 3: // Exit
                    out.println("Exiting user view...");
                    return;

                default:
                    out.println("Invalid choice. Please try again.");
                    enterToContinue();
                    break;
            }
        }
    }

    /**
     * @brief Loads users from a file into the linked list structure.
     * @param pathFileUsers Path to the file containing user data.
     * @throws IOException If an I/O error occurs.
     */
    public void loadUsersIntoList(String pathFileUsers) throws IOException {
        List<User> users = loadUsers(pathFileUsers);
        for (User user : users) {
            insertXORNode(user);
        }
    }

    /**
     * @brief Loads users from a file and constructs an XOR linked list.
     * @param pathFileUsers Path to the file containing user data.
     * @return Head of the XOR linked list, or null if no users exist.
     * @throws IOException If an I/O error occurs.
     */
    public XORNode loadUsersIntoXORList(String pathFileUsers) throws IOException {
        List<User> users = loadUsers(pathFileUsers);
        if (users.isEmpty()) {
            return null;
        }
        
        XORNode head = null;
        for (User user : users) {
            XORNode newNode = new XORNode(user);
            if (head == null) {
                head = newNode;
            } else {
                XORNode current = head;
                while (current.getNext() != null) {
                    current = current.getNext();
                }
                current.setNext(newNode);
                newNode.setPrev(current);
            }
        }
        return head;
    }
}