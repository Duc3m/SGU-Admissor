/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.bus.NganhBUS;
import com.sgu.admissor.bus.NganhToHopBUS;
import com.sgu.admissor.bus.ToHopBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.gui.MainFrame;
import jakarta.inject.Inject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import net.miginfocom.swing.MigLayout;

public class NganhPanel extends JPanel {
    private PaginatedTablePanel tablePanel;
    private final int PAGE_LIMIT = 20;
    
    private final NganhBUS nganhBUS;
    private final ToHopBUS toHopBUS;
    private final NganhToHopBUS nganhToHopBUS;
    
    private JComboBox<String> cbTieuChi;
    private JTextField txtSearch;
    private JComboBox<String> cbToHop;
    private JButton btnLoc;
    private JButton btnReload;

    @Inject
    public NganhPanel(NganhBUS nganhBUS, ToHopBUS toHopBUS, NganhToHopBUS nganhToHopBUS) {
        this.nganhBUS = nganhBUS;
        this.toHopBUS = toHopBUS;
        this.nganhToHopBUS = nganhToHopBUS;
        
        initLayout();
        initPopupMenu();
        
        loadToHopData(); 
        loadData(1);
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[250!]15[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        // --- Top Bar ---
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
        
        JButton btnAddNganh = new JButton("Thêm Ngành", new FlatSVGIcon("icons/plus.svg", 16, 16));
        btnAddNganh.setBackground(Color.decode("#eab308")); 
        btnAddNganh.setForeground(Color.WHITE);
        btnAddNganh.setFont(new Font("Segoe UI", Font.BOLD, 13));

        topBar.add(btnBack);
        topBar.add(new JLabel(""), "growx"); 
        topBar.add(btnAddNganh, "h 28!");

        // --- Side Bar ---
        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 12", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("BỘ LỌC TÌM KIẾM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 10");

        filterPanel.add(new JLabel("Tìm kiếm theo:"));
        String[] tieuChi = {"Mã ngành", "Tên ngành"}; 
        cbTieuChi = new JComboBox<>(tieuChi);
        filterPanel.add(cbTieuChi);

        filterPanel.add(new JLabel("Giá trị tìm kiếm:"));
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập thông tin...");
        filterPanel.add(txtSearch);

        filterPanel.add(new JLabel("Tổ hợp xét tuyển:"));
        cbToHop = new JComboBox<>();
        filterPanel.add(cbToHop);

        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
        
        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc = new JButton("Áp dụng", searchIcon);
        
        btnLoc.setBackground(Color.decode("#eab308"));
        btnLoc.setForeground(Color.WHITE);
        
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc, "growx, h 35!");
        filterPanel.add(filterButtons, "gaptop 15");

        add(topBar, "span 2, wrap");
        add(filterPanel, "growy");

        // --- Main Table ---
        String[] columns = {"ID", "Mã Ngành", "Tên Ngành", "Chỉ Tiêu", "Điểm Sàn", "Điểm Chuẩn", "Phương Thức", "Số TS Đăng Ký"};
        tablePanel = new PaginatedTablePanel(columns, page -> loadData(page));
        add(tablePanel, "grow");
        
        // Chỉnh kích thước từng cột
        JTable table = tablePanel.getTable();
        
        // ID
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Mã ngành 
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setMaxWidth(100);

        // Tên ngành
        table.getColumnModel().getColumn(2).setPreferredWidth(250);

        // Chỉ Tiêu
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setMaxWidth(80);

        // Điểm Sàn
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setMaxWidth(80);

        // Điểm Chuẩn
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setMaxWidth(80);

        // Phương Thức (Cần rộng để hiển thị "Tuyển thẳng, THPT...")
        table.getColumnModel().getColumn(6).setPreferredWidth(180);

        // Số TS Đăng Ký
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setMaxWidth(120);
        
        // --- Events ---
        btnLoc.addActionListener(e -> loadData(1));
        txtSearch.addActionListener(e -> loadData(1));
        btnReload.addActionListener(e -> {
            cbTieuChi.setSelectedIndex(0);
            txtSearch.setText("");
            cbToHop.setSelectedIndex(0);
            loadData(1);
        });
    }

    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem itemToHop = new JMenuItem("Xem tổ hợp môn");
        JMenuItem itemEdit = new JMenuItem("Sửa ngành");
        JMenuItem itemDelete = new JMenuItem("Xóa ngành");
        itemDelete.setForeground(Color.RED); 

