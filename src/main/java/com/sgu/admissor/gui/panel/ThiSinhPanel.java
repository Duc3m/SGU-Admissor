/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.ExcelImportBUS;
import com.sgu.admissor.bus.ExcelImportBUSV2;
import com.sgu.admissor.bus.ThiSinh2025BUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.ThiSinh2025;
import com.sgu.admissor.gui.MainFrame;
import com.sgu.admissor.gui.dialog.ThiSinhDetailDialog;
import com.sgu.admissor.util.ExcelFileClassifier.FileType;
import com.sgu.admissor.util.ExcelImportHelper;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.miginfocom.swing.MigLayout;
/**
 *
 * @author Duc3m
 */
public class ThiSinhPanel extends JPanel {
    private PaginatedTablePanel tablePanel;
    
    private final int PAGE_LIMIT = 20;
    private final ThiSinh2025BUS thiSinhBUS;
    private final ExcelImportBUSV2 excelImportV2;
    private final Provider<ThiSinhDetailDialog> thiSinhDetailProvider;
    private final AuthSession authsession;
    private List<ThiSinh2025> currentList = new ArrayList<>();
    private SwingWorker<Void, Void> importWorker;
    private JDialog loadingDialog;
    
    private JComboBox<String> cbTieuChi;
    private JTextField txtSearch;
    private JComboBox<String> cbDoiTuong;
    private JComboBox<String> cbKhuVuc;
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public ThiSinhPanel(ThiSinh2025BUS thiSinhBUS,
            ExcelImportBUSV2 excelImportV2,
            Provider<ThiSinhDetailDialog> thiSinhDetailProvider,
            AuthSession authsession) {
        this.thiSinhBUS = thiSinhBUS;
        this.excelImportV2 = excelImportV2;
        this.thiSinhDetailProvider = thiSinhDetailProvider;
        this.authsession = authsession;
        
        initLayout();
        initPopupMenu();
        
        loadData(1);
    }

    private void initLayout() {
        // Layout
        // Cột: [Sidebar] [Bảng]
        // Hàng: [TopBar]
        //       [Nội dung]
        setLayout(new MigLayout("fill, insets 15", "[250!]15[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        // Top Bar
        JPanel topBar = new JPanel(new MigLayout("insets 0", "[]10[grow]10[]"));
        topBar.setBackground(Color.WHITE);

        JButton btnBack = new JButton(new FlatSVGIcon("icons/arrow-left.svg", 22, 22));
        btnBack.putClientProperty("JButton.buttonType", "toolBarButton");
        btnBack.setBorderPainted(false);
        btnBack.addActionListener(e -> {
            // Tìm Frame chứa panel này
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame mainFrame) {
                mainFrame.backToDashboard();
            }
        });
        
        JButton btnImport = new JButton("Import Thí sinh", new FlatSVGIcon("icons/excel.svg", 16, 16));
        btnImport.setBackground(Color.decode("#10b981"));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImport.addActionListener(e -> { importExcelEvent(); });
        
        JButton btnImport2 = new JButton("Import Điểm DGNL - VSAT", new FlatSVGIcon("icons/excel.svg", 16, 16));
        btnImport2.setBackground(Color.decode("#10b981"));
        btnImport2.setForeground(Color.WHITE);
        btnImport2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnImport2.addActionListener(e -> { importExcelEvent2(); });

        topBar.add(btnBack);
        if(authsession.isAdmin()) {
            topBar.add(btnImport, "h 30!");
            topBar.add(btnImport2, "h 30!");
        }

        // Side Bar
        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 12", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("BỘ LỌC TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 10");

        // Tìm kiếm theo tiêu chí
        filterPanel.add(new JLabel("Tìm kiếm theo:"));
        String[] tieuChi = {"CCCD", "Họ và Tên", "ID Thí sinh"};
        cbTieuChi = new JComboBox<>(tieuChi);
        filterPanel.add(cbTieuChi);

        // Ô nhập dữ liệu tìm kiếm
        filterPanel.add(new JLabel("Giá trị tìm kiếm:"));
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập thông tin...");
        txtSearch.putClientProperty("JComponent.focusWidth", 0);
        filterPanel.add(txtSearch);

        // Đối tượng
        filterPanel.add(new JLabel("Đối tượng ưu tiên:"));
        String[] doiTuong = {"Tất cả", "Không", "01", "03b", "03c", "03d", 
            "04a", "04b", "05b", "06a", "06b", "06c", "07a"};
        cbDoiTuong = new JComboBox<>(doiTuong);
        filterPanel.add(cbDoiTuong);

        // Khu vực
        filterPanel.add(new JLabel("Khu vực tuyển sinh:"));
        String[] khuVuc = {"Tất cả", "1", "2", "2NT", "3"};
        cbKhuVuc = new JComboBox<>(khuVuc);
        filterPanel.add(cbKhuVuc);

        // Nút chức năng (Reload và Lọc) xếp ngang cạnh nhau ở dưới cùng sidebar
        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
        
        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc = new JButton("Áp dụng", searchIcon);
        btnReload.setMargin(new Insets(6, 10, 6, 10));
        btnLoc.setMargin(new Insets(6, 10, 6, 10));
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc, "growx, h 35!");
        
        btnLoc.setBackground(Color.decode("#0066cc"));
        btnLoc.setForeground(Color.WHITE);
        
        filterButtons.add(btnReload, "growx");
        filterButtons.add(btnLoc, "growx");
        
        filterPanel.add(filterButtons, "gaptop 15");

        // Hàng 1: TopBar
        add(topBar, "span 2, wrap");
        
        // Hàng 2: Cột 1 là Sidebar, Cột 2 là TablePanel
        add(filterPanel, "growy");

        // Panel bảng có phân trang
        String[] columns = {"ID", "CCCD", "Họ và Tên", "Giới tính", "Đối tượng", "Khu vực"};
        tablePanel = new PaginatedTablePanel(columns, page -> {
            loadData(page);
        });
        add(tablePanel, "grow");
        
        btnLoc.addActionListener(e -> loadData(1)); // Nhấn lọc -> Load trang 1
    
        txtSearch.addActionListener(e -> loadData(1)); // Nhấn Enter ở ô text -> Lọc luôn

        btnReload.addActionListener(e -> {
            // Reset toàn bộ bộ lọc
            cbTieuChi.setSelectedIndex(0);
            txtSearch.setText("");
            cbDoiTuong.setSelectedIndex(0);
            cbKhuVuc.setSelectedIndex(0);
            // Load lại dữ liệu như ban đầu
            loadData(1);
        });
    }
    
    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem itemView = new JMenuItem("Xem/Sửa thông tin");


