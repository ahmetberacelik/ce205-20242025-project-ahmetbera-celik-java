/**
 * @file IngredientManagement.java
 * @brief This file contains the implementation of the IngredientManagement class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.io.*;
import java.util.*;

/**
 * @class IngredientManagement
 * @brief Manages ingredient operations such as addition, deletion, search, and listing.
 *
 * The IngredientManagement class provides various methods to handle ingredients,
 * including managing Huffman encoding for ingredient names, performing operations on
 * linked lists, and file handling for persistent storage.
 */
public class IngredientManagement {
    private Scanner scanner; ///< Scanner for user input.
    private PrintStream out; ///< Output stream for messages.
    private UserAuthentication userAuth; ///< UserAuthentication instance for shared utilities.

    /**
     * @brief Constructor for IngredientManagement.
     * @param userAuth UserAuthentication instance.
     * @param scanner Scanner object for input.
     * @param out PrintStream object for output.
     */
    public IngredientManagement(UserAuthentication userAuth, Scanner scanner, PrintStream out) {
        this.userAuth = userAuth;
        this.scanner = scanner;
        this.out = out;
    }

    /**
     * @brief Constructs a Huffman tree from frequency data.
     * @param frequencies An array of character frequencies.
     * @return The root of the constructed Huffman tree.
     */
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

    /**
     * @brief Generates Huffman codes for a given Huffman tree.
     * @param root The root of the Huffman tree.
     * @param currentCode The current Huffman code.
     * @param codes Array to store generated Huffman codes.
     */
    public void generateHuffmanCodes(HuffmanTreeNode root, String currentCode, String[] codes) {
        if (root == null) return;

        if (root.getLeft() == null && root.getRight() == null) {
            codes[root.getCharacter()] = currentCode;
            return;
        }

        generateHuffmanCodes(root.getLeft(), currentCode + "0", codes);
        generateHuffmanCodes(root.getRight(), currentCode + "1", codes);
    }

    /**
     * @brief Encodes a string using Huffman codes.
     * @param input The string to encode.
     * @param codes The Huffman codes.
     * @return The encoded string.
     */
    public String encodeString(String input, String[] codes) {
        StringBuilder encodedStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            encodedStr.append(codes[ch]);
        }
        return encodedStr.toString();
    }

    /**
     * @brief Decodes a string using a Huffman tree.
     * @param root The root of the Huffman tree.
     * @param encodedStr The encoded string.
     * @return The decoded string.
     */
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

    /**
     * @brief Counts character frequencies in a string.
     * @param input The input string.
     * @return An array of character frequencies.
     */
    public int[] countFrequencies(String input) {
        int[] frequencies = new int[256];
        for (char ch : input.toCharArray()) {
            frequencies[ch]++;
        }
        return frequencies;
    }

    /**
     * @brief Adds a new ingredient to the list and saves it to a file.
     * @param head The head of the linked list.
     * @param name The name of the ingredient.
     * @param price The price of the ingredient.
     * @param filePath The file path for saving ingredients.
     * @return The updated head of the linked list.
     * @throws IOException If an I/O error occurs.
     */
    public Ingredient addIngredient(Ingredient head, String name, float price, String filePath) throws IOException {
        int[] freq = countFrequencies(name);
        HuffmanTreeNode root = constructHuffmanTree(freq);
        String[] codes = new String[256];
        generateHuffmanCodes(root, "", codes);

        String encodedName = encodeString(name, codes);
        String decodedName = decodeString(root, encodedName);

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

        saveIngredientsToFile(head, filePath);
        return head;
    }

    /**
     * @brief Saves the ingredient list to a file.
     * @param head The head of the linked list.
     * @param filePath The file path for saving ingredients.
     * @return True if the save operation is successful.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * @brief Lists ingredients in a doubly linked list format.
     * @param head The head of the linked list.
     * @return True if ingredients are listed, false otherwise.
     */
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
    /**
     * @brief Lists ingredients in an extended linked list format.
     * @param head The head of the linked list.
     * @return True if ingredients are listed, false otherwise.
     */
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
    /**
     * @brief Provides a menu to select the type of ingredient list to display.
     * @param head The head of the linked list.
     * @return True if a valid list type is selected and displayed, false otherwise.
     */
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
    /**
     * @brief Loads ingredients from a file into a linked list.
     * @param filePath The file path to load ingredients from.
     * @return The head of the linked list containing the ingredients, or null if the file does not exist.
     * @throws IOException If an I/O error occurs.
     */
    public Ingredient loadIngredientsFromFile(String filePath) throws IOException{
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
    /**
     * @brief Removes an ingredient from the linked list by its ID.
     * @param head The head of the linked list.
     * @param id The ID of the ingredient to remove.
     * @param filePath The file path to save the updated list.
     * @return The updated head of the linked list.
     * @throws IOException If an I/O error occurs.
     */
    public Ingredient removeIngredient(Ingredient head, int id, String filePath) throws IOException{
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