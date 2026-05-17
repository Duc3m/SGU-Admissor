/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.sgu.admissor.bus.UserBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.User;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Duc3m
 */
public class CreateUserDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnSave;
    private JButton btnCancel;
    
    private boolean isSaved = false;
    
    private final UserBUS userBUS;

    @Inject
    public CreateUserDialog(UserBUS userBUS) {
        this.userBUS = userBUS;
        initLayout();
        initEvents();
        setTitle("Thêm tài khoản mới");
        setModal(true);
        pack();
        setResizable(false);
    }

    public void showDialog(Window parent) {
        setLocationRelativeTo(parent);
        setVisible(true); 
    }
    
    private void initLayout() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35", "[right]20[grow]", "[]15[]15[]25[]"));

        add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập username...");
        add(txtUsername, "growx, h 35!");

        add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu...");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true"); // Thêm con mắt
        add(txtPassword, "growx, h 35!");

        add(new JLabel("Vai trò:"));
        String[] roles = {"user", "admin"};
        cbRole = new JComboBox<>(roles);
        add(cbRole, "growx, h 35!");

        btnSave = new JButton("Lưu tài khoản");
        this.getRootPane().setDefaultButton(btnSave);
        
        btnCancel = new JButton("Hủy bỏ");

        add(btnSave, "span 2, center, split 2, gapx 15, w 130!, h 35!");
        add(btnCancel, "w 100!, h 35!");
        
        this.getRootPane().setDefaultButton(btnSave);
    }

    private void initEvents() {
        btnCancel.addActionListener(e -> {
            isSaved = false;
            dispose();
        });

        btnSave.addActionListener(e -> {
            String rawUsername = txtUsername.getText().trim();
            String rawPassword = new String(txtPassword.getPassword());

            if (rawUsername.isEmpty() || rawPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User();
            newUser.setUsername(rawUsername);
            newUser.setPassword(rawPassword); 
            newUser.setIsActive(true);
            String role = cbRole.getSelectedItem().toString();
            newUser.setRole(role);

            BUSResult<User> result = userBUS.addUser(newUser);

            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isSaved = true;
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isSaved() { return isSaved; }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getRole() { return cbRole.getSelectedItem().toString(); }
}