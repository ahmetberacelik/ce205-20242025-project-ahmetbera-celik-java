/**

@file CostcalculatorTest.java
@brief This file contains the test cases for the Costcalculator class.
@details This file includes test methods to validate the functionality of the Costcalculator class. It uses JUnit for unit testing.
*/
package com.bera.yakup.hasan.enes.costcalculator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**

@class CostcalculatorTest
@brief This class represents the test class for the Costcalculator class.
@details The CostcalculatorTest class provides test methods to verify the behavior of the Costcalculator class. It includes test methods for addition, subtraction, multiplication, and division operations.
@author ugur.coruh
*/
public class CostcalculatorTest {

  /**
   * @brief This method is executed once before all test methods.
   * @throws Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @brief This method is executed once after all test methods.
   * @throws Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @brief This method is executed before each test method.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @brief This method is executed after each test method.
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * @brief Test method to validate the addition operation.
   *
   * @details This method creates an instance of the Costcalculator class and calls the `add` method with two integers. It asserts the expected result of the addition operation.
   */
  @Test
  public void testAddition() {
    Costcalculator costcalculator = new Costcalculator();
    int result = costcalculator.add(2, 3);
    assertEquals(5, result);
  }

}
