package com.bera.yakup.hasan.enes.costcalculator;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PriceAdjustment {

    private Scanner scanner;
    private PrintStream out;
    private UserAuthentication userAuth;
    private IngredientManagement ingredientManagement;

    public PriceAdjustment(UserAuthentication userAuth, IngredientManagement ingredientManagement, Scanner scanner, PrintStream out) {
        this.userAuth = userAuth;
        this.ingredientManagement = ingredientManagement;
        this.scanner = scanner;
        this.out = out;
    }


    /**
     * @param pathFileIngredients Path to the ingredient file.
     * @return Total number of ingredients printed.
     * @throws IOException If an I/O error occurs.
     * @brief Prints the ingredients loaded from a file to the console in descending order of price.
     */
    public int printIngredientsToConsole(String pathFileIngredients) throws IOException {
        List<Ingredient> ingredients = convertDoubleLinkToArray(pathFileIngredients);
        if (ingredients.isEmpty()) {
            return 0;
        }

        // Sort ingredients in descending order of price using heap sort
        sortIngredientsMaxHeap(ingredients, ingredients.size());

        // Write the ingredients to the console
        out.println("Ingredients (sorted by price in descending order):");
        for (Ingredient ingredient : ingredients) {
            out.println("+--------------------------------------+\n" +
                    "| Ingredient ID: " + ingredient.getId() + "\n" +
                    "| Name         : " + ingredient.getName() + "\n" +
                    "| Price        : $" + String.format("%.2f", ingredient.getPrice()) + "\n" +
                    "+--------------------------------------+\n");
        }

        return ingredients.size();
    }

    public List<Ingredient> convertDoubleLinkToArray(String pathFileIngredients) throws IOException {
        Ingredient ingredients = ingredientManagement.loadIngredientsFromFile(pathFileIngredients);
        if (ingredients == null) {
            out.println("Ingredients could not be loaded");
            userAuth.enterToContinue();
            return new ArrayList<>();
        }

        List<Ingredient> ingredientList = new ArrayList<>();
        Ingredient current = ingredients;
        while (current != null) {
            ingredientList.add(current);
            current = current.getNext();
        }

        return ingredientList;
    }

    /**
     * @param number The number to check.
     * @return True if the number is prime, otherwise false.
     * @brief Checks if a given number is a prime number.
     */
    public boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param number The starting number.
     * @return The next prime number.
     * @brief Finds the next prime number greater than or equal to a given number.
     */
    public int findNextPrime(int number) {
        while (!isPrime(number)) {
            number++;
        }
        return number;
    }

    /**
     * @param ingredients List of ingredients.
     * @param n           Size of the list.
     * @param i           Index of the root element.
     * @brief Heapify function for Max Heap.
     */
    public void heapify(List<Ingredient> ingredients, int n, int i) {
        int largest = i; // Initialize largest as root
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // If left child is larger than root
        if (left < n && ingredients.get(left).getPrice() > ingredients.get(largest).getPrice()) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && ingredients.get(right).getPrice() > ingredients.get(largest).getPrice()) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            Ingredient temp = ingredients.get(i);
            ingredients.set(i, ingredients.get(largest));
            ingredients.set(largest, temp);

            // Recursively heapify the affected sub-tree
            heapify(ingredients, n, largest);
        }
    }

}
