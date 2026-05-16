/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.inject.Inject;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.UserBUS;
import com.sgu.admissor.dto.BUSResult;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;

/**
 *
 * @author Duc3m
 */
public class ChangePasswordDialog extends JDialog {

    private final UserBUS userBUS;
    private final AuthSession authSession;

    private boolean isSuccess = false; 

    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JButton btnSave;
    private JButton btnCancel;

    @Inject
    public ChangePasswordDialog(UserBUS userBUS, AuthSession authSession) {
        this.userBUS = userBUS;
        this.authSession = authSession;

        setTitle("Đổi mật khẩu");
        setModal(true); 
        setSize(400, 300);
        setResizable(false);
        
        initLayout();
        initEvents();
    }

    public boolean showDialog(JFrame parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
        return isSuccess;
    }

    private void initLayout() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35", "[right]20[grow]", "[]15[]15[]25[]"));

        add(new JLabel("Mật khẩu cũ:"));
        txtOldPass = new JPasswordField();
        txtOldPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu hiện tại");
        txtOldPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        add(txtOldPass, "growx, h 35!");

        add(new JLabel("Mật khẩu mới:"));
        txtNewPass = new JPasswordField();
        txtNewPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu mới");
        txtNewPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        add(txtNewPass, "growx, h 35!");

        add(new JLabel("Xác nhận:"));
        txtConfirmPass = new JPasswordField();
        txtConfirmPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lại mật khẩu mới");
        txtConfirmPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        add(txtConfirmPass, "growx, h 35!");

        btnSave = new JButton("Lưu thay đổi");
        this.getRootPane().setDefaultButton(btnSave);

        btnCancel = new JButton("Hủy bỏ");

        add(btnSave, "span 2, center, split 2, gapx 15, w 120!, h 35!");
        add(btnCancel, "w 100!, h 35!");
    }

    private void initEvents() {
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> performChangePassword());
    }

    private void performChangePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = authSession.getCurrentUser().getId();
        BUSResult result = userBUS.changePassword(userId, oldPass, newPass);

        if (result.isSuccess()) {
            isSuccess = true;
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}