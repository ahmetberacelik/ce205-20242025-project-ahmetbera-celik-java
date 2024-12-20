/**
 * @file CostcalculatorApp.java
 * @brief Entry point for the Recipe Cost Calculator application.
 */

package com.bera.yakup.hasan.enes.costcalculator;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @class CostcalculatorApp
 * @brief The main application class for running the Recipe Cost Calculator.
 *
 * This class initializes the required components and starts the application by invoking
 * the main menu of the UserAuthentication module.
 */
public class CostcalculatorApp {

  /**
   * @brief The main method and entry point of the application.
   *
   * Initializes the application by creating instances of required classes and
   * calling the main menu. The application handles user authentication, ingredient
   * management, and recipe costing.
   *
   * @param args Command-line arguments (not used).
   * @throws IOException If an I/O error occurs during file operations.
   * @throws InterruptedException If the thread is interrupted.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    Scanner inputScanner = new Scanner(System.in); ///< Scanner for user input.

    // Initialize UserAuthentication and necessary file paths
    UserAuthentication userAuthentication = new UserAuthentication(inputScanner, System.out);
    String pathFileIngredients = "ingredients.bin"; ///< Path to the ingredients file.
    String pathFileRecipes = "recipes.bin"; ///< Path to the recipes file.
    String pathFileUsers = "users.bin"; ///< Path to the users file.

    // Start the main menu
    userAuthentication.mainMenu(pathFileUsers, pathFileIngredients, pathFileRecipes);
  }
}
