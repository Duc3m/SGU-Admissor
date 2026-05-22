/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.dao.BangQuyDoiDAO;
import com.sgu.admissor.dao.DiemCongDAO;
import com.sgu.admissor.dao.DiemThiDAO;
import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dao.ThiSinh2025DAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.BangQuyDoi;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.DiemThi;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.entity.ThiSinh2025;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class NguyenVongBUS {

    private static final Logger LOG = Logger.getLogger(NguyenVongBUS.class.getName());

    private static final int    BATCH_SIZE   = 500;
    private static final BigDecimal MAX_DIEM = new BigDecimal("30");
    private static final BigDecimal ZERO     = BigDecimal.ZERO;
    private static final BigDecimal NGUONG   = new BigDecimal("22.5");
    private static final BigDecimal BD_7_5   = new BigDecimal("7.5");
    private static final BigDecimal BD_3     = new BigDecimal("3");
    private static final BigDecimal MAX_DC   = new BigDecimal("3");

    private final NguyenVongDAO    nguyenVongDAO;
    private final DiemThiDAO       diemThiDAO;
    private final DiemCongDAO      diemCongDAO;
    private final NganhToHopDAO    nganhToHopDAO;
    private final BangQuyDoiDAO    bangQuyDoiDAO;
    private final ThiSinh2025DAO   thiSinhDAO;

    @Inject
    public NguyenVongBUS(NguyenVongDAO nguyenVongDAO,
                         DiemThiDAO diemThiDAO,
                         DiemCongDAO diemCongDAO,
                         NganhToHopDAO nganhToHopDAO,
                         BangQuyDoiDAO bangQuyDoiDAO,
                         ThiSinh2025DAO thiSinhDAO) {
        this.nguyenVongDAO  = nguyenVongDAO;
        this.diemThiDAO     = diemThiDAO;
        this.diemCongDAO    = diemCongDAO;
        this.nganhToHopDAO  = nganhToHopDAO;
        this.bangQuyDoiDAO  = bangQuyDoiDAO;
        this.thiSinhDAO     = thiSinhDAO;
    }

    // =========================================================================
    // CÁC HÀM CRUD CŨ
    // =========================================================================

    public BUSResult<List<NguyenVong>> getAllNguyenVong() {
        return BUSResult.successWithData("Lấy toàn bộ nguyện vọng thành công!", nguyenVongDAO.findAll());
    }

    @Transactional
    public BUSResult<NguyenVong> getNguyenVongById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID Nguyên vọng không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findById(id));
    }

    public BUSResult<List<NguyenVong>> getNguyenVongsByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findByCccd(cccd));
    }
    
    @Transactional
    public int countAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua) {
        try {
            return nguyenVongDAO.countAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public BUSResult<List<NguyenVong>> searchAdvanced(String tieuChi, String giaTri, String phuongThuc, String toHop, String ketQua, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<NguyenVong> list = nguyenVongDAO.searchAdvanced(tieuChi, giaTri, phuongThuc, toHop, ketQua, offset, limit);
            return BUSResult.successWithData("Truy xuất nguyện vọng thành công!", list);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi lấy danh sách nguyện vọng!");
        }
    }

    @Transactional
    public BUSResult<List<Object[]>> countPassedByNganhAndPhuongThuc() {
        try {
            List<Object[]> data = nguyenVongDAO.countPassedByNganhAndPhuongThuc();
            return BUSResult.successWithData("Thống kê số lượng trúng tuyển thành công!", data);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi thống kê số lượng trúng tuyển!");
        }
    }

    @Transactional
    public BUSResult<NguyenVong> addNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null
                || nv.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (nv.getNganh() == null || nv.getNganh().getMaNganh() == null || nv.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        if (nv.getPhuongThuc() == null || nv.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        String nvKey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
        nv.setNvKey(nvKey);
        if (nguyenVongDAO.findByNvKey(nvKey) != null) {
            return BUSResult.error("Nguyện vọng này đã tồn tại (trùng CCCD, Ngành và Phương thức)!");
        }
        if (nguyenVongDAO.insert(nv)) {
            return BUSResult.success("Thêm nguyên vọng mới thành công!");
        }
        return BUSResult.error("Thêm nguyên vọng thất bại!");
    }

    @Transactional
    public BUSResult addListNguyenVong(List<NguyenVong> nvList) {
        if (nvList == null || nvList.size() == 0) {
            return BUSResult.error("Không có nguyện vọng nào để add");
        }

        for (NguyenVong nv : nvList) {
            String nvkey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
            nv.setNvKey(nvkey);
        }

        if (!nguyenVongDAO.insertBatch(nvList)) {
            return BUSResult.error("Lỗi trong phương thức addNguyenVong");
        }
        return BUSResult.success("Thêm danh sách nguyện vọng thành công!");
    }

    @Transactional
    public BUSResult<NguyenVong> updateNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getId() == null || nv.getId() <= 0) {
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        NguyenVong existing = nguyenVongDAO.findById(nv.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        if (nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null || nv.getThiSinh().getCccd().trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (nv.getNganh() == null || nv.getNganh().getMaNganh() == null || nv.getNganh().getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        if (nv.getPhuongThuc() == null || nv.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        String nvKey = nv.getThiSinh().getCccd() + "_" + nv.getNganh().getMaNganh() + "_" + nv.getPhuongThuc();
        NguyenVong duplicate = nguyenVongDAO.findByNvKey(nvKey);
        if (duplicate != null && (duplicate.getId() == null || !duplicate.getId().equals(nv.getId()))) {
            return BUSResult.error("Lỗi cập nhật: Nguyện vọng đã tồn tại!");
        }
        existing.setThiSinh(nv.getThiSinh());
        existing.setNganh(nv.getNganh());
        existing.setThuTu(nv.getThuTu());
        existing.setDiemThxt(nv.getDiemThxt());
        existing.setDiemUtqd(nv.getDiemUtqd());
        existing.setDiemCong(nv.getDiemCong());
        existing.setDiemXetTuyen(nv.getDiemXetTuyen());
        existing.setKetQua(nv.getKetQua());
        existing.setPhuongThuc(nv.getPhuongThuc());
        existing.setToHopMon(nv.getToHopMon());
        existing.setNvKey(nvKey);

        if (nguyenVongDAO.update(existing)) {
            return BUSResult.success("Cập nhật nguyện vọng thành công!");
        }
        return BUSResult.error("Cập nhật nguyện vọng thất bại!");
    }

    @Transactional
    public BUSResult deleteNguyenVong(NguyenVong nv) {
        if (nv == null || nv.getId() == null || nv.getId() <= 0) {
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        NguyenVong existing = nguyenVongDAO.findById(nv.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        if (nguyenVongDAO.delete(existing)) {
            return BUSResult.success("Xóa nguyện vọng thành công!");
        }
        return BUSResult.error("Xóa nguyện vọng thất bại!");
    }

    // =========================================================================
    // TÍNH ĐIỂM XÉT TUYỂN
    // =========================================================================

    /**
     * Tính điểm xét tuyển cho 1 thí sinh theo CCCD.
     * Kết quả được lưu thẳng vào bảng NguyenVong.
     */
    @Transactional
    public BUSResult tinhDiemMotThiSinh(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return BUSResult.error("CCCD không hợp lệ!");
        }

        ThiSinh2025 ts = thiSinhDAO.findByCccd(cccd);
        if (ts == null) {
            return BUSResult.error("Không tìm thấy thí sinh CCCD=" + cccd);
        }

        List<NguyenVong> nvList = nguyenVongDAO.findByCccd(cccd);
        if (nvList == null || nvList.isEmpty()) {
            return BUSResult.error("Thí sinh không có nguyện vọng nào!");
        }

        // Prefetch dữ liệu cần thiết
        Map<String, DiemThi>         diemThiMap    = buildDiemThiMapByCccd(cccd);
        Map<String, DiemCong>        dcMap         = buildDiemCongMapByCccd(cccd);
        Map<String, List<NganhToHop>> nthMap       = buildNganhToHopMap();
        Map<String, List<BangQuyDoi>> vsatMap      = buildVsatMap();
        Map<String, List<BangQuyDoi>> dgnlMap      = buildDgnlMap();
        BigDecimal                   mdut          = tinhMDUT(ts);

        tinhVaGanDiemChoNvList(nvList, ts, diemThiMap, dcMap, nthMap, vsatMap, dgnlMap, mdut);

        if (!nguyenVongDAO.updateBatch(nvList)) {
            return BUSResult.error("Lưu điểm xét tuyển thất bại!");
        }
        return BUSResult.success("Tính điểm xét tuyển cho CCCD=" + cccd + " thành công!");
    }

    /**
     * Tính điểm xét tuyển cho TẤT CẢ thí sinh có nguyện vọng.
     * Prefetch toàn bộ một lần, update batch theo BATCH_SIZE.
     */
    @Transactional
    public BUSResult tinhDiemTatCa() {
        long startTotal = System.currentTimeMillis();
        LOG.info("=== BẮT ĐẦU TÍNH ĐIỂM XÉT TUYỂN TẤT CẢ THÍ SINH ===");

        // --- Prefetch toàn bộ một lần ---
        LOG.info("[1/6] Đang tải NganhToHop...");
        Map<String, List<NganhToHop>> nthMap  = buildNganhToHopMap();

        LOG.info("[2/6] Đang tải BangQuyDoi (VSAT + DGNL)...");
        Map<String, List<BangQuyDoi>> vsatMap = buildVsatMap();
        Map<String, List<BangQuyDoi>> dgnlMap = buildDgnlMap();

        LOG.info("[3/6] Đang tải DiemThi...");
        // cccd -> (phuongThuc -> DiemThi)
        Map<String, Map<String, DiemThi>> diemThiByCccd = new HashMap<>();
        for (DiemThi dt : diemThiDAO.findAll()) {
            if (dt.getThiSinh() == null || dt.getThiSinh().getCccd() == null) continue;
            diemThiByCccd
                .computeIfAbsent(dt.getThiSinh().getCccd(), k -> new HashMap<>())
                .put(dt.getPhuongThuc(), dt);
        }

        LOG.info("[4/6] Đang tải DiemCong...");
        // cccd -> (dcKey -> DiemCong)
        Map<String, Map<String, DiemCong>> diemCongByCccd = new HashMap<>();
        for (DiemCong dc : diemCongDAO.findAll()) {
            if (dc.getDcKey() == null) continue;
            // dcKey = cccd_maNganh_maToHop_phuongThuc
            String cccdFromKey = dc.getDcKey().split("_")[0];
            diemCongByCccd
                .computeIfAbsent(cccdFromKey, k -> new HashMap<>())
                .put(dc.getDcKey(), dc);
        }

        LOG.info("[5/6] Đang tải ThiSinh và NguyenVong...");
        Map<String, ThiSinh2025> tsMap = new HashMap<>();
        for (ThiSinh2025 ts : thiSinhDAO.findAll()) {
            if (ts.getCccd() != null) tsMap.put(ts.getCccd(), ts);
        }

        Map<String, List<NguyenVong>> nvByCccd = new HashMap<>();
        for (NguyenVong nv : nguyenVongDAO.findAll()) {
            if (nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null) continue;
            nvByCccd.computeIfAbsent(nv.getThiSinh().getCccd(), k -> new ArrayList<>()).add(nv);
        }

        int totalTs      = nvByCccd.size();
        int processedTs  = 0;
        int totalNv      = 0;
        int batchCount   = 0;

        LOG.info(String.format("[6/6] Bắt đầu tính điểm cho %d thí sinh...", totalTs));

        List<NguyenVong> updateBatch = new ArrayList<>();

        for (Map.Entry<String, List<NguyenVong>> entry : nvByCccd.entrySet()) {
            String      cccd = entry.getKey();
            ThiSinh2025 ts   = tsMap.get(cccd);
            if (ts == null) continue;

            Map<String, DiemThi>  diemThiMap = diemThiByCccd.getOrDefault(cccd, new HashMap<>());
            Map<String, DiemCong> dcMap      = diemCongByCccd.getOrDefault(cccd, new HashMap<>());

            BigDecimal mdut = tinhMDUT(ts);
            tinhVaGanDiemChoNvList(entry.getValue(), ts, diemThiMap, dcMap, nthMap, vsatMap, dgnlMap, mdut);
            updateBatch.addAll(entry.getValue());
            totalNv += entry.getValue().size();
            processedTs++;

            if (updateBatch.size() >= BATCH_SIZE) {
                nguyenVongDAO.updateBatch(updateBatch);
                batchCount++;
                LOG.info(String.format("  Đã lưu batch #%d | Thí sinh: %d/%d (%.1f%%) | Nguyện vọng: %d",
                        batchCount, processedTs, totalTs,
                        (processedTs * 100.0 / totalTs), totalNv));
                updateBatch.clear();
            }
        }

        if (!updateBatch.isEmpty()) {
            nguyenVongDAO.updateBatch(updateBatch);
            batchCount++;
            LOG.info(String.format("  Đã lưu batch #%d (cuối) | Thí sinh: %d/%d | Nguyện vọng: %d",
                    batchCount, processedTs, totalTs, totalNv));
        }

        long elapsed = System.currentTimeMillis() - startTotal;
        LOG.info(String.format(
            "=== HOÀN THÀNH: %d thí sinh, %d nguyện vọng, %d batch, thời gian: %d giây ===",
            processedTs, totalNv, batchCount, elapsed / 1000));

        return BUSResult.success(String.format(
            "Tính điểm xét tuyển hoàn tất: %d thí sinh, %d nguyện vọng (%d giây)",
            processedTs, totalNv, elapsed / 1000));
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /**
     * Core: tính và gán điểm cho danh sách NguyenVong của 1 thí sinh.
     */
    private void tinhVaGanDiemChoNvList(List<NguyenVong> nvList,
                                         ThiSinh2025 ts,
                                         Map<String, DiemThi> diemThiMap,
                                         Map<String, DiemCong> dcMap,
                                         Map<String, List<NganhToHop>> nthMap,
                                         Map<String, List<BangQuyDoi>> vsatMap,
                                         Map<String, List<BangQuyDoi>> dgnlMap,
                                         BigDecimal mdut) {
        String cccd = ts.getCccd();

        for (NguyenVong nv : nvList) {
            if (nv.getNganh() == null) continue;
            String maNganh  = nv.getNganh().getMaNganh();
            String pt       = nv.getPhuongThuc();

            DiemThi dt = diemThiMap.get(pt);
            if (dt == null) continue;

            List<NganhToHop> nthList = nthMap.getOrDefault(maNganh, new ArrayList<>());
            if (nthList.isEmpty()) continue;

            // --- Bước 1: Duyệt tất cả tổ hợp, tìm ĐTHGXT cao nhất ---
            BigDecimal bestDthgxt  = null;
            BigDecimal bestDthxt   = null;   // ĐTHXT tương ứng với best
            String     bestToHop   = null;

            for (NganhToHop nth : nthList) {
                if (nth.getToHop() == null) continue;

                // Bỏ qua KHAC + VSAT (không có bảng quy đổi)
                if (Boolean.TRUE.equals(nth.getKhac()) && PHUONGTHUC.PT[3].equals(pt)) continue;

                BigDecimal dthxt = tinhDTHXT(nth, dt, pt, vsatMap, dgnlMap);
                if (dthxt == null) continue;

                // ĐTHGXT = ĐTHXT - doLech (doLech=null hoặc 0 nếu là tổ hợp gốc)
                BigDecimal doLech  = nth.getDoLech() != null ? nth.getDoLech() : ZERO;
                if (PHUONGTHUC.PT[2].compareTo(pt) == 0){
                    doLech = ZERO;
                }
                
                BigDecimal dthgxt  = dthxt.subtract(doLech);

                if (bestDthgxt == null || dthgxt.compareTo(bestDthgxt) > 0) {
                    bestDthgxt = dthgxt;
                    bestDthxt  = dthxt;
                    bestToHop  = nth.getToHop().getMaToHop();
                }
            }

            if (bestDthgxt == null) continue;

            // --- Bước 2: Điểm cộng ---
            String     dcKey   = cccd + "_" + maNganh + "_" + bestToHop + "_" + pt;
            if (PHUONGTHUC.PT[2].compareTo(pt) == 0){
                dcKey = cccd + "_" + maNganh + "_" + "NONE" + "_" + pt;
            }
            
            DiemCong   dc      = dcMap.get(dcKey);
            BigDecimal diemCong = (dc != null && dc.getDiemTong() != null) ? dc.getDiemTong() : ZERO;
            // Điểm cộng không vượt quá 3
            if (diemCong.compareTo(MAX_DC) > 0) diemCong = MAX_DC;

            // --- Bước 3: Điểm ưu tiên ---
            // Kiểm tra (ĐTHGXT + ĐC) so với 22.5
            BigDecimal baseUuTien = bestDthgxt.add(diemCong);
            BigDecimal dut;
            if (baseUuTien.compareTo(NGUONG) < 0) {
                dut = mdut;
            } else {
                // ĐƯT = [(30 - ĐTHXT - ĐC) / 7.5] × MĐƯT
                BigDecimal tu   = MAX_DIEM.subtract(bestDthxt).subtract(diemCong);
                dut = tu.divide(BD_7_5, 10, RoundingMode.HALF_UP).multiply(mdut);
            }
            // ĐƯT không âm
            if (dut.compareTo(ZERO) < 0) dut = ZERO;

            // --- Bước 4: Điểm xét tuyển ---
            BigDecimal dxt = bestDthgxt.add(diemCong).add(dut);
            // Giới hạn tối đa 30 điểm
            if (dxt.compareTo(MAX_DIEM) > 0) dxt = MAX_DIEM;

            // --- Bước 5: Gán vào NguyenVong ---
            nv.setDiemThxt(bestDthxt.setScale(5, RoundingMode.HALF_UP));
            nv.setDiemCong(diemCong.setScale(2, RoundingMode.HALF_UP));
            nv.setDiemUtqd(dut.setScale(5, RoundingMode.HALF_UP));
            nv.setDiemXetTuyen(dxt.setScale(5, RoundingMode.HALF_UP));
            nv.setToHopMon(bestToHop);
        }
    }

    /**
     * Tính ĐTHXT cho 1 tổ hợp theo công thức:
     * - THPT/VSAT: [(d1×w1 + d2×w2 + d3×w3) / W] × 3  (VSAT: di đã quy đổi về thang 10)
     * - DGNL: tra BangQuyDoi trực tiếp → điểm thang 30
     * Trả null nếu thiếu dữ liệu.
     */
    private BigDecimal tinhDTHXT(NganhToHop nth, DiemThi dt, String pt,
                                  Map<String, List<BangQuyDoi>> vsatMap,
                                  Map<String, List<BangQuyDoi>> dgnlMap) {
        if (PHUONGTHUC.PT[2].equals(pt)) {
            // DGNL: tra bảng quy đổi theo tổ hợp
            String key = "DGNL_" + nth.getToHop().getMaToHop();
            if (dt.getNl1() == null) return null;
            BangQuyDoi bqd = timBangQuyDoi(dgnlMap.get(key), dt.getNl1());
            if (bqd == null) return null;
            return quyDoi(dt.getNl1(), bqd);
        }

        // THPT hoặc VSAT: tính theo hệ số 3 môn
        String flagMon1 = getFlagName(nth, 1);
        String flagMon2 = getFlagName(nth, 2);
        String flagMon3 = getFlagName(nth, 3);

        BigDecimal d1 = getDiemMon(dt, flagMon1, 1, PHUONGTHUC.PT[3].equals(pt), vsatMap);
        BigDecimal d2 = getDiemMon(dt, flagMon2, 2, PHUONGTHUC.PT[3].equals(pt), vsatMap);
        BigDecimal d3 = getDiemMon(dt, flagMon3, 3, PHUONGTHUC.PT[3].equals(pt), vsatMap);

        if (d1 == null || d2 == null || d3 == null) return null;

        int w1 = nth.getHsMon1() != null ? nth.getHsMon1() : 1;
        int w2 = nth.getHsMon2() != null ? nth.getHsMon2() : 1;
        int w3 = nth.getHsMon3() != null ? nth.getHsMon3() : 1;
        int W  = w1 + w2 + w3;
        if (W == 0) return null;

        // [(d1×w1 + d2×w2 + d3×w3) / W] × 3
        BigDecimal tong = d1.multiply(BigDecimal.valueOf(w1))
                           .add(d2.multiply(BigDecimal.valueOf(w2)))
                           .add(d3.multiply(BigDecimal.valueOf(w3)));
        return tong.divide(BigDecimal.valueOf(W), 10, RoundingMode.HALF_UP)
                   .multiply(BD_3);
    }

    /**
     * Lấy tên flag (môn học) theo vị trí (1/2/3) trong NganhToHop.
     * Nếu tổ hợp có KHAC=true, lấy trực tiếp từ toHop.mon1/mon2/mon3
     * để xác định môn nào là NK1/NK2/NK3.
     * Ngược lại, đếm flag true theo thứ tự chuẩn.
     */
    private String getFlagName(NganhToHop nth, int viTri) {
        if (nth == null || nth.getToHop() == null) {
            return null;
        }

        // Trích xuất trực tiếp tên môn (TO, VA, LI, N1, NK1...) lưu trong DB ToHop
        String monName = switch (viTri) {
            case 1 -> nth.getToHop().getMon1();
            case 2 -> nth.getToHop().getMon2();
            case 3 -> nth.getToHop().getMon3();
            default -> null;
        };

        return monName != null ? monName.trim().toUpperCase() : null;
    }

    /**
     * Lấy điểm 1 môn từ DiemThi theo tên flag.
     * isVsat=true → quy đổi qua BangQuyDoi trước khi trả về (thang 10).
     * viTri dùng để xác định NK1/NK2/NK3 cho tổ hợp KHAC.
     */
    private BigDecimal getDiemMon(DiemThi dt, String flagName, int viTri,
                                   boolean isVsat, Map<String, List<BangQuyDoi>> vsatMap) {
        if (flagName == null) return null;

        BigDecimal raw = switch (flagName) {
            case "TO"   -> dt.getTo();
            case "LI"   -> dt.getLi();
            case "HO"   -> dt.getHo();
            case "SI"   -> dt.getSi();
            case "SU"   -> dt.getSu();
            case "DI"   -> dt.getDi();
            case "VA"   -> dt.getVa();
            case "N1"   -> maxOf(dt.getN1Thi(), dt.getN1Cc());
            case "TI"   -> dt.getTi();
            case "KTPL" -> dt.getKtpl();
            // Môn đặc biệt từ toHop.mon1/mon2/mon3 khi KHAC=true
            case "NK1"  -> dt.getNk1();
            case "NK2"  -> dt.getNk2();
            case "NK3"  -> dt.getNk3();
            case "NK4"  -> dt.getNk4();
            case "NK5"  -> dt.getNk5();
            case "NK6"  -> dt.getNk6();
            default -> null;
        };

        if (raw == null) return null;

        if (isVsat) {
            BangQuyDoi bqd = timBangQuyDoi(vsatMap.get("VSAT_" + flagName), raw);
            if (bqd == null) return null;
            return quyDoi(raw, bqd);
        }
        return raw;
    }

    /**
     * Công thức quy đổi: y = c + ((x - a) / (b - a)) × (d - c)
     * x thuộc [a, b] → y thuộc [c, d]
     */
    private BigDecimal quyDoi(BigDecimal x, BangQuyDoi bqd) {
        if (x == null || bqd == null) return null;
        BigDecimal a = bqd.getDiemA();
        BigDecimal b = bqd.getDiemB();
        BigDecimal c = bqd.getDiemC();
        BigDecimal d = bqd.getDiemD();
        if (a == null || b == null || c == null || d == null) return null;

        BigDecimal bMinusA = b.subtract(a);
        if (bMinusA.compareTo(ZERO) == 0) return c;

        // y = c + ((x - a) / (b - a)) × (d - c)
        return c.add(
            x.subtract(a)
             .divide(bMinusA, 10, RoundingMode.HALF_UP)
             .multiply(d.subtract(c))
        );
    }

    /**
     * Tính mức độ ưu tiên (MĐƯT) = điểm khu vực + điểm đối tượng.
     * KV1=0.75, KV2-NT=0.5, KV2=0.25, KV3/khác=0
     * ĐT1-4=2.0, ĐT5-7=1.0, khác=0
     */
    private BigDecimal tinhMDUT(ThiSinh2025 ts) {
        BigDecimal kvDiem = ZERO;
        String kv = ts.getKhuVuc();
        if (kv != null) {
            kv = kv.trim().toUpperCase();
            kvDiem = switch (kv) {
                case "1"    -> new BigDecimal("0.75");
                case "2NT"  -> new BigDecimal("0.5");
                case "2"    -> new BigDecimal("0.25");
                default     -> ZERO;
            };
        }

        BigDecimal dtDiem = ZERO;
        String dt = ts.getDoiTuong();
        if (dt != null && !dt.trim().isEmpty()) {
            // Extract số đầu từ chuỗi (vd: "06a" → 6, "02" → 2)
            String numStr = dt.trim().replaceAll("[^0-9].*", "").replaceAll("[^0-9]", "");
            try {
                int num = Integer.parseInt(numStr);
                if (num >= 1 && num <= 4) {
                    dtDiem = new BigDecimal("2.0");
                } else if (num >= 5 && num <= 7) {
                    dtDiem = new BigDecimal("1.0");
                }
            } catch (NumberFormatException ignored) {}
        }

        return kvDiem.add(dtDiem);
    }

    // =========================================================================
    // BUILD MAPS
    // =========================================================================

    private Map<String, DiemThi> buildDiemThiMapByCccd(String cccd) {
        Map<String, DiemThi> map = new HashMap<>();
        for (DiemThi dt : diemThiDAO.findByCccd(cccd)) {
            if (dt.getPhuongThuc() != null) map.put(dt.getPhuongThuc(), dt);
        }
        return map;
    }

    private Map<String, DiemCong> buildDiemCongMapByCccd(String cccd) {
        Map<String, DiemCong> map = new HashMap<>();
        for (DiemCong dc : diemCongDAO.findByCccd(cccd)) {
            if (dc.getDcKey() != null) map.put(dc.getDcKey(), dc);
        }
        return map;
    }

    private Map<String, List<NganhToHop>> buildNganhToHopMap() {
        Map<String, List<NganhToHop>> map = new HashMap<>();
        for (NganhToHop nth : nganhToHopDAO.findAll()) {
            if (nth.getNganh() == null) continue;
            map.computeIfAbsent(nth.getNganh().getMaNganh(), k -> new ArrayList<>()).add(nth);
        }
        return map;
    }

    /**
     * vsatMap: "VSAT_TO" -> List<BangQuyDoi> (phuongThuc=VSAT, mon=TO)
     * Mỗi key có thể có nhiều bản ghi ứng với các khoảng [diemA, diemB] khác nhau.
     */
    private Map<String, List<BangQuyDoi>> buildVsatMap() {
        Map<String, List<BangQuyDoi>> map = new HashMap<>();
        for (BangQuyDoi bqd : bangQuyDoiDAO.findAll()) {
            if (PHUONGTHUC.PT[3].equals(bqd.getPhuongThuc()) && bqd.getMon() != null) {
                String key = "VSAT_" + bqd.getMon().trim().toUpperCase();
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(bqd);
            }
        }
        return map;
    }

    /**
     * dgnlMap: "DGNL_A00" -> List<BangQuyDoi> (phuongThuc=DGNL, toHop=A00)
     * Mỗi key có thể có nhiều bản ghi ứng với các khoảng [diemA, diemB] khác nhau.
     */
    private Map<String, List<BangQuyDoi>> buildDgnlMap() {
        Map<String, List<BangQuyDoi>> map = new HashMap<>();
        for (BangQuyDoi bqd : bangQuyDoiDAO.findAll()) {
            if (PHUONGTHUC.PT[2].equals(bqd.getPhuongThuc()) && bqd.getToHop() != null
                    && bqd.getToHop().getMaToHop() != null) {
                String key = "DGNL_" + bqd.getToHop().getMaToHop();
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(bqd);
            }
        }
        return map;
    }

    /**
     * Tìm BangQuyDoi trong danh sách có diemA <= x <= diemB.
     * Trả null nếu không tìm thấy khoảng phù hợp.
     */
    private BangQuyDoi timBangQuyDoi(List<BangQuyDoi> list, BigDecimal x) {
        if (list == null || x == null) return null;
        for (BangQuyDoi bqd : list) {
            if (bqd.getDiemA() == null || bqd.getDiemB() == null) continue;
            if (x.compareTo(bqd.getDiemA()) >= 0 && x.compareTo(bqd.getDiemB()) <= 0) {
                return bqd;
            }
        }
        return null;
    }

    // =========================================================================
    // UTILS
    // =========================================================================

    private BigDecimal maxOf(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) >= 0 ? a : b;
    }
}
