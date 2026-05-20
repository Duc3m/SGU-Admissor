package com.sgu.admissor.bus;

import com.google.inject.persist.Transactional;
import com.monitorjbl.xlsx.StreamingReader;
import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.dto.ToHopData;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.ThiSinh2025;
import com.sgu.admissor.entity.ToHop;
import com.sgu.admissor.util.PhanBoChiTieuUtil;
import com.sgu.admissor.util.TenMonUtil;
import com.sgu.admissor.util.ExcelFileClassifier;
import com.sgu.admissor.util.ExcelFileClassifier.ClassificationResult;
import com.sgu.admissor.util.ExcelFileClassifier.FileType;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
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
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Duc3m
 */
public class ExcelImportBUSV2 extends ExcelImportBUS {
    
    private final Provider<ThiSinh2025BUS> thiSinhBUSProvider;
    private final Provider<DiemThiBUS> diemThiBUSProvider;
    private final Provider<NganhBUS> nganhBUSProvider;
    private final Provider<ToHopBUS> toHopBUSProvider;
    private final Provider<NganhToHopBUS> nganhToHopBUSProvider;
    private final Provider<NguyenVongBUS> nguyenVongBUSProvider;
    private final Provider<DiemCongBUS> diemCongBUSProvider;
    private final Provider<EntityManager> entityManagerProvider;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BATCH_SIZE_V2 = 2000; 

    @Inject
    public ExcelImportBUSV2(
            Provider<ThiSinh2025BUS> thiSinhBUSProvider, 
            Provider<DiemThiBUS> diemThiBUSProvider, 
            Provider<NganhBUS> nganhBUSProvider, 
            Provider<ToHopBUS> toHopBUSProvider, 
            Provider<NganhToHopBUS> nganhToHopBUSProvider,
            Provider<NguyenVongBUS> nguyenVongBUSProvider,
            Provider<DiemCongBUS> diemCongBUSProvider,
            Provider<EntityManager> entityManagerProvider
    ) {
        super(thiSinhBUSProvider, diemThiBUSProvider, nganhBUSProvider, toHopBUSProvider, nganhToHopBUSProvider, nguyenVongBUSProvider, diemCongBUSProvider);
        
        this.thiSinhBUSProvider = thiSinhBUSProvider;
        this.diemThiBUSProvider = diemThiBUSProvider;
        this.nganhBUSProvider = nganhBUSProvider;
        this.toHopBUSProvider = toHopBUSProvider;
        this.nganhToHopBUSProvider = nganhToHopBUSProvider;
        this.nguyenVongBUSProvider = nguyenVongBUSProvider;
        this.diemCongBUSProvider = diemCongBUSProvider;
        this.entityManagerProvider = entityManagerProvider;
    }

    public BUSResult importFromFilesV2(File[] files, java.util.function.Consumer<Integer> progressCallback) {
        if (progressCallback == null) progressCallback = p -> {};
        progressCallback.accept(0);

        if (files == null || files.length == 0) {
            return BUSResult.error("Không có file để import!");
        }

        List<ClassificationResult> classified = ExcelFileClassifier.classifyAll(files);
        Map<FileType, File> fileMap = new EnumMap<>(FileType.class);
        List<String> warnings = new ArrayList<>();

        for (ClassificationResult result : classified) {
            FileType type = result.getType();
            if (type == FileType.UNKNOWN) {
                warnings.add("Không nhận diện được file: " + safeFileName(result.getFile()));
                continue;
            }
            if (fileMap.containsKey(type)) {
                warnings.add("Trùng loại file " + type.name() + ": " + safeFileName(result.getFile()));
                continue;
            }
            fileMap.put(type, result.getFile());
        }

        File chiTieu = fileMap.get(FileType.CHI_TIEU);
        File nguong = fileMap.get(FileType.NGUONG_DAU_VAO);
        File toHop = fileMap.get(FileType.TO_HOP_MON);
        File dsThiSinh = fileMap.get(FileType.DS_THI_SINH);
        File dgnlVsat = fileMap.get(FileType.DIEM_DGNL_VSAT);
        File nguyenVong = fileMap.get(FileType.NGUYEN_VONG);
        File uuTien = fileMap.get(FileType.UU_TIEN_XET_TUYEN);
        File quyDoi = fileMap.get(FileType.QUY_DOI_TIENG_ANH);
        
        if (chiTieu != null) {
            importChiTieuV2(chiTieu, progressCallback, 0, 5);
        } else {
            progressCallback.accept(5);
        }

        if (nguong != null) {
            importNguongDauVaoV2(nguong, progressCallback, 5, 10);
        } else {
            progressCallback.accept(10);
        }

        if (toHop != null) {
            importToHopMonV2(toHop, progressCallback, 10, 15);
        } else {
            progressCallback.accept(15);
        }

        if (dsThiSinh != null) {
            importThiSinhVaDiemV2(dsThiSinh, progressCallback, 15, 45);
        } else {
            progressCallback.accept(45);
        }

        if (dgnlVsat != null) {
            importDiemDGNLVaVSATV2(dgnlVsat, progressCallback, 45, 55);
        } else {
            progressCallback.accept(55);
        }

        if (nguyenVong != null) {
            importNguyenVongV2(nguyenVong, progressCallback, 55, 75);
        } else {
            progressCallback.accept(75);
        }

        if (uuTien != null) {
            importUuTienXetTuyenV2(uuTien, progressCallback, 75, 80);
        } else {
            progressCallback.accept(80);
        }

        if (quyDoi != null) {
            importQuyDoiTiengAnhV2(quyDoi, progressCallback, 80, 100);
        } else {
            progressCallback.accept(100);
        }

        if (warnings.isEmpty()) {
            return BUSResult.success("Import dữ liệu tuyển sinh thành công!");
        }
        return BUSResult.successWithData("Import thành công kèm cảnh báo!", warnings);
    }

