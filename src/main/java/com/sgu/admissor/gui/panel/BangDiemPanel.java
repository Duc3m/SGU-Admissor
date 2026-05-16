/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.bus.DiemCongBUS;
import com.sgu.admissor.bus.ToHopBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.gui.MainFrame;
import jakarta.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import net.miginfocom.swing.MigLayout;

public class BangDiemPanel extends JPanel {
    private PaginatedTablePanel tablePanel;
    private final int PAGE_LIMIT = 20;
    
    private final DiemCongBUS diemCongBUS;
    private final ToHopBUS toHopBUS;
    
    // Các Component bộ lọc
    private JTextField txtSearchCCCD;
    private JComboBox<String> cbToHop;
    private JComboBox<String> cbPhuongThuc;
    
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public BangDiemPanel(DiemCongBUS diemCongBUS, ToHopBUS toHopBUS) {
        this.diemCongBUS = diemCongBUS;
        this.toHopBUS = toHopBUS;
        
        initLayout();
        loadFilterData(); 
        loadData(1);
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[250!]15[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        // --- TOP BAR ---
        JPanel topBar = new JPanel(new MigLayout("insets 0", "[]10[grow]10[]"));
        topBar.setBackground(Color.WHITE);

        JButton btnBack = new JButton(new FlatSVGIcon("icons/arrow-left.svg", 22, 22));
        btnBack.putClientProperty("JButton.buttonType", "toolBarButton");
        btnBack.setBorderPainted(false);
        btnBack.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame mainFrame) {
                mainFrame.backToDashboard();
            }
        });

        topBar.add(btnBack);
        topBar.add(new JLabel(""), "growx"); 

        // --- SIDE BAR (BỘ LỌC) ---
        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 12", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("BỘ LỌC TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 10");

        // 1. Tìm theo CCCD
        filterPanel.add(new JLabel("Tìm kiếm CCCD:"));
        txtSearchCCCD = new JTextField();
        txtSearchCCCD.putClientProperty("JTextField.placeholderText", "Nhập CCCD...");
        filterPanel.add(txtSearchCCCD);

        // 2. Tổ hợp môn và phương thức
        filterPanel.add(new JLabel("Tổ hợp môn:"));
        cbToHop = new JComboBox<>();
        filterPanel.add(cbToHop);
        
        filterPanel.add(new JLabel("Phương thức:"));
        cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "THPT", "ĐGNL", "VSAT", "Tuyển thẳng"});
        filterPanel.add(cbPhuongThuc);

        // Nút chức năng
        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
        
        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc = new JButton("Áp dụng", searchIcon);
        btnLoc.setBackground(Color.decode("#3b82f6")); // Dùng màu xanh Blue cho Bảng Điểm
        btnLoc.setForeground(Color.WHITE);
        
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc, "growx, h 35!");
        filterPanel.add(filterButtons, "gaptop 15");

        add(topBar, "span 2, wrap");
        add(filterPanel, "growy");

        // --- MAIN TABLE ---
        String[] columns = {"ID", "CCCD", "Thứ Tự NV", "Tổ Hợp", "Phương thức", "Điểm CC", "Điểm ƯTXT", "Tổng Điểm Cộng"};
        tablePanel = new PaginatedTablePanel(columns, page -> loadData(page));
        add(tablePanel, "grow");
        
        // Căn chỉnh độ rộng cột
        JTable table = tablePanel.getTable();
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(120); table.getColumnModel().getColumn(1).setMaxWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100); table.getColumnModel().getColumn(2).setMaxWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100); table.getColumnModel().getColumn(3).setMaxWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(120); 
        table.getColumnModel().getColumn(5).setPreferredWidth(100); 
        table.getColumnModel().getColumn(6).setPreferredWidth(100); 
        table.getColumnModel().getColumn(7).setPreferredWidth(120);

        // --- EVENTS ---
        btnLoc.addActionListener(e -> loadData(1));
        txtSearchCCCD.addActionListener(e -> loadData(1)); // Nhấn Enter để tìm
        btnReload.addActionListener(e -> {
            txtSearchCCCD.setText("");
            cbToHop.setSelectedIndex(0);
            cbPhuongThuc.setSelectedIndex(0);
            loadData(1);
        });
    }

    private void loadFilterData() {
        cbToHop.addItem("Tất cả");
        if (toHopBUS != null) {
            List<ToHop> listToHop = toHopBUS.getAllToHop().getData(); 
            if(listToHop != null) {
                 for (ToHop th : listToHop) {
                    cbToHop.addItem(th.getMaToHop());
                }
            }
        }
    }

    private void loadData(int page) {
        String cccd = txtSearchCCCD.getText().trim();
        
        Object selTh = cbToHop.getSelectedItem();
        String toHop = selTh != null ? selTh.toString() : "Tất cả";
        
        Object selPt = cbPhuongThuc.getSelectedItem();
        String phuongThuc = selPt != null ? selPt.toString() : "Tất cả";
        
        int totalRecords = diemCongBUS.countAdvanced(cccd, toHop, phuongThuc);
        BUSResult<List<Object[]>> result = diemCongBUS.searchAdvanced(cccd, toHop, phuongThuc, page, PAGE_LIMIT);
        
        tablePanel.getTableModel().setRowCount(0);

        if (result.isSuccess() && result.getData() != null) {
             for (Object[] row : result.getData()) {
                // row[0] là DiemCong, row[1] là Thứ tự (Integer)
                DiemCong dc = (DiemCong) row[0];
                Integer thuTu = (Integer) row[1];

                tablePanel.getTableModel().addRow(new Object[]{
                    dc.getId(),
                    dc.getThiSinh() != null ? dc.getThiSinh().getCccd() : "",
                    thuTu != null ? thuTu : "N/A", // Nếu không join được (ko có NV) thì để N/A
                    dc.getToHop() != null ? dc.getToHop().getMaToHop() : "",
                    dc.getPhuongThuc() != null ? dc.getPhuongThuc() : "",
                    dc.getDiemCc() != null ? dc.getDiemCc() : "0.00",
                    dc.getDiemUtxt() != null ? dc.getDiemUtxt() : "0.00",
                    dc.getDiemTong() != null ? dc.getDiemTong() : "0.00",
                });
            }
        }
        tablePanel.syncPagination(page, totalRecords);
    }
}
