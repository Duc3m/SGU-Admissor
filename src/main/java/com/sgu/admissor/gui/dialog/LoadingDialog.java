package com.sgu.admissor.gui.dialog;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Window;

/**
 * 
 * @author Duc3m
 */
public class LoadingDialog extends JDialog {
    
    private JProgressBar progressBar;
    private JLabel lblStatus;

    public LoadingDialog(Window parent, String titleText) {
        super(parent, titleText, ModalityType.MODELESS);
        initLayout();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initLayout() {
        JPanel loadingPanel = new JPanel(new MigLayout("insets 20", "[center]"));
        
        loadingPanel.add(new JLabel("Hệ thống đang xử lý dữ liệu, vui lòng không tắt ứng dụng!"), "wrap");
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        loadingPanel.add(progressBar, "growx, w 320!, wrap");
        
        add(loadingPanel);
    }

    /**
     * Cập nhật giá trị phần trăm và trạng thái chữ lên giao diện
     */
    public void updateProgress(int progressValue) {
        if (progressBar != null) {
            progressBar.setValue(progressValue);
        }
        if (lblStatus != null) {
            lblStatus.setText("Tiến độ: " + progressValue + "%");
        }
    }
}