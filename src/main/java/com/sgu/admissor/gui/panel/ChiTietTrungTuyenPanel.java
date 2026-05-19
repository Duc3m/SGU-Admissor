/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.bus.ChiTietTrungTuyenBUS;
import com.sgu.admissor.bus.NganhBUS;
import com.sgu.admissor.bus.ToHopBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.ToHop;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Duc3m
 */
public class ChiTietTrungTuyenPanel extends JPanel {
    private final ChiTietTrungTuyenBUS chiTietTrungTuyenBUS;
    private final NganhBUS nganhBUS;
    private final ToHopBUS toHopBUS;
    private JComboBox<String> cbNganh;
    private PaginatedTablePanel tablePanel;
    private JTextField txtCccd;
    private JTextField txtHoTen;
    private JComboBox<String> cbToHop;
    private JTextField txtDiemMin;
    private JTextField txtDiemMax;
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public ChiTietTrungTuyenPanel(NganhBUS nganhBUS, ToHopBUS toHopBUS, ChiTietTrungTuyenBUS chiTietTrungTuyenBUS) {
        this.nganhBUS = nganhBUS;
        this.toHopBUS = toHopBUS;
        this.chiTietTrungTuyenBUS = chiTietTrungTuyenBUS;
        initLayout();
        loadNganhData();
        loadToHopData();
        loadData(1);
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[300!]-40[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new MigLayout("insets 0", "[]10[]push"));
        topBar.setBackground(Color.WHITE);

        JLabel lblNganh = new JLabel("Ngành:");
        lblNganh.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cbNganh = new JComboBox<>();
        cbNganh.addItem("Tất cả");

        topBar.add(lblNganh);
        topBar.add(cbNganh, "w 220!, h 30!");

        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 8", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("BỘ LỌC TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 6");

        filterPanel.add(new JLabel("CCCD:"));
        txtCccd = new JTextField();
        txtCccd.putClientProperty("JTextField.placeholderText", "Nhập CCCD...");
        txtCccd.putClientProperty("JComponent.focusWidth", 0);
        filterPanel.add(txtCccd, "h 30!");

        filterPanel.add(new JLabel("Họ và Tên:"));
        txtHoTen = new JTextField();
        txtHoTen.putClientProperty("JTextField.placeholderText", "Nhập họ tên...");
        txtHoTen.putClientProperty("JComponent.focusWidth", 0);
        filterPanel.add(txtHoTen, "h 30!");

        filterPanel.add(new JLabel("Tổ hợp:"));
        cbToHop = new JComboBox<>();
        filterPanel.add(cbToHop, "h 30!");

        filterPanel.add(new JLabel("Khoảng điểm số:"));
        JPanel rangePanel = new JPanel(new MigLayout("insets 0", "[grow][8!][grow]"));
        rangePanel.setBackground(filterPanel.getBackground());
        txtDiemMin = new JTextField();
        txtDiemMin.putClientProperty("JTextField.placeholderText", "Từ");
        txtDiemMin.putClientProperty("JComponent.focusWidth", 0);
        txtDiemMax = new JTextField();
        txtDiemMax.putClientProperty("JTextField.placeholderText", "Đến");
        txtDiemMax.putClientProperty("JComponent.focusWidth", 0);
        rangePanel.add(txtDiemMin, "growx, h 30!");
        rangePanel.add(new JLabel("–"), "");
        rangePanel.add(txtDiemMax, "growx, h 30!");
        filterPanel.add(rangePanel);

        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());

        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));

        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc    = new JButton("Áp dụng", searchIcon);
        btnLoc.setBackground(Color.decode("#0066cc"));
        btnLoc.setForeground(Color.WHITE);
        btnReload.setMargin(new Insets(6, 10, 6, 10));
        btnLoc.setMargin(new Insets(6, 10, 6, 10));
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc,    "growx, h 35!");
        filterPanel.add(filterButtons, "gaptop 8");

        String[] columns = {"Tên", "CCCD", "Điểm trúng tuyển", "Phương thức trúng tuyển", "Tổ hợp trúng tuyển"};
        tablePanel = new PaginatedTablePanel(columns, this::loadData);

        add(topBar,      "span 2, wrap");
        add(filterPanel, "growy");
        add(tablePanel,  "grow");

        btnLoc.addActionListener(e -> loadData(1));
        txtCccd.addActionListener(e -> loadData(1));
        txtHoTen.addActionListener(e -> loadData(1));
        txtDiemMin.addActionListener(e -> loadData(1));
        txtDiemMax.addActionListener(e -> loadData(1));
        cbNganh.addActionListener(e -> loadData(1));
        cbToHop.addActionListener(e -> loadData(1));

        btnReload.addActionListener(e -> resetFilters());
    }

    private void loadNganhData() {
        cbNganh.removeAllItems();
        cbNganh.addItem("Tất cả");

        if (nganhBUS == null) {
            return;
        }

        BUSResult<List<Nganh>> result = nganhBUS.getAllNganh();
        if (result != null && result.isSuccess() && result.getData() != null) {
            for (Nganh nganh : result.getData()) {
                String maNganh = nganh.getMaNganh() != null ? nganh.getMaNganh() : "";
                String tenNganh = nganh.getTenNganh() != null ? nganh.getTenNganh() : "";
                cbNganh.addItem("(" + maNganh + ") " + tenNganh);
            }
        }
    }

    private void loadToHopData() {
        cbToHop.removeAllItems();
        cbToHop.addItem("Tất cả");
        if (toHopBUS == null) {
            return;
        }
        List<ToHop> listToHop = toHopBUS.getAllToHop().getData();
        if (listToHop != null) {
            for (ToHop toHop : listToHop) {
                if (toHop != null && toHop.getMaToHop() != null) {
                    cbToHop.addItem(toHop.getMaToHop());
                }
            }
        }
    }

    private void resetFilters() {
        if (cbNganh.getItemCount() > 0) {
            cbNganh.setSelectedIndex(0);
        }
        txtCccd.setText("");
        txtHoTen.setText("");
        if (cbToHop.getItemCount() > 0) {
            cbToHop.setSelectedIndex(0);
        }
        txtDiemMin.setText("");
        txtDiemMax.setText("");
        loadData(1);
    }

    public void loadData(int page) {
        String cccd = txtCccd.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String toHop = cbToHop.getSelectedItem() != null ? cbToHop.getSelectedItem().toString() : "Tất cả";
        String maNganh = parseMaNganh();
        BigDecimal diemMin = parseDiem(txtDiemMin.getText().trim());
        BigDecimal diemMax = parseDiem(txtDiemMax.getText().trim());

        int totalRecords = chiTietTrungTuyenBUS.countAdvanced(cccd, hoTen, toHop, maNganh, diemMin, diemMax);
        BUSResult<List<NguyenVong>> result = chiTietTrungTuyenBUS.searchAdvanced(
            cccd, hoTen, toHop, maNganh, diemMin, diemMax, page, tablePanel.getRowsPerPage()
        );

        tablePanel.getTableModel().setRowCount(0);
        if (result.isSuccess() && result.getData() != null) {
            for (NguyenVong nv : result.getData()) {
                String hoTenVal = nv.getThiSinh() != null ? nv.getThiSinh().getHoTen() : "";
                String cccdVal = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : "";
                tablePanel.getTableModel().addRow(new Object[]{
                    hoTenVal,
                    cccdVal,
                    formatDiem(nv.getDiemXetTuyen()),
                    nv.getPhuongThuc(),
                    nv.getToHopMon()
                });
            }
        }

        tablePanel.syncPagination(page, totalRecords);
    }

    private String parseMaNganh() {
        Object selected = cbNganh.getSelectedItem();
        if (selected == null) {
            return "Tất cả";
        }
        String value = selected.toString().trim();
        if (value.equals("Tất cả")) {
            return "Tất cả";
        }
        int start = value.indexOf('(');
        int end = value.indexOf(')');
        if (start == 0 && end > start) {
            return value.substring(start + 1, end).trim();
        }
        return value;
    }

    private BigDecimal parseDiem(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String formatDiem(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return String.format("%.2f", value);
    }
}
