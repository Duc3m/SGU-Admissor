/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.persist.Transactional;
import com.monitorjbl.xlsx.StreamingReader;
import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.dto.ToHopData;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.ThiSinh2025;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.utils.PhanBoChiTieuUtil;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Duc3m
 */
public class ExcelImportBUS {
    
    private final Provider<ThiSinh2025BUS> thiSinhBUSProvider;
    private final Provider<DiemThiBUS> diemThiBUSProvider;
    private final Provider<NganhBUS> nganhBUSProvider;
    private final Provider<ToHopBUS> toHopBUSProvider;
    private final Provider<NganhToHopBUS> nganhToHopBUSProvider;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BATCH_SIZE = 500;

    @Inject
    public ExcelImportBUS(
        Provider<ThiSinh2025BUS> thiSinhBUSProvider,
        Provider<DiemThiBUS> diemThiBUSProvider,
        Provider<NganhBUS> nganhBUSProvider,
        Provider<ToHopBUS> toHopBUSProvider,
        Provider<NganhToHopBUS> nganhToHopBUSProvider
    ) {
        this.thiSinhBUSProvider = thiSinhBUSProvider;
        this.diemThiBUSProvider = diemThiBUSProvider;
        this.nganhBUSProvider = nganhBUSProvider;
        this.toHopBUSProvider = toHopBUSProvider;
        this.nganhToHopBUSProvider = nganhToHopBUSProvider;
    }
    
