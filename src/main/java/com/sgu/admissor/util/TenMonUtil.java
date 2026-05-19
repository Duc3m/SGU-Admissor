/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.util;

/**
 *
 * @author Duc3m
 */
public class TenMonUtil {
    public static String getTenMon(String maMon) {
        if (maMon == null) return "";
        
        return switch (maMon.toUpperCase().trim()) {
            case "TO" -> "Toán";
            case "LI" -> "Vật lí";
            case "HO" -> "Hóa học";
            case "SI" -> "Sinh học";
            case "SU" -> "Lịch sử";
            case "DI" -> "Địa lí";
            case "VA" -> "Ngữ văn";
            case "N1" -> "Tiếng Anh";
            case "CNCN" -> "Công nghệ Chăn nuôi";
            case "CNNN" -> "Công nghệ Nông nghiệp";
            case "TI" -> "Tin học";
            case "KTPL" -> "Giáo dục KT & PL";
            case "NK1" -> "Môn năng khiếu 1";
            case "NK2" -> "Môn năng khiếu 2";
            case "NK3" -> "Môn năng khiếu 3";
            case "NK4" -> "Môn năng khiếu 4";
            case "NK5" -> "Môn năng khiếu 5";
            case "NK6" -> "Môn năng khiếu 6";
            case "KHAC" -> "Môn năng khiếu";
            default -> maMon;
        };
    }
}
