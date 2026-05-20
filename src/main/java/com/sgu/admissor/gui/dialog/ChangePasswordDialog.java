package com.sgu.admissor.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.inject.Inject;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.UserBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.util.WindowUtil;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.Window;

/**
 * @author Duc3m
 */
public class ChangePasswordDialog extends JDialog {

    private final UserBUS userBUS;
    private final AuthSession authSession;

    private boolean isSuccess = false; 
    private boolean requireOldPassword = true; // Cờ kiểm tra trạng thái
    private int targetUserId; // ID của user sẽ bị đổi pass

    private JLabel lblOldPass;
    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JButton btnSave;
    private JButton btnCancel;

    @Inject
    public ChangePasswordDialog(UserBUS userBUS, AuthSession authSession) {
        super(WindowUtil.findMainWindow(), "Đổi mật khẩu");
        this.userBUS = userBUS;
        this.authSession = authSession;

        setModal(true); 
        setSize(400, 300);
        setResizable(false);
        
        initLayout();
        initEvents();
    }

    public boolean showDialog(Window parent) {
        return showDialog(parent, authSession.getCurrentUser().getId(), true);
    }

    public boolean showDialog(Window parent, int targetUserId, boolean requireOldPassword) {
        this.targetUserId = targetUserId;
        this.requireOldPassword = requireOldPassword;

        lblOldPass.setVisible(requireOldPassword);
        txtOldPass.setVisible(requireOldPassword);

        setTitle(requireOldPassword ? "Đổi mật khẩu" : "Reset Mật khẩu");

        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return isSuccess;
    }

    private void initLayout() {
        setLayout(new MigLayout("wrap 2, fillx, insets 25 35 25 35, hidemode 3", "[right]20[grow]", "[]15[]15[]25[]"));
        lblOldPass = new JLabel("Mật khẩu cũ:");
        txtOldPass = new JPasswordField();
        txtOldPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu hiện tại");
        txtOldPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        
        if(requireOldPassword) {
            add(lblOldPass);
            add(txtOldPass, "growx, h 35!");
        }

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
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BUSResult result;

        if (requireOldPassword) {
            String oldPass = new String(txtOldPass.getPassword());
            if (oldPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cũ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            result = userBUS.changePassword(targetUserId, oldPass, newPass);
            
        } else {
            result = userBUS.resetPassword(targetUserId, newPass); 
        }

        if (result != null && result.isSuccess()) {
            isSuccess = true;
            JOptionPane.showMessageDialog(this, 
                    requireOldPassword ? "Đổi mật khẩu thành công!" : "Reset mật khẩu thành công!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            String errorMsg = (result != null) ? result.getMessage() : "Có lỗi xảy ra!";
            JOptionPane.showMessageDialog(this, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}