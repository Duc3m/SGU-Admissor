/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.auth;

import com.sgu.admissor.entity.User;
/**
 *
 * @author Duc3m
 */
public class AuthSession {
    private User currentUser;

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    public boolean isAdmin() {
        if (currentUser == null) return false;
        return currentUser.getRole().equals("admin");
    }
}