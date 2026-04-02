/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class NguyenVongDAO extends GenericDAO<NguyenVong>{
    
    public NguyenVongDAO() {
        super(NguyenVong.class);
    }
    
    
    public List<NguyenVong> findByCccd(String cccd){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "FROM NguyenVong WHERE cccd = :cccdParam";
            
            Query<NguyenVong> query = session.createQuery(hql, NguyenVong.class);
            query.setParameter("cccdParam", cccd);
            
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public NguyenVong findByNvKey(String nvKey) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            String hql = "FROM NguyenVong WHERE nv_key = :keyParam";
            
            Query<NguyenVong> query = session.createQuery(hql, NguyenVong.class);
            query.setParameter("keyParam", nvKey);
            
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
