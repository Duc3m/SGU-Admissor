/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
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
    
    @Inject
    public MainFrame(Provider<ThiSinhPanel> thiSinhPanelProvider) {
        this.thiSinhPanelProvider = thiSinhPanelProvider;
        
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/logo.png"));
        Image scaledImage = logo.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaledImage);
        setTitle("SGU Admissor");
        setSize(1280, 720);
        setMinimumSize(new Dimension(950, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initTabbedPane();

        DashboardPanel homeDashboard = new DashboardPanel(this::openFunctionTab);
        tabbedPane.addTab("Home", new FlatSVGIcon("icons/home.svg", 16, 16), homeDashboard);
        
        add(tabbedPane);
    }


    private void initTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
        
        // Không cho phép đóng tab index 0 (Home)
        tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback",
            (BiConsumer<JTabbedPane, Integer>) (pane, index) -> {
                if (index > 0) pane.removeTabAt(index);
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
                newForm = new BangDiemPanel();
                newIcon = new FlatSVGIcon("icons/score.svg");
                break;
            case "Ngành":
                newForm = new NganhPanel();
                newIcon = new FlatSVGIcon("icons/major.svg");
                break;
            case "Nguyện vọng":
                newForm = new NguyenVongPanel();
                newIcon = new FlatSVGIcon("icons/aspiration.svg");
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

}