        itemToHop.addActionListener(e -> {
            int selectedRow = tablePanel.getSelectedRow();
            if (selectedRow != -1) {
                String maNganh = tablePanel.getTableModel().getValueAt(selectedRow, 1).toString();
                String tenNganh = tablePanel.getTableModel().getValueAt(selectedRow, 2).toString();
                showToHopDialog(maNganh, tenNganh);
            }
        });
        
        itemEdit.addActionListener(e -> showEditNganhDialog());
        itemDelete.addActionListener(e -> deleteNganhEvent());

        popupMenu.add(itemToHop);
        popupMenu.addSeparator();
        popupMenu.add(itemEdit);
        popupMenu.add(itemDelete);

        tablePanel.setRowPopupMenu(popupMenu);
    }

    private void loadToHopData() {
        cbToHop.addItem("Tất cả");
        // Kiểm tra hàm lấy tất cả ToHop trong ToHopBUS của bạn
        // Giả sử: BUSResult<List<ToHop>> result = toHopBUS.getAll();
        // Nếu trả về trực tiếp List<ToHop>, dùng cách dưới đây:
        List<ToHop> listToHop = toHopBUS.getAllToHop().getData(); 
        if(listToHop != null) {
             for (ToHop th : listToHop) {
                cbToHop.addItem(th.getMaToHop());
            }
        }
    }

    private void loadData(int page) {
        String tieuChi = cbTieuChi.getSelectedItem().toString();
        String giaTri = txtSearch.getText().trim();
        
        Object selectedToHop = cbToHop.getSelectedItem();
        String maToHop = selectedToHop != null ? selectedToHop.toString() : "Tất cả";
        
        int totalRecords = nganhBUS.countAdvanced(tieuChi, giaTri, maToHop);
        BUSResult<List<Object[]>> result = nganhBUS.searchAdvanced(tieuChi, giaTri, maToHop, page, PAGE_LIMIT);
        
        tablePanel.getTableModel().setRowCount(0);

        if (result.isSuccess() && result.getData() != null) {
             for (Object[] row : result.getData()) {
                Nganh n = (Nganh) row[0];
                Long soTS = (Long) row[1];
                
                StringBuilder pt = new StringBuilder();
                if (n.getTuyenThang() != null && n.getTuyenThang() == true) pt.append("Tuyển thẳng, ");
                if (n.getDgnl() != null && n.getDgnl() == true) pt.append("ĐGNL, ");
                if (n.getThpt() != null && n.getThpt() == true) pt.append("THPT, ");
                if (n.getVsat() != null && n.getVsat() == true) pt.append("VSAT, ");
                String phuongThuc = pt.length() > 0 ? pt.substring(0, pt.length() - 2) : "";

                tablePanel.getTableModel().addRow(new Object[]{
                    n.getId(),
                    n.getMaNganh(),
                    n.getTenNganh(),
                    n.getChiTieu(),
                    n.getDiemSan(),
                    n.getDiemTrungTuyen(),
                    phuongThuc,
                    soTS
                });
            }
        }
        
        tablePanel.syncPagination(page, totalRecords);
    }

    private void showToHopDialog(String maNganh, String tenNganh) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tổ hợp môn - " + maNganh + " - " + tenNganh, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new MigLayout("fill, insets 15", "[grow]", "[][grow]"));
        
