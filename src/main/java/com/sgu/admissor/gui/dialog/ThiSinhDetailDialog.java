/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.google.inject.Inject;
import com.sgu.admissor.auth.AuthSession;
import com.sgu.admissor.bus.DiemThiBUS;
import com.sgu.admissor.bus.ThiSinh2025BUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.ThiSinh2025;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import com.sgu.admissor.util.WindowUtil;

/**
 *
 * @author Duc3m
 */
public class ThiSinhDetailDialog extends JDialog {

    private final ThiSinh2025BUS thiSinhBUS;
    private ThiSinh2025 currentThiSinh;
    private DiemThiBUS diemThiBUS;
    private final AuthSession authSession;
    private boolean isDataChanged = false;

    private JTextField txtCccd, txtSoBaoDanh, txtNgaySinh, txtGioiTinh, txtKhuVuc, txtDoiTuong;
    
    private JTextField txtHoTen, txtDienThoai, txtEmail, txtNoiSinh;
    
    private DefaultTableModel scoreTableModel;

    private JButton btnEditSave;
    private JButton btnCloseCancel;

    private boolean isEditMode = false;

    @Inject
    public ThiSinhDetailDialog(ThiSinh2025BUS thiSinhBUS, DiemThiBUS diemThiBUS, AuthSession authSession) {
        super(WindowUtil.findMainWindow(), "Chi tiết Thí sinh");
        this.thiSinhBUS = thiSinhBUS;
        this.diemThiBUS = diemThiBUS;
        this.authSession = authSession;

//        setTitle("Chi tiết Thí sinh");
        setModal(true);
        setResizable(false);

        initLayout();
        initEvents();
    }

    public boolean showDialog(Window parent, ThiSinh2025 thiSinh) {
        this.currentThiSinh = thiSinh;
        this.isDataChanged = false;
        
        loadDataToForm();
        setEditMode(false);

        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        
        return isDataChanged;
    }

    private void initLayout() {
        // 1. Đổi màu nền của toàn bộ Dialog thành màu trắng
        getContentPane().setBackground(Color.WHITE);

        // 2. Cấu hình lại Layout:
        // - "[grow, fill, 220::]": Ép các cột chứa TextField phải rộng TỐI THIỂU 220px. 
        // - "40": Tăng khoảng cách ở giữa 2 vế (trái/phải) từ 30 lên 40px cho đỡ sát nhau.
        setLayout(new MigLayout(
            "wrap 4, fillx, insets 25 35 25 35", 
            "[right]15[grow, fill, 220::]40[right]15[grow, fill, 220::]", 
            "[]15[]"
        ));
        
        // Thông tin thí sinh

        add(new JLabel("CCCD:"));
        txtCccd = createReadOnlyTextField();
        add(txtCccd);

        add(new JLabel("Số báo danh:"));
        txtSoBaoDanh = createReadOnlyTextField();
        add(txtSoBaoDanh);

        add(new JLabel("Họ và tên:"));
        txtHoTen = createReadOnlyTextField();
        add(txtHoTen);

        add(new JLabel("Giới tính:"));
        txtGioiTinh = createReadOnlyTextField();
        add(txtGioiTinh);

        add(new JLabel("Ngày sinh:"));
        txtNgaySinh = createReadOnlyTextField();
        add(txtNgaySinh);

        add(new JLabel("Nơi sinh:"));
        txtNoiSinh = createReadOnlyTextField();
        add(txtNoiSinh);

        add(new JLabel("Số điện thoại:"));
        txtDienThoai = createReadOnlyTextField();
        add(txtDienThoai);

        add(new JLabel("Email:"));
        txtEmail = createReadOnlyTextField();
        add(txtEmail);

        add(new JLabel("Khu vực:"));
        txtKhuVuc = createReadOnlyTextField();
        add(txtKhuVuc);

        add(new JLabel("Đối tượng:"));
        txtDoiTuong = createReadOnlyTextField();
        add(txtDoiTuong);
        
        // Điểm thi thí sinh
        add(new JSeparator(), "span 4, growx, gapy 15 5");
        add(new JLabel("<html><b style='color:#000000; font-size:13px; text-align:center; display: block'>BẢNG ĐIỂM</b></html>"), "span 4, gapbottom 5");

        String[] cols = {"Môn Thi", "Điểm Số"};
        scoreTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable scoreTable = new JTable(scoreTableModel);
        scoreTable.setRowHeight(25);
        scoreTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Đưa vào ScrollPane và add vào Form
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        add(scrollPane, "span 4, growx, h 150!");

        btnEditSave = new JButton("Sửa thông tin");
        btnCloseCancel = new JButton("Đóng");

        add(btnEditSave, "span 4, center, split 2, gapx 15, gaptop 25, w 130!, h 35!");
        add(btnCloseCancel, "w 100!, h 35!");
    }

