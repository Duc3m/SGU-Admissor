/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sgu.admissor.bus.RoleBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;

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
        
        java.awt.EventQueue.invokeLater(() -> {
            // Ví dụ: MainFrame frame = injector.getInstance(MainFrame.class);
            // frame.setVisible(true);
            
            System.out.println("Hệ thống SGUAdmissor khởi động thành công!");
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistService.stop();
        }));
    }
    
}
