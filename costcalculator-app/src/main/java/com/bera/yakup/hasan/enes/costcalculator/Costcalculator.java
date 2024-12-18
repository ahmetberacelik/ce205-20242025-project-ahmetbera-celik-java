/**

@file Costcalculator.java
@brief This file serves as a demonstration file for the Costcalculator class.
@details This file contains the implementation of the Costcalculator class, which provides various mathematical operations.
*/

/**

@package com.bera.yakup.hasan.enes.costcalculator
@brief The com.bera.yakup.hasan.enes.costcalculator package contains all the classes and files related to the Costcalculator App.
*/
package com.bera.yakup.hasan.enes.costcalculator;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
/**

@class Costcalculator
@brief This class represents a Costcalculator that performs mathematical operations.
@details The Costcalculator class provides methods to perform mathematical operations such as addition, subtraction, multiplication, and division. It also supports logging functionality using the logger object.
@author ugur.coruh
*/
public class Costcalculator {

  /**
   * @brief Logger for the Costcalculator class.
   */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(Costcalculator.class);

  /**
   * @brief Calculates the sum of two integers.
   *
   * @details This function takes two integer values, `a` and `b`, and returns their sum. It also logs a message using the logger object.
   *
   * @param a The first integer value.
   * @param b The second integer value.
   * @return The sum of `a` and `b`.
   */
  public int add(int a, int b) {
    // Logging an informational message
    logger.info("Logging message");
    // Logging an error message
    logger.error("Error message");
    // Returning the sum of `a` and `b`
    return a + b;
  }
}
