/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.Role;

/**
 *
 * @author Admin
 */
public class RoleDAO extends GenericDAO<Role> {
    
    public RoleDAO() {
        super(Role.class);
    }
    
    public Role findByName(String roleName) {
        String hql = "FROM Role r WHERE r.name = :name";
        return emProvider.get().createQuery(hql, Role.class)
                .setParameter("name", roleName)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    
}
