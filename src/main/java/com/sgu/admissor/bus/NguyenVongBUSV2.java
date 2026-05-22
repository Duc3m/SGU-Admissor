package com.sgu.admissor.bus;

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
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Duc3m
 */
public class NguyenVongBUSV2 extends NguyenVongBUS {

    private static final Logger LOG = Logger.getLogger(NguyenVongBUSV2.class.getName());

    private static final int    BATCH_SIZE   = 2000;
    private static final BigDecimal MAX_DIEM = new BigDecimal("30");
    private static final BigDecimal ZERO     = BigDecimal.ZERO;
    private static final BigDecimal NGUONG   = new BigDecimal("22.5");
    private static final BigDecimal BD_7_5   = new BigDecimal("7.5");
    private static final BigDecimal BD_3     = new BigDecimal("3");
    private static final BigDecimal MAX_DC   = new BigDecimal("3");

    private final NguyenVongDAO    nguyenVongDAO;
    private final DiemThiDAO        diemThiDAO;
    private final DiemCongDAO      diemCongDAO;
    private final NganhToHopDAO    nganhToHopDAO;
    private final BangQuyDoiDAO    bangQuyDoiDAO;
    private final ThiSinh2025DAO    thiSinhDAO;

    @Inject
    public NguyenVongBUSV2(NguyenVongDAO nguyenVongDAO,
                           DiemThiDAO diemThiDAO,
                           DiemCongDAO diemCongDAO,
                           NganhToHopDAO nganhToHopDAO,
                           BangQuyDoiDAO bangQuyDoiDAO,
                           ThiSinh2025DAO thiSinhDAO) {
        super(nguyenVongDAO, diemThiDAO, diemCongDAO, nganhToHopDAO, bangQuyDoiDAO, thiSinhDAO);
        this.nguyenVongDAO  = nguyenVongDAO;
        this.diemThiDAO     = diemThiDAO;
        this.diemCongDAO    = diemCongDAO;
        this.nganhToHopDAO  = nganhToHopDAO;
        this.bangQuyDoiDAO  = bangQuyDoiDAO;
        this.thiSinhDAO     = thiSinhDAO;
    }

    private static class CachedNganhToHop {
        final NganhToHop original;
        final String flagMon1;
        final String flagMon2;
        final String flagMon3;
        final int w1, w2, w3, W;
        final BigDecimal doLech;

        CachedNganhToHop(NganhToHop nth, NguyenVongBUSV2 bus) {
            this.original = nth;
            this.flagMon1 = bus.getFlagName(nth, 1);
            this.flagMon2 = bus.getFlagName(nth, 2);
            this.flagMon3 = bus.getFlagName(nth, 3);
            this.w1 = nth.getHsMon1() != null ? nth.getHsMon1() : 1;
            this.w2 = nth.getHsMon2() != null ? nth.getHsMon2() : 1;
            this.w3 = nth.getHsMon3() != null ? nth.getHsMon3() : 1;
            this.W = w1 + w2 + w3;
            this.doLech = nth.getDoLech() != null ? nth.getDoLech() : BigDecimal.ZERO;
        }
    }

