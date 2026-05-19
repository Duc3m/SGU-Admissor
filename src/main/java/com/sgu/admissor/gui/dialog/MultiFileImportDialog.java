package com.sgu.admissor.gui.dialog;

import com.sgu.admissor.bus.ExcelImportBUS;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.util.ExcelFileClassifier;
import com.sgu.admissor.util.ExcelFileClassifier.ClassificationResult;
import com.sgu.admissor.util.ExcelFileClassifier.FileType;
import net.miginfocom.swing.MigLayout;

import jakarta.inject.Inject;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiFileImportDialog extends JDialog {
    private static final int EXPECTED_FILE_COUNT = 8;

    private final ExcelImportBUS excelImportBUS;
    private JTextArea txtFiles;
    private JLabel lblCount;
    private final Map<String, File> selectedFiles = new LinkedHashMap<>();
    private BUSResult lastResult;
    private JDialog loadingDialog;
    private SwingWorker<BUSResult, Void> importWorker;

    @Inject
    public MultiFileImportDialog(ExcelImportBUS excelImportBUS) {
        super((Window) null, "Import dữ liệu xét tuyển", ModalityType.APPLICATION_MODAL);
        this.excelImportBUS = excelImportBUS;
        initLayout();
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(null);
    }

    private void initLayout() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[grow]10[]"));

        JLabel lblTitle = new JLabel("Chọn 8 file Excel để import");
        lblTitle.setFont(lblTitle.getFont().deriveFont(15f));
        add(lblTitle, "wrap");

        txtFiles = new JTextArea();
        txtFiles.setEditable(false);
        txtFiles.setEnabled(false);
        txtFiles.setLineWrap(true);
        txtFiles.setWrapStyleWord(true);
        txtFiles.setTransferHandler(new FileDropHandler());

        JScrollPane scrollPane = new JScrollPane(txtFiles);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow, wrap");

        JPanel footer = new JPanel(new MigLayout("insets 0", "[grow]10[]10[]"));
        footer.setBackground(Color.WHITE);
        lblCount = new JLabel("Đã chọn: 0/" + EXPECTED_FILE_COUNT);

        JButton btnBrowse = new JButton("Chọn file");
        JButton btnImport = new JButton("Bắt đầu import");
        JButton btnCancel = new JButton("Đóng");

        btnImport.setBackground(Color.decode("#10B981"));
        btnImport.setForeground(Color.WHITE);

        btnBrowse.addActionListener(e -> browseFiles());
        btnImport.addActionListener(e -> handleImport());
        btnCancel.addActionListener(e -> dispose());

        footer.add(lblCount, "growx");
        footer.add(btnBrowse, "h 32!");
        footer.add(btnImport, "h 32!");
        footer.add(btnCancel, "h 32!");
        add(footer, "growx");
    }

    private void browseFiles() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            addFiles(fc.getSelectedFiles());
            refreshFileList();
        }
    }

    private void refreshFileList() {
        txtFiles.setText("");
        int count = selectedFiles.size();
        lblCount.setText("Đã chọn: " + count + "/" + EXPECTED_FILE_COUNT);
        StringBuilder sb = new StringBuilder();
        for (File file : selectedFiles.values()) {
            if (file == null) {
                continue;
            }
            sb.append(file.getAbsolutePath()).append("\n");
        }
        txtFiles.setText(sb.toString());
    }

    private void addFiles(File[] files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            selectedFiles.put(file.getAbsolutePath(), file);
        }
    }

    private void handleImport() {
        List<String> errors = validateFiles();
        if (!errors.isEmpty()) {
            lastResult = BUSResult.error("Không thể import");
            showMessage("Không thể import", errors, JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (importWorker != null) {
            return;
        }

        importWorker = new SwingWorker<>() {
            @Override
            protected BUSResult doInBackground() {
                return excelImportBUS.importFromFiles(selectedFiles.values().toArray(new File[0]));
            }

            @Override
            protected void done() {
                hideLoading();
                importWorker = null;
                try {
                    BUSResult result = get();
                    if (result == null || !result.isSuccess()) {
                        lastResult = result != null ? result : BUSResult.error("Import thất bại");
                        String message = result != null ? result.getMessage() : "Import thất bại.";
                        showMessage("Import thất bại", List.of(message), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    lastResult = result;

                    List<String> warnings = new ArrayList<>();
                    Object data = result.getData();
                    if (data instanceof List<?>) {
                        for (Object item : (List<?>) data) {
                            if (item != null) {
                                warnings.add(item.toString());
                            }
                        }
                    }

                    if (warnings.isEmpty()) {
                        JOptionPane.showMessageDialog(MultiFileImportDialog.this, result.getMessage(), "Import thành công", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessage(result.getMessage(), warnings, JOptionPane.INFORMATION_MESSAGE);
                    }
                    dispose();
                } catch (Exception ex) {
                    lastResult = BUSResult.error("Import thất bại");
                    showMessage("Import thất bại", List.of("Lỗi khi import: " + ex.getMessage()), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        importWorker.execute();
        showLoading();
    }

    private void showLoading() {
        if (loadingDialog != null) {
            return;
        }
        loadingDialog = new JDialog(this, "Đang xử lý...", ModalityType.MODELESS);
        JPanel loadingPanel = new JPanel(new MigLayout("insets 20", "[center]"));
        loadingPanel.add(new JLabel("Hệ thống đang nạp dữ liệu từ Excel, vui lòng không tắt ứng dụng!"), "wrap");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingPanel.add(progressBar, "growx, w 320!");
        loadingDialog.add(loadingPanel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setVisible(true);
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dispose();
            loadingDialog = null;
        }
    }

    public BUSResult showDialog(Window parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
        return lastResult;
    }

    private List<String> validateFiles() {
        List<String> errors = new ArrayList<>();
        if (selectedFiles.isEmpty()) {
            errors.add("Vui lòng chọn đủ " + EXPECTED_FILE_COUNT + " file.");
            return errors;
        }
        if (selectedFiles.size() != EXPECTED_FILE_COUNT) {
            errors.add("Số lượng file không đúng. Cần " + EXPECTED_FILE_COUNT + " file.");
            return errors;
        }

        for (File file : selectedFiles.values()) {
            if (file == null || !file.exists()) {
                errors.add("File không tồn tại hoặc bị lỗi: " + safeFileName(file));
                return errors;
            }
        }

        List<ClassificationResult> classified = ExcelFileClassifier.classifyAll(selectedFiles.values().toArray(new File[0]));
        Map<FileType, File> fileMap = new EnumMap<>(FileType.class);
        for (ClassificationResult result : classified) {
            if (result.getType() == FileType.UNKNOWN) {
                errors.add("Không nhận diện được file: " + safeFileName(result.getFile()));
                continue;
            }
            if (fileMap.containsKey(result.getType())) {
                errors.add("Trùng loại file: " + result.getType().name());
                continue;
            }
            fileMap.put(result.getType(), result.getFile());
        }

        if (fileMap.size() != EXPECTED_FILE_COUNT) {
            errors.add("Thiếu file bắt buộc hoặc có file không đúng định dạng.");
        }

        return errors;
    }

    private void showMessage(String title, List<String> lines, int messageType) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("- ").append(line).append("\n");
        }
        area.setText(sb.toString());
        area.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(520, 180));
        JOptionPane.showMessageDialog(this, scrollPane, title, messageType);
    }

    private String safeFileName(File file) {
        if (file == null) {
            return "(null)";
        }
        String name = file.getName();
        return name != null ? name : file.getPath();
    }

    private final class FileDropHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                Object data = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (data instanceof List<?>) {
                    List<?> list = (List<?>) data;
                    List<File> files = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof File) {
                            files.add((File) item);
                        }
                    }
                    addFiles(files.toArray(new File[0]));
                    refreshFileList();
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
    }
}
