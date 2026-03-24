/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.SysFunction;
import com.sgu.admissor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class SysFunctionDAO extends GenericDAO<SysFunction> {
    
    public SysFunctionDAO() {
        super(SysFunction.class);
    }
    
    public SysFunction findByName(String funcName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM SysFunction WHERE name = :nameParam";
            
            Query<SysFunction> query = session.createQuery(hql, SysFunction.class);
            query.setParameter("nameParam", funcName);
            
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
