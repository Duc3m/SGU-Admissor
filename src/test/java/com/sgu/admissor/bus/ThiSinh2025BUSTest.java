/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.ThiSinh2025DAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.ThiSinh2025;
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
public class ThiSinh2025BUSTest {

    @Mock
    private ThiSinh2025DAO thiSinh2025DAO;

    @InjectMocks
    private ThiSinh2025BUS thiSinh2025BUS;

    private ThiSinh2025 testThiSinh;

    @BeforeEach
    public void setUp() {
        testThiSinh = new ThiSinh2025();
        testThiSinh.setId(1);
        testThiSinh.setCccd("0123456789");
        testThiSinh.setSoBaoDanh("SGU2025-0001");
        testThiSinh.setHoTen("Nguyen Van A");
    }

    // ==========================================
    // TESTS FOR getAllThiSinh & getById/Cccd/SBD/HoTen
    // ==========================================
    @Test
    public void testGetAllThiSinh() {
        when(thiSinh2025DAO.findAll()).thenReturn(Arrays.asList(testThiSinh));
        BUSResult<List<ThiSinh2025>> result = thiSinh2025BUS.getAllThiSinh();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ thí sinh thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetThiSinhById_InvalidId() {
        BUSResult<ThiSinh2025> result1 = thiSinh2025BUS.getThiSinhById(null);
        assertFalse(result1.isSuccess());
        assertEquals("ID thí sinh không hợp lệ!", result1.getMessage());

        BUSResult<ThiSinh2025> result2 = thiSinh2025BUS.getThiSinhById(0);
        assertFalse(result2.isSuccess());
        assertEquals("ID thí sinh không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetThiSinhByCccd_InvalidCccd() {
        BUSResult<ThiSinh2025> result1 = thiSinh2025BUS.getThiSinhByCccd(null);
        assertFalse(result1.isSuccess());
        assertEquals("CCCD không hợp lệ!", result1.getMessage());

        BUSResult<ThiSinh2025> result2 = thiSinh2025BUS.getThiSinhByCccd("   ");
        assertFalse(result2.isSuccess());
        assertEquals("CCCD không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetThiSinhBySoBaoDanh_InvalidSoBaoDanh() {
        BUSResult<ThiSinh2025> result1 = thiSinh2025BUS.getThiSinhBySoBaoDanh(null);
        assertFalse(result1.isSuccess());
        assertEquals("Số báo danh không hợp lệ!", result1.getMessage());

        BUSResult<ThiSinh2025> result2 = thiSinh2025BUS.getThiSinhBySoBaoDanh("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Số báo danh không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetThiSinhByHoTen_InvalidHoTen() {
        BUSResult<List<ThiSinh2025>> result1 = thiSinh2025BUS.getThiSinhByHoTen(null);
        assertFalse(result1.isSuccess());
        assertEquals("Họ tên không hợp lệ!", result1.getMessage());

        BUSResult<List<ThiSinh2025>> result2 = thiSinh2025BUS.getThiSinhByHoTen("   ");
        assertFalse(result2.isSuccess());
        assertEquals("Họ tên không hợp lệ!", result2.getMessage());
    }

    // ==========================================
    // TESTS FOR addThiSinh
    // ==========================================
    @Test
    public void testAddThiSinh_NullData() {
        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(null);
        assertEquals("Thông tin thí sinh không hợp lệ!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_InvalidCccd() {
        ThiSinh2025 invalid = new ThiSinh2025();
        invalid.setSoBaoDanh("SGU2025-0001");
        invalid.setHoTen("Nguyen Van A");

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(invalid);

        assertEquals("CCCD không được để trống!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_InvalidSoBaoDanh() {
        ThiSinh2025 invalid = new ThiSinh2025();
        invalid.setCccd("0123456789");
        invalid.setHoTen("Nguyen Van A");

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(invalid);

        assertEquals("Số báo danh không được để trống!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_InvalidHoTen() {
        ThiSinh2025 invalid = new ThiSinh2025();
        invalid.setCccd("0123456789");
        invalid.setSoBaoDanh("SGU2025-0001");

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(invalid);

        assertEquals("Họ tên không được để trống!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_DuplicateCccd() {
        when(thiSinh2025DAO.findByCccd("0123456789")).thenReturn(testThiSinh);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(testThiSinh);

        assertEquals("CCCD đã tồn tại trong hệ thống!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_DuplicateSoBaoDanh() {
        when(thiSinh2025DAO.findByCccd("0123456789")).thenReturn(null);
        when(thiSinh2025DAO.findBySoBaoDanh("SGU2025-0001")).thenReturn(testThiSinh);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(testThiSinh);

        assertEquals("Số báo danh đã tồn tại trong hệ thống!", result.getMessage());
        verify(thiSinh2025DAO, never()).insert(any(ThiSinh2025.class));
    }

    @Test
    public void testAddThiSinh_Success() {
        when(thiSinh2025DAO.findByCccd("0123456789")).thenReturn(null);
        when(thiSinh2025DAO.findBySoBaoDanh("SGU2025-0001")).thenReturn(null);
        when(thiSinh2025DAO.insert(testThiSinh)).thenReturn(true);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(testThiSinh);

        assertEquals("Thêm thí sinh mới thành công!", result.getMessage());
    }

    @Test
    public void testAddThiSinh_Fail() {
        when(thiSinh2025DAO.findByCccd("0123456789")).thenReturn(null);
        when(thiSinh2025DAO.findBySoBaoDanh("SGU2025-0001")).thenReturn(null);
        when(thiSinh2025DAO.insert(testThiSinh)).thenReturn(false);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.addThiSinh(testThiSinh);

        assertEquals("Thêm thí sinh thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR updateThiSinh
    // ==========================================
    @Test
    public void testUpdateThiSinh_InvalidId() {
        ThiSinh2025 invalid = new ThiSinh2025();
        invalid.setId(0);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.updateThiSinh(invalid);
        assertEquals("ID thí sinh không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateThiSinh_NotFound() {
        when(thiSinh2025DAO.findById(1)).thenReturn(null);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.updateThiSinh(testThiSinh);
        assertEquals("Không tìm thấy thí sinh này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateThiSinh_Success() {
        ThiSinh2025 existing = new ThiSinh2025();
        existing.setId(1);

        when(thiSinh2025DAO.findById(1)).thenReturn(existing);
        when(thiSinh2025DAO.update(existing)).thenReturn(true);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.updateThiSinh(testThiSinh);
        assertEquals("Cập nhật thí sinh thành công!", result.getMessage());
    }

    @Test
    public void testUpdateThiSinh_Fail() {
        ThiSinh2025 existing = new ThiSinh2025();
        existing.setId(1);

        when(thiSinh2025DAO.findById(1)).thenReturn(existing);
        when(thiSinh2025DAO.update(existing)).thenReturn(false);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.updateThiSinh(testThiSinh);
        assertEquals("Cập nhật thí sinh thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteThiSinh
    // ==========================================
    @Test
    public void testDeleteThiSinh_InvalidId() {
        ThiSinh2025 invalid = new ThiSinh2025();
        invalid.setId(0);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.deleteThiSinh(invalid);
        assertEquals("ID thí sinh không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteThiSinh_NotFound() {
        when(thiSinh2025DAO.findById(1)).thenReturn(null);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.deleteThiSinh(testThiSinh);
        assertEquals("Không tìm thấy thí sinh này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteThiSinh_Success() {
        when(thiSinh2025DAO.findById(1)).thenReturn(testThiSinh);
        when(thiSinh2025DAO.delete(testThiSinh)).thenReturn(true);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.deleteThiSinh(testThiSinh);
        assertEquals("Xóa thí sinh thành công!", result.getMessage());
    }

    @Test
    public void testDeleteThiSinh_Fail() {
        when(thiSinh2025DAO.findById(1)).thenReturn(testThiSinh);
        when(thiSinh2025DAO.delete(testThiSinh)).thenReturn(false);

        BUSResult<ThiSinh2025> result = thiSinh2025BUS.deleteThiSinh(testThiSinh);
        assertEquals("Xóa thí sinh thất bại!", result.getMessage());
    }
}
