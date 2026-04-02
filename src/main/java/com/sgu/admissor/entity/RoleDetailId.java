/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@NoArgsConstructor
@Data
@Embeddable
public class RoleDetailId implements Serializable{
    
    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "function_id")
    private Integer functionId;

    public RoleDetailId(Integer roleId, Integer functionId) {
        this.roleId = roleId;
        this.functionId = functionId;
    }

}
