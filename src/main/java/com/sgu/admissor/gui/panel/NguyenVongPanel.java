/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.bus.NganhToHopBUS;
import com.sgu.admissor.bus.NguyenVongBUS;
import com.sgu.admissor.bus.ToHopBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.gui.MainFrame;
import jakarta.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import net.miginfocom.swing.MigLayout;

public class NguyenVongPanel extends JPanel {
    private PaginatedTablePanel tablePanel;
    private final int PAGE_LIMIT = 20;
    
    private final NguyenVongBUS nguyenVongBUS;
    private final ToHopBUS toHopBUS;
    private final NganhToHopBUS nganhToHopBUS;
    
    // Các Component bộ lọc
    private JComboBox<String> cbTieuChi;
    private JTextField txtSearch;
    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbKetQua;
    private JComboBox<String> cbToHop;
    
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public NguyenVongPanel(NguyenVongBUS nguyenVongBUS, ToHopBUS toHopBUS, NganhToHopBUS nganhToHopBUS) {
        this.nguyenVongBUS = nguyenVongBUS;
        this.toHopBUS = toHopBUS;
        this.nganhToHopBUS = nganhToHopBUS;
        
        initLayout();
        initPopupMenu();
        
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
        
        // Hiện tại tạm ẩn tính năng Thêm, nhưng cứ tạo nút cho chuẩn Form
        JButton btnAddNV = new JButton("Thêm Nguyện Vọng", new FlatSVGIcon("icons/plus.svg", 16, 16));
        btnAddNV.setBackground(Color.decode("#f97316")); // Màu cam của Nguyện Vọng
        btnAddNV.setForeground(Color.WHITE);
        btnAddNV.setFont(new Font("Segoe UI", Font.BOLD, 13));
        // btnAddNV.addActionListener(...) -> Khoan làm theo yêu cầu

        topBar.add(btnBack);
        topBar.add(new JLabel(""), "growx"); 
        topBar.add(btnAddNV, "h 28!");

        // --- SIDE BAR (BỘ LỌC) ---
        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 12", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("BỘ LỌC TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 10");

        // 1. Text Search
        filterPanel.add(new JLabel("Tìm kiếm theo:"));
        String[] tieuChi = {"CCCD", "Mã ngành", "Tên ngành"};
        cbTieuChi = new JComboBox<>(tieuChi);
        filterPanel.add(cbTieuChi);

        filterPanel.add(new JLabel("Giá trị tìm kiếm:"));
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập thông tin...");
        filterPanel.add(txtSearch);

        // 2. Phương thức
        filterPanel.add(new JLabel("Phương thức xét tuyển:"));
        cbPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "THPT", "ĐGNL", "VSAT", "Tuyển thẳng"});
        filterPanel.add(cbPhuongThuc);

        // 3. Tổ hợp môn
        filterPanel.add(new JLabel("Tổ hợp môn:"));
        cbToHop = new JComboBox<>();
        filterPanel.add(cbToHop);

        // 4. Kết quả
        filterPanel.add(new JLabel("Kết quả xét tuyển:"));
        cbKetQua = new JComboBox<>(new String[]{"Tất cả", "Trúng tuyển", "Không trúng tuyển", "Chưa xét"});
        filterPanel.add(cbKetQua);

        // Nút chức năng
        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
        
        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc = new JButton("Áp dụng", searchIcon);
        btnLoc.setBackground(Color.decode("#f97316"));
        btnLoc.setForeground(Color.WHITE);
        
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc, "growx, h 35!");
        filterPanel.add(filterButtons, "gaptop 15");

        add(topBar, "span 2, wrap");
        add(filterPanel, "growy");

        // --- MAIN TABLE ---
        String[] columns = {"ID", "CCCD", "Mã Ngành", "Tên Ngành", "Thứ Tự", "Điểm Xét Tuyển", "Kết Quả"};
        tablePanel = new PaginatedTablePanel(columns, page -> loadData(page));
        add(tablePanel, "grow");
        
        // Điều chỉnh Width các cột
        JTable table = tablePanel.getTable();
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  table.getColumnModel().getColumn(0).setMaxWidth(70);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100); table.getColumnModel().getColumn(1).setMaxWidth(130); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(90);  table.getColumnModel().getColumn(2).setMaxWidth(110); // Mã Ngành
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Tên Ngành (Thả tự do)
        table.getColumnModel().getColumn(4).setPreferredWidth(70);  table.getColumnModel().getColumn(4).setMaxWidth(90);  // Thứ tự
        table.getColumnModel().getColumn(5).setPreferredWidth(100); table.getColumnModel().getColumn(5).setMaxWidth(130); // Điểm xét tuyển
        table.getColumnModel().getColumn(6).setPreferredWidth(120); table.getColumnModel().getColumn(6).setMaxWidth(150); // Kết Quả

        // --- EVENTS ---
        btnLoc.addActionListener(e -> loadData(1));
        txtSearch.addActionListener(e -> loadData(1));
        btnReload.addActionListener(e -> {
            cbTieuChi.setSelectedIndex(0);
            txtSearch.setText("");
            cbPhuongThuc.setSelectedIndex(0);
            cbToHop.setSelectedIndex(0);
            cbKetQua.setSelectedIndex(0);
            loadData(1);
        });
    }

    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem itemDetail = new JMenuItem("Xem chi tiết");
        // JMenuItem itemEdit = new JMenuItem("Sửa nguyện vọng"); -> Khoan làm
        // JMenuItem itemDelete = new JMenuItem("Xóa nguyện vọng"); -> Khoan làm

        itemDetail.addActionListener(e -> showDetailDialog());

        popupMenu.add(itemDetail);
        tablePanel.setRowPopupMenu(popupMenu);
    }

    private void loadFilterData() {
        cbToHop.addItem("Tất cả");
        // Tái sử dụng ToHopBUS để đổ dữ liệu vào combobox
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
        String tieuChi = cbTieuChi.getSelectedItem().toString();
        String giaTri = txtSearch.getText().trim();
        
        Object selPt = cbPhuongThuc.getSelectedItem();
        String phuongThuc = selPt != null ? selPt.toString() : "Tất cả";
        
        Object selTh = cbToHop.getSelectedItem();
        String toHop = selTh != null ? selTh.toString() : "Tất cả";
        
        Object selKq = cbKetQua.getSelectedItem();
        String ketQua = selKq != null ? selKq.toString() : "Tất cả";
        
        int totalRecords = nguyenVongBUS.countAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua);
        BUSResult<List<NguyenVong>> result = nguyenVongBUS.searchAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua, page, PAGE_LIMIT);
        
        tablePanel.getTableModel().setRowCount(0);

        if (result.isSuccess() && result.getData() != null) {
             for (NguyenVong nv : result.getData()) {
                tablePanel.getTableModel().addRow(new Object[]{
                    nv.getId(),
                    nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : "",
                    nv.getNganh() != null ? nv.getNganh().getMaNganh() : "",
                    nv.getNganh() != null ? nv.getNganh().getTenNganh() : "",
                    nv.getThuTu(),
                    nv.getDiemXetTuyen(),
                    nv.getKetQua()
                });
            }
        }
        tablePanel.syncPagination(page, totalRecords);
    }

    // --- HÀM XEM CHI TIẾT NGUYỆN VỌNG ---
    private void showDetailDialog() {
        int selectedRow = tablePanel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nguyện vọng để xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy ID nguyện vọng
        Integer id = Integer.parseInt(tablePanel.getTableModel().getValueAt(selectedRow, 0).toString());
        
        // Query database lấy chi tiết đầy đủ
        NguyenVong nv = nguyenVongBUS.getNguyenVongById(id).getData();
        
        String maNganh = nv.getNganh().getMaNganh();
        String maToHop = nv.getToHopMon();
        String doLechText = "0.0"; 
        
        // Gọi hàm đã có sẵn: Lấy tất cả tổ hợp của ngành này
        BUSResult<List<NganhToHop>> nthResult = nganhToHopBUS.getNganhToHopByMaNganh(maNganh);
        
        if (nthResult.isSuccess() && nthResult.getData() != null) {
            // Lặp qua danh sách để tìm tổ hợp trùng khớp
            for (NganhToHop nth : nthResult.getData()) {
                if (nth.getToHop().getMaToHop().equals(maToHop)) {
                    // Nếu tìm thấy, lấy độ lệch và thoát vòng lặp
                    doLechText = nth.getDoLech() != null ? nth.getDoLech().toString() : "0.0";
                    break; 
                }
            }
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết nguyện vọng", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[120!][grow, fill]", "[]12[]"));

        // Add thông tin
        panel.add(new JLabel("<html><b>THÔNG TIN XÉT TUYỂN</b></html>"), "span 2, gapbottom 10");
        
        panel.add(new JLabel("Phương thức:")); 
        panel.add(new JLabel(nv.getPhuongThuc() != null ? nv.getPhuongThuc() : "N/A"));
        
        panel.add(new JLabel("Tổ hợp môn:")); 
        panel.add(new JLabel(nv.getToHopMon() != null ? nv.getToHopMon() : "N/A"));
        
        panel.add(new JLabel("Độ lệch tổ hợp:")); 
        panel.add(new JLabel("<html><b style='color:#e67e22;'>" + doLechText + "</b></html>"));
        
        panel.add(new JLabel("Điểm THXT:")); 
        panel.add(new JLabel(nv.getDiemThxt() != null ? nv.getDiemThxt().toString() : "N/A"));
        
        panel.add(new JLabel("Điểm UTQĐ:")); 
        panel.add(new JLabel(nv.getDiemUtqd() != null ? nv.getDiemUtqd().toString() : "N/A"));
        
        panel.add(new JLabel("Điểm Cộng:")); 
        panel.add(new JLabel(nv.getDiemCong() != null ? nv.getDiemCong().toString() : "0.0"));

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        panel.add(btnClose, "span 2, right, gaptop 15");

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}
