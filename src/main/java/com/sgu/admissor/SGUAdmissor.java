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
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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
            System.exit(0);
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
    
    private static boolean isDatabaseReady(Injector injector) {

        PersistService persistService = injector.getInstance(PersistService.class);
        
        try {
            persistService.start();
            UnitOfWork unitOfWork = injector.getInstance(UnitOfWork.class);
            unitOfWork.begin();
            try {
                EntityManager em = injector.getInstance(EntityManager.class);
                em.createNativeQuery("SELECT 1").getSingleResult();
            } finally {
                unitOfWork.end();
            }
            
            System.out.println("Kết nối Database thành công!");
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "LỖI KẾT NỐI DATABASE:\n" +
                "- Hãy đảm bảo MySQL Server đã được bật.\n" +
                "- Kiểm tra tên Database và Password trong persistence.xml.\n\n" +
                "Chi tiết lỗi: " + e.getMessage(),
                "SGU Admissor - Lỗi hệ thống", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
}
