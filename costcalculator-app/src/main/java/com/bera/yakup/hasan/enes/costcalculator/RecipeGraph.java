/**
 * @file RecipeGraph.java
 * @brief This file contains the implementation of the RecipeGraph class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @class RecipeGraph
 * @brief Represents a graph structure for managing relationships between recipes.
 *
 * The RecipeGraph class provides functionality to represent recipes as nodes in a graph,
 * where edges represent relationships between recipes based on shared categories or ingredients.
 * It supports operations such as adding edges, finding strongly connected components (SCCs)
 * using Tarjan's algorithm, and resetting the graph state.
 */
public class RecipeGraph {
    private List<List<Integer>> adjList; ///< Adjacency list representing the graph.
    private boolean[] visited; ///< Tracks visited nodes during graph traversal.
    private int[] discoveryTime; ///< Stores discovery times of nodes for Tarjan's algorithm.
    private int[] lowLink; ///< Stores low-link values of nodes for SCC detection.
    private boolean[] inStack; ///< Indicates if a node is currently in the recursion stack.
    private List<Integer> stack; ///< Stack used in Tarjan's algorithm.
    private int time; ///< Global time counter for discovery times.
    private Scanner scanner; ///< Scanner for user input.

    /**
     * @brief Constructor for the RecipeGraph class.
     *
     * @param nodeCount The number of nodes (recipes) in the graph.
     * @param scanner A Scanner object for user input.
     *
     * Initializes the adjacency list and other data structures required for graph operations.
     */
    public RecipeGraph(int nodeCount, Scanner scanner) {
        adjList = new ArrayList<>();
        visited = new boolean[nodeCount];
        discoveryTime = new int[nodeCount];
        lowLink = new int[nodeCount];
        inStack = new boolean[nodeCount];
        stack = new ArrayList<>();
        time = 0;
        this.scanner = scanner;

        for (int i = 0; i < nodeCount; i++) {
            adjList.add(new ArrayList<>());
            visited[i] = false;
            discoveryTime[i] = -1;
            lowLink[i] = -1;
            inStack[i] = false;
        }
    }

    /**
     * @brief Adds a directed edge between two nodes in the graph.
     *
     * @param src The source node.
     * @param dest The destination node.
     */
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
    }

    /**
     * @brief Identifies and prints strongly connected components (SCCs) in the graph.
     *
     * Uses Tarjan's algorithm to find SCCs and prints the recipes in each SCC.
     *
     * @param recipes The list of recipes corresponding to the graph nodes.
     */
    public void tarjanSCC(List<Recipe> recipes) {
        for (int i = 0; i < adjList.size(); i++) {
            if (discoveryTime[i] == -1) {
                tarjanSCCUtil(i, recipes);
            }
        }
    }

    /**
     * @brief Recursive utility function for Tarjan's algorithm.
     *
     * @param node The current node being processed.
     * @param recipes The list of recipes corresponding to the graph nodes.
     */
    private void tarjanSCCUtil(int node, List<Recipe> recipes) {
        discoveryTime[node] = lowLink[node] = time++;
        stack.add(node);
        inStack[node] = true;

        for (int neighbor : adjList.get(node)) {
            if (discoveryTime[neighbor] == -1) {
                tarjanSCCUtil(neighbor, recipes);
                lowLink[node] = Math.min(lowLink[node], lowLink[neighbor]);
            } else if (inStack[neighbor]) {
                lowLink[node] = Math.min(lowLink[node], discoveryTime[neighbor]);
            }
        }

        if (lowLink[node] == discoveryTime[node]) {
            System.out.println("Recipes in this SCC:");
            int sccNode;
            do {
                sccNode = stack.remove(stack.size() - 1);
                inStack[sccNode] = false;
                Recipe recipe = recipes.get(sccNode);
                System.out.printf("- Recipe %d: %s (Category: %d, Ingredients: %s)%n",
                        sccNode + 1, recipe.getName(), recipe.getCategory(), recipe.getIngredients());
            } while (sccNode != node);
            System.out.println();
        }
    }

    /**
     * @brief Builds a RecipeGraph from a list of recipes based on shared categories or ingredients.
     *
     * @param recipes The list of recipes to build the graph from.
     * @param scanner A Scanner object for user input.
     * @return A RecipeGraph object representing the relationships between recipes.
     */
    public static RecipeGraph buildGraphFromRecipes(List<Recipe> recipes, Scanner scanner) {
        int recipeCount = recipes.size();
        RecipeGraph graph = new RecipeGraph(recipeCount, scanner);

        for (int i = 0; i < recipeCount; i++) {
            for (int j = 0; j < recipeCount; j++) {
                if (i != j) {
                    Recipe recipeI = recipes.get(i);
                    Recipe recipeJ = recipes.get(j);

                    if (recipeI.getCategory() == recipeJ.getCategory()) {
                        graph.addEdge(i, j);
                    } else {
                        for (int ingredient : recipeI.getIngredients()) {
                            if (recipeJ.getIngredients().contains(ingredient)) {
                                graph.addEdge(i, j);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return graph;
    }

    /**
     * @brief Resets the state of the graph for reuse.
     *
     * Clears the stack and reinitializes discovery and low-link values for all nodes.
     */
    public void resetGraphState() {
        int nodeCount = adjList.size();
        visited = new boolean[nodeCount];
        discoveryTime = new int[nodeCount];
        lowLink = new int[nodeCount];
        inStack = new boolean[nodeCount];
        stack.clear();
        time = 0;

        // Reinitialize discovery and low-link values
        Arrays.fill(discoveryTime, -1);
        Arrays.fill(lowLink, -1);
    }
}
