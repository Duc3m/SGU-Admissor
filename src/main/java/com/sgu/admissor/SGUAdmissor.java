/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sgu.admissor.bus.RoleBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;

/**
 *
 * @author Admin
 */
public class SGUAdmissor {

    public static void main(String[] args) {     
          Injector injector = Guice.createInjector();
          RoleBUS roleBUS = injector.getInstance(RoleBUS.class);
          Role newRole = new Role();
          newRole.setName("Admin");
          BUSResult result = roleBUS.addRole(newRole);
          System.out.println(result.getMessage());
    }
    
}
