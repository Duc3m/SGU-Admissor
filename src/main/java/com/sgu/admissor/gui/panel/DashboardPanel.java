/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.gui.components.CutoutButton;
import com.sgu.admissor.gui.components.RoundStatisticButton;


import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Duc3m
 */
public class DashboardPanel extends JPanel {

    private final Consumer<String> onFunctionOpen;

    public DashboardPanel(Consumer<String> onFunctionOpen) {
        this.onFunctionOpen = onFunctionOpen;
        initLayout();
    }

    private void initLayout() {
        
        setLayout(new MigLayout("fill, wrap 1", "[center]", "[top] 40 [center, grow]"));
        setBackground(Color.WHITE);

        putClientProperty("JTabbedPane.tabClosable", false);

        JLabel lblGreeting = new JLabel("<html><font color='#0066cc'>Xin chào</font>, Trần Đức Em</html>");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 36));
        add(lblGreeting, "gaptop 40");

        JPanel gridPanel = new JPanel(new MigLayout("insets 0, gap 20", "[340!][340!]", "[160!][160!]"));
        gridPanel.setOpaque(false);

        CutoutButton btnThiSinh = createMenuButton("Thí sinh", 4, "#06b6d4", "icons/candidate.svg", SwingConstants.RIGHT);
        CutoutButton btnBangDiem = createMenuButton("Bảng điểm", 3, "#10b981", "icons/score.svg", SwingConstants.LEFT);
        CutoutButton btnNganh = createMenuButton("Ngành", 2, "#8b5cf6", "icons/major.svg", SwingConstants.RIGHT);
        CutoutButton btnNguyenVong = createMenuButton("Nguyện vọng", 1, "#f97316", "icons/aspiration.svg", SwingConstants.LEFT);

        // Khởi tạo nút Thống kê với Class mới
        RoundStatisticButton btnThongKe = new RoundStatisticButton(
            "icons/statistic.svg", 
            Color.decode("#0066cc") // Màu xanh dương chủ đạo
        );

        btnThongKe.addActionListener(e -> onFunctionOpen.accept("Thống kê"));
        gridPanel.add(btnThongKe, "pos 0.5al 0.5al, w 120!, h 120!");

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