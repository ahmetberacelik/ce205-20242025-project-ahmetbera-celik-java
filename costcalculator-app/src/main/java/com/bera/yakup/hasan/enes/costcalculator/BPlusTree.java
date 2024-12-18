/**
 * @file BPlusTree.java
 * @brief This file contains the implementation of the B+ Tree for managing Recipe objects.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * @class BPlusTreeNode
 * @brief Represents a node in the B+ tree.
 */
class BPlusTreeNode {
    boolean isLeaf; ///< Indicates if the node is a leaf.
    int keyCount; ///< Number of keys in the node.
    List<Integer> keys; ///< List of keys in the node.
    List<Recipe> recipes; ///< List of recipes (only in leaf nodes).
    List<BPlusTreeNode> children; ///< List of child nodes (only in internal nodes).
    BPlusTreeNode next; ///< Pointer to the next leaf node.

    /**
     * @brief Constructor for a BPlusTreeNode.
     * @param isLeaf Indicates if the node is a leaf.
     */
    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keyCount = 0;
        this.keys = new ArrayList<>();
        this.recipes = isLeaf ? new ArrayList<>() : null;
        this.children = isLeaf ? null : new ArrayList<>();
        this.next = null;
    }
}

/**
 * @class BPlusTree
 * @brief Implements a B+ tree for managing Recipe objects.
 */
public class BPlusTree {
    private BPlusTreeNode root; ///< Root node of the B+ tree.
    private static final int MAX_RECIPES = 5; ///< Maximum number of keys per node.

    /**
     * @brief Constructor for the BPlusTree.
     * Initializes the root as a leaf node.
     */
    public BPlusTree() {
        this.root = new BPlusTreeNode(true);
    }

    /**
     * @brief Inserts a key and its associated recipe into the B+ tree.
     * @param key The key to be inserted.
     * @param recipe The Recipe object to be associated with the key.
     */
    public void insert(int key, Recipe recipe) {
        BPlusTreeNode cursor = root;
        if (cursor.keyCount == MAX_RECIPES) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.children.add(root);
            insertInternal(key, recipe, root, newRoot);
            root = newRoot;
        } else {
            insertInternal(key, recipe, cursor, root);
        }
    }

    /**
     * @brief Handles the internal logic for inserting a key and recipe into the tree.
     * @param key The key to be inserted.
     * @param recipe The Recipe object to be associated with the key.
     * @param cursor The current node being processed.
     * @param parent The parent node of the cursor.
     */
    private void insertInternal(int key, Recipe recipe, BPlusTreeNode cursor, BPlusTreeNode parent) {
        if (cursor.keyCount < MAX_RECIPES) {
            int i = cursor.keyCount - 1;
            while (i >= 0 && cursor.keys.get(i) > key) {
                i--;
            }
            cursor.keys.add(i + 1, key);
            if (cursor.isLeaf) {
                cursor.recipes.add(i + 1, recipe);
            }
            cursor.keyCount++;
        } else {
            List<Integer> tempKeys = new ArrayList<>(cursor.keys);
            List<Recipe> tempRecipes = cursor.isLeaf ? new ArrayList<>(cursor.recipes) : null;

            int i = tempKeys.size() - 1;
            while (i >= 0 && tempKeys.get(i) > key) {
                i--;
            }
            tempKeys.add(i + 1, key);
            if (cursor.isLeaf) {
                tempRecipes.add(i + 1, recipe);
            }

            BPlusTreeNode newLeaf = new BPlusTreeNode(true);
            cursor.keyCount = tempKeys.size() / 2;
            newLeaf.keyCount = tempKeys.size() - cursor.keyCount;

            cursor.keys = tempKeys.subList(0, cursor.keyCount);
            newLeaf.keys = tempKeys.subList(cursor.keyCount, tempKeys.size());

            if (cursor.isLeaf) {
                cursor.recipes = tempRecipes.subList(0, cursor.keyCount);
                newLeaf.recipes = tempRecipes.subList(cursor.keyCount, tempRecipes.size());
                newLeaf.next = cursor.next;
                cursor.next = newLeaf;
            } else {
                newLeaf.children = cursor.children.subList(cursor.keyCount, cursor.children.size());
                cursor.children = cursor.children.subList(0, cursor.keyCount);
            }

            if (cursor == root) {
                BPlusTreeNode newRoot = new BPlusTreeNode(false);
                newRoot.keys.add(newLeaf.keys.get(0));
                newRoot.children.add(cursor);
                newRoot.children.add(newLeaf);
                newRoot.keyCount = 1;
                root = newRoot;
            } else {
                insertInternal(newLeaf.keys.get(0), null, findParent(root, cursor), parent);
            }
        }
    }

    /**
     * @brief Finds the parent of a given child node.
     * @param cursor The current node being processed.
     * @param child The child node whose parent is being searched for.
     * @return The parent node of the child node, or null if not found.
     */
    private BPlusTreeNode findParent(BPlusTreeNode cursor, BPlusTreeNode child) {
        if (cursor.isLeaf || cursor.children.get(0).isLeaf) return null;
        for (int i = 0; i <= cursor.keyCount; i++) {
            if (cursor.children.get(i) == child) return cursor;
            BPlusTreeNode parent = findParent(cursor.children.get(i), child);
            if (parent != null) return parent;
        }
        return null;
    }

    /**
     * @brief Searches for a recipe associated with a given key.
     * @param key The key to search for.
     */
    public void search(int key) {
        BPlusTreeNode cursor = root;
        while (!cursor.isLeaf) {
            for (int i = 0; i < cursor.keyCount; i++) {
                if (key < cursor.keys.get(i)) {
                    cursor = cursor.children.get(i);
                    break;
                }
                if (i == cursor.keyCount - 1) {
                    cursor = cursor.children.get(i + 1);
                }
            }
        }

        boolean found = false;
        while (cursor != null) {
            for (int i = 0; i < cursor.keyCount; i++) {
                if (cursor.keys.get(i) == key) {
                    System.out.println("Recipe found: " + cursor.recipes.get(i).getName());
                    found = true;
                }
            }
            cursor = cursor.next;
        }

        if (!found) {
            System.out.println("Recipe not found");
        }
    }
}
