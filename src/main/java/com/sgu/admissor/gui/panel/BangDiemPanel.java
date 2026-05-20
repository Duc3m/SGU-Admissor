/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.DiemCongBUS;
import com.sgu.admissor.bus.ExcelImportBUSV2;
import com.sgu.admissor.bus.ToHopBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.gui.MainFrame;
import com.sgu.admissor.util.ExcelFileClassifier;
import com.sgu.admissor.util.ExcelImportHelper;
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
    private final ExcelImportBUSV2 excelImportV2;
    private final AuthSession authSession;
    
    // Các Component bộ lọc
    private JTextField txtSearchCCCD;
    private JComboBox<String> cbToHop;
    private JComboBox<String> cbPhuongThuc;
    
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public BangDiemPanel(DiemCongBUS diemCongBUS, ToHopBUS toHopBUS,
            ExcelImportBUSV2 excelImportV2,
            AuthSession authSession) {
        this.diemCongBUS = diemCongBUS;
        this.toHopBUS = toHopBUS;
        this.excelImportV2 = excelImportV2;
        this.authSession = authSession;
        
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
        
        JButton btnImport = new JButton("Import Điểm Utxt", new FlatSVGIcon("icons/excel.svg", 16, 16));
        btnImport.setBackground(Color.decode("#10b981"));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImport.addActionListener(e -> { importExcelEvent(); });
        
        JButton btnImport2 = new JButton("Import Điểm QĐTA", new FlatSVGIcon("icons/excel.svg", 16, 16));
        btnImport2.setBackground(Color.decode("#10b981"));
        btnImport2.setForeground(Color.WHITE);
        btnImport2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImport2.addActionListener(e -> { importExcelEvent2(); });

        topBar.add(btnBack);
        if(authSession.isAdmin()) {
            topBar.add(btnImport, "h 30!");
            topBar.add(btnImport2, "h 30!");
        }
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
        filterPanel.add(cbToHop, "w 200!");
        
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
        String[] columns = {"STT", "CCCD Thí Sinh", "Tên Thí Sinh", "Số Lượng ĐC"};
        tablePanel = new PaginatedTablePanel(columns, page -> loadData(page));
        add(tablePanel, "grow");
        
        // Căn chỉnh cột
        JTable table = tablePanel.getTable();
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150); table.getColumnModel().getColumn(1).setMaxWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(250); 
        table.getColumnModel().getColumn(3).setPreferredWidth(120); table.getColumnModel().getColumn(3).setMaxWidth(150); // Số lượng

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
    
    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemDetail = new JMenuItem("Xem chi tiết điểm");
        itemDetail.addActionListener(e -> showDetailDialog());
        popupMenu.add(itemDetail);
        tablePanel.setRowPopupMenu(popupMenu);
    }

    private void loadFilterData() {
        cbToHop.addItem("Tất cả");
        if (toHopBUS != null) {
            List<ToHop> listToHop = toHopBUS.getAllToHop().getData();
            if (listToHop != null) {
                for (ToHop toHop : listToHop) {
                    if (toHop != null && toHop.getMaToHop() != null) {
                        String maToHop = toHop.getMaToHop();
                        String tenToHop = toHop.getTenToHop() != null ? toHop.getTenToHop() : "";
                        if (!tenToHop.isEmpty()) {
                            cbToHop.addItem(maToHop + " - " + tenToHop);
                        } else {
                            cbToHop.addItem(maToHop);
                        }
                    }
                }
            }
        }
    }
    
    private String parseMaToHop() {
        Object selected = cbToHop.getSelectedItem();
        if (selected == null) {
            return "Tất cả";
        }
        String value = selected.toString().trim();
        if (value.equals("Tất cả")) {
            return "Tất cả";
        }
        int splitIndex = value.indexOf(" - ");
        if (splitIndex > 0) {
            return value.substring(0, splitIndex).trim();
        }
        return value;
    }

    private void loadData(int page) {
        String cccd = txtSearchCCCD.getText().trim();
        String toHop = parseMaToHop();
        String phuongThuc = cbPhuongThuc.getSelectedItem() != null ? cbPhuongThuc.getSelectedItem().toString() : "Tất cả";
        
        int totalRecords = diemCongBUS.countDistinctCccd(cccd, toHop, phuongThuc);
        BUSResult<List<Object[]>> result = diemCongBUS.searchGroupedCandidates(cccd, toHop, phuongThuc, page, PAGE_LIMIT);
        
        tablePanel.getTableModel().setRowCount(0);

        if (result.isSuccess() && result.getData() != null) {
            int stt = (page - 1) * PAGE_LIMIT + 1;
            for (Object[] row : result.getData()) {
                // row[0] là CCCD, row[1] là Tên thí sinh, row[2] là Số lượng
                tablePanel.getTableModel().addRow(new Object[]{
                    stt++,
                    row[0] != null ? row[0].toString() : "",
                    row[1] != null ? row[1].toString() : "N/A", 
                    row[2] != null ? row[2].toString() : "0"
                });
            }
        }
        tablePanel.syncPagination(page, totalRecords);
    }
    
    private void showDetailDialog() {
        int selectedRow = tablePanel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thí sinh để xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cccd = tablePanel.getTableModel().getValueAt(selectedRow, 1).toString();
        String hoTen = tablePanel.getTableModel().getValueAt(selectedRow, 2).toString();
        
        BUSResult<List<DiemCong>> res = diemCongBUS.getDiemCongByCccd(cccd);
        if (!res.isSuccess() || res.getData() == null || res.getData().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Thí sinh này không có dữ liệu điểm cộng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Bảng Điểm Cộng Chi Tiết", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new MigLayout("fill, insets 15", "[grow]", "[][grow][]"));

        panel.add(new JLabel("<html><b style='color:#3b82f6; font-size:14px;'>DANH SÁCH ĐIỂM CỘNG THEO TỔ HỢP/NGÀNH</b></html>"), "wrap, gapbottom 10");

        String infoHtml = String.format(
            "<html>" +
            "<div style='margin-bottom: 8px;'>Họ và tên: <b style='font-size:12px;'>%s</b></div>" +
            "<div>CCCD: <b style='font-size:12px;'>%s</b></div>" +
            "</html>", 
            hoTen, cccd
        );
        panel.add(new JLabel(infoHtml), "wrap, gapbottom 12");
        
        // Tạo bảng con trong form
        String[] cols = {"Mã Ngành", "Tên Ngành", "Phương Thức", "Tổ Hợp", "Điểm CC", "Điểm ƯTXT", "Tổng Cộng"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; } // Không cho edit
        };
        JTable detailTable = new JTable(model);

        // Đổ dữ liệu vào bảng con
        for (DiemCong dc : res.getData()) {
            model.addRow(new Object[]{
                dc.getNganh() != null ? dc.getNganh().getMaNganh() : "",
                dc.getNganh() != null ? dc.getNganh().getTenNganh() : "",
                dc.getPhuongThuc() != null ? dc.getPhuongThuc() : "",
                dc.getToHop() != null ? dc.getToHop().getMaToHop() : "",
                dc.getDiemCc() != null ? dc.getDiemCc() : "0.00",
                dc.getDiemUtxt() != null ? dc.getDiemUtxt() : "0.00",
                dc.getDiemTong() != null ? dc.getDiemTong() : "0.00"
            });
        }

        // Add ScrollPane để cuộn nếu thí sinh có quá nhiều tổ hợp
        JScrollPane scrollPane = new JScrollPane(detailTable);
        panel.add(scrollPane, "grow, w 800!, h 250!, wrap"); // Rộng 800px, cao 250px

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        panel.add(btnClose, "right, gaptop 10");

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public void importExcelEvent() {
        ExcelImportHelper.importSingleExcel(this, excelImportV2, ExcelFileClassifier.FileType.UU_TIEN_XET_TUYEN, () -> loadData(1));
    }
    
    public void importExcelEvent2() {
        ExcelImportHelper.importSingleExcel(this, excelImportV2, ExcelFileClassifier.FileType.QUY_DOI_TIENG_ANH, () -> loadData(1));
    }
}
