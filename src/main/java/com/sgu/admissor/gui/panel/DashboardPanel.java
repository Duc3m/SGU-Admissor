/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.gui.components.*;
import com.sgu.admissor.gui.dialog.MultiFileImportDialog;
import jakarta.inject.Inject;
import jakarta.inject.Provider;


import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Duc3m
 */
public class DashboardPanel extends JPanel {

    private Consumer<String> onFunctionOpen;
    private final HeaderPanel header;
    private final AuthSession authSession;
    private final Provider<MultiFileImportDialog> importDialogProvider ;
    
    @Inject
    public DashboardPanel(HeaderPanel header,
            AuthSession authSession,
            Provider<MultiFileImportDialog> importDialogProvider) {
        this.header = header;
        this.authSession = authSession;
        this.importDialogProvider = importDialogProvider;
        initLayout();
    }
    
    public void setHeaderActions(Runnable onChangePassword, Runnable onLogout) {
        // Chuyền bóng thẳng xuống cho HeaderPanel
        header.setPopupActions(onChangePassword, onLogout);
    }
    
    public void setOnFunctionOpen(Consumer<String> onFunctionOpen) {
        this.onFunctionOpen = onFunctionOpen;
    }

    private void initLayout() {
        
        setLayout(new MigLayout("fillx, insets 20", "[center]", "[] [] [center, grow]"));
        setBackground(Color.WHITE);

        putClientProperty("JTabbedPane.tabClosable", false);
        
        add(header, "w 90%!, wrap");

        JPanel topActionPanel = new JPanel(new MigLayout("insets 0, fillx", "[]push[][]", "[center]"));
        topActionPanel.setOpaque(false);

        JLabel lblGreeting = new JLabel("<html><font color='#0066cc'>Xin chào</font>, " + authSession.getCurrentUser().getUsername() + "</html>");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topActionPanel.add(lblGreeting);
        
        PastelButton btnQuanLyTK = new PastelButton(
            "Quản lý tài khoản", 
            "icons/users.svg", 
            Color.decode("#64748b") 
        );
        btnQuanLyTK.addActionListener(e -> {onFunctionOpen.accept("Tài khoản");});
        
        
        PastelButton btnImport = new PastelButton(
            "Import dữ liệu", 
            "icons/excel.svg", 
            Color.decode("#107C41") 
        );
        btnImport.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            MultiFileImportDialog dialog = importDialogProvider.get();
            dialog.showDialog(parentWindow);
        });

        if(authSession.isAdmin()) {
            topActionPanel.add(btnQuanLyTK, "h 40!, w 180!");
            topActionPanel.add(btnImport, "h 40!, w 180!");
        }
        add(topActionPanel, "w 700!, gaptop 15, wrap");
        
        JPanel gridPanel = new JPanel(new MigLayout("insets 0, gap 20", "[340!][340!]", "[160!][160!]"));
        gridPanel.setOpaque(false);

        CutoutButton btnThiSinh = createMenuButton("Thí sinh", 4, "#06b6d4", "icons/candidate.svg", SwingConstants.RIGHT);
        CutoutButton btnBangDiem = createMenuButton("Bảng điểm", 3, "#10b981", "icons/score.svg", SwingConstants.LEFT);
        CutoutButton btnNganh = createMenuButton("Ngành", 2, "#eab308", "icons/major.svg", SwingConstants.RIGHT);
        CutoutButton btnNguyenVong = createMenuButton("Nguyện vọng", 1, "#f97316", "icons/aspiration.svg", SwingConstants.LEFT);

        RoundButton btnThongKe = new RoundButton(
            "icons/play.svg", 
            Color.decode("#0066cc")
        );

        btnThongKe.addActionListener(e -> onFunctionOpen.accept("Xét tuyển"));

        gridPanel.add(btnThiSinh, "cell 0 0, grow");
        gridPanel.add(btnBangDiem, "cell 1 0, grow");
        gridPanel.add(btnNganh, "cell 0 1, grow");
        gridPanel.add(btnNguyenVong, "cell 1 1, grow");
        gridPanel.add(btnThongKe, "pos 0.5al 0.5al, w 120!, h 120!");

        add(gridPanel);
    }


    private CutoutButton createMenuButton(String title, int corner, String hexColor, String iconPath, int textPosition) {
        Color themeColor = Color.decode(hexColor); 
        
        CutoutButton btn = new CutoutButton(title, corner, themeColor);
        btn.setHorizontalTextPosition(textPosition);
        
        btn.setForeground(themeColor);
        
        btn.setIconTextGap(15);

        try {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 32, 32);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            btn.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        btn.addActionListener(e -> onFunctionOpen.accept(title));
        return btn;
    }

}
