/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

/**
 *
 * @author Admin
 */
public class RoleDetail {
    private RoleDetailId id;
    private String action;

    public RoleDetail() {}

    public RoleDetailId getId() { return id; }
    public void setId(RoleDetailId id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
