/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import java.util.List;

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

    public List<T> findAll() {
        String hql = "FROM " + entityClass.getSimpleName();
        return emProvider.get().createQuery(hql, entityClass).getResultList();
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
