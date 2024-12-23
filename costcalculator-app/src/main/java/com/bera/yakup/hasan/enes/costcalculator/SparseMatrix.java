/**
 * @file SparseMatrix.java
 * @brief This file contains the implementation of the SparseMatrix and SparseMatrixNode classes.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class SparseMatrixNode
 * @brief Represents a node in the sparse matrix linked list.
 *
 * Each SparseMatrixNode object stores the row index, column index, value,
 * and a reference to the next node in the sparse matrix.
 */
class SparseMatrixNode {
    int row; ///< Row index.
    int col; ///< Column index.
    double value; ///< Value stored in the node.
    SparseMatrixNode next; ///< Pointer to the next node.

    /**
     * @brief Constructor for a SparseMatrixNode.
     * @param row Row index of the node.
     * @param col Column index of the node.
     * @param value Value to be stored in the node.
     */
    public SparseMatrixNode(int row, int col, double value) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.next = null;
    }
}

/**
 * @class SparseMatrix
 * @brief Implements a sparse matrix using a linked list.
 *
 * The SparseMatrix class manages a sparse matrix where non-zero values
 * are stored in a linked list of SparseMatrixNode objects.
 */
public class SparseMatrix {
    private SparseMatrixNode head; ///< Head of the linked list representing the sparse matrix.

    /**
     * @brief Constructor for the SparseMatrix class.
     * Initializes an empty sparse matrix.
     */
    public SparseMatrix() {
        this.head = null;
    }

    /**
     * @brief Inserts a new node into the sparse matrix.
     *
     * Adds a new non-zero value to the sparse matrix at the specified row and column.
     *
     * @param row Row index for the new node.
     * @param col Column index for the new node.
     * @param value Value to be stored in the new node.
     */
    public void insert(int row, int col, double value) {
        SparseMatrixNode newNode = new SparseMatrixNode(row, col, value);
        newNode.next = head;
        head = newNode;
    }

    /**
     * @brief Clears the sparse matrix.
     *
     * Frees the memory used by the sparse matrix by setting the head to null.
     */
    public void clear() {
        head = null; ///< Let the garbage collector handle memory cleanup.
    }

    /**
     * @brief Prints the contents of the sparse matrix.
     *
     * Iterates through the linked list and prints the row, column, and value
     * of each node in the sparse matrix.
     */
    public void printMatrix() {
        SparseMatrixNode current = head;
        while (current != null) {
            System.out.printf("Row: %d, Col: %d, Value: %.2f%n", current.row, current.col, current.value);
            current = current.next;
        }
    }
}
