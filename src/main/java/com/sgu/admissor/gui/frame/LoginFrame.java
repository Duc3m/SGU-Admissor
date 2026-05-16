/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.frame;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.UserBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.User;
import com.sgu.admissor.gui.MainFrame;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Duc3m
 */
public class LoginFrame extends JFrame {
    private final UserBUS userBUS;
    private final Provider<MainFrame> mainFrameProvider;
    private final AuthSession authSession;
    
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    
    @Inject
    public LoginFrame(UserBUS userBUS,
            Provider<MainFrame> mainFrameProvider,
            AuthSession authSession) {
        this.userBUS = userBUS;
        this.mainFrameProvider = mainFrameProvider;
        this.authSession = authSession;
        
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image scaledImage = logo.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);
        setTitle("SGU Admissor - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700); // Kích thước cửa sổ
        setLocationRelativeTo(null);
        
        initLayout();
        initEvents();
        
        txtUser.setText("ducem");
        txtPass.setText("123456");
    }

    private void initLayout() {
        setLayout(new MigLayout("insets 0", "[grow, center]", "[grow, center]"));
        getContentPane().setBackground(new Color(245, 247, 251));

        
        JPanel loginCard = new JPanel(new MigLayout("wrap, insets 40 50 40 50", "[fill, 320!]"));
        loginCard.setBackground(Color.WHITE);
        loginCard.putClientProperty(FlatClientProperties.STYLE, "arc: 40;");

        
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Không tìm thấy file logo!");
        }

        
        JLabel lblTitleMain = new JLabel("SGU Admissor", SwingConstants.CENTER);
        lblTitleMain.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitleMain.setForeground(Color.decode("#1e293b"));

        JLabel lblSubtitle = new JLabel("Hệ thống quản lý tuyển sinh", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.decode("#64748b"));

        
        txtUser = new JTextField();
        txtUser.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/person.svg", 18, 18));
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên đăng nhập hoặc Email");

        txtPass = new JPasswordField();
        txtPass.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/key.svg", 18, 18));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
        txtPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(Color.decode("#0066cc"));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 0; focusWidth: 0;");

        
        JLabel lblFooter1 = new JLabel("Mọi vấn đề về tài khoản vui lòng", SwingConstants.CENTER);
        lblFooter1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter1.setForeground(new Color(148, 163, 184));

        JLabel lblFooter2 = new JLabel("liên hệ quản trị viên", SwingConstants.CENTER);
        lblFooter2.setFont(new Font("Segoe UI", Font.BOLD, 12)); // In đậm dòng 2 để nhấn mạnh
        lblFooter2.setForeground(new Color(148, 163, 184));


        loginCard.add(lblLogo, "gapbottom 15");
        loginCard.add(lblTitleMain);
        loginCard.add(lblSubtitle, "gapbottom 30");
        loginCard.add(txtUser, "h 45!, gapbottom 15");
        loginCard.add(txtPass, "h 45!, gapbottom 25");
        loginCard.add(btnLogin, "h 50!, gapbottom 20");
        loginCard.add(lblFooter1);
        loginCard.add(lblFooter2);

        add(loginCard);
    }

    private void initEvents() {
        btnLogin.addActionListener(e -> performLogin());
        txtPass.addActionListener(e -> performLogin());
        txtUser.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        
        BUSResult<User> result = userBUS.login(username, password);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (result.isSuccess()) {
            User loggedInUser = result.getData();
            authSession.login(loggedInUser, null);
            
            MainFrame mainFrame = mainFrameProvider.get(); 
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
