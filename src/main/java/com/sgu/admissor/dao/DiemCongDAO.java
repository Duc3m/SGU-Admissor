/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class DiemCongDAO extends GenericDAO<DiemCong> {

    public DiemCongDAO() {
        super(DiemCong.class);
    }

    public List<DiemCong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemCong WHERE thiSinh.cccd = :cccdParam";
            Query<DiemCong> query = session.createQuery(hql, DiemCong.class);
            query.setParameter("cccdParam", cccd);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DiemCong findByDcKey(String dcKey) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemCong WHERE dcKey = :dcKeyParam";
            Query<DiemCong> query = session.createQuery(hql, DiemCong.class);
            query.setParameter("dcKeyParam", dcKey);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DiemCong> findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemCong WHERE nganh.maNganh = :maNganhParam";
            Query<DiemCong> query = session.createQuery(hql, DiemCong.class);
            query.setParameter("maNganhParam", maNganh);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
