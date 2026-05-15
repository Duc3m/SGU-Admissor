package com.sgu.admissor.bus;

import com.sgu.admissor.constants.PHUONGTHUC;
import com.sgu.admissor.dao.BangQuyDoiDAO;
import com.sgu.admissor.dao.DiemCongDAO;
import com.sgu.admissor.dao.DiemThiDAO;
import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dao.ThiSinh2025DAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Integrated test (Mockito) cho NguyenVongBUS.tinhDiemMotThiSinh.
 *
 * Kịch bản bao phủ:
 *   TC01 - CCCD rỗng / null
 *   TC02 - Không tìm thấy thí sinh
 *   TC03 - Không có nguyện vọng
 *   TC04 - Thiếu DiemThi → bỏ qua nguyện vọng, updateBatch [] → success
 *   TC05 - THPT, tổ hợp thường (A00: TO+LI+HO), không ĐC, không ưu tiên
 *   TC06 - THPT, có điểm cộng + ưu tiên (ĐTHGXT+ĐC < 22.5)
 *   TC07 - THPT, ĐTHGXT+ĐC >= 22.5 → công thức ưu tiên giảm dần
 *   TC08 - DGNL, tra bảng quy đổi theo tổ hợp
 *   TC09 - VSAT, quy đổi từng môn rồi tính tổng hợp
 *   TC10 - KHAC, tổ hợp có NK lấy từ toHop.mon1/mon2/mon3
 *   TC11 - N1, lấy max(n1Thi, n1Cc)
 *   TC12 - KHAC + VSAT → bỏ qua
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NguyenVongBUS - tinhDiemMotThiSinh")
public class NguyenVongBUSTinhDiemTest {

    // =================== PHUONGTHUC constants ===================
    // PHUONGTHUC.PT = {"", "XTT", "DGNL", "VSAT", "THPT"}
    private static final String PT_THPT = PHUONGTHUC.PT[4];
    private static final String PT_DGNL = PHUONGTHUC.PT[2];
    private static final String PT_VSAT = PHUONGTHUC.PT[3];

    // =================== Mocks ===================
    @Mock private NguyenVongDAO    nguyenVongDAO;
    @Mock private DiemThiDAO       diemThiDAO;
    @Mock private DiemCongDAO      diemCongDAO;
    @Mock private NganhToHopDAO    nganhToHopDAO;
    @Mock private BangQuyDoiDAO    bangQuyDoiDAO;
    @Mock private ThiSinh2025DAO   thiSinhDAO;

    @InjectMocks
    private NguyenVongBUS bus;

    // =================== Dữ liệu dùng chung ===================
    private static final String CCCD      = "079000000001";
    private static final String MA_NGANH  = "7480201";

    private ThiSinh2025 thiSinh;
    private Nganh       nganh;

    @BeforeEach
    void setUp() {
        thiSinh = new ThiSinh2025();
        thiSinh.setCccd(CCCD);
        // Mặc định không có ưu tiên / khu vực
        thiSinh.setKhuVuc("KV3");
        thiSinh.setDoiTuong("");

        nganh = new Nganh();
        nganh.setMaNganh(MA_NGANH);
    }

    // =========================================================
    // TC01 - Validate: CCCD rỗng / null
    // =========================================================
    @Test
    @DisplayName("TC01 - CCCD null hoặc rỗng trả lỗi ngay")
    void tc01_cccdRong() {
        assertEquals("CCCD không hợp lệ!", bus.tinhDiemMotThiSinh(null).getMessage());
        assertEquals("CCCD không hợp lệ!", bus.tinhDiemMotThiSinh("  ").getMessage());
    }

    // =========================================================
    // TC02 - Không tìm thấy thí sinh
    // =========================================================
    @Test
    @DisplayName("TC02 - Không tìm thấy thí sinh")
    void tc02_khongTimThayThiSinh() {
        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(null);
        BUSResult result = bus.tinhDiemMotThiSinh(CCCD);
        assertTrue(result.getMessage().contains("Không tìm thấy thí sinh"));
    }

