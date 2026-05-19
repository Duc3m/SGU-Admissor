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
    private final ThongKeSoLuongPanel thongKeSoLuongPanel;

    @Inject
    public XetTuyenPanel(ChiTietTrungTuyenPanel chiTietTrungTuyenPanel, ThongKeSoLuongPanel thongKeSoLuongPanel) {
        this.chiTietTrungTuyenPanel = chiTietTrungTuyenPanel;
        this.thongKeSoLuongPanel = thongKeSoLuongPanel;
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        setBackground(Color.WHITE);

        // Tabs con chứa chi tiết và thống kê
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Chi tiết trúng tuyển", chiTietTrungTuyenPanel);
        tabbedPane.addTab("Thống kê số lượng", thongKeSoLuongPanel);
        add(tabbedPane, "grow");
    }
}