    private void initEvents() {
        btnCloseCancel.addActionListener(e -> {
            if (isEditMode) {
                setEditMode(false);
                loadDataToForm();
            } else {
                
                dispose();
            }
        });

        btnEditSave.addActionListener(e -> {
            if(!authSession.isAdmin()) {
                JOptionPane.showMessageDialog(null, "Chỉ có admin được sửa thông tin thí sinh", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isEditMode) {
                setEditMode(true);
            } else {
                performSave();
            }
        });
    }

    private void loadDataToForm() {
        if (currentThiSinh == null) return;

        txtCccd.setText(currentThiSinh.getCccd());
        txtSoBaoDanh.setText(currentThiSinh.getSoBaoDanh());
        txtHoTen.setText(currentThiSinh.getHoTen());
        txtGioiTinh.setText(currentThiSinh.getGioiTinh());
        txtKhuVuc.setText(currentThiSinh.getKhuVuc());
        txtDoiTuong.setText(currentThiSinh.getDoiTuong().isEmpty() ? "Không" :currentThiSinh.getDoiTuong());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (currentThiSinh.getNgaySinh() != null) {
            txtNgaySinh.setText(currentThiSinh.getNgaySinh().format(formatter));
        } else {
            txtNgaySinh.setText("");
        }
        txtDienThoai.setText(currentThiSinh.getDienThoai());
        txtEmail.setText(currentThiSinh.getEmail());
        txtNoiSinh.setText(currentThiSinh.getNoiSinh());
        
        // 2. Load bảng điểm
        scoreTableModel.setRowCount(0);
        BUSResult<List<DiemThi>> resDiem = diemThiBUS.getDiemThiByCccd(currentThiSinh.getCccd());
        
        if (resDiem.isSuccess() && resDiem.getData() != null && !resDiem.getData().isEmpty()) {
            boolean hasScore = false;
            
            for (DiemThi dt : resDiem.getData()) {
                String pt = dt.getPhuongThuc() != null ? " (" + dt.getPhuongThuc() + ")" : "";
                
                Object[][] monThiVaDiem = {
                    {"Toán -" + pt, dt.getTo()},
                    {"Vật Lý -" + pt, dt.getLi()},
                    {"Hóa Học -" + pt, dt.getHo()},
                    {"Sinh Học -" + pt, dt.getSi()},
                    {"Lịch Sử -" + pt, dt.getSu()},
                    {"Địa Lý -" + pt, dt.getDi()},
                    {"Ngữ Văn -" + pt, dt.getVa()},
                    {"Tiếng Anh (Thi) -" + pt, dt.getN1Thi()},
                    {"Tiếng Anh (CC) -" + pt, dt.getN1Cc()},
                    {"CN Chăn nuôi -" + pt, dt.getCncn()},
                    {"CN Nông nghiệp -" + pt, dt.getCnnn()},
                    {"Tin Học -" + pt, dt.getTi()},
                    {"KT Pháp Luật -" + pt, dt.getKtpl()},
                    {"Đánh giá năng lực -" + pt, dt.getNl1()},
                    {"Năng Khiếu 1 -" + pt, dt.getNk1()},
                    {"Năng Khiếu 2 -" + pt, dt.getNk2()},
                    {"Năng Khiếu 3 -" + pt, dt.getNk3()},
                    {"Năng Khiếu 4 -" + pt, dt.getNk4()},
                    {"Năng Khiếu 5 -" + pt, dt.getNk5()},
                    {"Năng Khiếu 6 -" + pt, dt.getNk6()}
                };

                // Chỉ những môn nào KHÁC NULL (có điểm) mới được add vào bảng
                for (Object[] row : monThiVaDiem) {
                    if (row[1] != null) {
                        scoreTableModel.addRow(new Object[]{row[0], row[1].toString()});
                        hasScore = true;
                    }
                }
            }
            
            // Đề phòng trường hợp query ra nhưng toàn NULL hết
            if (!hasScore) {
                scoreTableModel.addRow(new Object[]{"Chưa có dữ liệu điểm", ""});
            }
            
        } else {
            scoreTableModel.addRow(new Object[]{"Chưa có dữ liệu điểm", ""});
        }
    }

    private void setEditMode(boolean enable) {
        this.isEditMode = enable;
        
        txtHoTen.setFocusable(enable);
        txtDienThoai.setFocusable(enable);
        txtEmail.setFocusable(enable);

        Color bgColor = enable ? new Color(235, 243, 255) : UIManager.getColor("TextField.background");
        txtHoTen.setBackground(bgColor);
        txtDienThoai.setBackground(bgColor);
        txtEmail.setBackground(bgColor);

        if (enable) {
            btnEditSave.setText("Lưu thay đổi");
            btnEditSave.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #10B981;"
                + "foreground: #FFFFFF;"
                + "hoverBackground: #34D399;"
                + "focusedBackground: #10B981;"
                + "focusWidth: 0;"
                + "borderWidth: 0");
            btnCloseCancel.setText("Hủy");
        } else {
            btnEditSave.setText("Sửa thông tin");
            btnEditSave.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #EAB308;"
                + "foreground: #000000;"
                + "hoverBackground: #ebbc2a;"
                + "focusedBackground: #EAB308;"
                + "focusWidth: 0;" 
                + "borderWidth: 0");
            btnCloseCancel.setText("Đóng");
        }
    }

    private void performSave() {
        String newHoTen = txtHoTen.getText().trim();
        String newDienThoai = txtDienThoai.getText().trim();
        String newEmail = txtEmail.getText().trim();
        currentThiSinh.setHoTen(newHoTen);
        currentThiSinh.setDienThoai(newDienThoai);
        currentThiSinh.setEmail(newEmail);
        
        BUSResult result = thiSinhBUS.updateThiSinh(currentThiSinh);

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            isDataChanged = true;
            setEditMode(false);
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createReadOnlyTextField() {
        JTextField txt = new JTextField();
        txt.setFocusable(false);
        return txt;
    }
    
}