/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.monitorjbl.xlsx.StreamingReader;
import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.ThiSinh2025;
import jakarta.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Duc3m
 */
public class ExcelImportBUS {
    
    private final ThiSinh2025BUS thiSinhBUS;
    private final DiemThiBUS diemThiBUS;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BATCH_SIZE = 500;

    @Inject
    public ExcelImportBUS(ThiSinh2025BUS thiSinhBUS, DiemThiBUS diemThiBUS) {
        this.thiSinhBUS = thiSinhBUS;
        this.diemThiBUS = diemThiBUS;
    }
    
    public void importThiSinhVaDiem(File excelFile) {
        List<ThiSinh2025> thiSinhBatch = new ArrayList<>();
        List<DiemThi> diemThiBatch = new ArrayList<>();
        DateTimeFormatter passwordFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        // Sử dụng StreamingReader để không tải toàn bộ file vào RAM
        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)    // Số dòng lưu trong bộ nhớ tạm
                     .bufferSize(4096)     // Kích thước buffer đọc file
                     .open(is)) {

            Sheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên
            boolean isHeaderRow = true;

            for (Row row : sheet) {
                // Bỏ qua dòng tiêu đề
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue;
                }

                // Nếu dòng trống (dựa trên cột CCCD) thì bỏ qua
                if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().isEmpty()) {
                    continue;
                }

                // Map dữ liệu vào ThiSinh2025
                ThiSinh2025 ts = new ThiSinh2025();
                ts.setCccd(getStringValue(row.getCell(1)));
                ts.setSoBaoDanh(getStringValue(row.getCell(1))); 
                ts.setHoTen(getStringValue(row.getCell(2)));
                
                String ngaySinhStr = getStringValue(row.getCell(3));
                if (!ngaySinhStr.isEmpty()) {
                    ts.setNgaySinh(LocalDate.parse(ngaySinhStr, dateFormatter));
                    ts.setPassword(LocalDate.parse(ngaySinhStr, dateFormatter).format(passwordFormatter));
                }
                ts.setGioiTinh(getStringValue(row.getCell(4)));
                ts.setDoiTuong(getStringValue(row.getCell(5)));
                ts.setKhuVuc(getStringValue(row.getCell(6)));
                ts.setNoiSinh(getStringValue(row.getCell(35)));

                // Map dữ liệu vào DiemThi
                DiemThi dt = new DiemThi();
                dt.setThiSinh(ts);
                dt.setTo(getBigDecimalValue(row.getCell(7)));
                dt.setVa(getBigDecimalValue(row.getCell(8)));
                dt.setLi(getBigDecimalValue(row.getCell(9)));
                dt.setHo(getBigDecimalValue(row.getCell(10)));
                dt.setSi(getBigDecimalValue(row.getCell(11)));
                dt.setSu(getBigDecimalValue(row.getCell(12)));
                dt.setDi(getBigDecimalValue(row.getCell(13)));
                dt.setN1Thi(getBigDecimalValue(row.getCell(15)));
                dt.setKtpl(getBigDecimalValue(row.getCell(17)));
                dt.setTi(getBigDecimalValue(row.getCell(18)));
                dt.setCncn(getBigDecimalValue(row.getCell(19)));
                dt.setCnnn(getBigDecimalValue(row.getCell(20)));
                dt.setNk1(getBigDecimalValue(row.getCell(22)));
                dt.setNk2(getBigDecimalValue(row.getCell(23)));
                dt.setNk3(getBigDecimalValue(row.getCell(24)));
                dt.setNk4(getBigDecimalValue(row.getCell(25)));
                dt.setNk5(getBigDecimalValue(row.getCell(26)));
                dt.setNk6(getBigDecimalValue(row.getCell(27)));
                dt.setPhuongThuc(PHUONGTHUC.THPT);

                thiSinhBatch.add(ts);
                diemThiBatch.add(dt);

                // Khi đạt đủ số lượng Batch thì lưu
                if (thiSinhBatch.size() >= BATCH_SIZE) {
                    saveBatchAndClear(thiSinhBatch, thiSinhBUS::addListThiSinh);
                    saveBatchAndClear(diemThiBatch, diemThiBUS::addListDiemThi);
                }
            }
            // Lưu nốt các bản ghi còn lại (cuối file chưa đủ batch_size)
            if (!thiSinhBatch.isEmpty()) {
                saveBatchAndClear(thiSinhBatch, thiSinhBUS::addListThiSinh);
                saveBatchAndClear(diemThiBatch, diemThiBUS::addListDiemThi);
            }

            System.out.println("Import thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi trong quá trình Import: " + e.getMessage());
        }
    }

  
    /**
     * Hàm Generic Import bảng
     * @param <T> Kiểu dữ liệu của bảng
     */
    private <T> void saveBatchAndClear(List<T> batch, Consumer<List<T>> saver) {
        if (batch != null && !batch.isEmpty()) {
            saver.accept(batch);
            batch.clear();
        }
    }

    
    private String getStringValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        return cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "";
    }

    private BigDecimal getBigDecimalValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;
        try {
            String val = cell.getStringCellValue().trim();
            if (val.isEmpty()) return null;
            return new BigDecimal(val);
        } catch (Exception e) {
            return null; // Trả về null nếu ô trống hoặc lỗi định dạng số
        }
    }
    
}
