/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.SysFunction;

/**
 *
 * @author Admin
 */
public class SysFunctionDAO extends GenericDAO<SysFunction> {
    
    public SysFunctionDAO() {
        super(SysFunction.class);
    }
    
    public SysFunction findByName(String funcName) {
        String hql = "FROM SysFunction f WHERE f.name = :name";
        return emProvider.get().createQuery(hql, SysFunction.class)
                .setParameter("name", funcName)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
