/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.ThiSinh2025;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class ThiSinh2025DAO extends GenericDAO<ThiSinh2025> {

    public ThiSinh2025DAO() {
        super(ThiSinh2025.class);
    }

    public ThiSinh2025 findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh2025 WHERE cccd = :cccdParam";
            Query<ThiSinh2025> query = session.createQuery(hql, ThiSinh2025.class);
            query.setParameter("cccdParam", cccd);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ThiSinh2025 findBySoBaoDanh(String soBaoDanh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh2025 WHERE soBaoDanh = :soBaoDanhParam";
            Query<ThiSinh2025> query = session.createQuery(hql, ThiSinh2025.class);
            query.setParameter("soBaoDanhParam", soBaoDanh);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ThiSinh2025> findByHoTen(String hoTen) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh2025 WHERE hoTen LIKE :hoTenParam";
            Query<ThiSinh2025> query = session.createQuery(hql, ThiSinh2025.class);
            query.setParameter("hoTenParam", "%" + hoTen + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
