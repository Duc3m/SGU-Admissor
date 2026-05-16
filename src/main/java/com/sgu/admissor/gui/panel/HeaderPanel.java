/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.entity.User;
import com.sgu.admissor.gui.components.UserPopup;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Duc3m
 */
public class HeaderPanel extends JPanel {
    
    private final AuthSession authSession;
    private final Provider<UserPopup> popupProvider;
    private Runnable onChangePassword;
    private Runnable onLogout;

    @Inject
    public HeaderPanel(AuthSession authSession, Provider<UserPopup> popupProvider) {
        this.authSession = authSession;
        this.popupProvider = popupProvider;
        initLayout();
    }
    
    public void setPopupActions(Runnable onChangePassword, Runnable onLogout) {
        this.onChangePassword = onChangePassword;
        this.onLogout = onLogout;
    }

    private void initLayout() {
        User currentUser = authSession.getCurrentUser();
        // Cấu hình MigLayout:
        // insets 10 20: Padding trên/dưới 0px, trái/phải 20px
        // fillx: Giãn hết chiều ngang
        // Cột: [Logo] [Tiêu đề] push [Nút User]
        // Hàng: [center] Căn giữa theo chiều dọc
        setLayout(new MigLayout("insets 0 20, fillx", "[][]push[]", "[center]"));
        setBackground(Color.WHITE);
        
        // Tạo đường viền mờ ở cạnh dưới để ngăn cách Header với phần Body
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lblLogo = new JLabel();
        
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            
            lblLogo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Không tìm thấy đường dẫn ảnh logo!");
        }


        JLabel lblTitle = new JLabel("SGU Admissor - Quản lý tuyển sinh");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));

        JButton btnUser = new JButton(currentUser.getUsername());
        try {
            btnUser.setIcon(new FlatSVGIcon("icons/person.svg", 26, 26));
        } catch (Exception e) {
            
        }
        
        btnUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnUser.putClientProperty("JButton.buttonType", "borderless");
        btnUser.putClientProperty("JComponent.focusWidth", 0);
        btnUser.setFocusable(false);


        btnUser.addActionListener(e -> {
            UserPopup popup = popupProvider.get();
            popup.showPopup(
                btnUser, 
                5, 
                btnUser.getHeight() + 5, 
                this.onChangePassword, 
                this.onLogout
            );
        });
        
        add(lblLogo);
        add(lblTitle, "gapleft 8");
        add(btnUser, "h 44!");
    }
}