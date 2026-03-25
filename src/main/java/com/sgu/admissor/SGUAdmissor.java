/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.sgu.admissor.bus.RoleBUS;
import com.sgu.admissor.bus.SysFunctionBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;
import com.sgu.admissor.entity.SysFunction;

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
        
        SysFunctionBUS sysFunctionBUS = new SysFunctionBUS();
        SysFunction newFunction = new SysFunction();
        newFunction.setName("Quat roi thang dia");
        SysFunction result2 = sysFunctionBUS.getSysFunctionByID(1);
        System.out.println(result.getMessage() + "\n" + result2);
    }
    
}