    @Transactional
    public BUSResult tinhDiemTatCaV2(java.util.function.Consumer<Integer> progressCallback) {
        if (progressCallback == null) progressCallback = p -> {};
        progressCallback.accept(0);

//        long startTotal = System.currentTimeMillis();
//        LOG.info("=== BẮT ĐẦU CHẠY ĐIỀU PHỐI TÍNH ĐIỂM XÉT TUYỂN V2 ===");

        Map<String, java.util.TreeMap<BigDecimal, BangQuyDoi>> bqdMasterMap = buildBangQuyDoiMapsV2();
        progressCallback.accept(2);

        Map<String, List<CachedNganhToHop>> cachedNthMap = new HashMap<>();
        for (NganhToHop nth : nganhToHopDAO.findAll()) {
            if (nth.getNganh() == null) continue;
            cachedNthMap.computeIfAbsent(nth.getNganh().getMaNganh(), k -> new ArrayList<>())
                         .add(new CachedNganhToHop(nth, this));
        }
        progressCallback.accept(4);

        Map<String, Map<String, DiemThi>> diemThiByCccd = new HashMap<>();
        for (DiemThi dt : diemThiDAO.findAll()) {
            if (dt.getThiSinh() == null || dt.getThiSinh().getCccd() == null) continue;
            diemThiByCccd.computeIfAbsent(dt.getThiSinh().getCccd(), k -> new HashMap<>())
                         .put(dt.getPhuongThuc(), dt);
        }
        progressCallback.accept(6);

        Map<String, Map<String, DiemCong>> diemCongByCccd = new HashMap<>();
        for (DiemCong dc : diemCongDAO.findAll()) {
            if (dc.getDcKey() == null) continue;
            String cccdFromKey = dc.getDcKey().split("_")[0];
            diemCongByCccd.computeIfAbsent(cccdFromKey, k -> new HashMap<>())
                          .put(dc.getDcKey(), dc);
        }
        progressCallback.accept(8);

        Map<String, ThiSinh2025> tsMap = new HashMap<>();
        for (ThiSinh2025 ts : thiSinhDAO.findAll()) {
            if (ts.getCccd() != null) tsMap.put(ts.getCccd(), ts);
        }

        Map<String, List<NguyenVong>> nvByCccd = new HashMap<>();
        for (NguyenVong nv : nguyenVongDAO.findAll()) {
            if (nv.getThiSinh() == null || nv.getThiSinh().getCccd() == null) continue;
            nvByCccd.computeIfAbsent(nv.getThiSinh().getCccd(), k -> new ArrayList<>()).add(nv);
        }
        progressCallback.accept(10);

        int totalTs = nvByCccd.size();
        int processedTs = 0;
        int totalNv = 0;

        List<NguyenVong> updateBatch = new ArrayList<>();

        for (Map.Entry<String, List<NguyenVong>> entry : nvByCccd.entrySet()) {
            String cccd = entry.getKey();
            ThiSinh2025 ts = tsMap.get(cccd);
            if (ts == null) continue;

            Map<String, DiemThi> diemThiMap = diemThiByCccd.getOrDefault(cccd, new HashMap<>());
            Map<String, DiemCong> dcMap = diemCongByCccd.getOrDefault(cccd, new HashMap<>());

            BigDecimal mdut = tinhMDUTV2(ts);
            tinhVaGanDiemChoNvListV2(entry.getValue(), ts, diemThiMap, dcMap, cachedNthMap, bqdMasterMap, mdut);

            updateBatch.addAll(entry.getValue());
            totalNv += entry.getValue().size();
            processedTs++;

            if (updateBatch.size() >= BATCH_SIZE) {
                nguyenVongDAO.updateBatch(updateBatch);
                updateBatch.clear();
            }

            if (totalTs > 0) {
                int pct = 10 + (processedTs * 90) / totalTs;
                progressCallback.accept(pct);
            }
        }

        if (!updateBatch.isEmpty()) {
            nguyenVongDAO.updateBatch(updateBatch);
        }

        progressCallback.accept(100);
        return BUSResult.success(String.format("Tính điểm hoàn tất: %d thí sinh, %d nguyện vọng!", processedTs, totalNv));
    }