        itemView.addActionListener(e -> {
            JTable table = tablePanel.getTable();
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {

                // Giả sử cột 0 chứa ID thí sinh
                int idThiSinh = (int) table.getValueAt(selectedRow, 0); 

                // Lấy object ThiSinh2025 từ DB lên
                ThiSinh2025 thiSinh = thiSinhBUS.getThiSinhById(idThiSinh).getData(); 

                if(thiSinh != null) {
                    ThiSinhDetailDialog dialog = thiSinhDetailProvider.get();
                    boolean isChanged = dialog.showDialog(SwingUtilities.getWindowAncestor(this), thiSinh);

                    // Nếu người dùng có bấm Lưu thay đổi thì load lại bảng
                    if (isChanged) {
                        loadData(1); 
                    }
                }
            }
        });

        popupMenu.add(itemView);

        tablePanel.setRowPopupMenu(popupMenu);
    }

    private void loadData(int page) {
        String tieuChi = cbTieuChi != null ? cbTieuChi.getSelectedItem().toString() : "CCCD";
        String giaTri = txtSearch != null ? txtSearch.getText().trim() : "";
        String doiTuong = cbDoiTuong != null ? cbDoiTuong.getSelectedItem().toString() : "Tất cả";
        String khuVuc = cbKhuVuc != null ? cbKhuVuc.getSelectedItem().toString() : "Tất cả";
        
        int totalRecords = thiSinhBUS.countAdvanced(tieuChi, giaTri, doiTuong, khuVuc);
        BUSResult<List<ThiSinh2025>> result = thiSinhBUS.searchAdvanced(tieuChi, giaTri, doiTuong, khuVuc, page, PAGE_LIMIT);
        currentList = result.getData();
        
        tablePanel.getTableModel().setRowCount(0);

        for (ThiSinh2025 ts : currentList) {
            tablePanel.getTableModel().addRow(new Object[]{
                    ts.getId(),
                    ts.getCccd(),
                    ts.getHoTen(),
                    ts.getGioiTinh(),
                    ts.getDoiTuong().equals("")?"Không":ts.getDoiTuong(), 
                    ts.getKhuVuc(),
            });
        }

        tablePanel.syncPagination(page, totalRecords);
    }
    
    public void importExcelEvent() {
        ExcelImportHelper.importSingleExcel(this, excelImportV2, FileType.DS_THI_SINH, () -> loadData(1));
    }

    public void importExcelEvent2() {
        ExcelImportHelper.importSingleExcel(this, excelImportV2, FileType.DIEM_DGNL_VSAT, () -> loadData(1));
    }
}
