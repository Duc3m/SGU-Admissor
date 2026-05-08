/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.NganhToHopDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NganhToHop;
import com.sgu.admissor.entity.ToHop;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NganhToHopBUSTest {

    @Mock
    private NganhToHopDAO nganhToHopDAO;

    @InjectMocks
    private NganhToHopBUS nganhToHopBUS;

    private NganhToHop testNganhToHop;
    private Nganh testNganh;
    private ToHop testToHop;

    @BeforeEach
    public void setUp() {
        testNganh = new Nganh();
        testNganh.setMaNganh("7480201");

        testToHop = new ToHop();
        testToHop.setMaToHop("A00");
        testToHop.setMon1("TO");
        testToHop.setMon2("LI");
        testToHop.setMon3("HO");

        testNganhToHop = new NganhToHop();
        testNganhToHop.setId(1);
        testNganhToHop.setNganh(testNganh);
        testNganhToHop.setToHop(testToHop);
    }

    // ==========================================
    // TESTS FOR getAllNganhToHop & getById/MaNganh/MaToHop
    // ==========================================
    @Test
    public void testGetAllNganhToHop() {
        when(nganhToHopDAO.findAll()).thenReturn(Arrays.asList(testNganhToHop));

        BUSResult<List<NganhToHop>> result = nganhToHopBUS.getAllNganhToHop();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ ngành - tổ hợp thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetNganhToHopById_InvalidId() {
        BUSResult<NganhToHop> result1 = nganhToHopBUS.getNganhToHopById(null);
        assertFalse(result1.isSuccess());
        assertEquals("ID ngành-tổ hợp không hợp lệ!", result1.getMessage());

        BUSResult<NganhToHop> result2 = nganhToHopBUS.getNganhToHopById(0);
        assertFalse(result2.isSuccess());
        assertEquals("ID ngành-tổ hợp không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetNganhToHopByMaNganh_Invalid() {
        BUSResult<List<NganhToHop>> result1 = nganhToHopBUS.getNganhToHopByMaNganh(null);
        assertFalse(result1.isSuccess());
        assertEquals("Mã ngành không hợp lệ!", result1.getMessage());

        BUSResult<List<NganhToHop>> result2 = nganhToHopBUS.getNganhToHopByMaNganh("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Mã ngành không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetNganhToHopByMaToHop_Invalid() {
        BUSResult<List<NganhToHop>> result1 = nganhToHopBUS.getNganhToHopByMaToHop(null);
        assertFalse(result1.isSuccess());
        assertEquals("Mã tổ hợp không hợp lệ!", result1.getMessage());

        BUSResult<List<NganhToHop>> result2 = nganhToHopBUS.getNganhToHopByMaToHop("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Mã tổ hợp không hợp lệ!", result2.getMessage());
    }

    // ==========================================
    // TESTS FOR addNganhToHop
    // ==========================================
    @Test
    public void testAddNganhToHop_NullData() {
        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(null);
        assertEquals("Thông tin ngành-tổ hợp không hợp lệ!", result.getMessage());
        verify(nganhToHopDAO, never()).insert(any(NganhToHop.class));
    }

    @Test
    public void testAddNganhToHop_InvalidMaNganh() {
        NganhToHop invalid = new NganhToHop();
        invalid.setNganh(new Nganh());
        invalid.setToHop(testToHop);

        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(invalid);

        assertEquals("Mã ngành không được để trống!", result.getMessage());
        verify(nganhToHopDAO, never()).insert(any(NganhToHop.class));
    }

    @Test
    public void testAddNganhToHop_InvalidMaToHop() {
        NganhToHop invalid = new NganhToHop();
        invalid.setNganh(testNganh);
        invalid.setToHop(new ToHop());

        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(invalid);

        assertEquals("Mã tổ hợp không được để trống!", result.getMessage());
        verify(nganhToHopDAO, never()).insert(any(NganhToHop.class));
    }

    @Test
    public void testAddNganhToHop_Duplicate() {
        when(nganhToHopDAO.findByMaNganhAndMaToHop("7480201", "A00")).thenReturn(testNganhToHop);

        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(testNganhToHop);

        assertEquals("Liên kết ngành - tổ hợp này đã tồn tại!", result.getMessage());
        verify(nganhToHopDAO, never()).insert(any(NganhToHop.class));
    }

    @Test
    public void testAddNganhToHop_Success() {
        when(nganhToHopDAO.findByMaNganhAndMaToHop("7480201", "A00")).thenReturn(null);
        when(nganhToHopDAO.insert(testNganhToHop)).thenReturn(true);

        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(testNganhToHop);

        assertEquals("Thêm liên kết ngành - tổ hợp thành công!", result.getMessage());
    }

    @Test
    public void testAddNganhToHop_Fail() {
        when(nganhToHopDAO.findByMaNganhAndMaToHop("7480201", "A00")).thenReturn(null);
        when(nganhToHopDAO.insert(testNganhToHop)).thenReturn(false);

        BUSResult<NganhToHop> result = nganhToHopBUS.addNganhToHop(testNganhToHop);

        assertEquals("Thêm liên kết ngành - tổ hợp thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR updateNganhToHop
    // ==========================================
    @Test
    public void testUpdateNganhToHop_InvalidId() {
        NganhToHop invalid = new NganhToHop();
        invalid.setId(0);

        BUSResult<NganhToHop> result = nganhToHopBUS.updateNganhToHop(invalid);
        assertEquals("ID ngành-tổ hợp không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateNganhToHop_NotFound() {
        when(nganhToHopDAO.findById(1)).thenReturn(null);

        BUSResult<NganhToHop> result = nganhToHopBUS.updateNganhToHop(testNganhToHop);
        assertEquals("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateNganhToHop_Success() {
        NganhToHop existing = new NganhToHop();
        existing.setId(1);

        when(nganhToHopDAO.findById(1)).thenReturn(existing);
        when(nganhToHopDAO.update(existing)).thenReturn(true);

        BUSResult<NganhToHop> result = nganhToHopBUS.updateNganhToHop(testNganhToHop);
        assertEquals("Cập nhật liên kết ngành - tổ hợp thành công!", result.getMessage());
    }

    @Test
    public void testUpdateNganhToHop_Fail() {
        NganhToHop existing = new NganhToHop();
        existing.setId(1);

        when(nganhToHopDAO.findById(1)).thenReturn(existing);
        when(nganhToHopDAO.update(existing)).thenReturn(false);

        BUSResult<NganhToHop> result = nganhToHopBUS.updateNganhToHop(testNganhToHop);
        assertEquals("Cập nhật liên kết ngành - tổ hợp thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteNganhToHop
    // ==========================================
    @Test
    public void testDeleteNganhToHop_InvalidId() {
        NganhToHop invalid = new NganhToHop();
        invalid.setId(0);

        BUSResult<NganhToHop> result = nganhToHopBUS.deleteNganhToHop(invalid);
        assertEquals("ID ngành-tổ hợp không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteNganhToHop_NotFound() {
        when(nganhToHopDAO.findById(1)).thenReturn(null);

        BUSResult<NganhToHop> result = nganhToHopBUS.deleteNganhToHop(testNganhToHop);
        assertEquals("Không tìm thấy liên kết ngành - tổ hợp này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteNganhToHop_Success() {
        when(nganhToHopDAO.findById(1)).thenReturn(testNganhToHop);
        when(nganhToHopDAO.delete(testNganhToHop)).thenReturn(true);

        BUSResult<NganhToHop> result = nganhToHopBUS.deleteNganhToHop(testNganhToHop);
        assertEquals("Xóa liên kết ngành - tổ hợp thành công!", result.getMessage());
    }

    @Test
    public void testDeleteNganhToHop_Fail() {
        when(nganhToHopDAO.findById(1)).thenReturn(testNganhToHop);
        when(nganhToHopDAO.delete(testNganhToHop)).thenReturn(false);

        BUSResult<NganhToHop> result = nganhToHopBUS.deleteNganhToHop(testNganhToHop);
        assertEquals("Xóa liên kết ngành - tổ hợp thất bại!", result.getMessage());
    }
}
