/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.NganhDAO;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
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

    private static final String KET_QUA_PASSED    = "PASSED";
    private static final String KET_QUA_CANCELLED = "CANCELLED";
    private static final String KET_QUA_KOXET     = "KOXET";
    private static final String KET_QUA_FAILED    = "FAILED";
    private static final String KET_QUA_HETSLOT   = "HETSLOT";

    private final NganhDAO nganhDAO;
    private final NguyenVongDAO nguyenVongDAO;

    @Inject
    public NganhBUS(NganhDAO nganhDAO, NguyenVongDAO nguyenVongDAO) {
        this.nganhDAO = nganhDAO;
        this.nguyenVongDAO = nguyenVongDAO;
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
        existing.setSlXtt(nganh.getSlXtt());
        existing.setSlDgnl(nganh.getSlDgnl());
        existing.setSlVsat(nganh.getSlVsat());
        existing.setSlThpt(nganh.getSlThpt());

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
        if (nganhDAO.delete(existing)) {
            return BUSResult.success("Xóa ngành thành công!");
        }
        return BUSResult.error("Xóa ngành thất bại!");
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
     * Tính kết quả xét tuyển cho một ngành cụ thể.
     * Batch size update DB dùng DEFAULT_UPDATE_BATCH_SIZE.
     *
     * @param maNganh Mã ngành cần tính
     * @return BUSResult thông báo kết quả
     */
    @Transactional
    public BUSResult tinhKetQuaNganh(String maNganh) {
        // 1. Validate
        if (maNganh == null || maNganh.trim().isEmpty()) {
            return BUSResult.error("Mã ngành không hợp lệ!");
        }

        // 2. Lấy thông tin ngành
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) {
            return BUSResult.error("Không tìm thấy ngành với mã: " + maNganh);
        }

        // 3. Copy slot vào biến local (không đụng DB)
        int slDgnl = nganh.getSlDgnl() != null ? nganh.getSlDgnl() : 0;
        int slVsat = nganh.getSlVsat() != null ? nganh.getSlVsat() : 0;
        int slThpt = nganh.getSlThpt() != null ? nganh.getSlThpt() : 0;

        LOGGER.info(String.format("[KQ] Bắt đầu tính kết quả ngành %s - %s", maNganh, nganh.getTenNganh()));
        LOGGER.info(String.format("[KQ] Slot ban đầu: DGNL=%d, VSAT=%d, THPT=%d", slDgnl, slVsat, slThpt));

        // 4. Lấy danh sách nguyện vọng đã sắp xếp
        List<NguyenVong> dsNguyenVong = nguyenVongDAO.findByMaNganhSorted(maNganh);
        LOGGER.info(String.format("[KQ] Tổng nguyện vọng cần xét: %d", dsNguyenVong.size()));

        // 5. Tracking
        Set<String> passedCccd  = new HashSet<>(); // cccd đã PASSED ở NV ưu tiên cao hơn
        Set<String> passedKeys  = new HashSet<>(); // cccd_thuTu đã PASSED (để check KOXET)

        // Bộ đếm tổng kết
        Map<String, Integer> counter = new HashMap<>();
        counter.put(KET_QUA_PASSED,    0);
        counter.put(KET_QUA_CANCELLED, 0);
        counter.put(KET_QUA_KOXET,     0);
        counter.put(KET_QUA_FAILED,    0);
        counter.put(KET_QUA_HETSLOT,   0);

        BigDecimal diemSan = nganh.getDiemSan();

        // 6. Duyệt từng nguyện vọng
        for (NguyenVong nv : dsNguyenVong) {
            String cccd   = nv.getThiSinh().getCccd();
            String key    = cccd + "_" + nv.getThuTu();
            String ketQua;
            
            if(nv.getDiemXetTuyen() == null){
                // thí sinh có điểm xét tuyển null tự đánh là failed
                ketQua = KET_QUA_FAILED;
            } else if (passedCccd.contains(cccd)) {
                // Thí sinh đã đậu NV ưu tiên cao hơn → huỷ NV này
                ketQua = KET_QUA_CANCELLED;

            } else if (passedKeys.contains(key)) {
                // Cùng thuTu, cùng thí sinh, PT khác đã PASSED → không xét
                ketQua = KET_QUA_KOXET;

            } else if (diemSan != null && nv.getDiemXetTuyen() != null
                    && nv.getDiemXetTuyen().compareTo(diemSan) <= 0) {
                // Dưới điểm sàn
                ketQua = KET_QUA_FAILED;

            } else {
                // Xét slot theo phương thức
                String pt = nv.getPhuongThuc();
                if (pt == null) {
                    LOGGER.warning(String.format(
                        "[KQ][DB_ERR] NguyenVong id=%d có phuongThuc NULL, bỏ qua", nv.getId()));
                    continue;
                }

                int slot;
                switch (pt) {
                    case "DGNL": slot = slDgnl; break;
                    case "VSAT": slot = slVsat; break;
                    case "THPT": slot = slThpt; break;
                    default:
                        LOGGER.warning(String.format(
                            "[KQ][DB_ERR] NguyenVong id=%d có phuongThuc không hợp lệ: '%s', bỏ qua", nv.getId(), pt));
                        continue;
                }

                if (slot > 0) {
                    ketQua = KET_QUA_PASSED;
                    // Giảm slot local
                    switch (pt) {
                        case "DGNL": slDgnl--; break;
                        case "VSAT": slVsat--; break;
                        case "THPT": slThpt--; break;
                    }
                    passedCccd.add(cccd);
                    passedKeys.add(key);
                    LOGGER.fine(String.format(
                        "[KQ] PASSED: cccd=%s, thuTu=%d, PT=%s, diem=%.5f | Slot còn: DGNL=%d VSAT=%d THPT=%d",
                        cccd, nv.getThuTu(), pt,
                        nv.getDiemXetTuyen() != null ? nv.getDiemXetTuyen().doubleValue() : 0.0,
                        slDgnl, slVsat, slThpt));
                } else {
                    ketQua = KET_QUA_HETSLOT;
                }
            }

            nv.setKetQua(ketQua);
            counter.merge(ketQua, 1, Integer::sum);
        }

        // 7. Log tổng kết
        LOGGER.info(String.format(
            "[KQ] Kết quả ngành %s: PASSED=%d | HETSLOT=%d | FAILED=%d | KOXET=%d | CANCELLED=%d",
            maNganh,
            counter.get(KET_QUA_PASSED),
            counter.get(KET_QUA_HETSLOT),
            counter.get(KET_QUA_FAILED),
            counter.get(KET_QUA_KOXET),
            counter.get(KET_QUA_CANCELLED)));
        LOGGER.info(String.format("[KQ] Slot còn lại: DGNL=%d, VSAT=%d, THPT=%d", slDgnl, slVsat, slThpt));

        // 8. Batch update về DB
        nguyenVongDAO.updateBatch(dsNguyenVong, DEFAULT_UPDATE_BATCH_SIZE);
        LOGGER.info(String.format("[KQ] Đã cập nhật %d nguyện vọng vào DB cho ngành %s", dsNguyenVong.size(), maNganh));

        return BUSResult.success(String.format("Tính kết quả ngành %s hoàn tất!", maNganh));
    }

    /**
     * Tính kết quả xét tuyển cho tất cả ngành, chia batch.
     * Batch size dùng DEFAULT_NGANH_BATCH_SIZE và DEFAULT_UPDATE_BATCH_SIZE.
     *
     * @return BUSResult thông báo kết quả
     */
    @Transactional
    public BUSResult tinhKetQuaTatCaNganh() {
        List<Nganh> dsNganh = nganhDAO.findAll();
        int total = dsNganh.size();
        LOGGER.info(String.format("[KQ_ALL] Bắt đầu tính kết quả tất cả ngành. Tổng: %d ngành | Batch ngành: %d | Batch update: %d",
            total, DEFAULT_NGANH_BATCH_SIZE, DEFAULT_UPDATE_BATCH_SIZE));

        int tongBatch = (int) Math.ceil((double) total / DEFAULT_NGANH_BATCH_SIZE);
        int passed = 0, failed = 0;

        for (int b = 0; b < tongBatch; b++) {
            int from = b * DEFAULT_NGANH_BATCH_SIZE;
            int to   = Math.min(from + DEFAULT_NGANH_BATCH_SIZE, total);
            List<Nganh> batch = dsNganh.subList(from, to);

            LOGGER.info(String.format("[KQ_ALL] --- Batch %d/%d (ngành %d-%d) ---", b + 1, tongBatch, from + 1, to));

            for (int i = 0; i < batch.size(); i++) {
                Nganh nganh = batch.get(i);
                int soThuTu = from + i + 1;
                LOGGER.info(String.format("[KQ_ALL] Xử lý ngành %d/%d: %s - %s",
                    soThuTu, total, nganh.getMaNganh(), nganh.getTenNganh()));

                BUSResult result = tinhKetQuaNganh(nganh.getMaNganh());
                if (result.isSuccess()) {
                    passed++;
                    LOGGER.info(String.format("[KQ_ALL] OK: %s - %s", nganh.getMaNganh(), result.getMessage()));
                } else {
                    failed++;
                    LOGGER.warning(String.format("[KQ_ALL] FAIL: %s - %s", nganh.getMaNganh(), result.getMessage()));
                }
            }
        }

        String summary = String.format("Hoàn tất tính kết quả tất cả ngành! Thành công: %d/%d, Thất bại: %d/%d",
            passed, total, failed, total);
        LOGGER.info("[KQ_ALL] " + summary);
        return BUSResult.success(summary);
    }
}