    private void tinhVaGanDiemChoNvListV2(List<NguyenVong> nvList,
                                          ThiSinh2025 ts,
                                          Map<String, DiemThi> diemThiMap,
                                          Map<String, DiemCong> dcMap,
                                          Map<String, List<CachedNganhToHop>> cachedNthMap,
                                          Map<String, java.util.TreeMap<BigDecimal, BangQuyDoi>> bqdMasterMap,
                                          BigDecimal mdut) {
        String cccd = ts.getCccd();

        for (NguyenVong nv : nvList) {
            if (nv.getNganh() == null) continue;
            String maNganh = nv.getNganh().getMaNganh();
            String pt = nv.getPhuongThuc();

            DiemThi dt = diemThiMap.get(pt);
            if (dt == null) continue;

            List<CachedNganhToHop> nthList = cachedNthMap.getOrDefault(maNganh, new ArrayList<>());
            if (nthList.isEmpty()) continue;

            BigDecimal bestDthgxt = null;
            BigDecimal bestDthxt = null;
            String bestToHop = null;

            for (CachedNganhToHop cNth : nthList) {
                NganhToHop nth = cNth.original;
                if (nth.getToHop() == null) continue;
                if (Boolean.TRUE.equals(nth.getKhac()) && PHUONGTHUC.PT[3].equals(pt)) continue;

                BigDecimal dthxt = tinhDTHXTV2(cNth, dt, pt, bqdMasterMap);
                if (dthxt == null) continue;

                BigDecimal doLech = cNth.doLech;
                if (PHUONGTHUC.PT[2].equals(pt)) {
                    doLech = ZERO;
                }

                BigDecimal dthgxt = dthxt.subtract(doLech);
                if (bestDthgxt == null || dthgxt.compareTo(bestDthgxt) > 0) {
                    bestDthgxt = dthgxt;
                    bestDthxt = dthxt;
                    bestToHop = nth.getToHop().getMaToHop();
                }
            }

            if (bestDthgxt == null) continue;

            String dcKey = cccd + "_" + maNganh + "_" + bestToHop + "_" + pt;
            if (PHUONGTHUC.PT[2].equals(pt)) {
                dcKey = cccd + "_" + maNganh + "_" + "NONE" + "_" + pt;
            }

            DiemCong dc = dcMap.get(dcKey);
            BigDecimal diemCong = (dc != null && dc.getDiemTong() != null) ? dc.getDiemTong() : ZERO;
            if (diemCong.compareTo(MAX_DC) > 0) diemCong = MAX_DC;

            BigDecimal baseUuTien = bestDthgxt.add(diemCong);
            BigDecimal dut;
            if (baseUuTien.compareTo(NGUONG) < 0) {
                dut = mdut;
            } else {
                BigDecimal tu = MAX_DIEM.subtract(bestDthxt).subtract(diemCong);
                dut = tu.divide(BD_7_5, 10, RoundingMode.HALF_UP).multiply(mdut);
            }
            if (dut.compareTo(ZERO) < 0) dut = ZERO;

            BigDecimal dxt = bestDthgxt.add(diemCong).add(dut);
            if (dxt.compareTo(MAX_DIEM) > 0) dxt = MAX_DIEM;

            nv.setDiemThxt(bestDthxt.setScale(5, RoundingMode.HALF_UP));
            nv.setDiemCong(diemCong.setScale(2, RoundingMode.HALF_UP));
            nv.setDiemUtqd(dut.setScale(5, RoundingMode.HALF_UP));
            nv.setDiemXetTuyen(dxt.setScale(5, RoundingMode.HALF_UP));
            nv.setToHopMon(bestToHop);
        }
    }

    private BigDecimal tinhDTHXTV2(CachedNganhToHop cNth, DiemThi dt, String pt,
                                   Map<String, java.util.TreeMap<BigDecimal, BangQuyDoi>> bqdMasterMap) {
        NganhToHop nth = cNth.original;
        if (PHUONGTHUC.PT[2].equals(pt)) {
            String key = "DGNL_" + nth.getToHop().getMaToHop();
            if (dt.getNl1() == null) return null;
            BangQuyDoi bqd = timBangQuyDoiV2(bqdMasterMap.get(key), dt.getNl1());
            if (bqd == null) return null;
            return quyDoi(dt.getNl1(), bqd);
        }

        BigDecimal d1 = getDiemMon(dt, cNth.flagMon1, 1, false, null);
        BigDecimal d2 = getDiemMon(dt, cNth.flagMon2, 2, false, null);
        BigDecimal d3 = getDiemMon(dt, cNth.flagMon3, 3, false, null);

        if (PHUONGTHUC.PT[3].equals(pt)) {
            if (d1 != null) d1 = quyDoi(d1, timBangQuyDoiV2(bqdMasterMap.get("VSAT_" + cNth.flagMon1), d1));
            if (d2 != null) d2 = quyDoi(d2, timBangQuyDoiV2(bqdMasterMap.get("VSAT_" + cNth.flagMon2), d2));
            if (d3 != null) d3 = quyDoi(d3, timBangQuyDoiV2(bqdMasterMap.get("VSAT_" + cNth.flagMon3), d3));
        }

        if (d1 == null || d2 == null || d3 == null) return null;
        if (cNth.W == 0) return null;

        BigDecimal tong = d1.multiply(BigDecimal.valueOf(cNth.w1))
                           .add(d2.multiply(BigDecimal.valueOf(cNth.w2)))
                           .add(d3.multiply(BigDecimal.valueOf(cNth.w3)));
        return tong.divide(BigDecimal.valueOf(cNth.W), 10, RoundingMode.HALF_UP).multiply(BD_3);
    }

