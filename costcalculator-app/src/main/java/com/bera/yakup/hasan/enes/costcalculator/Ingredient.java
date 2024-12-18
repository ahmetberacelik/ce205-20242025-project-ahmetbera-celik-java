/**
 * @file Ingredient.java
 * @brief This file contains the implementation of the Ingredient class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class Ingredient
 * @brief Represents an ingredient in a linked list structure.
 *
 * The Ingredient class encapsulates information about an ingredient, including
 * its ID, name, price, and pointers to the previous and next ingredients in the list.
 */
public class Ingredient {
    private int id; ///< Unique ingredient ID.
    private String name; ///< Ingredient name.
    private float price; ///< Ingredient price.
    private Ingredient prev; ///< Pointer to the previous ingredient in the list.
    private Ingredient next; ///< Pointer to the next ingredient in the list.

    /**
     * @brief Gets the ID of the ingredient.
     * @return The unique ID of the ingredient.
     */
    public int getId() { return id; }

    /**
     * @brief Sets the ID of the ingredient.
     * @param id The unique ID to set for the ingredient.
     */
    public void setId(int id) { this.id = id; }

    /**
     * @brief Gets the name of the ingredient.
     * @return The name of the ingredient.
     */
    public String getName() { return name; }

    /**
     * @brief Sets the name of the ingredient.
     * @param name The name to set for the ingredient.
     */
    public void setName(String name) { this.name = name; }

    /**
     * @brief Gets the price of the ingredient.
     * @return The price of the ingredient.
     */
    public float getPrice() { return price; }

    /**
     * @brief Sets the price of the ingredient.
     * @param price The price to set for the ingredient.
     */
    public void setPrice(float price) { this.price = price; }

    /**
     * @brief Gets the previous ingredient in the list.
     * @return A reference to the previous Ingredient object.
     */
    public Ingredient getPrev() { return prev; }

    /**
     * @brief Sets the previous ingredient in the list.
     * @param prev A reference to the previous Ingredient object.
     */
    public void setPrev(Ingredient prev) { this.prev = prev; }

    /**
     * @brief Gets the next ingredient in the list.
     * @return A reference to the next Ingredient object.
     */
    public Ingredient getNext() { return next; }

    /**
     * @brief Sets the next ingredient in the list.
     * @param next A reference to the next Ingredient object.
     */
    public void setNext(Ingredient next) { this.next = next; }
}
