package com.sgu.admissor.bus;

import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.NganhDAO;
import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * 
 * @author Duc3m
 */
public class NganhBUSV2 extends NganhBUS {

    private static final Logger LOGGER = Logger.getLogger(NganhBUSV2.class.getName());
    private static final int DEFAULT_NGANH_BATCH_SIZE = 10;
    private static final int DEFAULT_UPDATE_BATCH_SIZE = 2000;
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
    public NganhBUSV2(NganhDAO nganhDAO, NguyenVongDAO nguyenVongDAO, NganhToHopDAO nthDAO) {
        super(nganhDAO, nguyenVongDAO, nthDAO);
        this.nganhDAO = nganhDAO;
        this.nguyenVongDAO = nguyenVongDAO;
        this.nthDAO = nthDAO;
    }

    @Transactional
    public BUSResult tinhKetQuaTatCaNganhV2(java.util.function.Consumer<Integer> progressCallback) {
        if (progressCallback == null) progressCallback = p -> {};
        progressCallback.accept(0);

        List<Nganh> dsNganh = nganhDAO.findAll();
        Map<String, Nganh> nganhMap = dsNganh.stream()
                .collect(java.util.stream.Collectors.toMap(Nganh::getMaNganh, n -> n));

        int total = dsNganh.size();
        int tongBatch = (int) Math.ceil((double) total / DEFAULT_NGANH_BATCH_SIZE);

        Map<String, Integer> slotMap = new HashMap<>();
        List<NguyenVong> tatCaNV = new java.util.ArrayList<>();

        for (int b = 0; b < tongBatch; b++) {
            int from = b * DEFAULT_NGANH_BATCH_SIZE;
            int to = Math.min(from + DEFAULT_NGANH_BATCH_SIZE, total);
            for (int i = from; i < to; i++) {
                Nganh nganh = dsNganh.get(i);
                List<NguyenVong> dsNV = tinhSlotNganh(nganh.getMaNganh(), slotMap);
                if (dsNV != null) tatCaNV.addAll(dsNV);
            }
            int pct = ((b + 1) * 30) / tongBatch;
            progressCallback.accept(pct);
        }

        Map<String, List<NguyenVong>> nvByCccd = tatCaNV.stream()
                .collect(java.util.stream.Collectors.groupingBy(nv -> nv.getThiSinh().getCccd()));

        LOGGER.info("[KQ_ALL_V2] --- Bước 2: Giải quyết conflict PASSED cross-ngành ---");
        giaiQuyetConflictPassedV2(tatCaNV, slotMap, nvByCccd, progressCallback);

        LOGGER.info("[KQ_ALL_V2] --- Bước 3: Tính lại slot mở ra sau conflict ---");
        tinhLaiSlotSauConflictV2(tatCaNV, slotMap, nvByCccd, progressCallback);

        LOGGER.info(String.format("[KQ_ALL_V2] --- Bước 4: Cập nhật đồng loạt %d NV vào DB ---", tatCaNV.size()));
        nguyenVongDAO.updateBatch(tatCaNV, DEFAULT_UPDATE_BATCH_SIZE);
        progressCallback.accept(90);

        LOGGER.info("[KQ_ALL_V2] --- Bước 5: Cập nhật điểm chuẩn tuyển sinh ---");
        Map<String, java.util.Optional<BigDecimal>> diemMinByNganh = tatCaNV.stream()
                .filter(nv -> KET_QUA_PASSED.equals(nv.getKetQua()) && nv.getDiemXetTuyen() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        nv -> nv.getNganh().getMaNganh(),
                        java.util.stream.Collectors.mapping(NguyenVong::getDiemXetTuyen, java.util.stream.Collectors.minBy(BigDecimal::compareTo))
                ));

        for (Map.Entry<String, java.util.Optional<BigDecimal>> entry : diemMinByNganh.entrySet()) {
            if (entry.getValue().isPresent()) {
                Nganh nganh = nganhMap.get(entry.getKey());
                if (nganh != null) {
                    nganh.setDiemTrungTuyen(entry.getValue().get());
                    nganhDAO.update(nganh); 
                }
            }
        }

