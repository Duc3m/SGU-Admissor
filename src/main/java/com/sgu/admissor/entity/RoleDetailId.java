/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Admin
 */
public class RoleDetailId implements Serializable{
    private Integer roleId;
    private Integer functionId;

    public RoleDetailId() {}

    public RoleDetailId(Integer roleId, Integer functionId) {
        this.roleId = roleId;
        this.functionId = functionId;
    }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }

    // Bắt buộc phải có equals và hashCode cho Composite Key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDetailId that = (RoleDetailId) o;
        return Objects.equals(roleId, that.roleId) &&
               Objects.equals(functionId, that.functionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, functionId);
    }
}
