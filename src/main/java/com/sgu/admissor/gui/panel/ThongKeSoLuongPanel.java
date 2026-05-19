/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.sgu.admissor.bus.NganhBUS;
import com.sgu.admissor.bus.NguyenVongBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Duc3m
 */
public class ThongKeSoLuongPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private final NganhBUS nganhBUS;
    private final NguyenVongBUS nguyenVongBUS;

    @Inject
    public ThongKeSoLuongPanel(NganhBUS nganhBUS, NguyenVongBUS nguyenVongBUS) {
        this.nganhBUS = nganhBUS;
        this.nguyenVongBUS = nguyenVongBUS;
        initLayout();
        loadData();
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[grow]", "[grow]"));
        setBackground(Color.WHITE);

        String[] columns = {
                "Mã Ngành",
                "Tên Ngành",
                "Chỉ tiêu",
                "Số lượng đậu THPT",
                "Số lượng đậu ĐGNL",
                "Số lượng đậu V-SAT"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(420);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        add(scrollPane, "grow");
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (nganhBUS == null || nguyenVongBUS == null) {
            return;
        }
        BUSResult<List<Nganh>> result = nganhBUS.getAllNganh();
        BUSResult<List<Object[]>> countResult = nguyenVongBUS.countPassedByNganhAndPhuongThuc();
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return;
        }
        Map<String, Map<String, Integer>> countMap = new HashMap<>();
        if (countResult != null && countResult.isSuccess() && countResult.getData() != null) {
            for (Object[] row : countResult.getData()) {
                if (row == null || row.length < 3) {
                    continue;
                }
                String maNganh = row[0] != null ? row[0].toString() : "";
                String phuongThuc = row[1] != null ? row[1].toString() : "";
                int count = row[2] instanceof Number ? ((Number) row[2]).intValue() : 0;
                countMap.computeIfAbsent(maNganh, key -> new HashMap<>()).put(phuongThuc, count);
            }
        }
        for (Nganh nganh : result.getData()) {
            String maNganh = nganh.getMaNganh() != null ? nganh.getMaNganh() : "";
            String tenNganh = nganh.getTenNganh() != null ? nganh.getTenNganh() : "";
            Integer chiTieu = nganh.getChiTieu();
            Map<String, Integer> ptMap = countMap.getOrDefault(maNganh, new HashMap<>());
            int slThpt = ptMap.getOrDefault("THPT", 0);
            int slDgnl = ptMap.getOrDefault("DGNL", 0);
            int slVsat = ptMap.getOrDefault("VSAT", 0);
            tableModel.addRow(new Object[]{
                maNganh,
                tenNganh,
                chiTieu != null ? chiTieu : 0,
                slThpt,
                slDgnl,
                slVsat
            });
        }
    }
}
