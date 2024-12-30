package com.bera.yakup.hasan.enes.costcalculator;

/**
 * @class XORNode
 * @brief Represents a node in the XOR linked list structure
 */
public class XORNode {
    private User user;
    private XORNode prev;
    private XORNode next;

    /**
     * @brief Constructor for XORNode
     * @param user User data to store in the node
     */
    public XORNode(User user) {
        this.user = user;
        this.prev = null;
        this.next = null;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public XORNode getPrev() {
        return prev;
    }

    public void setPrev(XORNode prev) {
        this.prev = prev;
    }

    public XORNode getNext() {
        return next;
    }

    public void setNext(XORNode next) {
        this.next = next;
    }
} 