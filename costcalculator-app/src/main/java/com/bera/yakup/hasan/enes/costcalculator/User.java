/**
 * @file User.java
 * @brief This file contains the definition of the User class.
 */

package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class User
 * @brief Represents a user with personal information and credentials.
 *
 * The User class encapsulates the details of a user, including their unique ID,
 * name, surname, email, and password. It provides getter and setter methods for
 * each field to allow controlled access and modification.
 */
public class User {

    /**
     * @brief Unique user ID.
     */
    private int id;

    /**
     * @brief User's first name.
     */
    private String name;

    /**
     * @brief User's surname.
     */
    private String surname;

    /**
     * @brief User's email address.
     */
    private String email;

    /**
     * @brief User's password.
     */
    private String password;

    /**
     * @brief Gets the user's unique ID.
     * @return The user's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Sets the user's unique ID.
     * @param id The user's new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @brief Gets the user's first name.
     * @return The user's first name.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Sets the user's first name.
     * @param name The user's new first name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Gets the user's surname.
     * @return The user's surname.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @brief Sets the user's surname.
     * @param surname The user's new surname.
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @brief Gets the user's email address.
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Sets the user's email address.
     * @param email The user's new email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Gets the user's password.
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief Sets the user's password.
     * @param password The user's new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