//        JLabel lblHeader = new JLabel("Các tổ hợp xét tuyển của ngành " + tenNganh);
//        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        panel.add(lblHeader, "wrap, gapbottom 10");

        String[] cols = {"Mã Tổ Hợp", "Môn 1", "Môn 2", "Môn 3", "Độ lệch"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);

        // Lấy danh sách tổ hợp thông qua BUS
        List<NganhToHop> listNTH = nganhToHopBUS.getNganhToHopByMaNganh(maNganh).getData();
        if (listNTH != null) {
             for (NganhToHop nth : listNTH) {
                model.addRow(new Object[]{
                    nth.getToHop().getMaToHop(), 
                    nth.getToHop().getMon1() + (nth.getHsMon1() != null && nth.getHsMon1() == 2 ? " (x2)" : ""),
                    nth.getToHop().getMon2() + (nth.getHsMon2() != null && nth.getHsMon2() == 2 ? " (x2)" : ""),
                    nth.getToHop().getMon3() + (nth.getHsMon3() != null && nth.getHsMon3() == 2 ? " (x2)" : ""),
                    nth.getDoLech()
                });
            }
        }

        panel.add(new JScrollPane(table), "grow, w 500!, h 300!");
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // Xóa ngành
    private void deleteNganhEvent() {
        int selectedRow = tablePanel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ngành để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy ID ở cột 0, Tên ngành ở cột 2
        Integer id = Integer.parseInt(tablePanel.getTableModel().getValueAt(selectedRow, 0).toString());
        String tenNganh = tablePanel.getTableModel().getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa ngành: " + tenNganh + "?\nLưu ý: Hành động này không thể hoàn tác!", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Tạo object Nganh chỉ chứa ID để khớp với tham số của NganhBUS
            Nganh nganh = new Nganh();
            nganh.setId(id);
            
            BUSResult<Nganh> result = nganhBUS.deleteNganh(nganh);
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData(1); // Nạp lại bảng
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Edit ngành
    private void showEditNganhDialog() {
        int selectedRow = tablePanel.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ngành để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Lấy ID ở cột 0, Mã ngành ở cột 1
        Integer id = Integer.parseInt(tablePanel.getTableModel().getValueAt(selectedRow, 0).toString());
        String maNganh = tablePanel.getTableModel().getValueAt(selectedRow, 1).toString();
        
        // Lấy object cũ lên để bảo toàn các trường không cho phép sửa (Điểm chuẩn, số lượng TS...)
        Nganh nganh = nganhBUS.getNganhById(id).getData();
        if (nganh == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu ngành này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Sửa thông tin ngành", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[120!][grow, fill]", "[]15[]"));

        // Khởi tạo Component cho 4 trường được phép sửa
        JTextField txtTenNganh = new JTextField(nganh.getTenNganh());
        JSpinner spinChiTieu = new JSpinner(new SpinnerNumberModel(nganh.getChiTieu() != null ? nganh.getChiTieu() : 0, 0, 10000, 1));
        JTextField txtDiemSan = new JTextField(nganh.getDiemSan() != null ? nganh.getDiemSan().toString() : "");

        JCheckBox chkTuyenThang = new JCheckBox("Tuyển thẳng", nganh.getTuyenThang() != null && nganh.getTuyenThang());
        JCheckBox chkDGNL = new JCheckBox("ĐGNL", nganh.getDgnl() != null && nganh.getDgnl());
        JCheckBox chkTHPT = new JCheckBox("THPT", nganh.getThpt() != null && nganh.getThpt());
        JCheckBox chkVSAT = new JCheckBox("VSAT", nganh.getVsat() != null && nganh.getVsat());

        // Bố trí giao diện
        panel.add(new JLabel("Mã ngành:")); 
        panel.add(new JLabel("<html><b>" + maNganh + "</b></html>")); // Hiển thị mã ngành readonly

        panel.add(new JLabel("Tên ngành (*):")); panel.add(txtTenNganh);
        panel.add(new JLabel("Chỉ tiêu:")); panel.add(spinChiTieu);
        panel.add(new JLabel("Điểm sàn:")); panel.add(txtDiemSan);

        JPanel pnlPhuongThuc = new JPanel(new MigLayout("insets 0"));
        pnlPhuongThuc.add(chkTuyenThang); pnlPhuongThuc.add(chkDGNL); 
        pnlPhuongThuc.add(chkTHPT); pnlPhuongThuc.add(chkVSAT);
        panel.add(new JLabel("Phương thức:")); panel.add(pnlPhuongThuc);

        JButton btnSave = new JButton("Lưu thay đổi");
        btnSave.setBackground(Color.decode("#10b981"));
        btnSave.setForeground(Color.WHITE);
        
        btnSave.addActionListener(e -> {
            if (txtTenNganh.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên ngành!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ghi đè 4 trường được sửa vào object cũ
            nganh.setTenNganh(txtTenNganh.getText().trim());
            nganh.setChiTieu((Integer) spinChiTieu.getValue());
            
            try {
                if (!txtDiemSan.getText().trim().isEmpty()) {
                    nganh.setDiemSan(new java.math.BigDecimal(txtDiemSan.getText().trim()));
                } else {
                    nganh.setDiemSan(null);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Điểm sàn phải là con số hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nganh.setTuyenThang(chkTuyenThang.isSelected());
            nganh.setDgnl(chkDGNL.isSelected());
            nganh.setThpt(chkTHPT.isSelected());
            nganh.setVsat(chkVSAT.isSelected());

            // Thực thi Update
            BUSResult<Nganh> updateResult = nganhBUS.updateNganh(nganh);
            if (updateResult.isSuccess()) {
                JOptionPane.showMessageDialog(dialog, updateResult.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                // Load lại đúng trang hiện tại thay vì về đầu bảng
                loadData(tablePanel.getCurrentPage()); 
            } else {
                JOptionPane.showMessageDialog(dialog, updateResult.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(btnSave, "span 2, right, gaptop 15");

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}