    @Transactional
    public void importThiSinhVaDiem(File excelFile) {
        List<ThiSinh2025> thiSinhBatch = new ArrayList<>();
        List<DiemThi> diemThiBatch = new ArrayList<>();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();

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
                ThiSinh2025 ts = buildThiSinhFromRow(row);
                // Map dữ liệu vào DiemThi
                DiemThi dt = buildDiemThiFromRow(row, ts);
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
     * @param file1_ChiTieu        File 1: Chi_tieu_2025
     * @param file2_NguongDauVao   File 2: Nguong_dau_vao_2025
     * @param file3_ToHopMon       File 3: tohopmon
     */
    @Transactional
    public void importNganhVaToHop(File file1_ChiTieu, File file2_NguongDauVao, File file3_ToHopMon) {
        NganhBUS nganhBUS = nganhBUSProvider.get();
        ToHopBUS toHopBUS = toHopBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();

        Map<String, Nganh> nganhMap = new HashMap<>();
        Map<String, ToHop> toHopMap = new HashMap<>();
        
        List<ToHop> toHopList = new ArrayList<>(); // Danh sách chờ Insert mới
        List<NganhToHop> nganhToHopList = new ArrayList<>();

        // Lấy dữ liệu cũ từ DB lên để không bị lỗi Duplicate
        List<ToHop> existingToHops = toHopBUS.getAllToHop().getData();
        if (existingToHops != null) {
            for (ToHop t : existingToHops) {
                toHopMap.put(t.getMaToHop(), t);
            }
        }

        try {
//          File 1: Chi_tieu_2025
            try (InputStream is = new FileInputStream(file1_ChiTieu);
                Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
                for (Row row : wb.getSheetAt(0)) {
                    if (row.getRowNum() <= 1) continue; // Bỏ qua Header
                    String maNganh = getStringValue(row.getCell(1));
                    if (maNganh.isEmpty()) continue;
                    if (maNganh.length() > 10) break;
                    Nganh ng = new Nganh();
                    fillNganhInfo1(ng, row);
                    nganhMap.put(maNganh, ng);
                }
            }

//          File 2: Nguong_dau_vao_2025
            try (InputStream is = new FileInputStream(file2_NguongDauVao);
                Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
                for (Row row : wb.getSheetAt(0)) {
                    if (row.getRowNum() == 0) continue;
                    String maNganh = getStringValue(row.getCell(1)); 
                    if (maNganh.isEmpty()) continue;
                    Nganh ng = nganhMap.get(maNganh);
                    if (ng != null) {
                        fillNganhInfo2(ng, row);
                    }
                }
            }

//          File 3: tohopmon
            try (InputStream is = new FileInputStream(file3_ToHopMon);
                 Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
                 
                for (Row row : wb.getSheetAt(0)) {
                    if (row.getRowNum() == 0) continue;
                    String maNganh = getStringValue(row.getCell(1));
                    String chuoiToHop = getStringValue(row.getCell(3));
                    if (maNganh.isEmpty() || chuoiToHop.isEmpty()) continue;

                    // Bóc tách chuỗi Tổ hợp
                    ToHopData parsedData = parseToHopString(chuoiToHop);
                    String maToHop = parsedData.maToHop;

                    // Kiểm tra xem Tổ hợp này đã có trên RAM chưa. Nếu chưa thì tạo mới
                    ToHop th = toHopMap.get(maToHop);
                    if (th == null && !maToHop.isEmpty()) {
                        th = new ToHop();
                        th.setMaToHop(maToHop);
                        th.setMon1(parsedData.mon1);
                        th.setMon2(parsedData.mon2);
                        th.setMon3(parsedData.mon3);
                        th.setTenToHop(maToHop);
                        
                        toHopList.add(th);         // Thêm vào hàng chờ Insert DB
                        toHopMap.put(maToHop, th); // Nạp lên RAM để tái sử dụng
                    }

                    // Cập nhật Tổ Hợp Gốc cho Ngành 
                    Nganh ng = nganhMap.get(maNganh);
                    if (ng != null) {
                        String flagStr = getStringValue(row.getCell(6));
                        if (!flagStr.isEmpty()) {
                            // Lấy object ToHop từ trong Map ra để gán
                            String maToHopGoc = getStringValue(row.getCell(5));
                            ToHop thGoc = toHopMap.get(maToHopGoc);
                            if (thGoc != null) {
                                ng.setToHopGoc(thGoc); 
                            }
                        }
                    }

                    // Tạo Nganh_ToHop dựa trên các Object trên RAM
                    if (ng != null && th != null) {
                        NganhToHop nth = buildNganhToHop(ng, th, parsedData);
                        nganhToHopList.add(nth);
                    }
                }
            }

            // Lưu database theo thứ tự khóa ngoại
            // Bảng tohop
            List<ToHop> toHopBatch = new ArrayList<>();
            for (ToHop th : toHopList) {
                toHopBatch.add(th);
                if (toHopBatch.size() >= BATCH_SIZE) saveBatchAndClear(toHopBatch, toHopBUS::addListToHop);
            }
            saveBatchAndClear(toHopBatch, toHopBUS::addListToHop);

            // Bảng nganh
            List<Nganh> nganhBatch = new ArrayList<>();
            for (Nganh ng : nganhMap.values()) {
                nganhBatch.add(ng);
                if (nganhBatch.size() >= BATCH_SIZE) saveBatchAndClear(nganhBatch, nganhBUS::addListNganh);
            }
            saveBatchAndClear(nganhBatch, nganhBUS::addListNganh);

            // Bảng nganh_tohop
            List<NganhToHop> nthBatch = new ArrayList<>();
            for (NganhToHop nth : nganhToHopList) {
                nthBatch.add(nth);
                if (nthBatch.size() >= BATCH_SIZE) saveBatchAndClear(nthBatch, nganhToHopBUS::addListNganhToHop);
            }
            saveBatchAndClear(nthBatch, nganhToHopBUS::addListNganhToHop);

            System.out.println("Import Toàn bộ dữ liệu 3 file thành công!");

        } catch (Exception e) {
            System.err.println("Lỗi Import Dữ liệu: " + e.getMessage());
            e.printStackTrace();
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
    
    
    private ThiSinh2025 buildThiSinhFromRow(Row row) {
        DateTimeFormatter passwordFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
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
        return ts;
    }
    
    private DiemThi buildDiemThiFromRow(Row row, ThiSinh2025 ts) {
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
        dt.setPhuongThuc(PHUONGTHUC.PT[4]);
        return dt;
    }
    
    private ToHop buildToHopFromRow(Row row) {
        String rawToHop = getStringValue(row.getCell(3));
        if (rawToHop.isEmpty()) return null;

        String chuoiToHop = getStringValue(row.getCell(3)); //B03(TO-3,VA-3,SI-1)
        ToHopData parsedData = parseToHopString(chuoiToHop);

        ToHop th = new ToHop();
        th.setMaToHop(parsedData.maToHop); // Mã tổ hợp (VD: B03)
        th.setMon1(parsedData.mon1);    // Tên môn 1 (VD: TO)
        th.setMon2(parsedData.mon2);    // Tên môn 2 (VD: VA)
        th.setMon3(parsedData.mon3);    // Tên môn 3 (VD: SI)
        th.setTenToHop(parsedData.maToHop);

        return th;
    }
    
//  File Chi_tieu_2025
    private void fillNganhInfo1(Nganh ng, Row row) {
        String maNganh = getStringValue(row.getCell(1));
        int chiTieu = getIntegerValue(row.getCell(3));
        ng.setMaNganh(maNganh);
        ng.setTenNganh(getStringValue(row.getCell(2)));
        ng.setChiTieu(chiTieu);
        Map<String, Integer> phanBo = PhanBoChiTieuUtil.tinhPhanBoChiTieu(maNganh, chiTieu);
        if(phanBo.get("PT1") != 0) {
            ng.setTuyenThang(Boolean.TRUE);
            ng.setSlXtt(phanBo.get("PT1"));
        }
        if(phanBo.get("PT2") != 0) {
            ng.setDgnl(Boolean.TRUE);
            ng.setSlDgnl(phanBo.get("PT2"));
        }
        if(phanBo.get("PT3") != 0) {
            ng.setVsat(Boolean.TRUE);
            ng.setSlVsat(phanBo.get("PT3"));
        }
        if(phanBo.get("PT4") != 0) {
            ng.setThpt(Boolean.TRUE);
            ng.setSlThpt(phanBo.get("PT4"));
        }
    }
    
//  File Nguong_dau_vao_2025
    private void fillNganhInfo2(Nganh ng, Row row) {
        BigDecimal diemSan = getBigDecimalValue(row.getCell(3));
        ng.setDiemSan(diemSan);
        ng.setDiemTrungTuyen(diemSan);
    }
    
//  File tohopmon
    private void fillNganhInfo3(Nganh ng, Row row) {
        String flagStr = getStringValue(row.getCell(6));
        if( flagStr.equals("") || flagStr == null)
            return;
        String maToHop = getStringValue(row.getCell(5));
        ToHop toHopGoc = new ToHop();
        toHopGoc.setMaToHop(maToHop);
        ng.setToHopGoc(toHopGoc);
    }
    
    private NganhToHop buildNganhToHop(Nganh ng, ToHop th, ToHopData parsedData) {
        NganhToHop nth = new NganhToHop();
        
        // Truyền thẳng Object có sẵn trên RAM vào
        nth.setNganh(ng);
        nth.setToHop(th);

        // Gán các hệ số môn
        nth.setHsMon1(parsedData.hs1);
        nth.setHsMon2(parsedData.hs2);
        nth.setHsMon3(parsedData.hs3);

        // Bật Cờ
        setMonHocFlags(nth, parsedData);

        nth.setTbKey(ng.getMaNganh() + "_" + th.getMaToHop()); 

        return nth;
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
    
    private Integer getIntegerValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return 0;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (Exception ex) {
                return 0; // Hoặc trả về null tuỳ nghiệp vụ
            }
        }
    }
    
    private ToHopData parseToHopString(String input) {
        ToHopData data = new ToHopData();

        if (input == null || input.trim().isEmpty()) {
            return data;
        }

        if (input.contains("(")) {
            int openIdx = input.indexOf('(');
            int closeIdx = input.indexOf(')');
            
            // Lấy Mã Tổ hợp
            data.maToHop = input.substring(0, openIdx).trim();

            if (closeIdx > openIdx) {
                // Lấy phần ruột: "TO-3,VA-3,SI-1"
                String innerStr = input.substring(openIdx + 1, closeIdx);
                String[] subjects = innerStr.split(",");

                // Môn 1
                if (subjects.length > 0) {
                    String[] parts = subjects[0].split("-");
                    data.mon1 = parts[0].trim();
                    if (parts.length > 1) data.hs1 = Integer.parseInt(parts[1].trim());
                }
                // Môn 2
                if (subjects.length > 1) {
                    String[] parts = subjects[1].split("-");
                    data.mon2 = parts[0].trim();
                    if (parts.length > 1) data.hs2 = Integer.parseInt(parts[1].trim());
                }
                // Môn 3
                if (subjects.length > 2) {
                    String[] parts = subjects[2].split("-");
                    data.mon3 = parts[0].trim();
                    if (parts.length > 1) data.hs3 = Integer.parseInt(parts[1].trim());
                }
            }
        } else {
            // Trường hợp không có ngoặc, toàn bộ là mã tổ hợp
            data.maToHop = input.trim();
        }

        return data;
    }
    
    private void setMonHocFlags(NganhToHop nth, ToHopData data) {
        // Gộp 3 môn lại thành 1 chuỗi để check cho lẹ (VD: "TO VA SI")
        String dsMon = (data.mon1 + " " + data.mon2 + " " + data.mon3).toUpperCase();

        if (dsMon.contains("TO")) nth.setTo(true);
        if (dsMon.contains("VA")) nth.setVa(true);
        if (dsMon.contains("LI")) nth.setLi(true);
        if (dsMon.contains("HO")) nth.setHo(true);
        if (dsMon.contains("SI")) nth.setSi(true);
        if (dsMon.contains("SU")) nth.setSu(true);
        if (dsMon.contains("DI")) nth.setDi(true);
        if (dsMon.contains("TI")) nth.setTi(true);
        if (dsMon.contains("KTPL")) nth.setKtpl(true);
        // Tương tự cho các môn N1, KHAC nếu cần...
    }
    
}
