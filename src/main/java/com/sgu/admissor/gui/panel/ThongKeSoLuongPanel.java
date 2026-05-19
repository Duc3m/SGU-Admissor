/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author Duc3m
 */
public class ThongKeSoLuongPanel extends JPanel {
    private JTable table;

    public ThongKeSoLuongPanel() {
        initLayout();
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[grow]", "[grow]"));
        setBackground(Color.WHITE);

        String[] columns = {
                "Tên Ngành",
                "Chỉ tiêu",
                "Số lượng đậu THPT",
                "Số lượng đậu ĐGNL",
                "Số lượng đậu V-SAT"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        add(scrollPane, "grow");
    }
}
