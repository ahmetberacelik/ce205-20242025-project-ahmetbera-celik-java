/**
 * @file Bucket.java
 * @brief This file contains the implementation of the Bucket class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class Bucket
 * @brief Represents a bucket containing a linked list of ingredients.
 *
 * The Bucket class manages a linked list of Ingredient objects, allowing
 * for easy storage and retrieval within a hash table or similar data structure.
 */
public class Bucket {
    private Ingredient head; ///< Head of the linked list of ingredients.

    /**
     * @brief Gets the head of the linked list.
     * @return The head Ingredient object.
     */
    public Ingredient getHead() {
        return head;
    }

    /**
     * @brief Sets the head of the linked list.
     * @param head The Ingredient object to set as the head.
     */
    public void setHead(Ingredient head) {
        this.head = head;
    }
}
