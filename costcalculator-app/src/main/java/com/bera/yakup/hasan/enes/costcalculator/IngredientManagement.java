package com.bera.yakup.hasan.enes.costcalculator;

import java.io.*;
import java.util.*;

public class IngredientManagement {
    private Scanner scanner;
    private PrintStream out;
    private UserAuthentication userAuth;

    public IngredientManagement(UserAuthentication userAuth, Scanner scanner, PrintStream out) {
        this.userAuth = userAuth;
        this.scanner = scanner;
        this.out = out;
    }

    public HuffmanTreeNode constructHuffmanTree(int[] frequencies) {
        PriorityQueue<HuffmanTreeNode> queue = new PriorityQueue<>((a, b) -> a.getFrequency() - b.getFrequency());

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                queue.add(new HuffmanTreeNode((char) i, frequencies[i]));
            }
        }

        while (queue.size() > 1) {
            HuffmanTreeNode left = queue.poll();
            HuffmanTreeNode right = queue.poll();
            HuffmanTreeNode merged = new HuffmanTreeNode('\0', left.getFrequency() + right.getFrequency());
            merged.setLeft(left);
            merged.setRight(right);
            queue.add(merged);
        }

        return queue.poll();
    }

    public void generateHuffmanCodes(HuffmanTreeNode root, String currentCode, String[] codes) {
        if (root == null)
            return;

        if (root.getLeft() == null && root.getRight() == null) {
            codes[root.getCharacter()] = currentCode;
            return;
        }

        generateHuffmanCodes(root.getLeft(), currentCode + "0", codes);
        generateHuffmanCodes(root.getRight(), currentCode + "1", codes);
    }

    public String encodeString(String input, String[] codes) {
        StringBuilder encodedStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            encodedStr.append(codes[ch]);
        }
        return encodedStr.toString();
    }

    public String decodeString(HuffmanTreeNode root, String encodedStr) {
        StringBuilder decodedStr = new StringBuilder();
        HuffmanTreeNode current = root;
        for (char bit : encodedStr.toCharArray()) {
            current = (bit == '0') ? current.getLeft() : current.getRight();

            if (current.getLeft() == null && current.getRight() == null) {
                decodedStr.append(current.getCharacter());
                current = root;
            }
        }
        return decodedStr.toString();
    }

    public int[] countFrequencies(String input) {
        int[] frequencies = new int[256];
        for (char ch : input.toCharArray()) {
            frequencies[ch]++;
        }
        return frequencies;
    }

    public Ingredient addIngredient(Ingredient head, String name, float price, String filePath) throws IOException {

        // Step 1: Count frequencies
        int[] freq = countFrequencies(name);

        // Step 2: Construct Huffman Tree
        HuffmanTreeNode root = constructHuffmanTree(freq);

        // Step 3: Generate Huffman Codes
        String[] codes = new String[256];
        generateHuffmanCodes(root, "", codes);

        // Step 4: Encode and Decode Name
        String encodedName = encodeString(name, codes);
        String decodedName = decodeString(root, encodedName);

        // Step 5: Create new Ingredient
        Ingredient newIngredient = new Ingredient();
        int newId = 1;

        if (head != null) {
            Ingredient temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }
            newId = temp.getId() + 1;
        }

        newIngredient.setId(newId);
        newIngredient.setName(decodedName);
        newIngredient.setPrice(price);
        newIngredient.setPrev(null);
        newIngredient.setNext(null);

        if (head == null) {
            head = newIngredient;
        } else {
            Ingredient temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }
            temp.setNext(newIngredient);
            newIngredient.setPrev(temp);
        }

        // Step 6: Save Ingredients to File
        saveIngredientsToFile(head, filePath);

        return head;
    }

    public boolean saveIngredientsToFile(Ingredient head, String filePath) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath))) {
            Ingredient temp = head;

            while (temp != null) {
                out.writeInt(temp.getId());
                out.writeUTF(temp.getName());
                out.writeFloat(temp.getPrice());
                temp = temp.getNext();
            }
            return true;
        }
    }

    public boolean listIngredientsDLL(Ingredient head) {
        if (head == null) {
            out.println("No ingredients available.");
            return false;
        }

        Ingredient current = head;
        out.println("+----------------------------+");
        out.println("|Available Ingredients (DLL):|");
        out.println("+----------------------------+");

        while (current != null) {
            out.println("--------------------------------------------------------------------");
            out.printf("ID: %d, Name: %s, Price: %.2f\n", current.getId(), current.getName(), current.getPrice());
            current = current.getNext();
        }
        out.println("--------------------------------------------------------------------");
        return true;
    }

    public boolean listIngredientsXLL(Ingredient head) {
        if (head == null) {
            out.println("No ingredients available.");
            return false;
        }

        Ingredient current = head;
        out.println("+----+----------------------+------------+----------------------+--------+");
        out.println("| ID | Name                 | Price      | Next/Prev            | Price  |");
        out.println("+----+----------------------+------------+----------------------+--------+");

        while (current != null) {
            out.printf("| %-2d | %-20s | %-6.2f |", current.getId(), current.getName(), current.getPrice());

            if (current.getNext() != null) {
                out.printf(" %-20s | %-6.2f |\n", current.getNext().getName(), current.getNext().getPrice());
            } else {
                out.printf(" %-20s | %-6s |\n", "-", "-");
            }

            if (current.getPrev() != null) {
                out.printf("|    | %-20s | %-6s | %-20s | %-6.2f |\n", "", "", current.getPrev().getName(),
                        current.getPrev().getPrice());
            }

            out.println("+----+----------------------+------------+----------------------+--------+");
            current = current.getNext();
        }
        return true;
    }

    public boolean listIngredients(Ingredient head) {
        out.println("+--------------------------------------+");
        out.println("|            LIST TYPE MENU            |");
        out.println("+--------------------------------------+");
        out.println("| 1. DLL (Doubly Linked List)          |");
        out.println("| 2. XLL (Extended Linked List)        |");
        out.println("+--------------------------------------+");
        out.print("Enter your choice: ");

        int choice;
        try {
            choice = scanner.nextInt();
        } catch (Exception e) {
            out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return false;
        }

        switch (choice) {
            case 1:
                return listIngredientsDLL(head);
            case 2:
                return listIngredientsXLL(head);
            default:
                out.println("Invalid choice. Please try again.");
                return false;
        }
    }

    public Ingredient loadIngredientsFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        Ingredient head = null;
        Ingredient tail = null;

        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            while (in.available() > 0) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setId(in.readInt());
                newIngredient.setName(in.readUTF());
                newIngredient.setPrice(in.readFloat());
                newIngredient.setPrev(tail);
                newIngredient.setNext(null);

                if (head == null) {
                    head = newIngredient;
                } else {
                    tail.setNext(newIngredient);
                }
                tail = newIngredient;
            }
        }
        return head;
    }

    public Ingredient removeIngredient(Ingredient head, int id, String filePath) throws IOException {
        if (head == null) {
            out.println("No ingredients to remove.");
            return head;
        }

        Ingredient current = head;

        // Find the ingredient with the specified ID
        while (current != null && current.getId() != id) {
            current = current.getNext();
        }

        if (current == null) {
            out.printf("Ingredient with ID %d not found.\n", id);
            return head;
        }

        // Remove the ingredient from the list
        if (current.getPrev() != null) {
            current.getPrev().setNext(current.getNext());
        } else {
            head = current.getNext();
        }

        if (current.getNext() != null) {
            current.getNext().setPrev(current.getPrev());
        }

        out.printf("Ingredient with ID %d removed successfully.\n", id);

        // Save updated list to file
        saveIngredientsToFile(head, filePath);
        return head;
    }

    public Ingredient editIngredient(Ingredient head, String filePath) throws IOException {
        if (head == null) {
            out.println("No ingredients available to edit.");
            return head;
        }

        // Display the list of ingredients
        listIngredients(head);

        // Prompt for the ID of the ingredient to edit
        out.print("Enter the ID of the ingredient to edit: ");
        int id;
        try {
            id = scanner.nextInt();
        } catch (Exception e) {
            out.println("Invalid input. Please enter a valid ID.");
            scanner.nextLine(); // Clear invalid input
            return head;
        }

        // Find the ingredient with the specified ID
        Ingredient current = head;
        while (current != null && current.getId() != id) {
            current = current.getNext();
        }

        if (current == null) {
            out.printf("Ingredient with ID %d not found.\n", id);
            return head;
        }

        // Get the new name for the ingredient
        scanner.nextLine(); // Clear the buffer
        out.print("Enter the new name for the ingredient: ");
        String newName = scanner.nextLine();

        // Validate the new name
        if (newName.matches(".*\\d.*") || newName.isEmpty()) {
            out.println("Invalid ingredient name. Please enter a valid name without numbers.");
            return head;
        }

        // Update the ingredient's name
        current.setName(newName);
        out.println("Ingredient name updated successfully.");

        // Save updated ingredient list to file
        saveIngredientsToFile(head, filePath);
        return head;
    }

    public static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static boolean KMPSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPSArray(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                return true; // Pattern found
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    public void searchIngredientByKMP(Ingredient head, String searchName) {
        if (head == null) {
            out.println("No ingredients available to search.");
            return;
        }

        Ingredient current = head;
        boolean found = false;

        while (current != null) {
            if (KMPAlgorithm.KMPSearch(current.getName(), searchName)) {
                out.println("Ingredient found:");
                out.printf("ID: %d\n", current.getId());
                out.printf("Name: %s\n", current.getName());
                out.printf("Price: %.2f\n", current.getPrice());
                found = true;
                break;
            }
            current = current.getNext();
        }

        if (!found) {
            out.printf("Ingredient '%s' not found in the list.\n", searchName);
        }
    }

    public void printIngredientManagementMenu() {
        out.println("\n+--------------------------------------+");
        out.println("|       INGREDIENT MANAGEMENT MENU     |");
        out.println("+--------------------------------------+");
        out.println("| 1. View Ingredients                  |");
        out.println("| 2. Add Ingredient                    |");
        out.println("| 3. Remove Ingredient                 |");
        out.println("| 4. Edit Ingredient                   |");
        out.println("| 5. Search Ingredient by Name (KMP)   |");
        out.println("| 6. Exit                              |");
        out.println("+--------------------------------------+");
        out.print("Please enter a number to select: ");
    }

    public void printIngredientViewMenu() {
        out.println("+--------------------------------------+");
        out.println("| 1. Next                              |");
        out.println("| 2. Previous                          |");
        out.println("| 3. Exit View                         |");
        out.println("+--------------------------------------+");
        out.print("Please enter a number to select: ");
    }

    public boolean ingredientManagementMenu(String filePath) throws IOException, InterruptedException {
        Ingredient head = loadIngredientsFromFile(filePath);
        while (true) {
            userAuth.clearScreen();
            printIngredientManagementMenu();
            int choice = userAuth.getInput();

            if (choice == -2) {
                userAuth.handleInputError();
                userAuth.enterToContinue();
                continue;
            }

            switch (choice) {
                case 1:
                    // View Ingredients
                    if (head == null) {
                        out.println("No ingredients available.");
                        userAuth.enterToContinue();
                    } else {
                        Ingredient current = head;
                        while (true) {
                            userAuth.clearScreen();
                            out.println("Current Ingredient:");
                            out.printf("ID: %d\n", current.getId());
                            out.printf("Name: %s\n", current.getName());
                            out.printf("Price: %.2f\n", current.getPrice());
                            printIngredientViewMenu();
                            int viewChoice = userAuth.getInput();

                            if (viewChoice == -2) {
                                userAuth.handleInputError();
                                continue;
                            }

                            if (viewChoice == 1 && current.getNext() != null) {
                                current = current.getNext();
                            } else if (viewChoice == 2 && current.getPrev() != null) {
                                current = current.getPrev();
                            } else if (viewChoice == 3) {
                                break;
                            } else {
                                out.println("Invalid choice or no more ingredients in that direction.");
                                userAuth.enterToContinue();
                            }
                        }
                    }
                    break;

                case 2:
                    // Add Ingredient
                    while (true) {
                        try {
                            userAuth.clearScreen();
                            out.print("Enter ingredient name: ");
                            String name = scanner.nextLine();

                            if (name.isEmpty()) {
                                out.println("Ingredient name cannot be empty. Please try again.");
                                userAuth.enterToContinue();
                                continue;
                            }

                            out.print("Enter ingredient price: ");
                            float price = scanner.nextFloat();
                            scanner.nextLine(); // Clear buffer

                            if (price <= 0) {
                                out.println("Price must be greater than zero. Please try again.");
                                userAuth.enterToContinue();
                                continue;
                            }

                            head = addIngredient(head, name, price, filePath);
                            out.println("Ingredient added successfully.");
                            userAuth.enterToContinue();
                            break;
                        } catch (InputMismatchException e) {
                            out.println("Invalid input. Please enter a valid number for the price.");
                            scanner.nextLine(); // Clear invalid input
                            userAuth.enterToContinue();
                        }
                    }
                    break;

                case 3:
                    // Remove Ingredient
                    if (head == null) {
                        out.println("No ingredients to remove.");
                        userAuth.enterToContinue();
                    } else {
                        // Present the list type menu (DLL or XLL)
                        userAuth.clearScreen();
                        out.println("Choose list type to display:");
                        out.println("1. DLL (Doubly Linked List)");
                        out.println("2. XLL (Extended Linked List)");
                        out.print("Enter your choice: ");
                        int listChoice = userAuth.getInput();

                        if (listChoice == -2) {
                            userAuth.handleInputError();
                            userAuth.enterToContinue();
                            continue;
                        }

                        boolean displayed = false;
                        switch (listChoice) {
                            case 1:
                                displayed = listIngredientsDLL(head);
                                break;
                            case 2:
                                displayed = listIngredientsXLL(head);
                                break;
                            default:
                                out.println("Invalid choice. Please try again.");
                                userAuth.enterToContinue();
                                continue;
                        }

                        if (!displayed) {
                            userAuth.enterToContinue();
                            continue;
                        }

                        // Prompt for the ID of the ingredient to remove
                        out.print("Enter the ID of the ingredient to remove: ");
                        int id = userAuth.getInput();

                        if (id == -2) {
                            userAuth.handleInputError();
                            userAuth.enterToContinue();
                            continue;
                        }

                        head = removeIngredient(head, id, filePath);
                        out.println("Ingredient removed successfully.");
                        userAuth.enterToContinue();
                    }
                    break;

                case 4:
                    // Edit Ingredient
                    head = editIngredient(head, filePath);
                    userAuth.enterToContinue();
                    break;

                case 5:
                    // Search Ingredient by Name
                    out.print("Enter the ingredient name to search: ");
                    String searchName = scanner.nextLine();
                    if (searchName.isEmpty()) {
                        out.println("Search name cannot be empty.");
                        userAuth.enterToContinue();
                        continue;
                    }
                    searchIngredientByKMP(head, searchName);
                    userAuth.enterToContinue();
                    break;

                case 6:
                    // Exit
                    saveIngredientsToFile(head, filePath);
                    out.println("Exiting Ingredient Management Menu.");
                    return true;

                default:
                    out.println("Invalid choice. Please try again.");
                    userAuth.enterToContinue();
                    break;
            }
        }
    }

}
