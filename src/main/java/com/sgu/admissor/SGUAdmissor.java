/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sgu.admissor;

import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sgu.admissor.gui.frame.*;
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
            new JpaPersistModule("sgu_pu"),
            new SGUAdmissorModule()
        );
        
        if (!isDatabaseReady(injector)) {
            JOptionPane.showMessageDialog(null, 
                "LỖI KẾT NỐI DATABASE:\n" +
                "- Hãy đảm bảo MySQL Server đã được bật.\n" +
                "- Kiểm tra tên Database và Password trong persistence.xml.\n\n",
                "SGU Admissor - Lỗi hệ thống", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame loginFrame = injector.getInstance(LoginFrame.class);
                loginFrame.setVisible(true);
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
