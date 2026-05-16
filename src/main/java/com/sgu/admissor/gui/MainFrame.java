/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.gui.dialog.*;
import com.sgu.admissor.gui.frame.LoginFrame;
import com.sgu.admissor.gui.panel.*;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.*;
import java.util.function.BiConsumer;

/**
 *
 * @author Duc3m
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    private final Provider<ThiSinhPanel> thiSinhPanelProvider;
    private final Provider<NganhPanel> nganhPanelProvider;
    private final Provider<NguyenVongPanel> nguyenVongPanelProvider;
    private final Provider<BangDiemPanel> bangDiemPanelProvider;
    private final Provider<ThongKePanel> thongKePanelProvider;
    private final DashboardPanel dashboardPanel;
    private final Provider<LoginFrame> loginFrameProvider;
    private final Provider<ChangePasswordDialog> changePassDialogProvider;
    private final AuthSession authSession;
    
    @Inject
    public MainFrame(Provider<ThiSinhPanel> thiSinhPanelProvider,
            Provider<NganhPanel> nganhPanelProvider,
            Provider<NguyenVongPanel> nguyenVongPanelProvider,
            Provider<BangDiemPanel> bangDiemPanelProvider,
            Provider<ThongKePanel> thongKePanelProvider,
            DashboardPanel dashboardPanel,
            Provider<LoginFrame> loginFrameProvider,
            Provider<ChangePasswordDialog> changePassDialogProvider,
            AuthSession authSession) {
        this.thiSinhPanelProvider = thiSinhPanelProvider;
        this.nganhPanelProvider = nganhPanelProvider;
        this.nguyenVongPanelProvider = nguyenVongPanelProvider;
        this.bangDiemPanelProvider = bangDiemPanelProvider;
        this.thongKePanelProvider = thongKePanelProvider;
        this.dashboardPanel = dashboardPanel;
        this.loginFrameProvider = loginFrameProvider;
        this.changePassDialogProvider = changePassDialogProvider;
        this.authSession = authSession;
        this.dashboardPanel.setHeaderActions(this::handleChangePassword, this::handleLogout);
        
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image scaledImage = logo.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);
        setTitle("SGU Admissor");
        setSize(1280, 720);
        setMinimumSize(new Dimension(950, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initTabbedPane();

        this.dashboardPanel.setOnFunctionOpen(functionCode -> {
            this.openFunctionTab(functionCode);
        });
        tabbedPane.addTab("Home", new FlatSVGIcon("icons/home.svg", 16, 16), dashboardPanel);
        
        add(tabbedPane);
    }


    private void initTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
        
        // Không cho phép đóng tab index 0 (Home)
        tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback",
            (BiConsumer<JTabbedPane, Integer>) (pane, index) -> {
                if (index > 0) {
                    pane.removeTabAt(index);
                }
            });
    }

    private void openFunctionTab(String title) {
        // Kiểm tra tab trùng lặp
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(title)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        JPanel newForm;
        Icon newIcon = null;
        switch (title) {
            case "Thí sinh":
                newForm = thiSinhPanelProvider.get();
                newIcon = new FlatSVGIcon("icons/candidate.svg");
                break;
            case "Bảng điểm":
                newForm = bangDiemPanelProvider.get();
                newIcon = new FlatSVGIcon("icons/score.svg");
                break;
            case "Ngành":
                newForm = nganhPanelProvider.get();
                newIcon = new FlatSVGIcon("icons/major.svg");
                break;
            case "Nguyện vọng":
                newForm = nguyenVongPanelProvider.get();
                newIcon = new FlatSVGIcon("icons/aspiration.svg");
                break;
            case "Thống kê":
                newForm = thongKePanelProvider.get();
                newIcon = new FlatSVGIcon("icons/statistic.svg");
                break;
            default:
                throw new AssertionError();
        }
        

        tabbedPane.addTab(title, newIcon, newForm);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    public void backToDashboard() {
        tabbedPane.setSelectedIndex(0);
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authSession.logout();
            this.dispose();
            loginFrameProvider.get().setVisible(true);
        }
    }


    private void handleChangePassword() {
        ChangePasswordDialog dialog = changePassDialogProvider.get();
        
        boolean isChanged = dialog.showDialog(this);
        
        if (isChanged) {
            JOptionPane.showMessageDialog(this, "Vì lý do bảo mật, vui lòng đăng nhập lại bằng mật khẩu mới.");
            authSession.logout();
            this.dispose();
            loginFrameProvider.get().setVisible(true);
        }
    }

}
