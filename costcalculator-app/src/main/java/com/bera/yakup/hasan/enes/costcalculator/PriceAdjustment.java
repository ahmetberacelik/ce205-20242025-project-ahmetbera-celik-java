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


    /**
     * @param ingredients List of ingredients.
     * @param n           Total number of ingredients.
     * @brief Sorts the ingredients in descending order using Max Heap.
     */
    public void sortIngredientsMaxHeap(List<Ingredient> ingredients, int n) {
        // Build heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(ingredients, n, i);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            Ingredient temp = ingredients.get(0);
            ingredients.set(0, ingredients.get(i));
            ingredients.set(i, temp);

            // Call max heapify on the reduced heap
            heapify(ingredients, i, 0);
        }
    }

    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using linear probing.
     */
    public Ingredient linearProbingSearch(List<Ingredient> ingredients, int ingredientId) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getId() == ingredientId) {
                return ingredient;
            }
        }
        return null;
    }

    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using quadratic probing.
     */
    public Ingredient quadraticProbingSearch(List<Ingredient> ingredients, int ingredientId) {
        int totalIngredient = ingredients.size();
        int startIdx = ingredientId % totalIngredient;
        for (int i = 0; i < totalIngredient; i++) {
            int index = (startIdx + i * i) % totalIngredient;
            if (ingredients.get(index).getId() == ingredientId) {
                return ingredients.get(index);
            }
        }
        return null;
    }

    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using double hashing.
     */
    public Ingredient doubleHashingSearch(List<Ingredient> ingredients, int ingredientId) {
        int totalIngredient = ingredients.size();
        int hash1 = ingredientId % totalIngredient;
        int hash2 = 1 + (ingredientId % (totalIngredient - 1));
        for (int i = 0; i < totalIngredient; i++) {
            int index = (hash1 + i * hash2) % totalIngredient;
            if (ingredients.get(index).getId() == ingredientId) {
                return ingredients.get(index);
            }
        }
        return null;
    }


    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using progressive overflow.
     */
    public Ingredient progressiveOverflowSearch(List<Ingredient> ingredients, int ingredientId) {
        int totalIngredient = ingredients.size();
        int startIdx = ingredientId % totalIngredient;
        for (int i = 0; i < totalIngredient; i++) {
            int index = (startIdx + i) % totalIngredient;
            if (ingredients.get(index).getId() == ingredientId) {
                return ingredients.get(index);
            }
        }
        return null;
    }

    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @param c            The increment value for linear quotient.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using linear quotient.
     */
    public Ingredient linearQuotientSearch(List<Ingredient> ingredients, int ingredientId, int c) {
        int totalIngredient = ingredients.size();
        int startIdx = ingredientId % totalIngredient;
        for (int i = 0; i < totalIngredient; i++) {
            int index = (startIdx + i * c) % totalIngredient;
            if (ingredients.get(index).getId() == ingredientId) {
                return ingredients.get(index);
            }
        }
        return null;
    }

    /**
     * @param ingredients  List of ingredients.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient using Brent's method.
     */
    public Ingredient brentMethodSearch(List<Ingredient> ingredients, int ingredientId) {
        int totalIngredient = ingredients.size();
        int hash1 = ingredientId % totalIngredient;
        int hash2 = 1 + (ingredientId % (totalIngredient - 1));
        int bestIndex = hash1;
        for (int i = 0; i < totalIngredient; i++) {
            int index = (hash1 + i * hash2) % totalIngredient;
            if (ingredients.get(index).getId() == ingredientId) {
                return ingredients.get(index);
            }
            // Brent's Method: swap if we find a shorter path
            if (i < (bestIndex - hash1 + totalIngredient) % totalIngredient) {
                bestIndex = index;
            }
        }
        return (ingredients.get(bestIndex).getId() == ingredientId) ? ingredients.get(bestIndex) : null;
    }


    /**
     * @param buckets      List of buckets.
     * @param ingredientId The ID of the ingredient to search for.
     * @return The ingredient if found, otherwise null.
     * @brief Searches for an ingredient within a bucket.
     */
    public Ingredient bucketSearch(List<Bucket> buckets, int ingredientId) {
        int bucketSize = buckets.size();
        int bucketIndex = ingredientId % bucketSize;
        Ingredient current = buckets.get(bucketIndex).getHead();
        while (current != null) {
            if (current.getId() == ingredientId) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * @param pathFileIngredients Path to the ingredient file.
     * @return 1 if the ingredient price was successfully updated, otherwise 0.
     * @brief Adjusts the price of an ingredient.
     */
    public int adjustIngredientPrice(String pathFileIngredients) throws IOException, InterruptedException {
        userAuth.clearScreen();
        List<Ingredient> ingredients = convertDoubleLinkToArray(pathFileIngredients);

        if (ingredients.isEmpty()) {
            return 0;
        }

        // Ensure totalIngredient is a prime number for better hash distribution
        int totalIngredient = findNextPrime(ingredients.size());

        while (true) {
            userAuth.clearScreen();
            printIngredientsToConsole(pathFileIngredients);

            // Get ingredient id from user
            out.print("Enter Ingredient Id: ");
            int ingredientId = userAuth.getInput();

            // Check if ingredient id is valid
            if (ingredientId < 0) {
                out.println("Invalid Ingredient Id");
                userAuth.enterToContinue();
                continue;
            }

            // Select algorithm for searching the ingredient
            userAuth.clearScreen();
            out.println("\n+--------------------------------------+\n" +
                    "|     SELECT SEARCH ALGORITHM TO USE   |\n" +
                    "+--------------------------------------+\n" +
                    "| 1. Linear Probing                    |\n" +
                    "| 2. Quadratic Probing                 |\n" +
                    "| 3. Double Hashing                    |\n" +
                    "| 4. Progressive Overflow              |\n" +
                    "| 5. Use of Buckets                    |\n" +
                    "| 6. Linear Quotient                   |\n" +
                    "| 7. Brent's Method                    |\n" +
                    "+--------------------------------------+\n");
            out.print("Enter your choice (1-7): ");
            int algorithmChoice = scanner.nextInt();

            Ingredient ingredient = null;

            // Search for the ingredient using the selected algorithm
            switch (algorithmChoice) {
                case 1:
                    ingredient = linearProbingSearch(ingredients, ingredientId);
                    break;
                case 2:
                    ingredient = quadraticProbingSearch(ingredients, ingredientId);
                    break;
                case 3:
                    ingredient = doubleHashingSearch(ingredients, ingredientId);
                    break;
                case 4:
                    ingredient = progressiveOverflowSearch(ingredients, ingredientId);
                    break;
                case 5: {
                    int totalBuckets = findNextPrime(totalIngredient);
                    List<Bucket> buckets = new ArrayList<>(totalBuckets);
                    for (int i = 0; i < totalBuckets; i++) {
                        buckets.add(new Bucket());
                    }

                    // Fill buckets
                    for (Ingredient ing : ingredients) {
                        int bucketIndex = ing.getId() % totalBuckets;
                        ing.setNext(buckets.get(bucketIndex).getHead());
                        buckets.get(bucketIndex).setHead(ing);
                    }

                    ingredient = bucketSearch(buckets, ingredientId);

                    // Update the original ingredients list if found in bucket
                    if (ingredient != null) {
                        for (int i = 0; i < ingredients.size(); i++) {
                            if (ingredients.get(i).getId() == ingredient.getId()) {
                                ingredients.set(i, ingredient);
                                break;
                            }
                        }
                    }
                    break;
                }
                case 6:
                    ingredient = linearQuotientSearch(ingredients, ingredientId, 1); // Using increment value of 1
                    break;
                case 7:
                    ingredient = brentMethodSearch(ingredients, ingredientId);
                    break;
                default:
                    out.println("Invalid choice");
                    userAuth.enterToContinue();
                    continue;
            }

            // Check if ingredient was found
            if (ingredient == null) {
                out.println("Ingredient not found");
                userAuth.enterToContinue();
                break;
            }

            // Get new price from user
            out.print("Enter new price: ");
            float newPrice = scanner.nextFloat();

            // Update the price of the ingredient
            ingredient.setPrice(newPrice);

            // Save the updated ingredients to file
            try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(pathFileIngredients))) {
                for (Ingredient ing : ingredients) {
                    writer.writeInt(ing.getId());
                    writer.writeUTF(ing.getName());
                    writer.writeFloat(ing.getPrice());
                }
            }

            out.println("The ingredient was successfully updated");
            userAuth.enterToContinue();
            return 1;
        }

        return 0;
    }

}
