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
@Table(name = "nganh")
public class Nganh {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tohopgoc",
            referencedColumnName = "matohop"
    )
    private ToHop toHopGoc;
    
    @Column(name = "manganh", length = 45, unique = true)
    private String maNganh;
    
    @Column(name = "tennganh", length = 100)
    private String tenNganh;
    
    @Column(name = "chitieu")
    private Integer chiTieu;
    
    @Column(name = "diemsan", precision = 10, scale = 2)
    private BigDecimal diemSan;
    
    @Column(name = "diemtrungtuyen", precision = 10, scale = 2)
    private BigDecimal diemTrungTuyen;
    
    @Column(name = "tuyenthang")
    private Boolean tuyenThang;

    @Column(name = "dgnl")
    private Boolean dgnl;
    
    @Column(name = "thpt")
    private Boolean thpt;
    
    @Column(name = "vsat")
    private Boolean vsat;
    
    @Column(name = "sl_xtt")
    private Integer slXtt;
    
    @Column(name = "sl_dgnl")
    private Integer slDgnl;
    
    @Column(name = "sl_vsat")
    private Integer slVsat;
    
    @Column(name = "sl_thpt")
    private Integer slThpt;

}