    private Map<String, java.util.TreeMap<BigDecimal, BangQuyDoi>> buildBangQuyDoiMapsV2() {
        Map<String, java.util.TreeMap<BigDecimal, BangQuyDoi>> masterMap = new HashMap<>();
        List<BangQuyDoi> allBqd = bangQuyDoiDAO.findAll();

        for (BangQuyDoi bqd : allBqd) {
            String key = null;
            if (PHUONGTHUC.PT[3].equals(bqd.getPhuongThuc()) && bqd.getMon() != null) {
                key = "VSAT_" + bqd.getMon().trim().toUpperCase();
            } else if (PHUONGTHUC.PT[2].equals(bqd.getPhuongThuc()) && bqd.getToHop() != null 
                    && bqd.getToHop().getMaToHop() != null) {
                key = "DGNL_" + bqd.getToHop().getMaToHop();
            }

            if (key != null && bqd.getDiemA() != null) {
                masterMap.computeIfAbsent(key, k -> new java.util.TreeMap<>()).put(bqd.getDiemA(), bqd);
            }
        }
        return masterMap;
    }

    private BangQuyDoi timBangQuyDoiV2(java.util.TreeMap<BigDecimal, BangQuyDoi> treeMap, BigDecimal x) {
        if (treeMap == null || x == null) return null;
        Map.Entry<BigDecimal, BangQuyDoi> entry = treeMap.floorEntry(x);
        if (entry != null) {
            BangQuyDoi bqd = entry.getValue();
            if (bqd.getDiemB() != null && x.compareTo(bqd.getDiemB()) <= 0) {
                return bqd;
            }
        }
        return null;
    }

    private BigDecimal tinhMDUTV2(ThiSinh2025 ts) {
        BigDecimal kvDiem = ZERO;
        String kv = ts.getKhuVuc();
        if (kv != null) {
            kv = kv.trim();
            if ("1".equals(kv)) kvDiem = new BigDecimal("0.75");
            else if ("2NT".equalsIgnoreCase(kv)) kvDiem = new BigDecimal("0.5");
            else if ("2".equals(kv)) kvDiem = new BigDecimal("0.25");
        }

        BigDecimal dtDiem = ZERO;
        String dt = ts.getDoiTuong();
        if (dt != null && !dt.trim().isEmpty()) {
            dt = dt.trim();
            int end = 0;
            while (end < dt.length() && Character.isDigit(dt.charAt(end))) {
                end++;
            }
            if (end > 0) {
                try {
                    int num = Integer.parseInt(dt.substring(0, end));
                    if (num >= 1 && num <= 4) {
                        dtDiem = new BigDecimal("2.0");
                    } else if (num >= 5 && num <= 7) {
                        dtDiem = new BigDecimal("1.0");
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return kvDiem.add(dtDiem);
    }

    private String getFlagName(NganhToHop nth, int viTri) {
        if (nth == null || nth.getToHop() == null) {
            return null;
        }

        String monName = switch (viTri) {
            case 1 -> nth.getToHop().getMon1();
            case 2 -> nth.getToHop().getMon2();
            case 3 -> nth.getToHop().getMon3();
            default -> null;
        };

        return monName != null ? monName.trim().toUpperCase() : null;
    }

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
            case "NK1"  -> dt.getNk1();
            case "NK2"  -> dt.getNk2();
            case "NK3"  -> dt.getNk3();
            case "NK4"  -> dt.getNk4();
            case "NK5"  -> dt.getNk5();
            case "NK6"  -> dt.getNk6();
            default -> null;
        };
        return raw;
    }

    private BigDecimal quyDoi(BigDecimal x, BangQuyDoi bqd) {
        if (x == null || bqd == null) return null;
        BigDecimal a = bqd.getDiemA();
        BigDecimal b = bqd.getDiemB();
        BigDecimal c = bqd.getDiemC();
        BigDecimal d = bqd.getDiemD();
        if (a == null || b == null || c == null || d == null) return null;
        BigDecimal bMinusA = b.subtract(a);
        if (bMinusA.compareTo(ZERO) == 0) return c;
        return c.add(x.subtract(a).divide(bMinusA, 10, RoundingMode.HALF_UP).multiply(d.subtract(c)));
    }

    private BigDecimal maxOf(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) >= 0 ? a : b;
    }
}