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
import jakarta.persistence.JoinColumns;
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
@Table(name = "nganh_tohop")
public class NganhToHop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manganh",
            referencedColumnName = "manganh"
    )
    private Nganh nganh;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "matohop", referencedColumnName = "matohop"),
            @JoinColumn(name = "mon1", referencedColumnName = "mon1"),
            @JoinColumn(name = "mon2", referencedColumnName = "mon2"),
            @JoinColumn(name = "mon3", referencedColumnName = "mon3")
    })
    private ToHop toHop;
    
    @Column(name = "hs_mon1")
    private Integer hsMon1;
    @Column(name = "hs_mon2")
    private Integer hsMon2;
    @Column(name = "hs_mon3")
    private Integer hsMon3;
    
    @Column(name = "tb_keys", length = 45)
    private String tbKey;
    
    @Column(name = "N1")
    private Boolean n1;
    @Column(name = "TO")
    private Boolean to;
    @Column(name = "LI")
    private Boolean li;
    @Column(name = "HO")
    private Boolean ho;
    @Column(name = "SI")
    private Boolean si;
    @Column(name = "VA")
    private Boolean va;
    @Column(name = "SU")
    private Boolean su;
    @Column(name = "DI")
    private Boolean di;
    @Column(name = "TI")
    private Boolean ti;
    @Column(name = "KHAC")
    private Boolean khac;
    @Column(name = "KTPL")
    private Boolean ktpl;
    
    @Column(name = "dolech", precision = 6, scale = 2)
    private BigDecimal doLech;

}
