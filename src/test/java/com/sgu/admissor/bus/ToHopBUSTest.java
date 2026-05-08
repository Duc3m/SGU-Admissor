/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.ToHopDAO;
import com.sgu.admissor.dto.BUSResult;
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
public class ToHopBUSTest {

    @Mock
    private ToHopDAO toHopDAO;

    @InjectMocks
    private ToHopBUS toHopBUS;

    private ToHop testToHop;

    @BeforeEach
    public void setUp() {
        testToHop = new ToHop();
        testToHop.setId(1);
        testToHop.setMaToHop("A00");
        testToHop.setMon1("TO");
        testToHop.setMon2("LI");
        testToHop.setMon3("HO");
        testToHop.setTenToHop("Toan - Ly - Hoa");
    }

    // ==========================================
    // TESTS FOR getAllToHop & getById/MaToHop/TenToHop
    // ==========================================
    @Test
    public void testGetAllToHop() {
        when(toHopDAO.findAll()).thenReturn(Arrays.asList(testToHop));

        BUSResult<List<ToHop>> result = toHopBUS.getAllToHop();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ tổ hợp thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetToHopById_InvalidId() {
        BUSResult<ToHop> result1 = toHopBUS.getToHopById(null);
        assertFalse(result1.isSuccess());
        assertEquals("ID tổ hợp không hợp lệ!", result1.getMessage());

        BUSResult<ToHop> result2 = toHopBUS.getToHopById(0);
        assertFalse(result2.isSuccess());
        assertEquals("ID tổ hợp không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetToHopByMaToHop_Invalid() {
        BUSResult<ToHop> result1 = toHopBUS.getToHopByMaToHop(null);
        assertFalse(result1.isSuccess());
        assertEquals("Mã tổ hợp không hợp lệ!", result1.getMessage());

        BUSResult<ToHop> result2 = toHopBUS.getToHopByMaToHop("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Mã tổ hợp không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetToHopByTenToHop_Invalid() {
        BUSResult<List<ToHop>> result1 = toHopBUS.getToHopByTenToHop(null);
        assertFalse(result1.isSuccess());
        assertEquals("Tên tổ hợp không hợp lệ!", result1.getMessage());

        BUSResult<List<ToHop>> result2 = toHopBUS.getToHopByTenToHop("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Tên tổ hợp không hợp lệ!", result2.getMessage());
    }

    // ==========================================
    // TESTS FOR addToHop
    // ==========================================
    @Test
    public void testAddToHop_NullData() {
        BUSResult<ToHop> result = toHopBUS.addToHop(null);
        assertEquals("Thông tin tổ hợp không hợp lệ!", result.getMessage());
        verify(toHopDAO, never()).insert(any(ToHop.class));
    }

    @Test
    public void testAddToHop_InvalidMaToHop() {
        ToHop invalid = new ToHop();
        invalid.setMon1("TO");
        invalid.setMon2("LI");
        invalid.setMon3("HO");

        BUSResult<ToHop> result = toHopBUS.addToHop(invalid);

        assertEquals("Mã tổ hợp không được để trống!", result.getMessage());
        verify(toHopDAO, never()).insert(any(ToHop.class));
    }

    @Test
    public void testAddToHop_InvalidMon() {
        ToHop invalid = new ToHop();
        invalid.setMaToHop("A00");
        invalid.setMon1("TO");

        BUSResult<ToHop> result = toHopBUS.addToHop(invalid);

        assertEquals("Các môn trong tổ hợp không được để trống!", result.getMessage());
        verify(toHopDAO, never()).insert(any(ToHop.class));
    }

    @Test
    public void testAddToHop_DuplicateMaToHop() {
        when(toHopDAO.findByMaToHop("A00")).thenReturn(testToHop);

        BUSResult<ToHop> result = toHopBUS.addToHop(testToHop);

        assertEquals("Mã tổ hợp đã tồn tại trong hệ thống!", result.getMessage());
        verify(toHopDAO, never()).insert(any(ToHop.class));
    }

    @Test
    public void testAddToHop_Success() {
        when(toHopDAO.findByMaToHop("A00")).thenReturn(null);
        when(toHopDAO.insert(testToHop)).thenReturn(true);

        BUSResult<ToHop> result = toHopBUS.addToHop(testToHop);

        assertEquals("Thêm tổ hợp mới thành công!", result.getMessage());
    }

    @Test
    public void testAddToHop_Fail() {
        when(toHopDAO.findByMaToHop("A00")).thenReturn(null);
        when(toHopDAO.insert(testToHop)).thenReturn(false);

        BUSResult<ToHop> result = toHopBUS.addToHop(testToHop);

        assertEquals("Thêm tổ hợp thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR updateToHop
    // ==========================================
    @Test
    public void testUpdateToHop_InvalidId() {
        ToHop invalid = new ToHop();
        invalid.setId(0);

        BUSResult<ToHop> result = toHopBUS.updateToHop(invalid);
        assertEquals("ID tổ hợp không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateToHop_NotFound() {
        when(toHopDAO.findById(1)).thenReturn(null);

        BUSResult<ToHop> result = toHopBUS.updateToHop(testToHop);
        assertEquals("Không tìm thấy tổ hợp này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateToHop_Success() {
        ToHop existing = new ToHop();
        existing.setId(1);

        when(toHopDAO.findById(1)).thenReturn(existing);
        when(toHopDAO.update(existing)).thenReturn(true);

        BUSResult<ToHop> result = toHopBUS.updateToHop(testToHop);
        assertEquals("Cập nhật tổ hợp thành công!", result.getMessage());
    }

    @Test
    public void testUpdateToHop_Fail() {
        ToHop existing = new ToHop();
        existing.setId(1);

        when(toHopDAO.findById(1)).thenReturn(existing);
        when(toHopDAO.update(existing)).thenReturn(false);

        BUSResult<ToHop> result = toHopBUS.updateToHop(testToHop);
        assertEquals("Cập nhật tổ hợp thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteToHop
    // ==========================================
    @Test
    public void testDeleteToHop_InvalidId() {
        ToHop invalid = new ToHop();
        invalid.setId(0);

        BUSResult<ToHop> result = toHopBUS.deleteToHop(invalid);
        assertEquals("ID tổ hợp không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteToHop_NotFound() {
        when(toHopDAO.findById(1)).thenReturn(null);

        BUSResult<ToHop> result = toHopBUS.deleteToHop(testToHop);
        assertEquals("Không tìm thấy tổ hợp này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteToHop_Success() {
        when(toHopDAO.findById(1)).thenReturn(testToHop);
        when(toHopDAO.delete(testToHop)).thenReturn(true);

        BUSResult<ToHop> result = toHopBUS.deleteToHop(testToHop);
        assertEquals("Xóa tổ hợp thành công!", result.getMessage());
    }

    @Test
    public void testDeleteToHop_Fail() {
        when(toHopDAO.findById(1)).thenReturn(testToHop);
        when(toHopDAO.delete(testToHop)).thenReturn(false);

        BUSResult<ToHop> result = toHopBUS.deleteToHop(testToHop);
        assertEquals("Xóa tổ hợp thất bại!", result.getMessage());
    }
}
