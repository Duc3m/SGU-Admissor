package com.sgu.admissor.util;

import com.sgu.admissor.bus.ExcelImportBUSV2;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.gui.dialog.LoadingDialog;
import com.sgu.admissor.util.ExcelFileClassifier.FileType;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.util.List;

/**
 * Lớp tiện ích quản lý và điều phối sự kiện Import đơn lẻ cho cả 8 loại file Excel tuyển sinh
 * @author Duc3m
 */
public final class ExcelImportHelper {

    private ExcelImportHelper() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo!");
    }

    /**
     * Hàm dùng chung tối ưu hiển thị % để import một file bất kỳ trong 8 loại file dữ liệu
     * * @param parent         Component gọi hàm (this) để định vị hộp thoại thông báo
     * @param excelImportV2  Thực thể xử lý logic lõi V2 được Inject từ hệ thống
     * @param fileType       Loại file cần import (Lấy từ cấu trúc Enum FileType có sẵn của dự án)
     * @param onSuccess      Hành động callback cần kích hoạt sau khi import thành công (Ví dụ: reload table)
     */
    public static void importSingleExcel(Component parent, ExcelImportBUSV2 excelImportV2, FileType fileType, Runnable onSuccess) {
        if (fileType == null || fileType == FileType.UNKNOWN) {
            JOptionPane.showMessageDialog(parent, "Loại file import không hợp lệ!", "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // BƯỚC 1: Cấu hình động Tiêu đề hiển thị tương ứng với loại file được chọn
        String dialogTitle = switch (fileType) {
            case CHI_TIEU -> "Chọn file Excel Chỉ tiêu tuyển sinh";
            case NGUONG_DAU_VAO -> "Chọn file Excel Ngưỡng đảm bảo chất lượng (Điểm sàn)";
            case TO_HOP_MON -> "Chọn file Excel Danh mục Tổ hợp môn";
            case DS_THI_SINH -> "Chọn file Excel Danh sách thí sinh và Điểm tốt nghiệp";
            case DIEM_DGNL_VSAT -> "Chọn file Excel Điểm ĐGNL và Đánh giá tư duy V-SAT";
            case NGUYEN_VONG -> "Chọn file Excel Danh sách Nguyện vọng đăng ký";
            case UU_TIEN_XET_TUYEN -> "Chọn file Excel Danh sách Ưu tiên xét tuyển";
            case QUY_DOI_TIENG_ANH -> "Chọn file Excel Danh sách Quy đổi chứng chỉ Tiếng Anh";
            default -> "Chọn file Excel dữ liệu tuyển sinh";
        };

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int result = fileChooser.showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; 
        }

        File selectedFile = fileChooser.getSelectedFile();

        // BƯỚC 2: Gọi Dialog hiển thị tiến độ dùng chung bám theo app chính
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        final LoadingDialog progressDialog = new LoadingDialog(parentWindow, dialogTitle + "...");

        // BƯỚC 3: Kích hoạt luồng ngầm điều hướng xử lý
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Tự động phân phối luồng gọi đến đúng hàm V2 dựa trên Enum truyền vào
                switch (fileType) {
                    case CHI_TIEU -> {
                        BUSResult res = excelImportV2.importChiTieuV2(selectedFile, p -> publish(p), 0, 100);
                        if (res != null && !res.isSuccess()) throw new RuntimeException(res.getMessage());
                    }
                    case NGUONG_DAU_VAO -> {
                        BUSResult res = excelImportV2.importNguongDauVaoV2(selectedFile, p -> publish(p), 0, 100);
                        if (res != null && !res.isSuccess()) throw new RuntimeException(res.getMessage());
                    }
                    case TO_HOP_MON -> {
                        BUSResult res = excelImportV2.importToHopMonV2(selectedFile, p -> publish(p), 0, 100);
                        if (res != null && !res.isSuccess()) throw new RuntimeException(res.getMessage());
                    }
                    case DS_THI_SINH -> 
                        excelImportV2.importThiSinhVaDiemV2(selectedFile, p -> publish(p), 0, 100);
                    case DIEM_DGNL_VSAT -> 
                        excelImportV2.importDiemDGNLVaVSATV2(selectedFile, p -> publish(p), 0, 100);
                    case NGUYEN_VONG -> 
                        excelImportV2.importNguyenVongV2(selectedFile, p -> publish(p), 0, 100);
                    case UU_TIEN_XET_TUYEN -> 
                        excelImportV2.importUuTienXetTuyenV2(selectedFile, p -> publish(p), 0, 100);
                    case QUY_DOI_TIENG_ANH -> 
                        excelImportV2.importQuyDoiTiengAnhV2(selectedFile, p -> publish(p), 0, 100);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressDialog.updateProgress(latestProgress); // Đẩy % lên thanh progress
            }

            @Override
            protected void done() {
                progressDialog.dispose(); // Đóng giao diện chờ khi hoàn tất
                try {
                    get(); // Kiểm tra lỗi runtime phát sinh ngầm
                    JOptionPane.showMessageDialog(parent, "Nhập dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (onSuccess != null) {
                        onSuccess.run(); // Kích hoạt reload dữ liệu giao diện
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parent, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        progressDialog.setVisible(true);
        worker.execute();
    }
}