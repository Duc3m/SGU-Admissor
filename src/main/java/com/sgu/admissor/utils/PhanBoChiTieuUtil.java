/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Duc3m
 */
public class PhanBoChiTieuUtil {
    private static final double TY_LE_PT1 = 0.05; // 5% Tuyển thẳng
    private static final double TY_LE_PT2 = 0.25; // 25% ĐGNL
    private static final double TY_LE_PT3 = 0.30; // 30% V-SAT

    /**
     * Hàm tính toán phân bổ chỉ tiêu
     * @param maNganh Mã ngành (VD: 7140201)
     * @param tongChiTieu Tổng chỉ tiêu của ngành đó (VD: 100)
     * @return Map chứa số lượng slot cho từng phương thức
     */
    public static Map<String, Integer> tinhPhanBoChiTieu(String maNganh, int tongChiTieu) {
        
        // Xác định flag phương thức dựa theo Pattern chuỗi
        boolean xetPT1 = false;
        boolean xetPT2 = false;
        boolean xetPT3 = false;
        boolean xetPT4 = false;

        if (maNganh.equals("7140114")) {
            // 7140114: Xét cả 4
            xetPT1 = true; xetPT2 = true; xetPT3 = true; xetPT4 = true;
        } else if (maNganh.equals("7140201")) {
            // 7140201: CHỈ xét PT4
            xetPT4 = true;
        } else if (maNganh.startsWith("714")) {
            // Các mã 714 còn lại: Chỉ xét PT1 và PT4
            xetPT1 = true; xetPT4 = true;
        } else {
            // Các ngành ngoài khối 714
            xetPT1 = true; xetPT2 = true; xetPT3 = true; xetPT4 = true;
        }

        int slPT1 = 0, slPT2 = 0, slPT3 = 0, slPT4 = 0;
        if(xetPT1 && xetPT4 && !xetPT2 && !xetPT3) {
            slPT1 = xetPT1 ? (int) Math.round(tongChiTieu * 0.15) : 0;
        } else {
            slPT1 = xetPT1 ? (int) Math.round(tongChiTieu * TY_LE_PT1) : 0;
            slPT2 = xetPT2 ? (int) Math.round(tongChiTieu * TY_LE_PT2) : 0;
            slPT3 = xetPT3 ? (int) Math.round(tongChiTieu * TY_LE_PT3) : 0;
        }
        
        slPT4 = tongChiTieu - (slPT1 + slPT2 + slPT3);

        Map<String, Integer> ketQua = new HashMap<>();
        ketQua.put("PT1", slPT1);
        ketQua.put("PT2", slPT2);
        ketQua.put("PT3", slPT3);
        ketQua.put("PT4", slPT4);

        return ketQua;
    }
}
