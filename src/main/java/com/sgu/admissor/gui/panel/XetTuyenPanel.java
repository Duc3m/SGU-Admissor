/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;


/**
 *
 * @author Duc3m
 */
public class XetTuyenPanel extends JPanel {
    private final ChiTietTrungTuyenPanel chiTietTrungTuyenPanel;

    @Inject
    public XetTuyenPanel(ChiTietTrungTuyenPanel chiTietTrungTuyenPanel) {
        this.chiTietTrungTuyenPanel = chiTietTrungTuyenPanel;
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);

        // Nút Xét tuyển nổi bật
        JButton btnXetTuyen = new JButton("🚀 THỰC HIỆN XÉT TUYỂN");
        btnXetTuyen.putClientProperty("JButton.buttonType", "roundRect");
        btnXetTuyen.setBackground(Color.decode("#ef4444"));
        btnXetTuyen.setForeground(Color.WHITE);
        btnXetTuyen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(btnXetTuyen, "center, wrap 20");

        // Tabs con chứa chi tiết và thống kê
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Chi tiết trúng tuyển", chiTietTrungTuyenPanel);
        tabbedPane.addTab("Thống kê số lượng", new ThongKeSoLuongPanel());
        add(tabbedPane, "grow");
    }
}
