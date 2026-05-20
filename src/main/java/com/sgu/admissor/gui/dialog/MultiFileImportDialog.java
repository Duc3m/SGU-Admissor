package com.sgu.admissor.gui.dialog;

import com.sgu.admissor.bus.ExcelImportBUSV2;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.util.ExcelFileClassifier;
import com.sgu.admissor.util.ExcelFileClassifier.ClassificationResult;
import com.sgu.admissor.util.ExcelFileClassifier.FileType;
import com.sgu.admissor.util.WindowUtil;
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
    
    private final ExcelImportBUSV2 excelImportBUSV2;
    private JTextArea txtFiles;
    private JLabel lblCount;
    private final Map<String, File> selectedFiles = new LinkedHashMap<>();
    private BUSResult lastResult;
    private LoadingDialog loadingDialog;

    @Inject
    public MultiFileImportDialog(ExcelImportBUSV2 excelImportBUSV2) {
        super(WindowUtil.findMainWindow(), "Import dữ liệu xét tuyển", ModalityType.APPLICATION_MODAL);
        this.excelImportBUSV2 = excelImportBUSV2;
        initLayout();
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(getOwner());
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

        SwingWorker<BUSResult, Integer> worker = new SwingWorker<BUSResult, Integer>() {
            @Override
            protected BUSResult doInBackground() throws Exception {
                try {
                    File[] filesArray = selectedFiles.values().toArray(new File[0]);

                    return excelImportBUSV2.importFromFilesV2(filesArray,
                        progress -> {
                            publish(progress);
                    });
                } catch (Throwable t) {
                    t.printStackTrace();
                    return BUSResult.error("Lỗi hệ thống: " + t.getMessage());
                }
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);

                if (loadingDialog != null) {
                    loadingDialog.updateProgress(latestProgress);
                }
            }

            @Override
            protected void done() {
                try {
                    lastResult = get(); // Lấy kết quả trả về từ hàm doInBackground
                    if (loadingDialog != null) {
                        loadingDialog.dispose(); 
                        loadingDialog = null;
                    }

                    if (lastResult != null && lastResult.isSuccess()) {
                        JOptionPane.showMessageDialog(MultiFileImportDialog.this, 
                                lastResult.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        String errorMsg = (lastResult != null) ? lastResult.getMessage() : "Lỗi không xác định";
                        JOptionPane.showMessageDialog(MultiFileImportDialog.this, 
                                errorMsg, "Thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    if (loadingDialog != null) loadingDialog.dispose();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MultiFileImportDialog.this, 
                            "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        loadingDialog = new LoadingDialog(this, "Đang import dữ liệu tuyển sinh...");
        loadingDialog.setVisible(true);
        worker.execute();
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
