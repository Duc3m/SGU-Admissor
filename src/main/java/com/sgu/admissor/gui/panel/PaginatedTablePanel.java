/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.sgu.admissor.gui.components.PaginationFooter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Duc3m
 */
public class PaginatedTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationFooter paginationFooter;
    
    private final int ROWS_PER_PAGE = 20;
    private final Consumer<Integer> onPageLoad;

    public PaginatedTablePanel(String[] columnNames, Consumer<Integer> onPageLoad) {
        this.onPageLoad = onPageLoad;
        initLayout(columnNames);
    }

    private void initLayout(String[] columnNames) {
        setLayout(new MigLayout("fill, wrap 1, insets 0", "[fill]", "[grow, fill][]"));
        setBackground(Color.WHITE);

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Khởi tạo bảng có màu xen kẽ bằng renderer gốc
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                // Nếu dòng đó không bị bôi đen (không được chọn)
                if (!isRowSelected(row)) {
                    // Dòng chẵn màu Trắng, Dòng lẻ màu Xanh lơ siêu nhạt (hoặc xám nhạt tùy ý)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 243, 255)); 
                }
                return c;
            }
        };

        // Cấu hình kích thước và Font
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFocusable(false);


        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE); 
        add(scrollPane);

        paginationFooter = new PaginationFooter(page -> onPageLoad.accept(page));
        add(paginationFooter);
    }


    public void setRowPopupMenu(JPopupMenu popupMenu) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                // Kiểm tra xem có phải là sự kiện kích hoạt popup không
                if (e.isPopupTrigger()) {
                    // Lấy vị trí dòng con trỏ chuột đang trỏ tới
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        // Bắt buộc JTable bôi đen cái dòng vừa click chuột phải
                        table.setRowSelectionInterval(row, row);
                        // Hiển thị Menu
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }


    public DefaultTableModel getTableModel() { return tableModel; }
    public int getRowsPerPage() { return ROWS_PER_PAGE; }
    
    public int getSelectedRow() { return table.getSelectedRow(); }

    public void syncPagination(int currentPage, int totalRecords) {
        int totalPages = (int) Math.ceil((double) totalRecords / ROWS_PER_PAGE);
        paginationFooter.updatePagination(currentPage, totalPages, totalRecords);
    }
    
}