/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
public class NganhToHop {
    private Integer id;
    private String maNganh, maToHop;
    private String mon1; private Integer hsMon1;
    private String mon2; private Integer hsMon2;
    private String mon3; private Integer hsMon3;
    private String tbKey;
    private Boolean n1, to, li, ho, si, va, su, di, ti, khac, ktpl;
    private BigDecimal doLech;

    public NganhToHop() {
    
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getMaToHop() {
        return maToHop;
    }

    public void setMaToHop(String maToHop) {
        this.maToHop = maToHop;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public Integer getHsMon1() {
        return hsMon1;
    }

    public void setHsMon1(Integer hsMon1) {
        this.hsMon1 = hsMon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public Integer getHsMon2() {
        return hsMon2;
    }

    public void setHsMon2(Integer hsMon2) {
        this.hsMon2 = hsMon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public Integer getHsMon3() {
        return hsMon3;
    }

    public void setHsMon3(Integer hsMon3) {
        this.hsMon3 = hsMon3;
    }

    public String getTbKey() {
        return tbKey;
    }

    public void setTbKey(String tbKey) {
        this.tbKey = tbKey;
    }

    public Boolean getN1() {
        return n1;
    }

    public void setN1(Boolean n1) {
        this.n1 = n1;
    }

    public Boolean getTo() {
        return to;
    }

    public void setTo(Boolean to) {
        this.to = to;
    }

    public Boolean getLi() {
        return li;
    }

    public void setLi(Boolean li) {
        this.li = li;
    }

    public Boolean getHo() {
        return ho;
    }

    public void setHo(Boolean ho) {
        this.ho = ho;
    }

    public Boolean getSi() {
        return si;
    }

    public void setSi(Boolean si) {
        this.si = si;
    }

    public Boolean getVa() {
        return va;
    }

    public void setVa(Boolean va) {
        this.va = va;
    }

    public Boolean getSu() {
        return su;
    }

    public void setSu(Boolean su) {
        this.su = su;
    }

    public Boolean getDi() {
        return di;
    }

    public void setDi(Boolean di) {
        this.di = di;
    }

    public Boolean getTi() {
        return ti;
    }

    public void setTi(Boolean ti) {
        this.ti = ti;
    }

    public Boolean getKhac() {
        return khac;
    }

    public void setKhac(Boolean khac) {
        this.khac = khac;
    }

    public Boolean getKtpl() {
        return ktpl;
    }

    public void setKtpl(Boolean ktpl) {
        this.ktpl = ktpl;
    }

    public BigDecimal getDoLech() {
        return doLech;
    }

    public void setDoLech(BigDecimal doLech) {
        this.doLech = doLech;
    }
    
}
