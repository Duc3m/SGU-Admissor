/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.NganhDAO;
import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
import com.sgu.admissor.util.PhanBoChiTieuUtil;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class NganhBUS {

    private static final Logger LOGGER = Logger.getLogger(NganhBUS.class.getName());
    private static final int DEFAULT_NGANH_BATCH_SIZE = 10;
    private static final int DEFAULT_UPDATE_BATCH_SIZE = 100;
    private static final int MAX_CONFLICT_ITERATIONS = 50;

    private static final String KET_QUA_PASSED    = "PASSED";
    private static final String KET_QUA_CANCELLED = "CANCELLED";
    private static final String KET_QUA_KOXET     = "KOXET";
    private static final String KET_QUA_FAILED    = "FAILED";
    private static final String KET_QUA_HETSLOT   = "HETSLOT";

    private final NganhDAO nganhDAO;
    private final NguyenVongDAO nguyenVongDAO;
    private final NganhToHopDAO nthDAO;

    @Inject
    public NganhBUS(NganhDAO nganhDAO, NguyenVongDAO nguyenVongDAO, NganhToHopDAO nthDAO) {
        this.nganhDAO = nganhDAO;
        this.nguyenVongDAO = nguyenVongDAO;
        this.nthDAO = nthDAO;
    }

    @Transactional
    public BUSResult<List<Nganh>> getAllNganh() {
        return BUSResult.successWithData("Lấy toàn bộ ngành thành công!", nganhDAO.findAll());
    }

    @Transactional
    public BUSResult<Nganh> getNganhById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findById(id));
    }

    @Transactional
    public BUSResult<Nganh> getNganhByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findByMaNganh(maNganh));
    }

    @Transactional
    public BUSResult<List<Nganh>> getNganhByTenNganh(String tenNganh) {
        if (tenNganh == null || tenNganh.trim().isEmpty()) {
            return BUSResult.error("Tên ngành không hợp lệ!");
        }
        return BUSResult.successWithData("Lấy ngành thành công!", nganhDAO.findByTenNganh(tenNganh));
    }

    @Transactional
    public BUSResult<List<Nganh>> getNganhTuyenThang() {
        return BUSResult.successWithData("Lấy ngành tuyển thẳng thành công!", nganhDAO.findByTuyenThang(true));
    }

    @Transactional
    public BUSResult<Nganh> addNganh(Nganh nganh) {
        if (nganh == null) {
            return BUSResult.error("Thông tin ngành không hợp lệ!");
        }
        if (nganh.getMaNganh() == null || nganh.getMaNganh().trim().isEmpty()) {
            return BUSResult.error("Mã ngành không được để trống!");
        }
        if (nganh.getTenNganh() == null || nganh.getTenNganh().trim().isEmpty()) {
            return BUSResult.error("Tên ngành không được để trống!");
        }
        if (nganhDAO.findByMaNganh(nganh.getMaNganh()) != null) {
            return BUSResult.error("Mã ngành đã tồn tại trong hệ thống!");
        }
        if (nganhDAO.insert(nganh)) {
            return BUSResult.success("Thêm ngành mới thành công!");
        }
        return BUSResult.error("Thêm ngành thất bại!");
    }
    
    @Transactional
    public BUSResult addListNganh(List<Nganh> listNganh) {
        if (listNganh == null || listNganh.size() == 0) {
            return BUSResult.error("Không có ngành nào để add");
        }
        if (!nganhDAO.insertBatch(listNganh)) {
            return BUSResult.error("Lỗi trong phương thức addListNganh");
        }
        return BUSResult.success("Thêm danh sách ngành thành công!");
    }

    @Transactional
    public BUSResult<Nganh> updateNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        Nganh existing = nganhDAO.findById(nganh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }
        existing.setMaNganh(nganh.getMaNganh());
        existing.setTenNganh(nganh.getTenNganh());
        existing.setToHopGoc(nganh.getToHopGoc());
        existing.setChiTieu(nganh.getChiTieu());
        existing.setDiemSan(nganh.getDiemSan());
        existing.setDiemTrungTuyen(nganh.getDiemTrungTuyen());
        existing.setTuyenThang(nganh.getTuyenThang());
        existing.setDgnl(nganh.getDgnl());
        existing.setThpt(nganh.getThpt());
        existing.setVsat(nganh.getVsat());
        int chiTieu = nganh.getChiTieu();
        Map<String, Integer> phanBo = PhanBoChiTieuUtil.tinhPhanBoChiTieu(nganh.getMaNganh(), chiTieu);
        if(phanBo.get("PT1") != 0) {
            existing.setTuyenThang(Boolean.TRUE);
            existing.setSlXtt(phanBo.get("PT1"));
        }
        if(phanBo.get("PT2") != 0) {
            existing.setDgnl(Boolean.TRUE);
            existing.setSlDgnl(phanBo.get("PT2"));
        }
        if(phanBo.get("PT3") != 0) {
            existing.setVsat(Boolean.TRUE);
            existing.setSlVsat(phanBo.get("PT3"));
        }
        if(phanBo.get("PT4") != 0) {
            existing.setThpt(Boolean.TRUE);
            existing.setSlThpt(phanBo.get("PT4"));
        }

        if (nganhDAO.update(existing)) {
            return BUSResult.success("Cập nhật ngành thành công!");
        }
        return BUSResult.error("Cập nhật ngành thất bại!");
    }

    @Transactional
    public BUSResult<Nganh> deleteNganh(Nganh nganh) {
        if (nganh == null || nganh.getId() == null || nganh.getId() <= 0) {
            return BUSResult.error("ID ngành không hợp lệ!");
        }
        Nganh existing = nganhDAO.findById(nganh.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy ngành này trong hệ thống!");
        }
        try {
            nthDAO.deleteByMaNganh(existing.getMaNganh());

            if (nganhDAO.delete(existing)) {
                return BUSResult.success("Xóa ngành và các dữ liệu tổ hợp liên quan thành công!");
            }

            return BUSResult.error("Xóa ngành thất bại!");
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi hệ thống khi xóa ngành: " + e.getMessage());
        }
    }
    
    @Transactional
    public int countAdvanced(String tieuChi, String giaTri, String maToHop) {
        try {
            return nganhDAO.countTotalAdvanced(tieuChi, giaTri, maToHop);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Transactional
    public BUSResult<List<Object[]>> searchAdvanced(String tieuChi, String giaTri, String maToHop, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<Object[]> list = nganhDAO.searchAdvancedWithCount(tieuChi, giaTri, maToHop, offset, limit);
            return BUSResult.successWithData("Truy xuất dữ liệu thành công!", list);
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi khi truy xuất dữ liệu Ngành!");
        }
    }

    // =========================================================
    // TÍNH KẾT QUẢ XÉT TUYỂN
    // =========================================================

    /**
     * [PUBLIC] Tính kết quả xét tuyển cho 1 ngành cụ thể (dùng cho UI).
     * Tự động kéo thêm NV ngành khác của cùng thí sinh để giải quyết conflict cross-ngành.
     */
    @Transactional
    public BUSResult tinhKetQuaMotNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        LOGGER.info(String.format("[KQ_1] === Bắt đầu tính kết quả 1 ngành: %s ===", maNganh));

        Map<String, Integer> slotMap = new HashMap<>();

        // Bước 1: xét slot ngành này
        List<NguyenVong> dsNV = tinhSlotNganh(maNganh, slotMap);
        if (dsNV == null) {
            return BUSResult.error("Không tìm thấy ngành với mã: " + maNganh);
        }

        // Lấy thêm NV ngành khác của cùng thí sinh để conflict resolution đúng
        List<String> dsCccd = dsNV.stream()
                .map(nv -> nv.getThiSinh().getCccd())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        List<NguyenVong> dsNVKhac = nguyenVongDAO.findByCccdList(dsCccd).stream()
                .filter(nv -> !nv.getNganh().getMaNganh().equals(maNganh))
                .collect(java.util.stream.Collectors.toList());

        LOGGER.info(String.format("[KQ_1] Kéo thêm %d NV ngành khác của %d thí sinh để xử lý conflict",
                dsNVKhac.size(), dsCccd.size()));

        List<NguyenVong> tatCaNV = new java.util.ArrayList<>(dsNV);
        tatCaNV.addAll(dsNVKhac);

        // Bước 2: giải quyết conflict cross-ngành
        giaiQuyetConflictPassed(tatCaNV, slotMap);

        // Bước 3: tính lại slot được mở ra, lặp đến ổn định
        tinhLaiSlotSauConflict(tatCaNV, slotMap);

        // Bước 4: update DB nguyện vọng
        nguyenVongDAO.updateBatch(tatCaNV, DEFAULT_UPDATE_BATCH_SIZE);
        LOGGER.info(String.format("[KQ_1] Đã cập nhật %d NV vào DB", tatCaNV.size()));
        logTongKetDanhSach("[KQ_1]", tatCaNV);

        // Bước 5: ghi lại diemTrungTuyen vào ngành
        LOGGER.info(String.format("[KQ_1] --- Bước 5: Cập nhật diemTrungTuyen ngành %s ---", maNganh));
        capNhatDiemTrungTuyen(tatCaNV, java.util.Collections.singletonList(maNganh));

        LOGGER.info(String.format("[KQ_1] === Hoàn tất ngành %s ===", maNganh));

        return BUSResult.success(String.format("Tính kết quả ngành %s hoàn tất!", maNganh));
    }

    /**
     * [PUBLIC] Tính kết quả xét tuyển cho tất cả ngành (dùng cho UI).
     */
    @Transactional
    public BUSResult tinhKetQuaTatCaNganh() {
        List<Nganh> dsNganh = nganhDAO.findAll();
        int total = dsNganh.size();
        int tongBatch = (int) Math.ceil((double) total / DEFAULT_NGANH_BATCH_SIZE);
        LOGGER.info(String.format(
                "[KQ_ALL] === Bắt đầu tính kết quả tất cả ngành. Tổng: %d ngành | Batch ngành: %d | Batch update: %d | Max conflict iter: %d ===",
                total, DEFAULT_NGANH_BATCH_SIZE, DEFAULT_UPDATE_BATCH_SIZE, MAX_CONFLICT_ITERATIONS));

        Map<String, Integer> slotMap = new HashMap<>();
        List<NguyenVong> tatCaNV = new java.util.ArrayList<>();

        // Bước 1: xét slot từng ngành, gom vào tatCaNV
        for (int b = 0; b < tongBatch; b++) {
            int from = b * DEFAULT_NGANH_BATCH_SIZE;
            int to   = Math.min(from + DEFAULT_NGANH_BATCH_SIZE, total);
            LOGGER.info(String.format("[KQ_ALL] --- Batch %d/%d (ngành %d-%d) ---", b + 1, tongBatch, from + 1, to));

            for (int i = from; i < to; i++) {
                Nganh nganh = dsNganh.get(i);
                LOGGER.info(String.format("[KQ_ALL] [Bước 1] Xét slot ngành %d/%d: %s - %s",
                        i + 1, total, nganh.getMaNganh(), nganh.getTenNganh()));
                List<NguyenVong> dsNV = tinhSlotNganh(nganh.getMaNganh(), slotMap);
                if (dsNV != null) {
                    tatCaNV.addAll(dsNV);
                } else {
                    LOGGER.warning(String.format("[KQ_ALL] Bỏ qua ngành %s do không tìm thấy trong DB", nganh.getMaNganh()));
                }
            }
        }
        LOGGER.info(String.format("[KQ_ALL] Bước 1 hoàn tất. Tổng NV đã xét sơ bộ: %d", tatCaNV.size()));
        logTongKetDanhSach("[KQ_ALL][Sau bước 1]", tatCaNV);
        logSlotMap("[KQ_ALL][Slot sau bước 1]", slotMap);

        // Bước 2: giải quyết conflict cross-ngành
        LOGGER.info("[KQ_ALL] --- Bước 2: Giải quyết conflict PASSED cross-ngành ---");
        giaiQuyetConflictPassed(tatCaNV, slotMap);
        LOGGER.info("[KQ_ALL] Bước 2 hoàn tất.");
        logTongKetDanhSach("[KQ_ALL][Sau bước 2]", tatCaNV);
        logSlotMap("[KQ_ALL][Slot sau bước 2]", slotMap);

        // Bước 3: tính lại slot được mở ra, lặp đến ổn định
        LOGGER.info("[KQ_ALL] --- Bước 3: Tính lại slot được mở ra ---");
        tinhLaiSlotSauConflict(tatCaNV, slotMap);
        LOGGER.info("[KQ_ALL] Bước 3 hoàn tất.");
        logTongKetDanhSach("[KQ_ALL][Sau bước 3]", tatCaNV);
        logSlotMap("[KQ_ALL][Slot final]", slotMap);

        // Bước 4: update DB nguyện vọng
        LOGGER.info(String.format("[KQ_ALL] --- Bước 4: Update %d NV vào DB ---", tatCaNV.size()));
        nguyenVongDAO.updateBatch(tatCaNV, DEFAULT_UPDATE_BATCH_SIZE);
        LOGGER.info("[KQ_ALL] Bước 4 hoàn tất.");

        // Bước 5: ghi lại diemTrungTuyen vào từng ngành
        LOGGER.info("[KQ_ALL] --- Bước 5: Cập nhật diemTrungTuyen tất cả ngành ---");
        List<String> dsMaNganh = dsNganh.stream()
                .map(Nganh::getMaNganh)
                .collect(java.util.stream.Collectors.toList());
        capNhatDiemTrungTuyen(tatCaNV, dsMaNganh);
        LOGGER.info("[KQ_ALL] Bước 5 hoàn tất.");

        String summary = String.format("Hoàn tất tính kết quả tất cả %d ngành!", total);
        LOGGER.info("[KQ_ALL] === " + summary + " ===");
        return BUSResult.success(summary);
    }
    
    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * [PRIVATE] Bước 5: Tính và ghi lại diemTrungTuyen vào từng ngành.
     * diemTrungTuyen = điểm thấp nhất trong các NV được đánh PASSED của ngành đó.
     * Nếu không có NV nào PASSED thì giữ nguyên giá trị cũ.
     */
    private void capNhatDiemTrungTuyen(List<NguyenVong> tatCaNV, List<String> dsMaNganh) {
        // Group NV PASSED theo maNganh
        Map<String, java.util.Optional<BigDecimal>> diemMinByNganh = tatCaNV.stream()
                .filter(nv -> KET_QUA_PASSED.equals(nv.getKetQua())
                        && nv.getDiemXetTuyen() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        nv -> nv.getNganh().getMaNganh(),
                        java.util.stream.Collectors.mapping(
                                NguyenVong::getDiemXetTuyen,
                                java.util.stream.Collectors.minBy(BigDecimal::compareTo))));

        int total = dsMaNganh.size();
        int idx = 0;
        for (String maNganh : dsMaNganh) {
            idx++;
            double pct = (double) idx / total * 100.0;

            java.util.Optional<BigDecimal> diemMinOpt = diemMinByNganh.get(maNganh);
            if (diemMinOpt == null || !diemMinOpt.isPresent()) {
                LOGGER.info(String.format("[DTT] [%d/%d | %.1f%%] Ngành %s: không có NV PASSED, bỏ qua cập nhật diemTrungTuyen",
                        idx, total, pct, maNganh));
                continue;
            }

            BigDecimal diemTrungTuyen = diemMinOpt.get();
            Nganh nganh = nganhDAO.findByMaNganh(maNganh);
            if (nganh == null) {
                LOGGER.warning(String.format("[DTT] [%d/%d | %.1f%%] Không tìm thấy ngành %s, bỏ qua",
                        idx, total, pct, maNganh));
                continue;
            }

            BigDecimal diemCu = nganh.getDiemTrungTuyen();
            nganh.setDiemTrungTuyen(diemTrungTuyen);
            nganhDAO.update(nganh);

            LOGGER.info(String.format("[DTT] [%d/%d | %.1f%%] Ngành %s: diemTrungTuyen %s → %s",
                    idx, total, pct, maNganh,
                    diemCu != null ? diemCu.toPlainString() : "NULL",
                    diemTrungTuyen.toPlainString()));
        }

        LOGGER.info(String.format("[DTT] Hoàn tất cập nhật diemTrungTuyen cho %d ngành", total));
    }

    /**
     * [PRIVATE] Bước 1: Xét slot + đánh kết quả sơ bộ trong phạm vi 1 ngành.
     * Đánh: CANCELLED (trong ngành), KOXET, FAILED, PASSED, HETSLOT.
     * Cập nhật slotMap theo từng PASSED.
     * KHÔNG update DB.
     *
     * @return List NV đã set ketQua in-memory, hoặc null nếu không tìm thấy ngành
     */
    private List<NguyenVong> tinhSlotNganh(String maNganh, Map<String, Integer> slotMap) {
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) {
            LOGGER.warning(String.format("[SLOT] Không tìm thấy ngành %s, bỏ qua", maNganh));
            return null;
        }

        // Khởi tạo slot local và ghi vào slotMap
        int slDgnl = nganh.getSlDgnl() != null ? nganh.getSlDgnl() : 0;
        int slVsat = nganh.getSlVsat() != null ? nganh.getSlVsat() : 0;
        int slThpt = nganh.getSlThpt() != null ? nganh.getSlThpt() : 0;
        slotMap.put(maNganh + "_DGNL", slDgnl);
        slotMap.put(maNganh + "_VSAT", slVsat);
        slotMap.put(maNganh + "_THPT", slThpt);

        LOGGER.info(String.format("[SLOT] Ngành %s - %s | Slot ban đầu: DGNL=%d, VSAT=%d, THPT=%d",
                maNganh, nganh.getTenNganh(), slDgnl, slVsat, slThpt));

        List<NguyenVong> dsNV = nguyenVongDAO.findByMaNganhSorted(maNganh);
        LOGGER.info(String.format("[SLOT] Ngành %s: %d NV cần xét", maNganh, dsNV.size()));

        BigDecimal diemSan = nganh.getDiemSan();
        Set<String> passedCccdTrongNganh = new HashSet<>(); // CANCELLED trong ngành này
        Set<String> passedKeyTrongNganh  = new HashSet<>(); // KOXET trong ngành này

        for (NguyenVong nv : dsNV) {
            String cccd = nv.getThiSinh().getCccd();
            String key  = cccd + "_" + nv.getThuTu();
            String ketQua;

            if (nv.getDiemXetTuyen() == null) {
                LOGGER.warning(String.format("[SLOT][DB_ERR] NV id=%d cccd=%s diemXetTuyen NULL → FAILED", nv.getId(), cccd));
                ketQua = KET_QUA_FAILED;

            } else if (passedCccdTrongNganh.contains(cccd)) {
                ketQua = KET_QUA_CANCELLED;

            } else if (passedKeyTrongNganh.contains(key)) {
                ketQua = KET_QUA_KOXET;

            } else if (diemSan != null && nv.getDiemXetTuyen().compareTo(diemSan) < 0) {
                ketQua = KET_QUA_FAILED;

            } else {
                String pt = nv.getPhuongThuc();
                if (pt == null) {
                    LOGGER.warning(String.format("[SLOT][DB_ERR] NV id=%d cccd=%s phuongThuc NULL, bỏ qua", nv.getId(), cccd));
                    continue;
                }
                String slotKey = maNganh + "_" + pt;
                if (!slotMap.containsKey(slotKey)) {
                    LOGGER.warning(String.format("[SLOT][DB_ERR] NV id=%d cccd=%s phuongThuc='%s' không hợp lệ, bỏ qua", nv.getId(), cccd, pt));
                    continue;
                }

                int slot = slotMap.get(slotKey);
                if (slot > 0) {
                    ketQua = KET_QUA_PASSED;
                    slotMap.put(slotKey, slot - 1);
                    passedCccdTrongNganh.add(cccd);
                    passedKeyTrongNganh.add(key);
                    LOGGER.fine(String.format(
                            "[SLOT] PASSED: ngành=%s cccd=%s thuTu=%d PT=%s diem=%.5f | slot[%s] còn %d",
                            maNganh, cccd, nv.getThuTu(), pt, nv.getDiemXetTuyen().doubleValue(),
                            slotKey, slotMap.get(slotKey)));
                } else {
                    ketQua = KET_QUA_HETSLOT;
                }
            }

            nv.setKetQua(ketQua);
        }

        logTongKetDanhSach(String.format("[SLOT][%s]", maNganh), dsNV);
        LOGGER.info(String.format("[SLOT] Ngành %s | Slot còn lại: DGNL=%d, VSAT=%d, THPT=%d",
                maNganh,
                slotMap.getOrDefault(maNganh + "_DGNL", 0),
                slotMap.getOrDefault(maNganh + "_VSAT", 0),
                slotMap.getOrDefault(maNganh + "_THPT", 0)));

        return dsNV;
    }

    /**
     * [PRIVATE] Bước 2: Giải quyết conflict thí sinh có nhiều PASSED cross-ngành.
     * Giữ PASSED thuTu nhỏ nhất, các PASSED còn lại → CANCELLED, mở slot lại vào slotMap.
     * Tất cả NV khác của thí sinh đã PASSED (không phải cái được giữ) cũng → CANCELLED.
     * KHÔNG update DB.
     */
    private void giaiQuyetConflictPassed(List<NguyenVong> tatCaNV, Map<String, Integer> slotMap) {
        // Group các NV PASSED theo cccd
        Map<String, List<NguyenVong>> passedByCccd = tatCaNV.stream()
                .filter(nv -> KET_QUA_PASSED.equals(nv.getKetQua()))
                .collect(java.util.stream.Collectors.groupingBy(nv -> nv.getThiSinh().getCccd()));

        int conflictCount = 0;
        int tongThiSinh = passedByCccd.size();
        int tsIdx = 0;

        LOGGER.info(String.format("[CONFLICT] Bắt đầu xử lý conflict: %d thí sinh có NV PASSED", tongThiSinh));

        for (Map.Entry<String, List<NguyenVong>> entry : passedByCccd.entrySet()) {
            tsIdx++;
            String cccd = entry.getKey();
            List<NguyenVong> dsPassedCuaTs = entry.getValue();
            double pct = (double) tsIdx / tongThiSinh * 100.0;

            LOGGER.info(String.format("[CONFLICT] [%d/%d | %.1f%%] Đang xử lý thí sinh cccd=%s | Số NV PASSED: %d",
                    tsIdx, tongThiSinh, pct, cccd, dsPassedCuaTs.size()));

            if (dsPassedCuaTs.size() > 1) {
                // Sắp xếp: thuTu ASC → giữ cái đầu tiên
                dsPassedCuaTs.sort(java.util.Comparator.comparingInt(NguyenVong::getThuTu));
                NguyenVong giuLai = dsPassedCuaTs.get(0);

                LOGGER.info(String.format(
                        "[CONFLICT] [%d/%d | %.1f%%] cccd=%s → CONFLICT! Giữ thuTu=%d ngành=%s PT=%s | Huỷ %d NV: %s",
                        tsIdx, tongThiSinh, pct, cccd,
                        giuLai.getThuTu(), giuLai.getNganh().getMaNganh(), giuLai.getPhuongThuc(),
                        dsPassedCuaTs.size() - 1,
                        dsPassedCuaTs.subList(1, dsPassedCuaTs.size()).stream()
                                .map(nv -> String.format("thuTu=%d ngành=%s PT=%s",
                                        nv.getThuTu(), nv.getNganh().getMaNganh(), nv.getPhuongThuc()))
                                .collect(java.util.stream.Collectors.joining(", "))));

                for (int i = 1; i < dsPassedCuaTs.size(); i++) {
                    NguyenVong huy = dsPassedCuaTs.get(i);
                    String slotKey = huy.getNganh().getMaNganh() + "_" + huy.getPhuongThuc();
                    huy.setKetQua(KET_QUA_CANCELLED);
                    slotMap.merge(slotKey, 1, Integer::sum);
                    LOGGER.info(String.format(
                            "[CONFLICT] [%d/%d | %.1f%%] Đổi PASSED→CANCELLED: cccd=%s thuTu=%d ngành=%s PT=%s | slot[%s] mở lại → %d",
                            tsIdx, tongThiSinh, pct,
                            cccd, huy.getThuTu(), huy.getNganh().getMaNganh(), huy.getPhuongThuc(),
                            slotKey, slotMap.get(slotKey)));
                    conflictCount++;
                }
            } else {
                LOGGER.fine(String.format("[CONFLICT] [%d/%d | %.1f%%] cccd=%s → OK, chỉ có 1 PASSED",
                        tsIdx, tongThiSinh, pct, cccd));
            }

            // Đánh CANCELLED cho tất cả NV không phải PASSED được giữ của thí sinh này
            NguyenVong nvPassedGiuLai = dsPassedCuaTs.get(0);
            for (NguyenVong nv : tatCaNV) {
                if (!nv.getThiSinh().getCccd().equals(cccd)) continue;
                if (nv == nvPassedGiuLai) continue;
                if (KET_QUA_FAILED.equals(nv.getKetQua()) || KET_QUA_KOXET.equals(nv.getKetQua())
                        || KET_QUA_CANCELLED.equals(nv.getKetQua())) continue;
                if (KET_QUA_PASSED.equals(nv.getKetQua())) {
                    // Đã xử lý ở vòng lặp dsPassedCuaTs ở trên
                    continue;
                }
                LOGGER.fine(String.format(
                        "[CONFLICT] [%d/%d | %.1f%%] Đổi %s→CANCELLED (thí sinh đã có NV PASSED): cccd=%s thuTu=%d ngành=%s",
                        tsIdx, tongThiSinh, pct,
                        nv.getKetQua(), cccd, nv.getThuTu(), nv.getNganh().getMaNganh()));
                nv.setKetQua(KET_QUA_CANCELLED);
            }
        }

        LOGGER.info(String.format("[CONFLICT] Hoàn tất: xử lý %d thí sinh, đổi %d NV PASSED→CANCELLED",
                tongThiSinh, conflictCount));
    }
    
    /**
     * [PRIVATE] Bước 3: Tính lại slot được mở ra sau conflict.
     * Duyệt các NV HETSLOT → đổi PASSED nếu slot còn.
     * Lặp đến khi ổn định hoặc đạt MAX_CONFLICT_ITERATIONS.
     * KHÔNG update DB.
     */
    private void tinhLaiSlotSauConflict(List<NguyenVong> tatCaNV, Map<String, Integer> slotMap) {
        for (int iter = 1; iter <= MAX_CONFLICT_ITERATIONS; iter++) {
            // Group NV HETSLOT theo ngành, sort diemXetTuyen DESC, thuTu ASC
            Map<String, List<NguyenVong>> hetSlotByNganh = tatCaNV.stream()
                    .filter(nv -> KET_QUA_HETSLOT.equals(nv.getKetQua()))
                    .collect(java.util.stream.Collectors.groupingBy(nv -> nv.getNganh().getMaNganh()));

            int tongHetSlot = hetSlotByNganh.values().stream().mapToInt(List::size).sum();
            LOGGER.info(String.format("[RECALC] --- Vòng %d/%d | Còn %d NV HETSLOT cần xét ---",
                    iter, MAX_CONFLICT_ITERATIONS, tongHetSlot));

            if (tongHetSlot == 0) {
                LOGGER.info(String.format("[RECALC] Không còn NV HETSLOT nào, dừng sớm sau vòng %d.", iter));
                break;
            }

            int changed = 0;
            int nvIdx = 0;

            for (Map.Entry<String, List<NguyenVong>> entry : hetSlotByNganh.entrySet()) {
                String maNganh = entry.getKey();
                List<NguyenVong> dsHetSlot = entry.getValue();
                dsHetSlot.sort(java.util.Comparator
                        .comparing(NguyenVong::getDiemXetTuyen,
                                java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))
                        .thenComparingInt(NguyenVong::getThuTu));

                LOGGER.info(String.format("[RECALC] Vòng %d | Ngành %s: %d NV HETSLOT cần xét",
                        iter, maNganh, dsHetSlot.size()));

                for (NguyenVong nv : dsHetSlot) {
                    nvIdx++;
                    String cccd = nv.getThiSinh().getCccd();
                    double pct = (double) nvIdx / tongHetSlot * 100.0;

                    LOGGER.fine(String.format("[RECALC] Vòng %d | [%d/%d | %.1f%%] Xét HETSLOT: cccd=%s thuTu=%d ngành=%s PT=%s diem=%s",
                            iter, nvIdx, tongHetSlot, pct,
                            cccd, nv.getThuTu(), maNganh, nv.getPhuongThuc(),
                            nv.getDiemXetTuyen() != null ? nv.getDiemXetTuyen().toPlainString() : "NULL"));

                    // Kiểm tra thí sinh đã có NV PASSED chưa
                    boolean daCoPassedKhac = tatCaNV.stream().anyMatch(other ->
                            other.getThiSinh().getCccd().equals(cccd)
                            && KET_QUA_PASSED.equals(other.getKetQua())
                            && other != nv);
                    if (daCoPassedKhac) {
                        LOGGER.info(String.format(
                                "[RECALC] Vòng %d | [%d/%d | %.1f%%] cccd=%s thuTu=%d ngành=%s → thí sinh đã có PASSED khác, đổi HETSLOT→CANCELLED",
                                iter, nvIdx, tongHetSlot, pct, cccd, nv.getThuTu(), maNganh));
                        nv.setKetQua(KET_QUA_CANCELLED);
                        changed++;
                        continue;
                    }

                    String pt = nv.getPhuongThuc();
                    if (pt == null) continue;
                    String slotKey = maNganh + "_" + pt;
                    int slot = slotMap.getOrDefault(slotKey, 0);

                    if (slot > 0) {
                        BigDecimal diemSan = nv.getNganh().getDiemSan();
                        if (diemSan != null && nv.getDiemXetTuyen() != null
                                && nv.getDiemXetTuyen().compareTo(diemSan) < 0) {
                            LOGGER.fine(String.format(
                                    "[RECALC] Vòng %d | [%d/%d | %.1f%%] cccd=%s dưới điểm sàn (%.2f < %.2f), bỏ qua",
                                    iter, nvIdx, tongHetSlot, pct,
                                    cccd, nv.getDiemXetTuyen().doubleValue(), diemSan.doubleValue()));
                            continue;
                        }
                        nv.setKetQua(KET_QUA_PASSED);
                        slotMap.put(slotKey, slot - 1);
                        changed++;
                        LOGGER.info(String.format(
                                "[RECALC] Vòng %d | [%d/%d | %.1f%%] HETSLOT→PASSED: cccd=%s thuTu=%d ngành=%s PT=%s diem=%.5f | slot[%s] còn %d",
                                iter, nvIdx, tongHetSlot, pct,
                                cccd, nv.getThuTu(), maNganh, pt,
                                nv.getDiemXetTuyen().doubleValue(), slotKey, slotMap.get(slotKey)));

                        // Thí sinh vừa PASSED → CANCELLED tất cả HETSLOT còn lại của họ
                        for (NguyenVong other : tatCaNV) {
                            if (!other.getThiSinh().getCccd().equals(cccd)) continue;
                            if (other == nv) continue;
                            if (KET_QUA_HETSLOT.equals(other.getKetQua())) {
                                LOGGER.fine(String.format(
                                        "[RECALC] Vòng %d | Đổi HETSLOT→CANCELLED (thí sinh vừa PASSED): cccd=%s thuTu=%d ngành=%s",
                                        iter, cccd, other.getThuTu(), other.getNganh().getMaNganh()));
                                other.setKetQua(KET_QUA_CANCELLED);
                                changed++;
                            }
                        }
                    } else {
                        LOGGER.fine(String.format(
                                "[RECALC] Vòng %d | [%d/%d | %.1f%%] cccd=%s ngành=%s PT=%s slot[%s]=0, giữ HETSLOT",
                                iter, nvIdx, tongHetSlot, pct, cccd, maNganh, pt, slotKey));
                    }
                }
            }

            LOGGER.info(String.format("[RECALC] Vòng %d hoàn tất: thay đổi %d NV", iter, changed));
            logSlotMap(String.format("[RECALC][Slot sau vòng %d]", iter), slotMap);

            if (changed == 0) {
                LOGGER.info(String.format("[RECALC] Ổn định sau %d vòng lặp, dừng.", iter));
                break;
            }
            if (iter == MAX_CONFLICT_ITERATIONS) {
                LOGGER.warning(String.format("[RECALC] Đạt giới hạn %d vòng lặp, dừng cưỡng bức!", MAX_CONFLICT_ITERATIONS));
            }
        }
    }
    
    // =========================================================
    // LOG HELPERS
    // =========================================================

    private void logTongKetDanhSach(String prefix, List<NguyenVong> dsNV) {
        Map<String, Long> counts = dsNV.stream()
                .filter(nv -> nv.getKetQua() != null)
                .collect(java.util.stream.Collectors.groupingBy(NguyenVong::getKetQua, java.util.stream.Collectors.counting()));
        LOGGER.info(String.format(
                "%s Tổng kết: PASSED=%d | HETSLOT=%d | FAILED=%d | KOXET=%d | CANCELLED=%d | Chưa xét=%d",
                prefix,
                counts.getOrDefault(KET_QUA_PASSED,    0L),
                counts.getOrDefault(KET_QUA_HETSLOT,   0L),
                counts.getOrDefault(KET_QUA_FAILED,    0L),
                counts.getOrDefault(KET_QUA_KOXET,     0L),
                counts.getOrDefault(KET_QUA_CANCELLED, 0L),
                dsNV.stream().filter(nv -> nv.getKetQua() == null).count()));
    }

    private void logSlotMap(String prefix, Map<String, Integer> slotMap) {
        slotMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> LOGGER.info(String.format("%s slot[%s] = %d", prefix, e.getKey(), e.getValue())));
    }
}
