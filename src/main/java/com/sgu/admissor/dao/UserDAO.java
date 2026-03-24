/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.User;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class UserDAO extends GenericDAO<User>{
    
    public UserDAO() {
        super(User.class);
    }
    
    public User findByUsername(String userName){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "FROM User WHERE username = :usernameParam";
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("usernameParam", userName);
            
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<User> findByRoleId(Integer roleId){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "FROM User WHERE roleId = :roldIdParam";
            
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("roldIdParam", roleId);
            
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
