/**
 * @file Recipe.java
 * @brief This file contains the implementation of the Recipe class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Recipe
 * @brief Represents a recipe with a name, category, and a list of ingredients.
 *
 * The Recipe class encapsulates the details of a recipe, including its name,
 * category, and the list of ingredients by their IDs. It provides methods for
 * managing the recipe's attributes and ingredients.
 */
public class Recipe {
    private String name; ///< Recipe name.
    private int category; ///< Recipe category (e.g., soup, appetizer, main course, dessert).
    private List<Integer> ingredients; ///< List of ingredient IDs used in the recipe.

    /**
     * @brief Default constructor for the Recipe class.
     *
     * Initializes the recipe with an empty name, category set to 0, and an empty list of ingredients.
     */
    public Recipe() {
        this.name = "";
        this.category = 0;
        this.ingredients = new ArrayList<>();
    }

    /**
     * @brief Parameterized constructor for the Recipe class.
     *
     * @param name The name of the recipe.
     * @param category The category of the recipe.
     *
     * Initializes the recipe with the given name and category, and an empty list of ingredients.
     */
    public Recipe(String name, int category) {
        this.name = name;
        this.category = category;
        this.ingredients = new ArrayList<>(); // Initialize as an empty list
    }

    /**
     * @brief Gets the name of the recipe.
     * @return The name of the recipe.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Sets the name of the recipe.
     * @param name The name to set for the recipe.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Gets the category of the recipe.
     * @return The category of the recipe.
     */
    public int getCategory() {
        return category;
    }

    /**
     * @brief Sets the category of the recipe.
     * @param category The category to set for the recipe.
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * @brief Gets the list of ingredient IDs used in the recipe.
     * @return A list of ingredient IDs.
     */
    public List<Integer> getIngredients() {
        return ingredients;
    }

    /**
     * @brief Sets the list of ingredient IDs used in the recipe.
     * @param ingredients A list of ingredient IDs to set for the recipe.
     */
    public void setIngredients(List<Integer> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * @brief Adds an ingredient to the recipe by its ID.
     * @param ingredientId The ID of the ingredient to add.
     */
    public void addIngredient(int ingredientId) {
        ingredients.add(ingredientId); // Add ingredient to the list
    }

    /**
     * @brief Gets the number of ingredients in the recipe.
     * @return The number of ingredients in the recipe.
     */
    public int getIngredientCount() {
        return ingredients.size();
    }
}