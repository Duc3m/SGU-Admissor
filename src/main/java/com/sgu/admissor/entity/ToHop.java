/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "tohop")
public class ToHop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "matohop", length = 45)
    private String maToHop;
    
    @Column(name = "mon1", length = 10)
    private String mon1;
    @Column(name = "mon2", length = 10)
    private String mon2;
    @Column(name = "mon3", length = 10)
    private String mon3;
    
    @Column(name = "tentohop", length = 100)
    private String tenToHop;

}
