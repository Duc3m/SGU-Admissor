/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.User;
import java.util.List;

/**
 *
 * @author Admin
 */
public class UserDAO extends GenericDAO<User>{
    
    public UserDAO() {
        super(User.class);
    }
    
    public User findByUsername(String userName){
        String hql = "FROM User u WHERE u.username = :username";
        return emProvider.get().createQuery(hql, User.class)
                .setParameter("username", userName)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    
    public List<User> findByRoleId(Integer roleId){
        String hql = "FROM User u WHERE u.role.id = :roleId";
        return emProvider.get().createQuery(hql, User.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }
}
