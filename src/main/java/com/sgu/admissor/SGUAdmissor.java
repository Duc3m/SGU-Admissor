/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sgu.admissor.bus.ExcelImportBUS;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Admin
 */
public class SGUAdmissor {

    public static void main(String[] args) {     
        
        Injector injector = Guice.createInjector(
            new JpaPersistModule("sgu_pu") 
        );
        
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();
        
//        java.awt.EventQueue.invokeLater(() -> {
//            // Ví dụ: MainFrame frame = injector.getInstance(MainFrame.class);
//            // frame.setVisible(true);
//            
//            System.out.println("Hệ thống SGUAdmissor khởi động thành công!");
//        });

        ExcelImportBUS excelImportBUS = injector.getInstance(ExcelImportBUS.class);
//        String filePath1 = "data/Chi_tieu_2025.xlsx";
//        String filePath2 = "data/Nguong_dau_vao_2025.xlsx";
//        String filePath3 = "data/tohopmon.xlsx";
//        
//        File file1 = new File(filePath1);
//        File file2 = new File(filePath2);
//        File file3 = new File(filePath3);
//        
//        if (!file1.exists() || !file2.exists() || !file3.exists()) {
//            System.err.println("Files not found");
//            return;
//        }
//
//        long startTime = System.currentTimeMillis();        
//        excelImportBUS.importNganhVaToHop(file1, file2, file3);
//        long endTime = System.currentTimeMillis();
//        System.out.println("Imported in: " + (endTime - startTime) + " ms.");
//        
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            persistService.stop();
//        }));

        String nvExcelFilePath = "data/Nguyenvong.xlsx";
        
        File file = new File(nvExcelFilePath);
        
        if (!file.exists()){
            System.err.println("File not found");
            return;
        }
        
        long startTime = System.currentTimeMillis();        
        excelImportBUS.importNguyenVong(file);
        long endTime = System.currentTimeMillis();
        System.out.println("Imported in: " + (endTime - startTime) + " ms.");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistService.stop();
        }));
    }
    
}
