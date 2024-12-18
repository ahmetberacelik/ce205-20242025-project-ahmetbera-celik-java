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

}
