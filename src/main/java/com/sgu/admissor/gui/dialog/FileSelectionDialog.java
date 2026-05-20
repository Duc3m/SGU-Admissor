/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.dialog;

import com.sgu.admissor.util.WindowUtil;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Duc3m
 */
public class FileSelectionDialog extends JDialog {
    private JTextField txtFile1, txtFile2, txtFile3;
    private File[] selectedFiles = new File[3];
    private boolean isConfirmed = false;

    public FileSelectionDialog(Window parent) {
        super(WindowUtil.findMainWindow(), "Chọn các tệp dữ liệu", ModalityType.APPLICATION_MODAL);
        initLayout();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initLayout() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new MigLayout("wrap 3, insets 20", "[right]10[grow, fill]10[100!]", "[]15[]"));

        txtFile1 = createReadOnlyTextField();
        txtFile2 = createReadOnlyTextField();
        txtFile3 = createReadOnlyTextField();

        addRow("Danh sách thí sinh:", txtFile1, 0);
        addRow("File điểm thi:", txtFile2, 1);
        addRow("Bảng quy đổi:", txtFile3, 2);

        JButton btnCancel = new JButton("Đóng");
        JButton btnImport = new JButton("Bắt đầu Import");
        btnImport.setBackground(Color.decode("#10B981"));
        btnImport.setForeground(Color.WHITE);

        btnImport.addActionListener(e -> {
            if (validateFiles()) {
                isConfirmed = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());

        add(btnImport, "span 3, center, split 2, gaptop 10, w 140!, h 35!");
        add(btnCancel, "w 100!, h 35!");
    }

    private void addRow(String label, JTextField txt, int index) {
        add(new JLabel(label));
        add(txt);
        JButton btnBrowse = new JButton("Chọn file");
        btnBrowse.addActionListener(e -> browseFile(txt, index));
        add(btnBrowse);
    }

    private void browseFile(JTextField txt, int index) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFiles[index] = fc.getSelectedFile();
            txt.setText(selectedFiles[index].getAbsolutePath());
        }
    }

    private boolean validateFiles() {
        for (File f : selectedFiles) {
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ 3 file!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private JTextField createReadOnlyTextField() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        return txt;
    }

    public File[] showDialog() {
        setVisible(true);
        return isConfirmed ? selectedFiles : null;
    }
}