        progressCallback.accept(100);
        return BUSResult.success(String.format("Hoàn tất tính kết quả cho toàn bộ %d ngành!", total));
    }

    private void giaiQuyetConflictPassedV2(List<NguyenVong> tatCaNV, Map<String, Integer> slotMap, 
                                           Map<String, List<NguyenVong>> nvByCccd, java.util.function.Consumer<Integer> progressCallback) {
        Map<String, List<NguyenVong>> passedByCccd = tatCaNV.stream()
                .filter(nv -> KET_QUA_PASSED.equals(nv.getKetQua()))
                .collect(java.util.stream.Collectors.groupingBy(nv -> nv.getThiSinh().getCccd()));

        int tongThiSinh = passedByCccd.size();
        int tsIdx = 0;

        for (Map.Entry<String, List<NguyenVong>> entry : passedByCccd.entrySet()) {
            tsIdx++;
            String cccd = entry.getKey();
            List<NguyenVong> dsPassedCuaTs = entry.getValue();

            if (dsPassedCuaTs.size() > 1) {
                dsPassedCuaTs.sort(java.util.Comparator.comparingInt(NguyenVong::getThuTu));
//                NguyenVong giuLai = dsPassedCuaTs.get(0);
                for (int i = 1; i < dsPassedCuaTs.size(); i++) {
                    NguyenVong huy = dsPassedCuaTs.get(i);
                    String slotKey = huy.getNganh().getMaNganh() + "_" + huy.getPhuongThuc();
                    huy.setKetQua(KET_QUA_CANCELLED);
                    slotMap.merge(slotKey, 1, Integer::sum);
                }
            }

            NguyenVong nvPassedGiuLai = dsPassedCuaTs.get(0);
            List<NguyenVong> dsNvCuaTs = nvByCccd.get(cccd);
            if (dsNvCuaTs != null) {
                for (NguyenVong nv : dsNvCuaTs) {
                    if (nv == nvPassedGiuLai) continue;
                    String kq = nv.getKetQua();
                    if (KET_QUA_FAILED.equals(kq) || KET_QUA_KOXET.equals(kq) || KET_QUA_CANCELLED.equals(kq)) continue;
                    nv.setKetQua(KET_QUA_CANCELLED);
                }
            }

            if (tongThiSinh > 0) {
                int pct = 30 + (tsIdx * 20) / tongThiSinh;
                progressCallback.accept(pct);
            }
        }
    }

    private void tinhLaiSlotSauConflictV2(List<NguyenVong> tatCaNV, Map<String, Integer> slotMap, 
                                          Map<String, List<NguyenVong>> nvByCccd, java.util.function.Consumer<Integer> progressCallback) {
        for (int iter = 1; iter <= MAX_CONFLICT_ITERATIONS; iter++) {
            Map<String, List<NguyenVong>> hetSlotByNganh = tatCaNV.stream()
                    .filter(nv -> KET_QUA_HETSLOT.equals(nv.getKetQua()))
                    .collect(java.util.stream.Collectors.groupingBy(nv -> nv.getNganh().getMaNganh()));

            int tongHetSlot = hetSlotByNganh.values().stream().mapToInt(List::size).sum();
            if (tongHetSlot == 0) {
                progressCallback.accept(80);
                break;
            }

            int changed = 0;
            for (Map.Entry<String, List<NguyenVong>> entry : hetSlotByNganh.entrySet()) {
                String maNganh = entry.getKey();
                List<NguyenVong> dsHetSlot = entry.getValue();
                dsHetSlot.sort(java.util.Comparator
                        .comparing(NguyenVong::getDiemXetTuyen, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))
                        .thenComparingInt(NguyenVong::getThuTu));

                for (NguyenVong nv : dsHetSlot) {
                    String cccd = nv.getThiSinh().getCccd();
                    List<NguyenVong> dsNvCuaTs = nvByCccd.get(cccd);

                    boolean daCoPassedKhac = false;
                    if (dsNvCuaTs != null) {
                        for (NguyenVong other : dsNvCuaTs) {
                            if (other != nv && KET_QUA_PASSED.equals(other.getKetQua())) {
                                daCoPassedKhac = true;
                                break;
                            }
                        }
                    }

                    if (daCoPassedKhac) {
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
                        if (diemSan != null && nv.getDiemXetTuyen() != null && nv.getDiemXetTuyen().compareTo(diemSan) < 0) {
                            continue;
                        }
                        nv.setKetQua(KET_QUA_PASSED);
                        slotMap.put(slotKey, slot - 1);
                        changed++;

                        if (dsNvCuaTs != null) {
                            for (NguyenVong other : dsNvCuaTs) {
                                if (other != nv && KET_QUA_HETSLOT.equals(other.getKetQua())) {
                                    other.setKetQua(KET_QUA_CANCELLED);
                                    changed++;
                                }
                            }
                        }
                    }
                }
            }

            int pct = 50 + (iter * 30) / MAX_CONFLICT_ITERATIONS;
            progressCallback.accept(pct);

            if (changed == 0) {
                progressCallback.accept(80);
                break;
            }
        }
    }

    private List<NguyenVong> tinhSlotNganh(String maNganh, Map<String, Integer> slotMap) {
        Nganh nganh = nganhDAO.findByMaNganh(maNganh);
        if (nganh == null) return null;

        int slDgnl = nganh.getSlDgnl() != null ? nganh.getSlDgnl() : 0;
        int slVsat = nganh.getSlVsat() != null ? nganh.getSlVsat() : 0;
        int slThpt = nganh.getSlThpt() != null ? nganh.getSlThpt() : 0;
        slotMap.put(maNganh + "_DGNL", slDgnl);
        slotMap.put(maNganh + "_VSAT", slVsat);
        slotMap.put(maNganh + "_THPT", slThpt);

        List<NguyenVong> dsNV = nguyenVongDAO.findByMaNganhSorted(maNganh);
        BigDecimal diemSan = nganh.getDiemSan();
        java.util.Set<String> passedCccdTrongNganh = new java.util.HashSet<>();
        java.util.Set<String> passedKeyTrongNganh  = new java.util.HashSet<>();

        for (NguyenVong nv : dsNV) {
            String cccd = nv.getThiSinh().getCccd();
            String key  = cccd + "_" + nv.getThuTu();
            String ketQua;

            if (nv.getDiemXetTuyen() == null) {
                ketQua = KET_QUA_FAILED;
            } else if (passedCccdTrongNganh.contains(cccd)) {
                ketQua = KET_QUA_CANCELLED;
            } else if (passedKeyTrongNganh.contains(key)) {
                ketQua = KET_QUA_KOXET;
            } else if (diemSan != null && nv.getDiemXetTuyen().compareTo(diemSan) < 0) {
                ketQua = KET_QUA_FAILED;
            } else {
                String pt = nv.getPhuongThuc();
                if (pt == null) continue;
                String slotKey = maNganh + "_" + pt;
                if (!slotMap.containsKey(slotKey)) continue;

                int slot = slotMap.get(slotKey);
                if (slot > 0) {
                    ketQua = KET_QUA_PASSED;
                    slotMap.put(slotKey, slot - 1);
                    passedCccdTrongNganh.add(cccd);
                    passedKeyTrongNganh.add(key);
                } else {
                    ketQua = KET_QUA_HETSLOT;
                }
            }
            nv.setKetQua(ketQua);
        }
        return dsNV;
    }
}