    @Transactional
    public BUSResult importChiTieuV2(File fileChiTieu, Consumer<Integer> callback, int start, int end) {
        NganhBUS nganhBUS = nganhBUSProvider.get();
        Map<String, Nganh> nganhMap = new HashMap<>();
        Set<String> existingMaNganh = new HashSet<>();

        List<Nganh> existing = nganhBUS.getAllNganh().getData();
        if (existing != null) {
            for (Nganh ng : existing) {
                if (ng.getMaNganh() != null) existingMaNganh.add(ng.getMaNganh());
            }
        }

        try (InputStream is = new FileInputStream(fileChiTieu);
             Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
            Sheet sheet = wb.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;

            for (Row row : sheet) {
                current++;
                if (row.getRowNum() <= 1) continue;
                String maNganh = getStringValue(row.getCell(1));
                if (maNganh.isEmpty()) continue;
                if (maNganh.length() > 10) break;
                if (existingMaNganh.contains(maNganh)) continue;
                
                Nganh ng = new Nganh();
                fillNganhInfo1(ng, row);
                nganhMap.put(maNganh, ng);

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi Import Chi_tieu_2025: " + e.getMessage());
        }

        List<Nganh> nganhBatch = new ArrayList<>();
        for (Nganh ng : nganhMap.values()) {
            nganhBatch.add(ng);
            if (nganhBatch.size() >= BATCH_SIZE_V2) saveBatchAndClear(nganhBatch, nganhBUS::addListNganh);
        }
        saveBatchAndClear(nganhBatch, nganhBUS::addListNganh);
        return BUSResult.success("Import Chi_tieu_2025 thành công!");
    }

    @Transactional
    public BUSResult importNguongDauVaoV2(File fileNguongDauVao, Consumer<Integer> callback, int start, int end) {
        NganhBUS nganhBUS = nganhBUSProvider.get();
        List<String> warnings = new ArrayList<>();
        if (!hasAnyNganh(nganhBUS)) {
            warnings.add("Cảnh báo: Chưa có dữ liệu Ngành trong DB.");
        }

        try (InputStream is = new FileInputStream(fileNguongDauVao);
             Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
            Sheet sheet = wb.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;

            for (Row row : sheet) {
                current++;
                if (row.getRowNum() == 0) continue;
                String maNganh = getStringValue(row.getCell(1));
                if (maNganh.isEmpty()) continue;
                
                BUSResult<Nganh> result = nganhBUS.getNganhByMaNganh(maNganh);
                if (result != null && result.isSuccess() && result.getData() != null) {
                    Nganh ng = result.getData();
                    fillNganhInfo2(ng, row);
                    nganhBUS.updateNganh(ng); 
                }

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }
            EntityManager em = entityManagerProvider.get();
            if (em != null) { em.flush(); em.clear(); }
            
            return BUSResult.success("Import Nguong_dau_vao_2025 thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi Import Nguong_dau_vao_2025: " + e.getMessage());
        }
    }

    @Transactional
    public BUSResult importToHopMonV2(File fileToHopMon, Consumer<Integer> callback, int start, int end) {
        NganhBUS nganhBUS = nganhBUSProvider.get();
        ToHopBUS toHopBUS = toHopBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();

        Map<String, Nganh> nganhMap = new HashMap<>();
        Map<String, ToHop> toHopMap = new HashMap<>();
        List<ToHop> toHopList = new ArrayList<>();
        List<NganhToHop> nganhToHopList = new ArrayList<>();
        List<Nganh> nganhUpdateList = new ArrayList<>();
        Set<String> nganhNeedUpdate = new HashSet<>();

        List<Nganh> existingNganh = nganhBUS.getAllNganh().getData();
        if (existingNganh != null) {
            for (Nganh ng : existingNganh) {
                if (ng.getMaNganh() != null) nganhMap.put(ng.getMaNganh(), ng);
            }
        }

        List<ToHop> existingToHops = toHopBUS.getAllToHop().getData();
        if (existingToHops != null) {
            for (ToHop t : existingToHops) {
                if (t.getMaToHop() != null) toHopMap.put(t.getMaToHop(), t);
            }
        }

        try (InputStream is = new FileInputStream(fileToHopMon);
             Workbook wb = StreamingReader.builder().rowCacheSize(100).open(is)) {
            Sheet sheet = wb.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;

            for (Row row : sheet) {
                current++;
                if (row.getRowNum() == 0) continue;
                String maNganh = getStringValue(row.getCell(1));
                String chuoiToHop = getStringValue(row.getCell(3));
                if (maNganh.isEmpty() || chuoiToHop.isEmpty()) continue;

                ToHopData parsedData = parseToHopString(chuoiToHop);
                String maToHop = parsedData.maToHop;
                if (maToHop == null || maToHop.isEmpty()) continue;

                ToHop th = toHopMap.get(maToHop);
                if (th == null) {
                    th = new ToHop();
                    th.setMaToHop(maToHop);
                    th.setMon1(parsedData.mon1);
                    th.setMon2(parsedData.mon2);
                    th.setMon3(parsedData.mon3);
                    th.setTenToHop(buildTenToHop(th.getMon1(), th.getMon2(), th.getMon3()));

                    toHopList.add(th);
                    toHopMap.put(maToHop, th);
                }

                Nganh ng = nganhMap.get(maNganh);
                if (ng != null) {
                    String flagStr = getStringValue(row.getCell(6));
                    if (!flagStr.isEmpty()) {
                        String maToHopGoc = getStringValue(row.getCell(5));
                        ToHop thGoc = toHopMap.get(maToHopGoc);
                        if (thGoc != null) {
                            ng.setToHopGoc(thGoc);
                            if (nganhNeedUpdate.add(maNganh)) {
                                nganhUpdateList.add(ng);
                            }
                        }
                    }
                }

                BigDecimal doLech = getBigDecimalValue(row.getCell(7));
                if (ng != null && th != null) {
                    nganhToHopList.add(buildNganhToHop(ng, th, parsedData, doLech));
                }

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi Import tohopmon: " + e.getMessage());
        }

        for (int i = 0; i < toHopList.size(); i += BATCH_SIZE_V2) {
            toHopBUS.addListToHop(new ArrayList<>(toHopList.subList(i, Math.min(i + BATCH_SIZE_V2, toHopList.size()))));
        }
        for (Nganh ng : nganhUpdateList) {
            nganhBUS.updateNganh(ng);
        }
        for (int i = 0; i < nganhToHopList.size(); i += BATCH_SIZE_V2) {
            nganhToHopBUS.addListNganhToHop(new ArrayList<>(nganhToHopList.subList(i, Math.min(i + BATCH_SIZE_V2, nganhToHopList.size()))));
        }

        EntityManager em = entityManagerProvider.get();
        if (em != null) { em.flush(); em.clear(); }
        return BUSResult.success("Import tohopmon thành công!");
    }

    @Transactional
    public void importThiSinhVaDiemV2(File excelFile, Consumer<Integer> callback, int start, int end) {
        List<ThiSinh2025> thiSinhBatch = new ArrayList<>();
        List<DiemThi> diemThiBatch = new ArrayList<>();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();

        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;
            boolean isHeaderRow = true;

            for (Row row : sheet) {
                current++;
                if (isHeaderRow) { isHeaderRow = false; continue; }
                if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().isEmpty()) continue;

                ThiSinh2025 ts = buildThiSinhFromRow(row);
                DiemThi dt = buildDiemThiFromRow(row, ts);
                thiSinhBatch.add(ts);
                diemThiBatch.add(dt);

                if (thiSinhBatch.size() >= BATCH_SIZE_V2) {
                    saveBatchAndClear(thiSinhBatch, thiSinhBUS::addListThiSinh);
                    saveBatchAndClear(diemThiBatch, diemThiBUS::addListDiemThi);
                }

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }
            if (!thiSinhBatch.isEmpty()) {
                saveBatchAndClear(thiSinhBatch, thiSinhBUS::addListThiSinh);
                saveBatchAndClear(diemThiBatch, diemThiBUS::addListDiemThi);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi importThiSinhVaDiemV2: " + e.getMessage());
        }
    }

    @Transactional
    public void importDiemDGNLVaVSATV2(File excelFile, Consumer<Integer> callback, int start, int end) {
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        int totalRows = 0;
        try (InputStream checkIs = new FileInputStream(excelFile);
             Workbook checkWb = StreamingReader.builder().rowCacheSize(10).open(checkIs)) {
            Sheet s0 = checkWb.getSheetAt(0);
            if (s0 != null) totalRows += s0.getLastRowNum();
            Sheet s1 = checkWb.getSheetAt(1);
            if (s1 != null) totalRows += s1.getLastRowNum();
        } catch (Exception ignored) {}

        int processedRows = 0;

        // Sheet 0: VSAT
        try (InputStream is = new FileInputStream(excelFile);
             Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {

            Map<String, DiemThi> vsatDiemMap = new HashMap<>();
            Sheet vsatSheet = workbook.getSheetAt(0);
            if (vsatSheet != null) {
                boolean isHeader = true;
                for (Row row : vsatSheet) {
                    processedRows++;
                    if (isHeader) { isHeader = false; continue; }
                    String cccd = getStringValue(row.getCell(1));
                    if (cccd.isEmpty()) continue;

                    String dotThi   = getStringValue(row.getCell(2)).trim();
                    String maDvtctd = getStringValue(row.getCell(10)).trim();
                    String maMonThi = getStringValue(row.getCell(6)).toUpperCase().trim();
                    BigDecimal diem = getBigDecimalValue(row.getCell(8));

                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    if (ts == null) continue;

                    String vsatKey = cccd + "_" + dotThi + "_" + maDvtctd;
                    DiemThi dt = vsatDiemMap.computeIfAbsent(vsatKey, k -> {
                        DiemThi newDt = new DiemThi();
                        newDt.setThiSinh(ts);
                        newDt.setPhuongThuc(PHUONGTHUC.PT[3]);
                        return newDt;
                    });

                    String maMon = maMonThi.endsWith("_VS") ? maMonThi.substring(0, maMonThi.length() - 3) : maMonThi;
                    switch (maMon) {
                        case "TO": case "M1": dt.setTo(diem);    break;
                        case "LI": case "M2": dt.setLi(diem);    break;
                        case "HO": case "M3": dt.setHo(diem);    break;
                        case "SI": case "M4": dt.setSi(diem);    break;
                        case "VA": case "M5": dt.setVa(diem);    break;
                        case "SU": case "M6": dt.setSu(diem);    break;
                        case "DI": case "M7": dt.setDi(diem);    break;
                        case "N1": case "M8": dt.setN1Thi(diem); break;
                        case "TI":   dt.setTi(diem);    break;
                        case "KTPL": dt.setKtpl(diem);  break;
                    }

                    if (totalRows > 0 && callback != null) {
                        callback.accept(start + (processedRows * (end - start)) / totalRows);
                    }
                }
            }

            List<DiemThi> vsatBatch = new ArrayList<>(vsatDiemMap.values());
            for (int i = 0; i < vsatBatch.size(); i += BATCH_SIZE_V2) {
                saveBatchAndClear(new ArrayList<>(vsatBatch.subList(i, Math.min(i + BATCH_SIZE_V2, vsatBatch.size()))), diemThiBUS::addListDiemThi);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Sheet 1: DGNL
        try (InputStream is2 = new FileInputStream(excelFile);
             Workbook workbook2 = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is2)) {

            Map<String, DiemThi> dgnlDiemMap = new HashMap<>();
            Sheet dgnlSheet = workbook2.getSheetAt(1);
            if (dgnlSheet != null) {
                boolean isHeader = true;
                for (Row row : dgnlSheet) {
                    processedRows++;
                    if (isHeader) { isHeader = false; continue; }
                    String cccd = getStringValue(row.getCell(1));
                    if (cccd.isEmpty()) continue;

                    BigDecimal diem = getBigDecimalValue(row.getCell(8));
                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    if (ts == null) continue;

                    DiemThi existing = dgnlDiemMap.get(cccd);
                    if (existing == null) {
                        DiemThi dt = new DiemThi();
                        dt.setThiSinh(ts);
                        dt.setPhuongThuc(PHUONGTHUC.PT[2]);
                        dt.setNl1(diem);
                        dgnlDiemMap.put(cccd, dt);
                    } else {
                        BigDecimal currentNl1 = existing.getNl1();
                        if (diem != null && (currentNl1 == null || diem.compareTo(currentNl1) > 0)) {
                            existing.setNl1(diem);
                        }
                    }

                    if (totalRows > 0 && callback != null) {
                        callback.accept(start + (processedRows * (end - start)) / totalRows);
                    }
                }
            }

            List<DiemThi> dgnlBatch = new ArrayList<>(dgnlDiemMap.values());
            for (int i = 0; i < dgnlBatch.size(); i += BATCH_SIZE_V2) {
                saveBatchAndClear(new ArrayList<>(dgnlBatch.subList(i, Math.min(i + BATCH_SIZE_V2, dgnlBatch.size()))), diemThiBUS::addListDiemThi);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void importNguyenVongV2(File excelFilePath, Consumer<Integer> callback, int start, int end) {
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();
        NganhBUS nganhBUS = nganhBUSProvider.get();
        
        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        Map<String, Nganh> nganhMap = new HashMap<>();
        List<Nganh> allNganh = nganhBUS.getAllNganh().getData();
        if (allNganh != null) {
            for (Nganh ng : allNganh) {
                if (ng.getMaNganh() != null) nganhMap.put(ng.getMaNganh(), ng);
            }
        }

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
        
        List<NguyenVong> nvBatch = new ArrayList<>();
        
        try (InputStream is = new FileInputStream(excelFilePath);
             Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {
            int[] sheets = {1, 2};
            int startRow = 5;
            
            int totalRows = 0;
            for (int sheetIndex : sheets) {
                Sheet s = workbook.getSheetAt(sheetIndex);
                if (s != null) totalRows += s.getLastRowNum();
            }
            int processedRowsCount = 0;
            
            for (int sheetIndex : sheets) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) continue;
                
                for (Row row : sheet) {
                    processedRowsCount++;
                    if (row.getRowNum() < startRow) continue;
                    
                    String cccd = getStringValue(row.getCell(1));
                    int thuTuNV = getIntegerValue(row.getCell(2));
                    String maNganh = getStringValue(row.getCell(5));
                    
                    if (cccd.isEmpty()) continue;
                    if (maNganh.isEmpty() || thuTuNV == 0) {
                        throw new RuntimeException("Dữ liệu thiếu ở dòng " + (row.getRowNum() + 1));
                    }
                    
                    ThiSinh2025 ts = thiSinhMap.get(cccd);
                    Nganh ng = nganhMap.get(maNganh);
                    if (ts == null || ng == null) continue;
                    
                    Set<String> phuongThucList = phuongThucMap.getOrDefault(cccd, new HashSet<>());
                    for (String pt : phuongThucList) {
                        if (!kiemTraNganhCoXetPhuongThuc(ng, pt)) continue;
                        
                        NguyenVong nv = new NguyenVong();
                        nv.setThiSinh(ts);
                        nv.setThuTu(thuTuNV);
                        nv.setNganh(ng);
                        nv.setPhuongThuc(pt);
                        nvBatch.add(nv);
                    }
                    
                    if (nvBatch.size() >= BATCH_SIZE_V2) {
                        saveBatchAndClear(nvBatch, nguyenVongBUS::addListNguyenVong);
                    }

                    if (totalRows > 0 && callback != null) {
                        callback.accept(start + (processedRowsCount * (end - start)) / totalRows);
                    }
                }
            }
            if (!nvBatch.isEmpty()) {
                saveBatchAndClear(nvBatch, nguyenVongBUS::addListNguyenVong);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi giải mã file nguyện vọng: " + e.getMessage());
        }
    }

    @Transactional
    public void importUuTienXetTuyenV2(File excelFile, Consumer<Integer> callback, int start, int end) {
        DiemCongBUS diemCongBUS = diemCongBUSProvider.get();
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        Map<String, List<NguyenVong>> nvMap = new HashMap<>();
        List<NguyenVong> allNv = nguyenVongBUS.getAllNguyenVong().getData();
        if (allNv != null) {
            for (NguyenVong nv : allNv) {
                String cccd = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : null;
                if (cccd == null) continue;
                nvMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(nv);
            }
        }

        Map<String, List<NganhToHop>> nganhToHopMap = new HashMap<>();
        List<NganhToHop> allNth = nganhToHopBUS.getAllNganhToHop().getData();
        if (allNth != null) {
            for (NganhToHop nth : allNth) {
                String maNganh = nth.getNganh() != null ? nth.getNganh().getMaNganh() : null;
                if (maNganh == null) continue;
                nganhToHopMap.computeIfAbsent(maNganh, k -> new ArrayList<>()).add(nth);
            }
        }

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
             Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;
            boolean isHeader = true;

            for (Row row : sheet) {
                current++;
                if (isHeader) { isHeader = false; continue; }

                String cccd   = getStringValue(row.getCell(1));
                String maMon  = getStringValue(row.getCell(4)).toUpperCase().trim();
                BigDecimal diemCoMon     = getBigDecimalValue(row.getCell(6));
                BigDecimal diemKhongCoMon = getBigDecimalValue(row.getCell(7));

                if (cccd.isEmpty()) continue;
                ThiSinh2025 ts = thiSinhMap.get(cccd);
                if (ts == null) continue;

                List<NguyenVong> nvList = nvMap.getOrDefault(cccd, new ArrayList<>());
                Set<String> seenDcKey = new HashSet<>();

                for (NguyenVong nv : nvList) {
                    if (nv.getNganh() == null) continue;
                    Nganh nganh = nv.getNganh();
                    String maNganh = nganh.getMaNganh();
                    String phuongThuc = nv.getPhuongThuc();
                    if (phuongThuc == null || phuongThuc.toUpperCase().contains("DGNL")) continue;

                    if (!isNganhAcceptsPhuongThuc(nganh, phuongThuc)) continue;

                    boolean isToHopMethod = (phuongThuc.toUpperCase().contains("THPT") || phuongThuc.toUpperCase().contains("VSAT"));

                    if (isToHopMethod) {
                        List<NganhToHop> nthList = nganhToHopMap.getOrDefault(maNganh, new ArrayList<>());
                        for (NganhToHop nth : nthList) {
                            if (nth.getToHop() == null) continue;
                            boolean coMon = coMonTrongToHop(nth, maMon);
                            BigDecimal diemUtxt = coMon ? diemCoMon : diemKhongCoMon;
                            if (diemUtxt.compareTo(BigDecimal.ZERO) == 0) continue;
                            
                            upsertDiemCong(ts, nganh, nth.getToHop(), phuongThuc, diemUtxt, dcExistingMap, insertBatch, updateBatch, seenDcKey);
                        }
                    } else {
                        BigDecimal diemUtxt = diemKhongCoMon;
                        if (diemUtxt.compareTo(BigDecimal.ZERO) == 0) continue;
                        upsertDiemCong(ts, nganh, null, phuongThuc, diemUtxt, dcExistingMap, insertBatch, updateBatch, seenDcKey);
                    }
                }

                if (insertBatch.size() >= BATCH_SIZE_V2) saveBatchAndClear(insertBatch, diemCongBUS::addListDiemCong);
                if (updateBatch.size() >= BATCH_SIZE_V2) saveBatchAndClear(updateBatch, diemCongBUS::updateBatchDiemCong);

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }

            if (!insertBatch.isEmpty()) saveBatchAndClear(insertBatch, diemCongBUS::addListDiemCong);
            if (!updateBatch.isEmpty()) saveBatchAndClear(updateBatch, diemCongBUS::updateBatchDiemCong);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi importUuTienXetTuyenV2: " + e.getMessage());
        }
    }

    @Transactional
    public void importQuyDoiTiengAnhV2(File excelFile, Consumer<Integer> callback, int start, int end) {
        DiemCongBUS diemCongBUS = diemCongBUSProvider.get();
        DiemThiBUS diemThiBUS = diemThiBUSProvider.get();
        NguyenVongBUS nguyenVongBUS = nguyenVongBUSProvider.get();
        NganhToHopBUS nganhToHopBUS = nganhToHopBUSProvider.get();
        ThiSinh2025BUS thiSinhBUS = thiSinhBUSProvider.get();

        Map<String, ThiSinh2025> thiSinhMap = new HashMap<>();
        List<ThiSinh2025> allThiSinh = thiSinhBUS.getAllThiSinh().getData();
        if (allThiSinh != null) {
            for (ThiSinh2025 ts : allThiSinh) {
                if (ts.getCccd() != null) thiSinhMap.put(ts.getCccd(), ts);
            }
        }

        Map<String, List<DiemThi>> diemThiMap = new HashMap<>();
        List<DiemThi> allDiemThi = diemThiBUS.getAllDiemThi().getData();
        if (allDiemThi != null) {
            for (DiemThi dt : allDiemThi) {
                String cccd = dt.getThiSinh() != null ? dt.getThiSinh().getCccd() : null;
                if (cccd == null || dt.getNl1() != null) continue;
                diemThiMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(dt);
            }
        }

        Map<String, List<NguyenVong>> nvMap = new HashMap<>();
        List<NguyenVong> allNv = nguyenVongBUS.getAllNguyenVong().getData();
        if (allNv != null) {
            for (NguyenVong nv : allNv) {
                String cccd = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : null;
                if (cccd == null) continue;
                nvMap.computeIfAbsent(cccd, k -> new ArrayList<>()).add(nv);
            }
        }

        Map<String, List<NganhToHop>> nganhToHopMap = new HashMap<>();
        List<NganhToHop> allNth = nganhToHopBUS.getAllNganhToHop().getData();
        if (allNth != null) {
            for (NganhToHop nth : allNth) {
                String maNganh = nth.getNganh() != null ? nth.getNganh().getMaNganh() : null;
                if (maNganh == null) continue;
                nganhToHopMap.computeIfAbsent(maNganh, k -> new ArrayList<>()).add(nth);
            }
        }

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
             Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int current = 0;
            boolean isHeader = true;

            for (Row row : sheet) {
                current++;
                if (isHeader) { isHeader = false; continue; }

                String cccd        = getStringValue(row.getCell(1));
                BigDecimal diemQuyDoi = getBigDecimalValue(row.getCell(4));
                BigDecimal diemCongCc = getBigDecimalValue(row.getCell(5));

                if (cccd.isEmpty()) continue;
                ThiSinh2025 ts = thiSinhMap.get(cccd);
                if (ts == null) continue;

                List<DiemThi> dtList = diemThiMap.getOrDefault(cccd, new ArrayList<>());
                for (DiemThi dt : dtList) {
                    dt.setN1Cc(diemQuyDoi);
                    diemThiUpdateBatch.add(dt);
                    if (diemThiUpdateBatch.size() >= BATCH_SIZE_V2) {
                        flushDiemThiUpdateV2(diemThiUpdateBatch, diemThiBUS);
                    }
                }

                List<NguyenVong> nvList = nvMap.getOrDefault(cccd, new ArrayList<>());
                Set<String> seenDcKey = new HashSet<>();

                for (NguyenVong nv : nvList) {
                    if (nv.getNganh() == null) continue;
                    String maNganh = nv.getNganh().getMaNganh();
                    String phuongThuc = nv.getPhuongThuc();
                    if (phuongThuc == null) continue;

                    boolean isDgnl = phuongThuc.toUpperCase().contains("DGNL") || phuongThuc.toUpperCase().contains("ĐGNL");

                    if (isDgnl) {
                        String dcKey = cccd + "_" + maNganh + "_NONE_" + phuongThuc;
                        if (!seenDcKey.add(dcKey)) continue;

                        DiemCong existing = dcExistingMap.get(dcKey);
                        if (existing != null) {
                            existing.setDiemCc(diemCongCc);
                            existing.setDiemTong(tinhDiemTong(existing.getDiemUtxt(), diemCongCc));
                            dcUpdateBatch.add(existing);
                        } else {
                            DiemCong dc = new DiemCong();
                            dc.setThiSinh(ts);
                            dc.setNganh(nv.getNganh());
                            dc.setToHop(null);
                            dc.setPhuongThuc(phuongThuc);
                            dc.setDiemCc(diemCongCc);
                            dc.setDiemTong(tinhDiemTong(null, diemCongCc));
                            dc.setDcKey(dcKey);
                            dcInsertBatch.add(dc);
                            dcExistingMap.put(dcKey, dc);
                        }
                    } else {
                        List<NganhToHop> nthList = nganhToHopMap.getOrDefault(maNganh, new ArrayList<>());
                        for (NganhToHop nth : nthList) {
                            if (nth.getToHop() == null) continue;
                            if (Boolean.TRUE.equals(nth.getN1())) continue;

                            String maToHop = nth.getToHop().getMaToHop();
                            String dcKey = cccd + "_" + maNganh + "_" + maToHop + "_" + phuongThuc;
                            if (!seenDcKey.add(dcKey)) continue;

                            DiemCong existing = dcExistingMap.get(dcKey);
                            if (existing != null) {
                                existing.setDiemCc(diemCongCc);
                                existing.setDiemTong(tinhDiemTong(existing.getDiemUtxt(), diemCongCc));
                                dcUpdateBatch.add(existing);
                            } else {
                                DiemCong dc = new DiemCong();
                                dc.setThiSinh(ts);
                                dc.setNganh(nv.getNganh());
                                dc.setToHop(nth.getToHop());
                                dc.setPhuongThuc(phuongThuc);
                                dc.setDiemCc(diemCongCc);
                                dc.setDiemTong(tinhDiemTong(null, diemCongCc));
                                dc.setDcKey(dcKey);
                                dcInsertBatch.add(dc);
                                dcExistingMap.put(dcKey, dc);
                            }
                        }
                    }

                    if (dcInsertBatch.size() >= BATCH_SIZE_V2) saveBatchAndClear(dcInsertBatch, diemCongBUS::addListDiemCong);
                    if (dcUpdateBatch.size() >= BATCH_SIZE_V2) saveBatchAndClear(dcUpdateBatch, diemCongBUS::updateBatchDiemCong);
                }

                if (totalRows > 0 && callback != null) {
                    callback.accept(start + (current * (end - start)) / totalRows);
                }
            }

            if (!diemThiUpdateBatch.isEmpty()) flushDiemThiUpdateV2(diemThiUpdateBatch, diemThiBUS);
            if (!dcInsertBatch.isEmpty()) saveBatchAndClear(dcInsertBatch, diemCongBUS::addListDiemCong);
            if (!dcUpdateBatch.isEmpty()) saveBatchAndClear(dcUpdateBatch, diemCongBUS::updateBatchDiemCong);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi importQuyDoiTiengAnhV2: " + e.getMessage());
        }
    }

    private void flushDiemThiUpdateV2(List<DiemThi> batch, DiemThiBUS diemThiBUS) {
        for (DiemThi dt : batch) {
            diemThiBUS.updateDiemThi(dt);
        }
        batch.clear();
    }

    private <T> void saveBatchAndClear(List<T> batch, Consumer<List<T>> saver) {
        if (batch != null && !batch.isEmpty()) {
            saver.accept(batch);
            batch.clear();
            
            EntityManager em = entityManagerProvider.get();
            if (em != null) {
                em.flush(); 
                em.clear(); 
            }
        }
    }

    // =========================================================================
    // CÁC HÀM HELPER KHÁC NẰM Ở LỚP CHA TRƯỚC ĐÓ
    // =========================================================================

    private String safeFileName(File file) {
        if (file == null) return "(null)";
        String name = file.getName();
        return name != null ? name : file.getPath();
    }

    private boolean hasAnyNganh(NganhBUS nganhBUS) {
        BUSResult<List<Nganh>> result = nganhBUS.getAllNganh();
        return result != null && result.isSuccess() && result.getData() != null && !result.getData().isEmpty();
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
            return null;
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
                return 0;
            }
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

    private void fillNganhInfo2(Nganh ng, Row row) {
        BigDecimal diemSan = getBigDecimalValue(row.getCell(3));
        ng.setDiemSan(diemSan);
        ng.setDiemTrungTuyen(diemSan);
    }

    private ToHopData parseToHopString(String input) {
        ToHopData data = new ToHopData();
        if (input == null || input.trim().isEmpty()) return data;

        if (input.contains("(")) {
            int openIdx = input.indexOf('(');
            int closeIdx = input.indexOf(')');
            data.maToHop = input.substring(0, openIdx).trim();

            if (closeIdx > openIdx) {
                String innerStr = input.substring(openIdx + 1, closeIdx);
                String[] subjects = innerStr.split(",");
                if (subjects.length > 0) {
                    String[] parts = subjects[0].split("-");
                    data.mon1 = parts[0].trim();
                    if (parts.length > 1) data.hs1 = Integer.parseInt(parts[1].trim());
                }
                if (subjects.length > 1) {
                    String[] parts = subjects[1].split("-");
                    data.mon2 = parts[0].trim();
                    if (parts.length > 1) data.hs2 = Integer.parseInt(parts[1].trim());
                }
                if (subjects.length > 2) {
                    String[] parts = subjects[2].split("-");
                    data.mon3 = parts[0].trim();
                    if (parts.length > 1) data.hs3 = Integer.parseInt(parts[1].trim());
                }
            }
        } else {
            data.maToHop = input.trim();
        }
        return data;
    }

    private String buildTenToHop(String mon1, String mon2, String mon3) {
        List<String> dsMon = new ArrayList<>();
        if (mon1 != null && !mon1.trim().isEmpty()) dsMon.add(TenMonUtil.getTenMon(mon1));
        if (mon2 != null && !mon2.trim().isEmpty()) dsMon.add(TenMonUtil.getTenMon(mon2));
        if (mon3 != null && !mon3.trim().isEmpty()) dsMon.add(TenMonUtil.getTenMon(mon3));
        return String.join(", ", dsMon);
    }

    private NganhToHop buildNganhToHop(Nganh ng, ToHop th, ToHopData parsedData, BigDecimal doLech) {
        NganhToHop nth = new NganhToHop();
        nth.setNganh(ng);
        nth.setToHop(th);
        nth.setHsMon1(parsedData.hs1);
        nth.setHsMon2(parsedData.hs2);
        nth.setHsMon3(parsedData.hs3);
        nth.setDoLech(doLech);
        setMonHocFlags(nth, parsedData);
        nth.setTbKey(ng.getMaNganh() + "_" + th.getMaToHop()); 
        return nth;
    }

    private void setMonHocFlags(NganhToHop nth, ToHopData data) {
        String dsMon = (data.mon1 + " " + data.mon2 + " " + data.mon3).toUpperCase();
        if (dsMon.contains("TO"))    nth.setTo(true);
        if (dsMon.contains("VA"))    nth.setVa(true);
        if (dsMon.contains("LI"))    nth.setLi(true);
        if (dsMon.contains("HO"))    nth.setHo(true);
        if (dsMon.contains("SI"))    nth.setSi(true);
        if (dsMon.contains("SU"))    nth.setSu(true);
        if (dsMon.contains("DI"))    nth.setDi(true);
        if (dsMon.contains("TI"))    nth.setTi(true);
        if (dsMon.contains("KTPL")) nth.setKtpl(true);
        if (dsMon.contains("N1"))    nth.setN1(true);
        if (dsMon.contains("NK") || dsMon.contains("CNCN") || dsMon.contains("CNNN")) {
            nth.setKhac(true);
        }
    }

    private boolean kiemTraNganhCoXetPhuongThuc(Nganh ng, String phuongthuc){
        if (ng == null || phuongthuc == null) return false;
        String pt = phuongthuc.trim().toUpperCase();
        switch (pt) {
            case "THPT": return Boolean.TRUE.equals(ng.getThpt());
            case "DGNL": return Boolean.TRUE.equals(ng.getDgnl());
            case "VSAT": return Boolean.TRUE.equals(ng.getVsat());
            case "XTT":  return Boolean.TRUE.equals(ng.getTuyenThang());
            default:     return false;
        }
    }

    private boolean isNganhAcceptsPhuongThuc(Nganh nganh, String phuongThuc) {
        String pt = phuongThuc.toUpperCase();
        if (pt.contains("THPT")) {
            return nganh.getThpt() != null && nganh.getThpt();
        } else if (pt.contains("DGNL") || pt.contains("ĐGNL")) {
            return nganh.getDgnl() != null && nganh.getDgnl();
        } else if (pt.contains("VSAT") || pt.contains("V-SAT")) {
            return nganh.getVsat() != null && nganh.getVsat();
        } else if (pt.contains("TUYỂN THẲNG") || pt.contains("XTT")) {
            return nganh.getTuyenThang() != null && nganh.getTuyenThang();
        }
        return false;
    }

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
            default:     return false;
        }
    }

    private BigDecimal tinhDiemTong(BigDecimal diemUtxt, BigDecimal diemCc) {
        BigDecimal tong = BigDecimal.ZERO;
        if (diemUtxt != null) tong = tong.add(diemUtxt);
        if (diemCc   != null) tong = tong.add(diemCc);
        BigDecimal max = new BigDecimal("3.00");
        return tong.compareTo(max) > 0 ? max : tong;
    }
    
    // VÁ LỖI: Thêm hàm helper này vào cuối class ExcelImportBUSV2
    private void upsertDiemCong(ThiSinh2025 ts, Nganh nganh, ToHop toHop, String phuongThuc,
                                BigDecimal diemUtxt, Map<String, DiemCong> dcExistingMap,
                                List<DiemCong> insertBatch, List<DiemCong> updateBatch, Set<String> seenDcKey) {
        String maToHop = toHop != null ? toHop.getMaToHop() : "NONE";
        String dcKey = ts.getCccd() + "_" + nganh.getMaNganh() + "_" + maToHop + "_" + phuongThuc;

        if (!seenDcKey.add(dcKey)) return;

        DiemCong existing = dcExistingMap.get(dcKey);
        if (existing != null) {
            existing.setDiemUtxt(diemUtxt);
            existing.setDiemTong(tinhDiemTong(diemUtxt, existing.getDiemCc()));
            updateBatch.add(existing);
        } else {
            DiemCong dc = new DiemCong();
            dc.setThiSinh(ts);
            dc.setNganh(nganh);
            dc.setToHop(toHop);
            dc.setPhuongThuc(phuongThuc);
            dc.setDiemUtxt(diemUtxt);
            dc.setDiemCc(null);
            dc.setDiemTong(tinhDiemTong(diemUtxt, null));
            dc.setDcKey(dcKey);
            
            insertBatch.add(dc);
            dcExistingMap.put(dcKey, dc);
        }
    }
}