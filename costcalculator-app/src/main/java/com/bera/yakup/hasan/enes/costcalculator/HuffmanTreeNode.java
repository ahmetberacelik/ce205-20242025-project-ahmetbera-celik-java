/**
 * @file HuffmanTreeNode.java
 * @brief This file contains the implementation of the HuffmanTreeNode class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class HuffmanTreeNode
 * @brief Represents a node in a Huffman Tree.
 *
 * The HuffmanTreeNode class stores a character and its frequency,
 * along with pointers to its left and right children.
 */
public class HuffmanTreeNode {
    private char character; ///< The character represented by this node.
    private int frequency; ///< The frequency of the character.
    private HuffmanTreeNode left; ///< Pointer to the left child.
    private HuffmanTreeNode right; ///< Pointer to the right child.

    /**
     * @brief Constructor for a HuffmanTreeNode.
     * @param character The character represented by this node.
     * @param frequency The frequency of the character.
     */
    public HuffmanTreeNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    /**
     * @brief Gets the character of the node.
     * @return The character represented by this node.
     */
    public char getCharacter() {
        return character;
    }

    /**
     * @brief Gets the frequency of the character.
     * @return The frequency of the character.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @brief Gets the left child of the node.
     * @return The left child node.
     */
    public HuffmanTreeNode getLeft() {
        return left;
    }

    /**
     * @brief Sets the left child of the node.
     * @param left The HuffmanTreeNode to set as the left child.
     */
    public void setLeft(HuffmanTreeNode left) {
        this.left = left;
    }

    /**
     * @brief Gets the right child of the node.
     * @return The right child node.
     */
    public HuffmanTreeNode getRight() {
        return right;
    }

    /**
     * @brief Sets the right child of the node.
     * @param right The HuffmanTreeNode to set as the right child.
     */
    public void setRight(HuffmanTreeNode right) {
        this.right = right;
    }
}
