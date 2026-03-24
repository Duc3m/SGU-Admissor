/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.sgu.admissor.bus.RoleBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;

/**
 *
 * @author Admin
 */
public class SGUAdmissor {

    public static void main(String[] args) {
        RoleBUS roleBUS = new RoleBUS();
        Role newRole = new Role();
        newRole.setName("admin");
        BUSResult result = roleBUS.addRole(newRole);
        System.out.println(result.getMessage());
    }
    
}
