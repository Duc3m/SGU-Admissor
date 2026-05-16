/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.auth;

import com.sgu.admissor.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Duc3m
 */
public class AuthSession {
    private User currentUser;
    private Map<String, Set<String>> permissionMap = new HashMap<>();

    public void login(User user, Map<String, Set<String>> permissions) {
        this.currentUser = user;
        this.permissionMap = permissions;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    public boolean hasPermission(String functionCode, String actionCode) {
        if (currentUser == null) return false;
        
        if ("ADMIN".equals(currentUser.getRole().getName())) return true;

        Set<String> actions = permissionMap.get(functionCode);
        return actions != null && actions.contains(actionCode);
    }
    
    public boolean canAccessFunction(String functionCode) {
        return "ADMIN".equals(currentUser.getRole().getName()) || permissionMap.containsKey(functionCode);
    }
}