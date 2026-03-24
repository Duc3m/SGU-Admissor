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
public class Nganh {
    private Integer id;
    private String maNganh;
    private String tenNganh;
    private String toHopGoc;
    private Integer chiTieu;
    private BigDecimal diemSan;
    private BigDecimal diemTrungTuyen;
    private Boolean tuyenThang;
    private Boolean dgnl;
    private Boolean thpt;
    private Boolean vsat;
    private Integer slXtt;
    private Integer slDgnl;
    private Integer slVsat;
    private Integer slThpt;

    public Nganh() {
    
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public String getToHopGoc() { return toHopGoc; }
    public void setToHopGoc(String toHopGoc) { this.toHopGoc = toHopGoc; }

    public Integer getChiTieu() { return chiTieu; }
    public void setChiTieu(Integer chiTieu) { this.chiTieu = chiTieu; }

    public BigDecimal getDiemSan() { return diemSan; }
    public void setDiemSan(BigDecimal diemSan) { this.diemSan = diemSan; }

    public BigDecimal getDiemTrungTuyen() { return diemTrungTuyen; }
    public void setDiemTrungTuyen(BigDecimal diemTrungTuyen) { this.diemTrungTuyen = diemTrungTuyen; }

    public Boolean getTuyenThang() { return tuyenThang; }
    public void setTuyenThang(Boolean tuyenThang) { this.tuyenThang = tuyenThang; }

    public Boolean getDgnl() { return dgnl; }
    public void setDgnl(Boolean dgnl) { this.dgnl = dgnl; }

    public Boolean getThpt() { return thpt; }
    public void setThpt(Boolean thpt) { this.thpt = thpt; }

    public Boolean getVsat() { return vsat; }
    public void setVsat(Boolean vsat) { this.vsat = vsat; }

    public Integer getSlXtt() { return slXtt; }
    public void setSlXtt(Integer slXtt) { this.slXtt = slXtt; }

    public Integer getSlDgnl() { return slDgnl; }
    public void setSlDgnl(Integer slDgnl) { this.slDgnl = slDgnl; }

    public Integer getSlVsat() { return slVsat; }
    public void setSlVsat(Integer slVsat) { this.slVsat = slVsat; }

    public Integer getSlThpt() { return slThpt; }
    public void setSlThpt(Integer slThpt) { this.slThpt = slThpt; }
}
