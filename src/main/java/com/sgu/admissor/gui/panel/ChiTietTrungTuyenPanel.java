/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.*;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.gui.dialog.LoadingDialog;
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
    private final NganhToHopBUS nganhToHopBUS;
    private final NguyenVongBUS nguyenVongBUS;
    private final NguyenVongBUSV2 nvBUSV2;
    private final NganhBUSV2 nganhBUSV2;
    private final AuthSession authSession;
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
    public ChiTietTrungTuyenPanel(NganhBUS nganhBUS,
            ToHopBUS toHopBUS,
            NganhToHopBUS nganhToHopBUS,
            NguyenVongBUS nguyenVongBUS,
            NguyenVongBUSV2 nvBUSV2,
            NganhBUSV2 nganhBUSV2,
            ChiTietTrungTuyenBUS chiTietTrungTuyenBUS,
            AuthSession authSession) {
        this.nganhBUS = nganhBUS;
        this.toHopBUS = toHopBUS;
        this.nganhToHopBUS = nganhToHopBUS;
        this.nguyenVongBUS = nguyenVongBUS;
        this.nvBUSV2 = nvBUSV2;
        this.nganhBUSV2 = nganhBUSV2;
        this.chiTietTrungTuyenBUS = chiTietTrungTuyenBUS;
        this.authSession = authSession;
        initLayout();
        loadNganhData();
        loadToHopData();
        loadData(1);
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[300!]-40[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new MigLayout("insets 0", "[]10[]10[]"));
        topBar.setBackground(Color.WHITE);

        JLabel lblNganh = new JLabel("Ngành:");
        lblNganh.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cbNganh = new JComboBox<>();
        cbNganh.addItem("Tất cả");

        topBar.add(lblNganh);
        topBar.add(cbNganh, "w 220!, h 30!");

        JButton btnTinhDiem = new JButton("Thực hiện tính điểm");
        btnTinhDiem.putClientProperty("JButton.buttonType", "roundRect");
        btnTinhDiem.setBackground(Color.decode("#16a34a"));
        btnTinhDiem.setForeground(Color.WHITE);
        btnTinhDiem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnXetTuyen = new JButton("Thực hiện xét tuyển");
        btnXetTuyen.putClientProperty("JButton.buttonType", "roundRect");
        btnXetTuyen.setBackground(Color.decode("#2563eb"));
        btnXetTuyen.setForeground(Color.WHITE);
        btnXetTuyen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if(authSession.isAdmin()) {
            topBar.add(btnTinhDiem, "h 26!");
            topBar.add(btnXetTuyen, "h 26!");
        }

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
        filterPanel.add(cbToHop, "h 30!, w 200!");

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

        String[] columns = {"Tên", "CCCD", "Tên ngành", "Điểm trúng tuyển", "Phương thức trúng tuyển", "Tổ hợp trúng tuyển"};
        tablePanel = new PaginatedTablePanel(columns, this::loadData);
        JTable table = tablePanel.getTable();
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Tên
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(280); // Tên ngành (để rộng hẳn để không bị che tên ngành dài)
        table.getColumnModel().getColumn(3).setPreferredWidth(95);  // Điểm chuẩn / Điểm trúng tuyển
        table.getColumnModel().getColumn(4).setPreferredWidth(130); // Phương thức trúng tuyển
        table.getColumnModel().getColumn(5).setPreferredWidth(130); // Tổ hợp trúng tuyển

        add(topBar,      "span 2, wrap");
        add(filterPanel, "growy");
        add(tablePanel,  "grow");

        btnLoc.addActionListener(e -> loadData(1));
        cbNganh.addActionListener(e -> {
            loadToHopData();
            loadData(1);
        });

        btnTinhDiem.addActionListener(e -> handleTinhDiem());
        btnXetTuyen.addActionListener(e -> handleXetTuyen());
        
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
        String maNganh = parseMaNganh();
        if (!"Tất cả".equals(maNganh) && nganhToHopBUS != null) {
            BUSResult<List<NganhToHop>> result = nganhToHopBUS.getNganhToHopByMaNganh(maNganh);
            if (result != null && result.isSuccess() && result.getData() != null) {
                for (NganhToHop nth : result.getData()) {
                    ToHop toHop = nth.getToHop();
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
            return;
        }

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
    
    private void handleTinhDiem() {
        final LoadingDialog progressDialog = new LoadingDialog(null, "Đang tính toán điểm xét tuyển...");
        
        SwingWorker<BUSResult, Integer> worker = new SwingWorker<BUSResult, Integer>() {
            @Override
            protected BUSResult doInBackground() throws Exception {
                try {
                    return nvBUSV2.tinhDiemTatCaV2(progress -> publish(progress));
                } catch (Throwable t) {
                    t.printStackTrace();
                    return BUSResult.error("Lỗi: " + t.getMessage());
                }
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressDialog.updateProgress(latestProgress);
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    BUSResult result = get();
                    if (result != null && result.isSuccess()) {
                        JOptionPane.showMessageDialog(null, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, result != null ? result.getMessage() : "Lỗi", "Thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        progressDialog.setVisible(true);
        worker.execute();
    }
    
    private void handleXetTuyen() {
        final LoadingDialog progressDialog = new LoadingDialog(null, "Đang lọc ảo và xét kết quả trúng tuyển...");
        
        SwingWorker<BUSResult, Integer> worker = new SwingWorker<BUSResult, Integer>() {
            @Override
            protected BUSResult doInBackground() throws Exception {
                try {
                    return nganhBUSV2.tinhKetQuaTatCaNganhV2(progress -> publish(progress));
                } catch (Throwable t) {
                    t.printStackTrace();
                    return BUSResult.error("Lỗi: " + t.getMessage());
                }
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressDialog.updateProgress(latestProgress);
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    BUSResult result = get();
                    if (result != null && result.isSuccess()) {
                        JOptionPane.showMessageDialog(null, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, result != null ? result.getMessage() : "Lỗi", "Thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        progressDialog.setVisible(true);
        worker.execute();
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
        String toHop = parseMaToHop();
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
                String nganhVal = nv.getNganh().getTenNganh();
                tablePanel.getTableModel().addRow(new Object[]{
                    hoTenVal,
                    cccdVal,
                    nganhVal,
                    formatDiem(nv.getDiemXetTuyen()),
                    nv.getPhuongThuc(),
                    nv.getToHopMon()
                });
            }
        }

        tablePanel.syncPagination(page, totalRecords);
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
