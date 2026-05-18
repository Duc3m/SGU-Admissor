/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.persist.Transactional;
import com.monitorjbl.xlsx.StreamingReader;
import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.dto.ToHopData;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.Nganh;
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
import java.util.Set;
import java.util.HashSet;
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
    private final Provider<NguyenVongBUS> nguyenVongBUSProvider;
    private final Provider<DiemCongBUS> diemCongBUSProvider;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BATCH_SIZE = 500;

    @Inject
    public ExcelImportBUS(
            Provider<ThiSinh2025BUS> thiSinhBUSProvider, 
            Provider<DiemThiBUS> diemThiBUSProvider, 
            Provider<NganhBUS> nganhBUSProvider, 
            Provider<ToHopBUS> toHopBUSProvider, 
            Provider<NganhToHopBUS> nganhToHopBUSProvider,
            Provider<NguyenVongBUS> nguyenVongBUSProvider,
            Provider<DiemCongBUS> diemCongBUSProvider
            ) {
        this.thiSinhBUSProvider = thiSinhBUSProvider;
        this.diemThiBUSProvider = diemThiBUSProvider;
        this.nganhBUSProvider = nganhBUSProvider;
        this.toHopBUSProvider = toHopBUSProvider;
        this.nganhToHopBUSProvider = nganhToHopBUSProvider;
        this.nguyenVongBUSProvider = nguyenVongBUSProvider;
        this.diemCongBUSProvider = diemCongBUSProvider;
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

                    // Cập nhật Tổ Hợp Gốc cho Ngành (cột G index 6 = flag "Gốc")
                    Nganh ng = nganhMap.get(maNganh);
                    if (ng != null) {
                        String flagStr = getStringValue(row.getCell(6));
                        if (!flagStr.isEmpty()) {
                            // Lấy object ToHop từ trong Map ra để gán (cột F index 5 = TEN_TO_HOP = maToHopGoc)
                            String maToHopGoc = getStringValue(row.getCell(5));
                            ToHop thGoc = toHopMap.get(maToHopGoc);
                            if (thGoc != null) {
                                ng.setToHopGoc(thGoc); 
                            }
                        }
                    }

                    // Đọc độ lệch (cột H index 7)
                    BigDecimal doLech = getBigDecimalValue(row.getCell(7));

                    // Tạo Nganh_ToHop dựa trên các Object trên RAM
                    if (ng != null && th != null) {
                        NganhToHop nth = buildNganhToHop(ng, th, parsedData, doLech);
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
     * Import điểm thi từ file Excel có 2 sheet: VSAT (sheet 0) và DGNL (sheet 1).
     * - VSAT: mỗi thí sinh có nhiều dòng (mỗi dòng 1 môn), gom thành 1 DiemThi duy nhất.
     * - DGNL: mỗi dòng là 1 thí sinh, điểm lưu vào nl1.
     * Phương thức xác định theo PHUONGTHUC.PT[3] (VSAT) và PHUONGTHUC.PT[2] (DGNL).
     */
    @Transactional
    public void importDiemDGNLVaVSAT(File excelFile) {
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        // Prefetch thí sinh để tránh query DB nhiều lần
        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        // ---- Sheet 0: VSAT ----
        // 1 thí sinh có thể thi nhiều đợt (DOTTHI) tại nhiều địa điểm (MADVTCTD).
        // Mỗi combo (cccd + dotthi + madvtctd) là 1 DiemThi riêng — tất cả đều hợp lệ.
        // Trong mỗi combo, nhiều dòng môn thi được gom thành 1 DiemThi duy nhất.
        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100).bufferSize(4096).open(is)) {

            // key = cccd + "_" + dotthi + "_" + madvtctd
            Map<String, DiemThi> vsatDiemMap = new HashMap<>();
            Sheet vsatSheet = workbook.getSheetAt(0);
            if (vsatSheet != null) {
                boolean isHeader = true;
                for (Row row : vsatSheet) {
                    if (isHeader) { isHeader = false; continue; }
                    String cccd = getStringValue(row.getCell(1));
                    if (cccd.isEmpty()) continue;

                    String dotThi   = getStringValue(row.getCell(2)).trim();
                    String maDvtctd = getStringValue(row.getCell(10)).trim();
                    String maMonThi = getStringValue(row.getCell(6)).toUpperCase().trim();
                    BigDecimal diem = getBigDecimalValue(row.getCell(8));

                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    if (ts == null) {
                        System.err.println("VSAT: Không tìm thấy thí sinh CCCD=" + cccd + ", bỏ qua dòng " + (row.getRowNum() + 1));
                        continue;
                    }

                    // Key phân biệt từng lần thi (đợt + địa điểm)
                    String vsatKey = cccd + "_" + dotThi + "_" + maDvtctd;

                    // Lấy hoặc tạo mới DiemThi cho combo (thí sinh, đợt, địa điểm) này
                    DiemThi dt = vsatDiemMap.get(vsatKey);
                    if (dt == null) {
                        dt = new DiemThi();
                        dt.setThiSinh(ts);
                        dt.setPhuongThuc(PHUONGTHUC.PT[3]);
                        vsatDiemMap.put(vsatKey, dt);
                    }

                    // Bỏ hậu tố _VS để lấy mã môn chuẩn
                    String maMon = maMonThi.endsWith("_VS")
                            ? maMonThi.substring(0, maMonThi.length() - 3)
                            : maMonThi;

                    switch (maMon) {
                        case "TO":   dt.setTo(diem);    break;
                        case "LI":   dt.setLi(diem);    break;
                        case "HO":   dt.setHo(diem);    break;
                        case "SI":   dt.setSi(diem);    break;
                        case "SU":   dt.setSu(diem);    break;
                        case "DI":   dt.setDi(diem);    break;
                        case "VA":   dt.setVa(diem);    break;
                        case "N1":   dt.setN1Thi(diem); break;
                        case "TI":   dt.setTi(diem);    break;
                        case "KTPL": dt.setKtpl(diem);  break;
                        // M1-M8: map cứng theo thứ tự môn chuẩn VSAT
                        case "M1": dt.setTo(diem);    break; // Toán
                        case "M2": dt.setLi(diem);    break; // Vật lý
                        case "M3": dt.setHo(diem);    break; // Hóa
                        case "M4": dt.setSi(diem);    break; // Sinh
                        case "M5": dt.setVa(diem);    break; // Văn
                        case "M6": dt.setSu(diem);    break; // Lịch sử
                        case "M7": dt.setDi(diem);    break; // Địa lý
                        case "M8": dt.setN1Thi(diem); break; // Tiếng Anh
                        default:
                            System.err.println("VSAT: Mã môn không xác định: " + maMonThi + " dòng " + (row.getRowNum() + 1));
                            break;
                    }
                }
            }

            // Lưu batch VSAT
            List<DiemThi> vsatBatch = new ArrayList<>(vsatDiemMap.values());
            for (int i = 0; i < vsatBatch.size(); i += BATCH_SIZE) {
                List<DiemThi> sub = vsatBatch.subList(i, Math.min(i + BATCH_SIZE, vsatBatch.size()));
                diemThiBUS.addListDiemThi(new ArrayList<>(sub));
            }
            System.out.println("Import VSAT thành công: " + vsatBatch.size() + " bản ghi (đợt/địa điểm).");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi import điểm VSAT: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // ---- Sheet 1: DGNL ----
        // 1 thí sinh có thể thi nhiều đợt → chỉ lưu 1 dòng với điểm NL1 cao nhất.
        // Mở stream riêng vì StreamingReader không hỗ trợ seek ngược.
        try (InputStream is2 = new FileInputStream(excelFile);
             Workbook workbook2 = StreamingReader.builder()
                     .rowCacheSize(100).bufferSize(4096).open(is2)) {

            // key = cccd, value = DiemThi đang giữ điểm cao nhất
            Map<String, DiemThi> dgnlDiemMap = new HashMap<>();

            Sheet dgnlSheet = workbook2.getSheetAt(1);
            if (dgnlSheet != null) {
                boolean isHeader = true;
                for (Row row : dgnlSheet) {
                    if (isHeader) { isHeader = false; continue; }
                    String cccd = getStringValue(row.getCell(1));
                    if (cccd.isEmpty()) continue;

                    BigDecimal diem = getBigDecimalValue(row.getCell(8));

                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    if (ts == null) {
                        System.err.println("DGNL: Không tìm thấy thí sinh CCCD=" + cccd + ", bỏ qua dòng " + (row.getRowNum() + 1));
                        continue;
                    }

                    DiemThi existing = dgnlDiemMap.get(cccd);
                    if (existing == null) {
                        // Lần đầu gặp thí sinh này
                        DiemThi dt = new DiemThi();
                        dt.setThiSinh(ts);
                        dt.setPhuongThuc(PHUONGTHUC.PT[2]);
                        dt.setNl1(diem);
                        dgnlDiemMap.put(cccd, dt);
                    } else {
                        // Đã có — giữ điểm cao hơn
                        BigDecimal currentNl1 = existing.getNl1();
                        if (diem != null && (currentNl1 == null || diem.compareTo(currentNl1) > 0)) {
                            existing.setNl1(diem);
                        }
                    }
                }
            }

            // Lưu batch DGNL
            List<DiemThi> dgnlBatch = new ArrayList<>(dgnlDiemMap.values());
            for (int i = 0; i < dgnlBatch.size(); i += BATCH_SIZE) {
                List<DiemThi> sub = dgnlBatch.subList(i, Math.min(i + BATCH_SIZE, dgnlBatch.size()));
                diemThiBUS.addListDiemThi(new ArrayList<>(sub));
            }
            System.out.println("Import DGNL thành công: " + dgnlBatch.size() + " thí sinh (điểm cao nhất).");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi import điểm DGNL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void importNguyenVong(File excelFilePath){
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();
        NganhBUS nganhBUS = nganhBUSProvider.get();
        
        // Prefetch để khỏi phải query DB, tiết kiệm time
        // Prefetch THI SINH
        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        // Prefetch NGANH
        Map<String, Nganh> nganhMap = new HashMap<>();
        List<Nganh> allNganh = nganhBUS.getAllNganh().getData();
        if (allNganh != null) {
            for (Nganh ng : allNganh) {
                if (ng.getMaNganh() != null) nganhMap.put(ng.getMaNganh(), ng);
            }
        }

        // Prefetch DIEMTHI -> phuongthuc
        Map<String, Set<String>> phuongThucMap = new HashMap<>();
        List<DiemThi> allDiemThi = diemThiBUS.getAllDiemThi().getData();
        if (allDiemThi != null) {
            for (DiemThi dt : allDiemThi) {
                String cccd = dt.getThiSinh() != null ? dt.getThiSinh().getCccd() : null;
                String pt = dt.getPhuongThuc();
                if (cccd == null || pt == null || pt.trim().isEmpty()) continue;

                phuongThucMap.computeIfAbsent(cccd, k -> new HashSet<>()).add(pt);
            }
        }
        
        List<NguyenVong> nvBatch = new ArrayList();
        
        try (InputStream is = new FileInputStream(excelFilePath);
                Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is)){
            int[] sheets = {1, 2}; // sheet 2 và 3
            int startRow = 5; // dòng 6
            
            for(int sheetIndex : sheets) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) continue;
                
                for (Row row : sheet){
                    if (row.getRowNum() < startRow) continue;
                    
                    String cccd = getStringValue(row.getCell(1));
                    int thuTuNV = getIntegerValue(row.getCell(2));
                    String maNganh = getStringValue(row.getCell(5));
                    
                    // CCCD trống thì skip
                    if (cccd == null || cccd.isEmpty()) continue;
                    
                    // Thiếu mã ngành hoặc thứ tự nguyện vọng thì dừng import
                    if (maNganh == null || maNganh.isEmpty() || thuTuNV == 0){
                        throw new RuntimeException("Dữ liệu thiếu ở dòng " + (row.getRowNum() + 1));
                    }
                    
                    // Nếu ko có sẵn thí sinh này thì dừng import
                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    if (ts == null){
                        throw new RuntimeException("Không tìm thấy thí sinh: " + cccd + " (dòng " + (row.getRowNum() + 1) + ")");
                    }
                    
                    // Nếu ngành này chưa có thì dừng import
                    Nganh ng = nganhMap.get(maNganh);
                    if (ng == null) {
                        throw new RuntimeException("Không tìm thấy ngành: " + maNganh + " (dòng " + (row.getRowNum() + 1) + ")");
                    }
                    
                    // Lấy danh sách phương thức từ BUS
                    Set<String> phuongThucList = phuongThucMap.getOrDefault(cccd, new HashSet<>());
                    
                    if (phuongThucList.isEmpty()) continue;
                    
                    for (String pt : phuongThucList){
                        if(!kiemTraNganhCoXetPhuongThuc(ng, pt)) continue;
                        
                        NguyenVong nv = new NguyenVong();
                        nv.setThiSinh(ts);
                        nv.setThuTu(thuTuNV);
                        nv.setNganh(ng);
                        nv.setPhuongThuc(pt);
                        nv.setToHopMon(null);
                        // nv_key tự set ở BUS
                        nvBatch.add(nv);
                    }
                    
                    if (nvBatch.size() >= BATCH_SIZE){
                        saveBatchAndClear(nvBatch, nguyenVongBUS::addListNguyenVong);
                    }
                }
            }
            
            if (!nvBatch.isEmpty()) {
                saveBatchAndClear(nvBatch, nguyenVongBUS::addListNguyenVong);
            }
            
            System.out.println("Import nguyện vọng từ file Excel thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi import nguyện vọng: " + e.getMessage());
            throw new RuntimeException(e); // để rollback
        }
    }

    /**
     * Import file Ưu tiên xét tuyển (sheet 0: ds thi sinh).
     * Cột: B=CCCD, E=Mã môn, F=Loại giải, G=Điểm cộng có môn đạt giải, H=Điểm cộng không có môn đạt giải.
     *
     * Logic:
     * - Với mỗi thí sinh → lấy danh sách nguyện vọng (dedup theo thuTu+maNganh, bỏ trùng phương thức)
     * - Với mỗi nguyện vọng duy nhất → lấy tất cả tổ hợp môn của ngành đó
     * - Với mỗi tổ hợp môn → kiểm tra có chứa mã môn đạt giải không:
     *     + Có → diemUtxt = cột G
     *     + Không có → diemUtxt = cột H
     * - Upsert DiemCong theo dc_key = cccd_maNganh_maToHop:
     *     + Chưa có → tạo mới
     *     + Đã có → chỉ cập nhật diemUtxt, tính lại diemTong
     * - diemTong = min(diemUtxt + diemCc, 3.0), diemCc null thì coi là 0
     */
    @Transactional
    public void importUuTienXetTuyen(File excelFile) {
        DiemCongBUS diemCongBUS = diemCongBUSProvider.get();
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        // Prefetch toàn bộ thí sinh
        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        // Prefetch nguyện vọng: cccd -> List<NguyenVong>
        Map<String, List<NguyenVong>> nvMap = new HashMap<>();
        List<NguyenVong> allNv = nguyenVongBUS.getAllNguyenVong().getData();
        if (allNv != null) {
            for (NguyenVong nv : allNv) {
                String cccd = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : null;
                if (cccd == null) continue;
                nvMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(nv);
            }
        }

        // Prefetch NganhToHop: maNganh -> List<NganhToHop>
        Map<String, List<NganhToHop>> nganhToHopMap = new HashMap<>();
        List<NganhToHop> allNth = nganhToHopBUS.getAllNganhToHop().getData();
        if (allNth != null) {
            for (NganhToHop nth : allNth) {
                String maNganh = nth.getNganh() != null ? nth.getNganh().getMaNganh() : null;
                if (maNganh == null) continue;
                nganhToHopMap.computeIfAbsent(maNganh, k -> new ArrayList<>()).add(nth);
            }
        }

        // Prefetch DiemCong đã có: dcKey -> DiemCong (để upsert)
        Map<String, DiemCong> dcExistingMap = new HashMap<>();
        List<DiemCong> allDc = diemCongBUS.getAllDiemCong().getData();
        if (allDc != null) {
            for (DiemCong dc : allDc) {
                if (dc.getDcKey() != null) dcExistingMap.put(dc.getDcKey(), dc);
            }
        }

        List<DiemCong> insertBatch = new ArrayList<>();
        List<DiemCong> updateBatch = new ArrayList<>();

        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100).bufferSize(4096).open(is)) {

            Sheet sheet = workbook.getSheetAt(0); // sheet "ds thi sinh"
            if (sheet == null) {
                System.err.println("Không tìm thấy sheet 0 trong file ưu tiên xét tuyển.");
                return;
            }

            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) { isHeader = false; continue; }

                String cccd   = getStringValue(row.getCell(1));
                String maMon  = getStringValue(row.getCell(4)).toUpperCase().trim(); // Cột E (index 4)
                BigDecimal diemCoMon     = getBigDecimalValue(row.getCell(6)); // Cột G (index 6)
                BigDecimal diemKhongCoMon = getBigDecimalValue(row.getCell(7)); // Cột H (index 7)

                if (cccd.isEmpty()) continue;

                ThiSinh2025 ts = thiSinhMap.get(cccd);
                if (ts == null) {
                    System.err.println("UT: Không tìm thấy thí sinh CCCD=" + cccd);
                    continue;
                }

                // Lấy tất cả nguyện vọng của thí sinh — tạo DiemCong cho từng phương thức riêng
                List<NguyenVong> nvList = nvMap.getOrDefault(cccd, new ArrayList<>());
                // Dedup theo (maNganh, maToHop, phuongThuc) để tránh insert trùng trong 1 lần chạy
                Set<String> seenDcKey = new HashSet<>();

                for (NguyenVong nv : nvList) {
                    if (nv.getNganh() == null) continue;
                    String maNganh = nv.getNganh().getMaNganh();
                    List<NganhToHop> nthList = nganhToHopMap.getOrDefault(maNganh, new ArrayList<>());

                    for (NganhToHop nth : nthList) {
                        if (nth.getToHop() == null) continue;
                        String maToHop = nth.getToHop().getMaToHop();

                        // Kiểm tra tổ hợp có chứa mã môn đạt giải không
                        boolean coMon = coMonTrongToHop(nth, maMon);
                        BigDecimal diemUtxt = coMon ? diemCoMon : diemKhongCoMon;

                        String phuongThuc = nv.getPhuongThuc();
                        String dcKey = cccd + "_" + maNganh + "_" + maToHop + "_" + phuongThuc;
                        if (!seenDcKey.add(dcKey)) continue;

                        DiemCong existing = dcExistingMap.get(dcKey);
                        if (existing != null) {
                            // Upsert: cập nhật diemUtxt, tính lại diemTong
                            existing.setDiemUtxt(diemUtxt);
                            existing.setDiemTong(tinhDiemTong(diemUtxt, existing.getDiemCc()));
                            updateBatch.add(existing);
                        } else {
                            // Tạo mới
                            DiemCong dc = new DiemCong();
                            dc.setThiSinh(ts);
                            dc.setNganh(nv.getNganh());
                            dc.setToHop(nth.getToHop());
                            dc.setPhuongThuc(nv.getPhuongThuc());
                            dc.setDiemUtxt(diemUtxt);
                            dc.setDiemCc(null);
                            dc.setDiemTong(tinhDiemTong(diemUtxt, null));
                            dc.setDcKey(dcKey);
                            insertBatch.add(dc);
                            // Đưa vào map để tránh insert trùng nếu thí sinh có nhiều dòng trong file
                            dcExistingMap.put(dcKey, dc);
                        }

                        if (insertBatch.size() >= BATCH_SIZE) {
                            saveBatchAndClear(insertBatch, diemCongBUS::addListDiemCong);
                        }
                        if (updateBatch.size() >= BATCH_SIZE) {
                            saveBatchAndClear(updateBatch, diemCongBUS::updateBatchDiemCong);
                        }
                    }
                }
            }

            // Flush còn lại
            if (!insertBatch.isEmpty()) saveBatchAndClear(insertBatch, diemCongBUS::addListDiemCong);
            if (!updateBatch.isEmpty()) saveBatchAndClear(updateBatch, diemCongBUS::updateBatchDiemCong);

            System.out.println("Import ưu tiên xét tuyển thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi import ưu tiên xét tuyển: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Import file Danh sách quy đổi điểm Tiếng Anh (sheet 0: import_xettuyen).
     * Cột: B=CCCD, E=Điểm quy đổi (thang 10), F=Điểm cộng (diemCc).
     *
     * Logic:
     * 1. Cập nhật n1Cc trên tất cả DiemThi của thí sinh đó (mọi phương thức)
     * 2. Với mỗi nguyện vọng duy nhất của thí sinh → lấy tổ hợp môn của ngành
     * 3. Với tổ hợp KHÔNG có môn TI (tiếng anh) → upsert DiemCong.diemCc = diemCongCc
     *    - Đã có → cộng dồn nếu chưa có diemCc, hoặc cập nhật; tính lại diemTong
     *    - Chưa có → tạo mới với diemCc
     * 4. diemTong = min(diemUtxt + diemCc, 3.0)
     */
    @Transactional
    public void importQuyDoiTiengAnh(File excelFile) {
        DiemCongBUS diemCongBUS = diemCongBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        // Prefetch thí sinh
        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        // Prefetch DiemThi: cccd -> List<DiemThi> (cần update n1Cc)
        Map<String, List<DiemThi>> diemThiMap = new HashMap<>();
        List<DiemThi> allDiemThi = diemThiBUS.getAllDiemThi().getData();
        if (allDiemThi != null) {
            for (DiemThi dt : allDiemThi) {
                String cccd = dt.getThiSinh() != null ? dt.getThiSinh().getCccd() : null;
                if (cccd == null) continue;
                diemThiMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(dt);
            }
        }

        // Prefetch nguyện vọng
        Map<String, List<NguyenVong>> nvMap = new HashMap<>();
        List<NguyenVong> allNv = nguyenVongBUS.getAllNguyenVong().getData();
        if (allNv != null) {
            for (NguyenVong nv : allNv) {
                String cccd = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : null;
                if (cccd == null) continue;
                nvMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(nv);
            }
        }

        // Prefetch NganhToHop
        Map<String, List<NganhToHop>> nganhToHopMap = new HashMap<>();
        List<NganhToHop> allNth = nganhToHopBUS.getAllNganhToHop().getData();
        if (allNth != null) {
            for (NganhToHop nth : allNth) {
                String maNganh = nth.getNganh() != null ? nth.getNganh().getMaNganh() : null;
                if (maNganh == null) continue;
                nganhToHopMap.computeIfAbsent(maNganh, k -> new ArrayList<>()).add(nth);
            }
        }

        // Prefetch DiemCong đã có
        Map<String, DiemCong> dcExistingMap = new HashMap<>();
        List<DiemCong> allDc = diemCongBUS.getAllDiemCong().getData();
        if (allDc != null) {
            for (DiemCong dc : allDc) {
                if (dc.getDcKey() != null) dcExistingMap.put(dc.getDcKey(), dc);
            }
        }

        List<DiemThi> diemThiUpdateBatch = new ArrayList<>();
        List<DiemCong> dcInsertBatch = new ArrayList<>();
        List<DiemCong> dcUpdateBatch = new ArrayList<>();

        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100).bufferSize(4096).open(is)) {

            Sheet sheet = workbook.getSheetAt(0); // sheet "import_xettuyen"
            if (sheet == null) {
                System.err.println("Không tìm thấy sheet 0 trong file quy đổi tiếng Anh.");
                return;
            }

            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) { isHeader = false; continue; }

                String cccd        = getStringValue(row.getCell(1));
                BigDecimal diemQuyDoi = getBigDecimalValue(row.getCell(4)); // Cột E: Điểm quy đổi
                BigDecimal diemCongCc = getBigDecimalValue(row.getCell(5)); // Cột F: Điểm cộng

                if (cccd.isEmpty()) continue;

                ThiSinh2025 ts = thiSinhMap.get(cccd);
                if (ts == null) {
                    System.err.println("CC: Không tìm thấy thí sinh CCCD=" + cccd);
                    continue;
                }

                // 1. Cập nhật n1Cc trên tất cả DiemThi của thí sinh
                List<DiemThi> dtList = diemThiMap.getOrDefault(cccd, new ArrayList<>());
                for (DiemThi dt : dtList) {
                    dt.setN1Cc(diemQuyDoi);
                    diemThiUpdateBatch.add(dt);
                    if (diemThiUpdateBatch.size() >= BATCH_SIZE) {
                        flushDiemThiUpdate(diemThiUpdateBatch, diemThiBUS);
                    }
                }

                // 2. Lấy nguyện vọng duy nhất (dedup theo thuTu+maNganh)
                List<NguyenVong> nvList = nvMap.getOrDefault(cccd, new ArrayList<>());
                // Dedup theo (maNganh, maToHop, phuongThuc) — tạo DiemCong cho từng phương thức riêng
                Set<String> seenDcKey = new HashSet<>();

                for (NguyenVong nv : nvList) {
                    if (nv.getNganh() == null) continue;
                    String maNganh = nv.getNganh().getMaNganh();
                    List<NganhToHop> nthList = nganhToHopMap.getOrDefault(maNganh, new ArrayList<>());

                    for (NganhToHop nth : nthList) {
                        if (nth.getToHop() == null) continue;

                        // Chỉ áp dụng cho tổ hợp KHÔNG có môn Ngoại ngữ/Tiếng Anh (N1)
                        // Tổ hợp đã có N1 thì thí sinh dùng điểm thi thực tế, không dùng chứng chỉ để cộng
                        Boolean coN1 = nth.getN1();
                        if (Boolean.TRUE.equals(coN1)) continue;

                        String maToHop = nth.getToHop().getMaToHop();
                        String phuongThuc = nv.getPhuongThuc();
                        String dcKey = cccd + "_" + maNganh + "_" + maToHop + "_" + phuongThuc;
                        if (!seenDcKey.add(dcKey)) continue;

                        DiemCong existing = dcExistingMap.get(dcKey);
                        if (existing != null) {
                            // Cập nhật diemCc, tính lại diemTong
                            existing.setDiemCc(diemCongCc);
                            existing.setDiemTong(tinhDiemTong(existing.getDiemUtxt(), diemCongCc));
                            dcUpdateBatch.add(existing);
                        } else {
                            // Tạo mới DiemCong chỉ có diemCc
                            DiemCong dc = new DiemCong();
                            dc.setThiSinh(ts);
                            dc.setNganh(nv.getNganh());
                            dc.setToHop(nth.getToHop());
                            dc.setPhuongThuc(nv.getPhuongThuc());
                            dc.setDiemUtxt(null);
                            dc.setDiemCc(diemCongCc);
                            dc.setDiemTong(tinhDiemTong(null, diemCongCc));
                            dc.setDcKey(dcKey);
                            dcInsertBatch.add(dc);
                            dcExistingMap.put(dcKey, dc);
                        }

                        if (dcInsertBatch.size() >= BATCH_SIZE) {
                            saveBatchAndClear(dcInsertBatch, diemCongBUS::addListDiemCong);
                        }
                        if (dcUpdateBatch.size() >= BATCH_SIZE) {
                            saveBatchAndClear(dcUpdateBatch, diemCongBUS::updateBatchDiemCong);
                        }
                    }
                }
            }

            // Flush còn lại
            if (!diemThiUpdateBatch.isEmpty()) flushDiemThiUpdate(diemThiUpdateBatch, diemThiBUS);
            if (!dcInsertBatch.isEmpty()) saveBatchAndClear(dcInsertBatch, diemCongBUS::addListDiemCong);
            if (!dcUpdateBatch.isEmpty()) saveBatchAndClear(dcUpdateBatch, diemCongBUS::updateBatchDiemCong);

            System.out.println("Import quy đổi điểm Tiếng Anh thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi import quy đổi Tiếng Anh: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    //Check xem ngành có xét pt này khi import nguyện vọng hay ko
    private boolean kiemTraNganhCoXetPhuongThuc(Nganh ng, String phuongthuc){
        if (ng == null || phuongthuc == null) return false;
        
        String pt = phuongthuc.trim().toUpperCase();
        
        switch (pt) {
            case "THPT":
                return Boolean.TRUE.equals(ng.getThpt());
            case "DGNL":
                return Boolean.TRUE.equals(ng.getDgnl());
            case "VSAT":
                return Boolean.TRUE.equals(ng.getVsat());
            case "XTT":
                return Boolean.TRUE.equals(ng.getTuyenThang());
            default:
                return false;
        }
    }

    /** Kiểm tra tổ hợp môn có chứa mã môn đạt giải không */
    private boolean coMonTrongToHop(NganhToHop nth, String maMon) {
        if (maMon == null || maMon.isEmpty()) return false;
        switch (maMon) {
            case "TO":   return Boolean.TRUE.equals(nth.getTo());
            case "LI":   return Boolean.TRUE.equals(nth.getLi());
            case "HO":   return Boolean.TRUE.equals(nth.getHo());
            case "SI":   return Boolean.TRUE.equals(nth.getSi());
            case "SU":   return Boolean.TRUE.equals(nth.getSu());
            case "DI":   return Boolean.TRUE.equals(nth.getDi());
            case "VA":   return Boolean.TRUE.equals(nth.getVa());
            case "N1":   return Boolean.TRUE.equals(nth.getN1());
            case "TI":   return Boolean.TRUE.equals(nth.getTi());
            case "KTPL": return Boolean.TRUE.equals(nth.getKtpl());
            case "KHAC": return false; // Môn khác không thuộc tổ hợp nào → luôn dùng mức thấp hơn
            default:     return false;
        }
    }

    /** Tính diemTong = min(diemUtxt + diemCc, 3.0); null coi là 0 */
    private BigDecimal tinhDiemTong(BigDecimal diemUtxt, BigDecimal diemCc) {
        BigDecimal tong = BigDecimal.ZERO;
        if (diemUtxt != null) tong = tong.add(diemUtxt);
        if (diemCc   != null) tong = tong.add(diemCc);
        BigDecimal max = new BigDecimal("3.00");
        return tong.compareTo(max) > 0 ? max : tong;
    }

    /** Flush batch update DiemThi (dùng merge từng cái qua BUS) */
    private void flushDiemThiUpdate(List<DiemThi> batch, DiemThiBUS diemThiBUS) {
        for (DiemThi dt : batch) {
            diemThiBUS.updateDiemThi(dt);
        }
        batch.clear();
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
    
    private NganhToHop buildNganhToHop(Nganh ng, ToHop th, ToHopData parsedData, BigDecimal doLech) {
        NganhToHop nth = new NganhToHop();
        
        // Truyền thẳng Object có sẵn trên RAM vào
        nth.setNganh(ng);
        nth.setToHop(th);

        // Gán các hệ số môn
        nth.setHsMon1(parsedData.hs1);
        nth.setHsMon2(parsedData.hs2);
        nth.setHsMon3(parsedData.hs3);

        // Gán độ lệch
        nth.setDoLech(doLech);

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
        // Gộp 3 môn lại thành 1 chuỗi để check (VD: "TO VA SI")
        String dsMon = (data.mon1 + " " + data.mon2 + " " + data.mon3).toUpperCase();

        if (dsMon.contains("TO"))   nth.setTo(true);
        if (dsMon.contains("VA"))   nth.setVa(true);
        if (dsMon.contains("LI"))   nth.setLi(true);
        if (dsMon.contains("HO"))   nth.setHo(true);
        if (dsMon.contains("SI"))   nth.setSi(true);
        if (dsMon.contains("SU"))   nth.setSu(true);
        if (dsMon.contains("DI"))   nth.setDi(true);
        if (dsMon.contains("TI"))   nth.setTi(true);
        if (dsMon.contains("KTPL")) nth.setKtpl(true);
        if (dsMon.contains("N1"))   nth.setN1(true);   // Ngoại ngữ (D01, D14...)
        // NK1..NK6, CNCN, CNNN → đánh cờ "khác"
        if (dsMon.contains("NK") || dsMon.contains("CNCN") || dsMon.contains("CNNN")) {
            nth.setKhac(true);
        }
    }
    
}
