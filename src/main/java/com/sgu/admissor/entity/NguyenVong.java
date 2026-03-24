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
public class NguyenVong {
    private Integer id;
    private String cccd;
    private String maNganh;
    private Integer thuTu;
    private BigDecimal diemThxt;
    private BigDecimal diemUtqd;
    private BigDecimal diemCong;
    private BigDecimal diemXetTuyen;
    private String ketQua;
    private String nvKey;
    private String phuongThuc;
    private String toHopMon;

    public NguyenVong() {
    
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public Integer getThuTu() {
        return thuTu;
    }

    public void setThuTu(Integer thuTu) {
        this.thuTu = thuTu;
    }

    public BigDecimal getDiemThxt() {
        return diemThxt;
    }

    public void setDiemThxt(BigDecimal diemThxt) {
        this.diemThxt = diemThxt;
    }

    public BigDecimal getDiemUtqd() {
        return diemUtqd;
    }

    public void setDiemUtqd(BigDecimal diemUtqd) {
        this.diemUtqd = diemUtqd;
    }

    public BigDecimal getDiemCong() {
        return diemCong;
    }

    public void setDiemCong(BigDecimal diemCong) {
        this.diemCong = diemCong;
    }

    public BigDecimal getDiemXetTuyen() {
        return diemXetTuyen;
    }

    public void setDiemXetTuyen(BigDecimal diemXetTuyen) {
        this.diemXetTuyen = diemXetTuyen;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public String getNvKey() {
        return nvKey;
    }

    public void setNvKey(String nvKey) {
        this.nvKey = nvKey;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getToHopMon() {
        return toHopMon;
    }

    public void setToHopMon(String toHopMon) {
        this.toHopMon = toHopMon;
    }
    
}
