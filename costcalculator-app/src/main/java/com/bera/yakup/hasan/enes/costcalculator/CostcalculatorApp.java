/**

@file CostcalculatorApp.java
@brief This file serves as the main application file for the Costcalculator App.
@details This file contains the entry point of the application, which is the main method. It initializes the necessary components and executes the Costcalculator App.
*/
/**

@package com.bera.yakup.hasan.enes.costcalculator
@brief The com.bera.yakup.hasan.enes.costcalculator package contains all the classes and files related to the Costcalculator App.
*/
package com.bera.yakup.hasan.enes.costcalculator;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 *
 * @class CostcalculatorApp
 * @brief This class represents the main application class for the Costcalculator
 *        App.
 * @details The CostcalculatorApp class provides the entry point for the Costcalculator
 *          App. It initializes the necessary components, performs calculations,
 *          and handles exceptions.
 * @author ugur.coruh
 */
public class CostcalculatorApp {
  /**
   * @brief Logger for the CostcalculatorApp class.
   */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(CostcalculatorApp.class);

  /**
   * @brief The main entry point of the Costcalculator App.
   *
   * @details The main method is the starting point of the Costcalculator App. It
   *          initializes the logger, performs logging, displays a greeting
   *          message, and handles user input.
   *
   * @param args The command-line arguments passed to the application.
   */
  public static void main(String[] args) {
    // Logging messages for informational purposes
    logger.info("Logging message");
    // Logging an error message
    logger.error("Error message");
    // Displaying a greeting message
    System.out.println("Hello World!");

    try {
      // Checking if command-line arguments are provided
      if (args != null) {
        // Checking if there are any arguments
        if (args.length > 0) {
          // Checking if the first argument is "1"
          if (args[0].equals("1")) {
            // Throwing a dummy IOException
            throw new IOException("Dummy Exception...");
          }
        }
      }

      // Prompting the user to press Enter to continue
      System.out.println("Press Enter to Continue...");
      // Reading user input from the console
      System.in.read();
      // Displaying a closing message
      System.out.println("Thank you...");
    } catch (IOException e) {
      // Logging the exception
      logger.error(e.toString());
      // Printing the exception stack trace
      e.printStackTrace();
    }
  }

}
