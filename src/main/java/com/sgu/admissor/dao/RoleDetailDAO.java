/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dao;

import com.sgu.admissor.entity.RoleDetail;
import com.sgu.admissor.entity.RoleDetailId;
import java.util.List;

/**
 *
 * @author Admin
 */
public class RoleDetailDAO extends GenericDAO<RoleDetail> {

    public RoleDetailDAO() {
        super(RoleDetail.class);
    }

    public RoleDetail findByRoleDetailId(RoleDetailId id) {
        return emProvider.get().find(RoleDetail.class, id);
    }
    
    public List<RoleDetail> findByRoleId(Integer roleId) {
        String hql = "FROM RoleDetail rd WHERE rd.id.roleId = :roleId";
        return emProvider.get().createQuery(hql, RoleDetail.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    public List<RoleDetail> findByFunctionId(Integer functionId) {
        String hql = "FROM RoleDetail rd WHERE rd.id.functionId = :functionId";
        return emProvider.get().createQuery(hql, RoleDetail.class)
                .setParameter("functionId", functionId)
                .getResultList();
    }

}
