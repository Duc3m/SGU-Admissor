/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class GenericDAO<T> {
    private final Class<T> entityClass;
    private static final int BATCH_SIZE = 500;
    
    @Inject
    protected Provider<EntityManager> emProvider;
    
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    public void refresh(T entity) {
        try {
            var em = emProvider.get();
            em.refresh(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<T> findAll() {
        String hql = "FROM " + entityClass.getSimpleName();
        return emProvider.get().createQuery(hql, entityClass).getResultList();
    }
    
    public int countAll() {
        try {
            var em = emProvider.get();
            String hql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            
            Long count = (Long) em.createQuery(hql).getSingleResult();
            return count.intValue();
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int countByCondition(String whereClause, Map<String, Object> params) {
        try {
            var em = emProvider.get();
            String hql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e " + whereClause;
            Query query = em.createQuery(hql);
            if (params != null) {
                params.forEach(query::setParameter);
            }
            return ((Long) query.getSingleResult()).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public List<T> findByCondition(String whereClause, Map<String, Object> params, int page, int limit) {
        try {
            var em = emProvider.get();
            String hql = "FROM " + entityClass.getSimpleName() + " e " + whereClause;
            Query query = em.createQuery(hql, entityClass);
            if (params != null) {
                params.forEach(query::setParameter);
            }
            int offset = (page - 1) * limit;
            return query.setFirstResult(offset).setMaxResults(limit).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<T> getByPage(int page, int limit) {
        try {
            var em = emProvider.get();
            String hql = "FROM " + entityClass.getSimpleName();
            
            int offset = (page - 1) * limit;

            return em.createQuery(hql, entityClass)
                     .setFirstResult(offset)
                     .setMaxResults(limit)
                     .getResultList();
                     
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public T findById(Integer id) {
        return emProvider.get().find(entityClass, id);
    }

    public boolean insert(T entity) {
        try {
            emProvider.get().persist(entity);
            emProvider.get().flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean insertBatch(List<T> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return false;
        }
        try {
            var em = emProvider.get();
            int i = 0;
            for (T e : entityList) {
                em.persist(e);
                i++;
                if (i % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(T entity) {
        try {
            emProvider.get().merge(entity);
            emProvider.get().flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(T entity) {
        try {
            emProvider.get().remove(emProvider.get().merge(entity));
            emProvider.get().flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
