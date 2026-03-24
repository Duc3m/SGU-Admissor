/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.Role;
import com.sgu.admissor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class RoleDAO extends GenericDAO<Role> {
    
    public RoleDAO() {
        super(Role.class);
    }
    
    public Role findByName(String roleName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            String hql = "FROM Role WHERE name = :nameParam";
            
            Query<Role> query = session.createQuery(hql, Role.class);
            query.setParameter("nameParam", roleName);
            
            return query.uniqueResult();   
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
