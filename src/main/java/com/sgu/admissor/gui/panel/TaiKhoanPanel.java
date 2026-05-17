/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.sgu.admissor.bus.UserBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.User;
import com.sgu.admissor.gui.MainFrame;
import com.sgu.admissor.gui.dialog.ChangePasswordDialog;
import com.sgu.admissor.gui.dialog.CreateUserDialog;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import net.miginfocom.swing.MigLayout;
/**
 *
 * @author Duc3m
 */
public class TaiKhoanPanel extends JPanel {
    private PaginatedTablePanel tablePanel;
    private final int PAGE_LIMIT = 20;
    
    private final UserBUS userBUS;
    private final Provider<CreateUserDialog> createUserProvider;
    private final Provider<ChangePasswordDialog> changePassProvider;

    private JTextField txtSearchUsername;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    private JButton btnLoc;
    private JButton btnReload;
    
    private List<User> currentList = new ArrayList<>();

    @Inject
    public TaiKhoanPanel(UserBUS userBUS,
            Provider<CreateUserDialog> createUserProvider,
            Provider<ChangePasswordDialog> changePassProvider) {
        this.userBUS = userBUS;
        this.createUserProvider = createUserProvider;
        this.changePassProvider = changePassProvider;
        
        initLayout();
        initPopupMenu();
        
        loadData(1);
    }

    private void initLayout() {
        setLayout(new MigLayout("fill, insets 15", "[250!]15[grow, fill]", "[][grow, fill]"));
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new MigLayout("insets 0", "[]10[grow]10[]")); // 'push' để đẩy nút thêm về góc phải
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
        
        JButton btnAdd = new JButton("Thêm tài khoản", new FlatSVGIcon("icons/plus.svg", 16, 16));
        btnAdd.setBackground(Color.decode("#0066cc"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        topBar.add(btnBack);
        topBar.add(btnAdd, "h 30!");

        JPanel filterPanel = new JPanel(new MigLayout("wrap 1, insets 20, gapy 12", "[fill]"));
        filterPanel.setBackground(new Color(252, 252, 252));
        filterPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("LỌC TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        filterPanel.add(lblTitle, "gapbottom 10");

        filterPanel.add(new JLabel("Tên đăng nhập:"));
        txtSearchUsername = new JTextField();
        txtSearchUsername.putClientProperty("JTextField.placeholderText", "Nhập username...");
        filterPanel.add(txtSearchUsername);

        filterPanel.add(new JLabel("Vai trò:"));
        String[] roles = {"Tất cả", "admin", "user"};
        cbRole = new JComboBox<>(roles);

        filterPanel.add(cbRole);

        filterPanel.add(new JLabel("Trạng thái:"));
        String[] statuses = {"Tất cả", "Đang hoạt động", "Bị khóa"};
        cbStatus = new JComboBox<>(statuses);
        filterPanel.add(cbStatus);

        JPanel filterButtons = new JPanel(new MigLayout("insets 0", "[grow]10[grow]"));
        filterButtons.setBackground(filterPanel.getBackground());
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
        
        btnReload = new JButton("Làm mới", new FlatSVGIcon("icons/arrow-clockwise.svg", 16, 16));
        btnLoc = new JButton("Áp dụng", searchIcon);
        btnLoc.setBackground(Color.decode("#0066cc"));
        btnLoc.setForeground(Color.WHITE);
        
        filterButtons.add(btnReload, "growx, h 35!");
        filterButtons.add(btnLoc, "growx, h 35!");
        filterPanel.add(filterButtons, "gaptop 15");

        add(topBar, "span 2, wrap");
        add(filterPanel, "growy");

        String[] columns = {"ID", "Tên đăng nhập", "Vai trò", "Trạng thái"};
        tablePanel = new PaginatedTablePanel(columns, page -> {
             loadData(page);
        });
        
        add(tablePanel, "grow");
        JTable table = tablePanel.getTable();
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        btnLoc.addActionListener(e -> loadData(1));
    
        txtSearchUsername.addActionListener(e -> loadData(1));

        btnReload.addActionListener(e -> {
            cbRole.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            loadData(1);
        });
        
        btnAdd.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            CreateUserDialog dialog = createUserProvider.get();
            dialog.showDialog(parentWindow);

            if (dialog.isSaved()) {
                loadData(1);
            }
        });
    }
    
    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem itemEdit = new JMenuItem("Đổi mật khẩu");
        JMenuItem itemChange = new JMenuItem("Đổi quyền");
        JMenuItem itemLock = new JMenuItem("Khóa/Mở khóa tài khoản");
        JMenuItem itemDelete = new JMenuItem("Xóa tài khoản");
        itemDelete.setForeground(Color.RED); 

        popupMenu.add(itemEdit);
        popupMenu.add(itemChange);
        popupMenu.add(itemLock);
        popupMenu.addSeparator();
        popupMenu.add(itemDelete);
        
        itemEdit.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JTable table = tablePanel.getTable();
            int selectedUserId = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            ChangePasswordDialog dialog = changePassProvider.get();
            dialog.showDialog(parentWindow, selectedUserId, false);
        });
        
        itemChange.addActionListener(e -> {
            JTable table = tablePanel.getTable();
            int selectedUserId = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            if(selectedUserId == 1) {
                JOptionPane.showMessageDialog(this, "Không thể đổi quyền tài khoản admin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BUSResult result = userBUS.changeRole(selectedUserId);
            if(result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            loadData(1);
        });
        
        itemLock.addActionListener(e -> {
            JTable table = tablePanel.getTable();
            int selectedUserId = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            if(selectedUserId == 1) {
                JOptionPane.showMessageDialog(this, "Không thể khóa tài khoản admin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BUSResult result = userBUS.toggleLock(selectedUserId);
            if(result.isSuccess()) {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            loadData(1);
        });
        
        itemDelete.addActionListener(e -> {
            JTable table = tablePanel.getTable();
            int selectedUserId = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            if(selectedUserId == 1) {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản admin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Bạn có thật sự muốn xóa tài khoản này?", 
                    "Xác nhận xóa", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                BUSResult result = userBUS.deleteUser(selectedUserId);
                if(result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                loadData(1);
            };
        });

        tablePanel.setRowPopupMenu(popupMenu);
    }
    
    private void loadData(int page) {
        String username = txtSearchUsername != null ? txtSearchUsername.getText().trim() : "";
        String selectedRole = cbRole != null ? cbRole.getSelectedItem().toString() : "Tất cả";
        int statusIndex = cbStatus != null ? cbStatus.getSelectedIndex() : 0;
        Integer status = (statusIndex == 0) ? -1 : (statusIndex == 1 ? 1 : 0);

        int totalRecords = userBUS.countAdvanced(username, selectedRole, status);

        BUSResult<List<User>>result = userBUS.searchAdvanced(username, selectedRole, status, page, PAGE_LIMIT);
        currentList = result.getData(); 


        tablePanel.getTableModel().setRowCount(0);

        for (User user : currentList) {
            boolean isActive = user.getIsActive() != null && user.getIsActive(); 
            String statusName = isActive ? "Đang hoạt động" : "Bị khóa";

            tablePanel.getTableModel().addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    statusName
            });
        }

        tablePanel.syncPagination(page, totalRecords);
    }
}

class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = (value != null) ? value.toString() : "";

        if (!isSelected) {
            if ("Đang hoạt động".equals(status)) {
                c.setForeground(Color.decode("#10b981"));
            } else if ("Bị khóa".equals(status)) {
                c.setForeground(Color.decode("#dc3545"));
            } else {
                c.setForeground(table.getForeground());
            }
        }
        c.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        return c;
    }
}