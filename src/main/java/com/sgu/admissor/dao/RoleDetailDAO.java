/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.RoleDetail;
import com.sgu.admissor.entity.RoleDetailId;
import com.sgu.admissor.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 *
 * @author Admin
 */
public class RoleDetailDAO {

    public List<RoleDetail> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM RoleDetail";
            return session.createQuery(hql, RoleDetail.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RoleDetail findById(RoleDetailId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(RoleDetail.class, id);
        }
    }

    public List<RoleDetail> findByRoleId(Integer roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM RoleDetail WHERE id.roleId = :roleIdParam";
            Query<RoleDetail> query = session.createQuery(hql, RoleDetail.class);
            query.setParameter("roleIdParam", roleId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<RoleDetail> findByFunctionId(Integer functionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM RoleDetail WHERE id.functionId = :functionIdParam";
            Query<RoleDetail> query = session.createQuery(hql, RoleDetail.class);
            query.setParameter("functionIdParam", functionId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insert(RoleDetail entity) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(RoleDetail entity) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(RoleDetail entity) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
