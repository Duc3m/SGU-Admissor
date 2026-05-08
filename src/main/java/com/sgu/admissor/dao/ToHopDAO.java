/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class ToHopDAO extends GenericDAO<ToHop> {

    public ToHopDAO() {
        super(ToHop.class);
    }

    public ToHop findByMaToHop(String maToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ToHop WHERE maToHop = :maToHopParam";
            Query<ToHop> query = session.createQuery(hql, ToHop.class);
            query.setParameter("maToHopParam", maToHop);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ToHop> findByTenToHop(String tenToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ToHop WHERE tenToHop LIKE :tenToHopParam";
            Query<ToHop> query = session.createQuery(hql, ToHop.class);
            query.setParameter("tenToHopParam", "%" + tenToHop + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
