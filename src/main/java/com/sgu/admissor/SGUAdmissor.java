/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sgu.admissor.gui.MainFrame;
import jakarta.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Admin
 */
public class SGUAdmissor {

    public static void main(String[] args) {     
        System.setProperty(FlatSystemProperties.UI_SCALE, "110%");
        FlatMacLightLaf.setup();
        UIManager.put("Button.arc", 15);
        
        Injector injector = Guice.createInjector(
            new JpaPersistModule("sgu_pu") 
        );
        
        if (!isDatabaseReady(injector)) {
            // Hiện thông báo lỗi giao diện (rất quan trọng với app Desktop)
            JOptionPane.showMessageDialog(null, 
                "LỖI KẾT NỐI DATABASE:\n" +
                "- Hãy đảm bảo MySQL Server đã được bật.\n" +
                "- Kiểm tra tên Database và Password trong persistence.xml.\n\n",
                "SGU Admissor - Lỗi hệ thống", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1); // Tắt app ngay lập tức
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = injector.getInstance(MainFrame.class);
                mainFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public static boolean isDatabaseReady(Injector injector) {
        try {
            PersistService persistService = injector.getInstance(PersistService.class);
            persistService.start();
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
}
