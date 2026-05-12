/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;

/**
 *
 * @author Duc3m
 */
public class PaginationFooter extends JPanel {

    private JLabel lblInfo;
    private JPagination pagination;
    private JTextField txtGoTo;
    private JButton btnGo;
    
    private int totalPages = 1;
    // Sử dụng Consumer để báo ra ngoài mỗi khi người dùng muốn đổi trang
    private final Consumer<Integer> onPageChange;

    public PaginationFooter(Consumer<Integer> onPageChange) {
        this.onPageChange = onPageChange;
        initLayout();
        initEvents();
    }

    private void initLayout() {
        setLayout(new MigLayout("insets 10 0 0 0", "[][grow, center][][][]"));
        setBackground(Color.WHITE);

        lblInfo = new JLabel("Tổng số: 0");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        add(lblInfo);

        pagination = new JPagination();
        pagination.setBackground(Color.WHITE);
        pagination.setItemSize(new Dimension(45, 28));
        pagination.setItemGap(4);
        pagination.setNoVisualPadding(true);
        add(pagination);

        add(new JLabel("Đến trang:"));

        txtGoTo = new JTextField(4);
        txtGoTo.setHorizontalAlignment(JTextField.CENTER);
        add(txtGoTo);

        btnGo = new JButton("Go");
        btnGo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGo.setBackground(Color.decode("#0066cc"));
        btnGo.setForeground(Color.WHITE);
        add(btnGo);
    }

    private void initEvents() {
        // Sự kiện từ thanh điều hướng của thư viện
        pagination.addChangeListener(e -> {
            // Lấy trang hiện tại đang được chọn từ model
            int currentPage = pagination.getSelectedPage();
            
            // Chỉ thực hiện chuyển trang nếu page > 0
            if (currentPage > 0) {
                onPageChange.accept(currentPage);
            }
        });

        // Sự kiện nút Go hoặc Enter
        java.awt.event.ActionListener goAction = e -> {
            try {
                String input = txtGoTo.getText().trim();
                if (input.isEmpty()) return;

                int targetPage = Integer.parseInt(input);
                if (targetPage < 1 || targetPage > totalPages) {
                    JOptionPane.showMessageDialog(null, "Trang không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Cập nhật lại UI của thanh điều hướng và báo ra ngoài
                pagination.setPageRange(targetPage, totalPages);
                onPageChange.accept(targetPage);
                txtGoTo.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Chỉ nhập số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        };
        btnGo.addActionListener(goAction);
        txtGoTo.addActionListener(goAction);
    }

    // Các hàm để Form cha truyền dữ liệu vào cập nhật giao diện
    public void updatePagination(int currentPage, int totalPages, int totalRecords) {
        this.totalPages = totalPages;
        this.lblInfo.setText("Tổng số: " + totalRecords);
        this.pagination.setPageRange(currentPage, totalPages);
    }
    
}