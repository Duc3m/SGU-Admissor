/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.entity.User;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Duc3m
 */
public class UserPopup extends JPopupMenu {

    private JPanel profilePanel;
    private final AuthSession session;

    @Inject
    public UserPopup(AuthSession session) {
        this.session = session;
    }
    
    public void showPopup(Component parent, int x, int y, Runnable onChangePassword, Runnable onLogout) {
        this.removeAll();

        User currentUser = session.getCurrentUser();
        String userName = (currentUser != null) ? currentUser.getUsername() : "Guest";
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Unknown";

        
        profilePanel = new JPanel(new MigLayout("insets 10 10 10 15", "[min!]10[pref!]", "[]-5[]"));
        profilePanel.setOpaque(false); 

        JLabel lblAvatar = new JLabel(new AvatarIcon(userName, 40, Color.decode("#0066cc"), Color.WHITE));

        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(Color.GRAY);

        profilePanel.add(lblAvatar, "span 1 2, aligny center"); 
        profilePanel.add(lblName, "wrap"); 
        profilePanel.add(lblRole);         

        add(profilePanel);
        addSeparator();

        
        add(createMenuItem("Đổi mật khẩu", "icons/key.svg", Color.decode("#4b5563"), onChangePassword));
        add(createMenuItem("Đăng xuất", "icons/logout.svg", Color.decode("#ef4444"), onLogout));
        
        
        this.show(parent, x, y);
    }

    private JMenuItem createMenuItem(String text, String iconPath, Color color, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setForeground(color);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        try {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 18, 18);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
            item.setIcon(icon);
        } catch (Exception e) {}

        if (action != null) {
            item.addActionListener(e -> action.run());
        }
        return item;
    }
}