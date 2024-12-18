/**
 * @file KMPAlgorithm.java
 * @brief This file contains the implementation of the KMP (Knuth-Morris-Pratt) algorithm for pattern matching.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class KMPAlgorithm
 * @brief Implements the KMP algorithm for efficient substring search.
 *
 * The KMPAlgorithm class provides methods to compute the Longest Prefix Suffix (LPS) array
 * and perform pattern matching in a given text using the KMP algorithm.
 */
public class KMPAlgorithm {

    /**
     * @brief Computes the Longest Prefix Suffix (LPS) array for the given pattern.
     *
     * The LPS array is used by the KMP algorithm to skip unnecessary comparisons
     * during the search process. It represents the longest proper prefix which
     * is also a suffix for each substring of the pattern.
     *
     * @param pattern The pattern for which the LPS array is computed.
     * @return An array representing the LPS values for the pattern.
     */
    public static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * @brief Performs pattern matching in a given text using the KMP algorithm.
     *
     * This method searches for the presence of a pattern in a given text. It uses
     * the precomputed LPS array to efficiently find the pattern without
     * unnecessary comparisons.
     *
     * @param text The text to search within.
     * @param pattern The pattern to search for.
     * @return True if the pattern is found in the text, false otherwise.
     */
    public static boolean KMPSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPSArray(pattern);

        int i = 0;
        int j = 0;

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                return true; // Pattern found
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }
}
