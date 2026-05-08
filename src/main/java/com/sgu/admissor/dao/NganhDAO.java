/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class NganhDAO extends GenericDAO<Nganh> {

    public NganhDAO() {
        super(Nganh.class);
    }

    public Nganh findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nganh WHERE maNganh = :maNganhParam";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("maNganhParam", maNganh);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Nganh> findByTenNganh(String tenNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nganh WHERE tenNganh LIKE :tenNganhParam";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("tenNganhParam", "%" + tenNganh + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Nganh> findByTuyenThang(boolean tuyenThang) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nganh WHERE tuyenThang = :tuyenThangParam";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("tuyenThangParam", tuyenThang);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
