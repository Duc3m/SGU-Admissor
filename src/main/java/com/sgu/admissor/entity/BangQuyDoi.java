/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "bangquydoi")
public class BangQuyDoi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tohop",
            referencedColumnName = "matohop"
    )
    private ToHop toHop;
    
    @Column(name = "mon", length = 45)
    private String mon;
    
    @Column(name = "diema", precision = 6, scale = 2)
    private BigDecimal diemA;
    @Column(name = "diemb", precision = 6, scale = 2)
    private BigDecimal diemB;
    @Column(name = "diemc", precision = 6, scale = 2)
    private BigDecimal diemC;
    @Column(name = "diemd", precision = 6, scale = 2)
    private BigDecimal diemD;
    
    @Column(name = "maquydoi", length = 45)
    private String maQuyDoi;
    
    @Column(name = "phanvi", length = 45)
    private String phanVi;

}