    // =========================================================
    // TC03 - Không có nguyện vọng
    // =========================================================
    @Test
    @DisplayName("TC03 - Thí sinh không có nguyện vọng")
    void tc03_khongCoNguyenVong() {
        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        BUSResult result = bus.tinhDiemMotThiSinh(CCCD);
        assertEquals("Thí sinh không có nguyện vọng nào!", result.getMessage());
    }

    // =========================================================
    // TC04 - Không có DiemThi → bỏ qua, updateBatch thành công
    // =========================================================
    @Test
    @DisplayName("TC04 - Thiếu DiemThi, nguyện vọng bị bỏ qua nhưng vẫn success")
    void tc04_khongCoDiemThi() {
        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(makeNv(PT_THPT)));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(Collections.emptyList());
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        BUSResult result = bus.tinhDiemMotThiSinh(CCCD);
        assertTrue(result.isSuccess());
    }

    // =========================================================
    // TC05 - THPT, A00 (TO+LI+HO), hệ số 1-1-1, không DC, không UT
    //
    //   Điểm: TO=9, LI=8, HO=7  → ĐTHXT = (9+8+7)/3 * 3 = 24.00
    //   doLech=0 → ĐTHGXT=24.00
    //   ĐC=0, MĐƯT=0 → ĐXT=24.00
    // =========================================================
    @Test
    @DisplayName("TC05 - THPT A00, không ĐC, không ưu tiên → ĐXT = 24.00")
    void tc05_thptKhongDcKhongUuTien() {
        NguyenVong nv = makeNv(PT_THPT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_THPT);
        dt.setTo(bd("9"));
        dt.setLi(bd("8"));
        dt.setHo(bd("7"));

        NganhToHop nth = makeNthThpt("A00", false, false); // TO+LI+HO, hs 1-1-1

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // ĐTHXT = (9+8+7)/3*3 = 24.00
        assertBD("24.00000", nv.getDiemThxt());
        assertBD("0.00",     nv.getDiemCong());
        assertBD("0.00000",  nv.getDiemUtqd());
        assertBD("24.00000", nv.getDiemXetTuyen());
        assertEquals("A00", nv.getToHopMon());
    }

    // =========================================================
    // TC06 - THPT, có ĐC=1.5, KV1+ĐT07 → MĐƯT=0.75+1.0=1.75
    //   Điểm: TO=7, LI=7, HO=7 → ĐTHXT=21, ĐTHGXT=21
    //   baseUuTien = 21+1.5 = 22.5 → KHÔNG < 22.5 → công thức giảm dần
    //   ĐƯT = (30-21-1.5)/7.5 * 1.75 = 7.5/7.5 * 1.75 = 1.75
    //   ĐXT = 21 + 1.5 + 1.75 = 24.25
    // =========================================================
    @Test
    @DisplayName("TC06 - THPT, ĐC=1.5, KV1+ĐT07, ĐTHGXT+ĐC=22.5 → công thức giảm dần")
    void tc06_thptCoUuTienGiamDan() {
        thiSinh.setKhuVuc("KV1");
        thiSinh.setDoiTuong("07");

        NguyenVong nv = makeNv(PT_THPT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_THPT);
        dt.setTo(bd("7")); dt.setLi(bd("7")); dt.setHo(bd("7"));

        NganhToHop nth = makeNthThpt("A00", false, false);

        DiemCong dc = new DiemCong();
        String dcKey = CCCD + "_" + MA_NGANH + "_A00_" + PT_THPT;
        dc.setDcKey(dcKey);
        dc.setDiemTong(bd("1.5"));

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(List.of(dc));
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // ĐTHXT=21, ĐC=1.5, baseUuTien=22.5 → giảm dần
        // ĐƯT = (30-21-1.5)/7.5 * 1.75 = 7.5/7.5 * 1.75 = 1.75
        assertBD("21.00000", nv.getDiemThxt());
        assertBD("1.50",     nv.getDiemCong());
        assertBD("1.75000",  nv.getDiemUtqd());
        assertBD("24.25000", nv.getDiemXetTuyen());
    }

    // =========================================================
    // TC07 - THPT, ĐTHGXT+ĐC < 22.5 → ưu tiên đầy đủ MĐƯT
    //   Điểm: TO=6, LI=6, HO=6 → ĐTHXT=18, ĐTHGXT=18
    //   ĐC=0, KV2-NT=0.5, ĐT03=2.0 → MĐƯT=2.5
    //   baseUuTien=18 < 22.5 → ĐƯT=MĐƯT=2.5
    //   ĐXT=18+0+2.5=20.5
    // =========================================================
    @Test
    @DisplayName("TC07 - THPT, ĐTHGXT+ĐC < 22.5 → ĐƯT = MĐƯT đầy đủ")
    void tc07_thptUuTienDayDu() {
        thiSinh.setKhuVuc("KV2-NT");
        thiSinh.setDoiTuong("03");

        NguyenVong nv = makeNv(PT_THPT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_THPT);
        dt.setTo(bd("6")); dt.setLi(bd("6")); dt.setHo(bd("6"));

        NganhToHop nth = makeNthThpt("A00", false, false);

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        assertBD("18.00000", nv.getDiemThxt());
        assertBD("0.00",     nv.getDiemCong());
        assertBD("2.50000",  nv.getDiemUtqd());
        assertBD("20.50000", nv.getDiemXetTuyen());
    }

    // =========================================================
    // TC08 - DGNL, bảng quy đổi tổ hợp A00
    //   nl1=800, BQD: diemA=700, diemB=900, diemC=20, diemD=25
    //   y = 20 + (800-700)/(900-700) * (25-20) = 20 + (100/200)*5 = 22.5
    //   ĐTHXT=22.5, ĐTHGXT=22.5, không ĐC, không UT
    //   ĐXT = 22.5
    // =========================================================
    @Test
    @DisplayName("TC08 - DGNL, quy đổi điểm tổng hợp → ĐXT = 22.5")
    void tc08_dgnl() {
        NguyenVong nv = makeNv(PT_DGNL);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_DGNL);
        dt.setNl1(bd("800"));

        ToHop toHop = makeToHop("A00", null, null, null);
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setTo(true); nth.setLi(true); nth.setHo(true);
        nth.setDoLech(bd("0"));

        BangQuyDoi bqd = makeBqd(PT_DGNL, toHop, null, "700", "900", "20", "25");

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(List.of(bqd));
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // y = 20 + (100/200)*5 = 22.5
        assertBD("22.50000", nv.getDiemThxt());
        assertBD("22.50000", nv.getDiemXetTuyen());
        assertEquals("A00", nv.getToHopMon());
    }

    // =========================================================
    // TC09 - VSAT, tổ hợp A00 (TO+LI+HO), mỗi môn quy đổi riêng
    //   TO raw=650, BQD VSAT TO: a=600,b=700,c=7,d=8 → y=7+(50/100)*1=7.5
    //   LI raw=700, BQD VSAT LI: a=700,b=800,c=8,d=9 → x=700=a → không nằm trong (a,b] → null → skip
    //
    //   Dùng: TO raw=650→7.5, LI raw=750→8.5 (a=700,b=800,c=8,d=9), HO raw=800→9.0 (a=700,b=800,c=8,d=9)
    //   ĐTHXT = (7.5+8.5+9)/3*3 = 25.0
    // =========================================================
    @Test
    @DisplayName("TC09 - VSAT, quy đổi từng môn rồi tính tổng hợp → ĐXT = 25.0")
    void tc09_vsat() {
        NguyenVong nv = makeNv(PT_VSAT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_VSAT);
        dt.setTo(bd("650"));
        dt.setLi(bd("750"));
        dt.setHo(bd("800"));

        ToHop toHop = makeToHop("A00", null, null, null);
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setTo(true); nth.setLi(true); nth.setHo(true);
        nth.setDoLech(bd("0"));

        // BQD VSAT TO: (600,700] → c=7,d=8
        BangQuyDoi bqdTo = makeBqd(PT_VSAT, null, "TO", "600", "700", "7", "8");
        // BQD VSAT LI: (700,800] → c=8,d=9 (dùng cho cả LI và HO)
        BangQuyDoi bqdLi = makeBqd(PT_VSAT, null, "LI", "700", "800", "8", "9");
        BangQuyDoi bqdHo = makeBqd(PT_VSAT, null, "HO", "700", "800", "8", "9");

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(List.of(bqdTo, bqdLi, bqdHo));
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // TO=7.5, LI=8.5, HO=8.5+... tính lại:
        // LI raw=750: y=8+(750-700)/(800-700)*(9-8)=8+0.5=8.5
        // HO raw=800: y=8+(800-700)/(800-700)*(9-8)=8+1.0=9.0
        // ĐTHXT=(7.5+8.5+9.0)/3*3=25.0
        assertBD("25.00000", nv.getDiemThxt());
        assertBD("25.00000", nv.getDiemXetTuyen());
    }

    // =========================================================
    // TC10 - KHAC, tổ hợp H01 (mon1=TO, mon2=NK1, mon3=NK2)
    //   TO=8, NK1=7, NK2=9 → ĐTHXT=(8+7+9)/3*3=24
    // =========================================================
    @Test
    @DisplayName("TC10 - KHAC, lấy điểm NK từ toHop.mon1/mon2/mon3 → ĐXT = 24.0")
    void tc10_khac() {
        NguyenVong nv = makeNv(PT_THPT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_THPT);
        dt.setTo(bd("8"));
        dt.setNk1(bd("7"));
        dt.setNk2(bd("9"));

        // Tổ hợp H01: mon1=TO, mon2=NK1, mon3=NK2
        ToHop toHop = makeToHop("H01", "TO", "NK1", "NK2");
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setKhac(true);   // Flag KHAC = true
        nth.setDoLech(bd("0"));

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        assertBD("24.00000", nv.getDiemThxt());
        assertBD("24.00000", nv.getDiemXetTuyen());
        assertEquals("H01", nv.getToHopMon());
    }

    // =========================================================
    // TC11 - N1, lấy max(n1Thi, n1Cc)
    //   n1Thi=7.5, n1Cc=8.0 → dùng 8.0
    //   Tổ hợp D01: TO=9, N1
    //   TO=9, N1=max(7.5,8.0)=8.0 → ĐTHXT=(9+8)/2*3 ... nhưng hệ số 1-1
    //   Thực ra tổ hợp 3 môn: thêm VA=8.5
    //   ĐTHXT=(9+8.0+8.5)/3*3=25.5
    // =========================================================
    @Test
    @DisplayName("TC11 - N1, max(n1Thi=7.5, n1Cc=8.0)=8.0 → tính đúng điểm")
    void tc11_n1MaxCcVsThi() {
        NguyenVong nv = makeNv(PT_THPT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_THPT);
        dt.setTo(bd("9"));
        dt.setN1Thi(bd("7.5"));
        dt.setN1Cc(bd("8.0"));
        dt.setVa(bd("8.5"));

        // Tổ hợp D07: TO + N1 + VA
        ToHop toHop = makeToHop("D07", null, null, null);
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setTo(true); nth.setN1(true); nth.setVa(true);
        nth.setDoLech(bd("0"));

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // (9 + 8.0 + 8.5) / 3 * 3 = 25.5
        assertBD("25.50000", nv.getDiemThxt());
        assertBD("25.50000", nv.getDiemXetTuyen());
    }

    // =========================================================
    // TC12 - KHAC + VSAT → bỏ qua (không tính được)
    // =========================================================
    @Test
    @DisplayName("TC12 - KHAC + VSAT → nguyện vọng bị bỏ qua, diemXetTuyen = null")
    void tc12_khacVsat() {
        NguyenVong nv = makeNv(PT_VSAT);

        DiemThi dt = new DiemThi();
        dt.setThiSinh(thiSinh);
        dt.setPhuongThuc(PT_VSAT);
        dt.setTo(bd("700"));
        dt.setNk1(bd("750"));

        ToHop toHop = makeToHop("H01", "TO", "NK1", "NK2");
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setKhac(true);
        nth.setDoLech(bd("0"));

        when(thiSinhDAO.findByCccd(CCCD)).thenReturn(thiSinh);
        when(nguyenVongDAO.findByCccd(CCCD)).thenReturn(List.of(nv));
        when(diemThiDAO.findByCccd(CCCD)).thenReturn(List.of(dt));
        when(diemCongDAO.findByCccd(CCCD)).thenReturn(Collections.emptyList());
        when(nganhToHopDAO.findAll()).thenReturn(List.of(nth));
        when(bangQuyDoiDAO.findAll()).thenReturn(Collections.emptyList());
        when(nguyenVongDAO.updateBatch(anyList())).thenReturn(true);

        bus.tinhDiemMotThiSinh(CCCD);

        // Nguyện vọng bị bỏ qua → không gán điểm
        assertNull(nv.getDiemXetTuyen());
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private NguyenVong makeNv(String phuongThuc) {
        NguyenVong nv = new NguyenVong();
        nv.setThiSinh(thiSinh);
        nv.setNganh(nganh);
        nv.setPhuongThuc(phuongThuc);
        return nv;
    }

    /**
     * Tạo NganhToHop cho THPT, tổ hợp A00 (TO+LI+HO), hệ số 1-1-1.
     * hasN1=true → thay HO bằng N1, hasKhac → đánh dấu KHAC
     */
    private NganhToHop makeNthThpt(String maToHop, boolean hasN1, boolean hasKhac) {
        ToHop toHop = makeToHop(maToHop, null, null, null);
        NganhToHop nth = new NganhToHop();
        nth.setNganh(nganh);
        nth.setToHop(toHop);
        nth.setHsMon1(1); nth.setHsMon2(1); nth.setHsMon3(1);
        nth.setTo(true);
        nth.setLi(true);
        if (hasN1) {
            nth.setN1(true);
        } else {
            nth.setHo(true);
        }
        if (hasKhac) nth.setKhac(true);
        nth.setDoLech(bd("0"));
        return nth;
    }

    private ToHop makeToHop(String maToHop, String mon1, String mon2, String mon3) {
        ToHop t = new ToHop();
        t.setMaToHop(maToHop);
        t.setMon1(mon1);
        t.setMon2(mon2);
        t.setMon3(mon3);
        return t;
    }

    private BangQuyDoi makeBqd(String pt, ToHop toHop, String mon,
                                String a, String b, String c, String d) {
        BangQuyDoi bqd = new BangQuyDoi();
        bqd.setPhuongThuc(pt);
        bqd.setToHop(toHop);
        bqd.setMon(mon);
        bqd.setDiemA(bd(a));
        bqd.setDiemB(bd(b));
        bqd.setDiemC(bd(c));
        bqd.setDiemD(bd(d));
        return bqd;
    }

    private BigDecimal bd(String val) {
        return new BigDecimal(val);
    }

    private void assertBD(String expected, BigDecimal actual) {
        assertNotNull(actual, "Giá trị không được null, mong đợi: " + expected);
        BigDecimal exp = new BigDecimal(expected);
        assertEquals(0, exp.compareTo(actual.setScale(exp.scale(), RoundingMode.HALF_UP)),
                String.format("Mong đợi %s nhưng nhận được %s", expected, actual.toPlainString()));
    }
}
