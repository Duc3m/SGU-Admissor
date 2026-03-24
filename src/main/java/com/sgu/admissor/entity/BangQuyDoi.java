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
public class BangQuyDoi {
    private Integer id;
    private String phuongThuc;
    private String toHop;
    private String mon;
    private BigDecimal diemA, diemB, diemC, diemD;
    private String maQuyDoi;
    private String phanVi;

    public BangQuyDoi() {
    
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getToHop() {
        return toHop;
    }

    public void setToHop(String toHop) {
        this.toHop = toHop;
    }

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public BigDecimal getDiemA() {
        return diemA;
    }

    public void setDiemA(BigDecimal diemA) {
        this.diemA = diemA;
    }

    public BigDecimal getDiemB() {
        return diemB;
    }

    public void setDiemB(BigDecimal diemB) {
        this.diemB = diemB;
    }

    public BigDecimal getDiemC() {
        return diemC;
    }

    public void setDiemC(BigDecimal diemC) {
        this.diemC = diemC;
    }

    public BigDecimal getDiemD() {
        return diemD;
    }

    public void setDiemD(BigDecimal diemD) {
        this.diemD = diemD;
    }

    public String getMaQuyDoi() {
        return maQuyDoi;
    }

    public void setMaQuyDoi(String maQuyDoi) {
        this.maQuyDoi = maQuyDoi;
    }

    public String getPhanVi() {
        return phanVi;
    }

    public void setPhanVi(String phanVi) {
        this.phanVi = phanVi;
    }
    
}
