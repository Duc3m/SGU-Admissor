/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class UserDAO extends GenericDAO<User>{
    
    public UserDAO() {
        super(User.class);
    }
    
    public int countAdvanced(String username, String role, Integer status) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = buildWhereClause(username, role, status, params);
        
        return countByCondition(whereClause, params); 
    }
    
    public List<User> searchAdvanced(String username, String role, Integer status, int page, int limit) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = buildWhereClause(username, role, status, params);
        
        return findByCondition(whereClause, params, page, limit); 
    }
    
    private String buildWhereClause(String username, String role, Integer status, Map<String, Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");

        if (username != null && !username.trim().isEmpty()) {
            where.append(" AND e.username LIKE :username");
            params.put("username", "%" + username.trim() + "%");
        }

        if (role != null && !role.contains("Tất cả")) {
            where.append(" AND e.role = :role");
            params.put("role", role);
        }

        if (status != null && status >= 0) {
            where.append(" AND e.isActive = :isActive");
            params.put("isActive", status == 1);
        }

        return where.toString();
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
