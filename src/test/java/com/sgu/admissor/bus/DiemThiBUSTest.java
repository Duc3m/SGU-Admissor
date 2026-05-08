/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.DiemThiDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemThi;
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
public class DiemThiBUSTest {

    @Mock
    private DiemThiDAO diemThiDAO;

    @InjectMocks
    private DiemThiBUS diemThiBUS;

    private DiemThi testDiemThi;
    private ThiSinh2025 testThiSinh;

    @BeforeEach
    public void setUp() {
        testThiSinh = new ThiSinh2025();
        testThiSinh.setCccd("0123456789");

        testDiemThi = new DiemThi();
        testDiemThi.setId(1);
        testDiemThi.setThiSinh(testThiSinh);
        testDiemThi.setPhuongThuc("100");
    }

    @Test
    public void testGetAllDiemThi() {
        when(diemThiDAO.findAll()).thenReturn(Arrays.asList(testDiemThi));

        BUSResult<List<DiemThi>> result = diemThiBUS.getAllDiemThi();

        assertEquals("Lấy toàn bộ điểm thi thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetDiemThiById_InvalidId() {
        assertEquals("ID điểm thi không hợp lệ!", diemThiBUS.getDiemThiById(null).getMessage());
        assertEquals("ID điểm thi không hợp lệ!", diemThiBUS.getDiemThiById(0).getMessage());
    }

    @Test
    public void testGetDiemThiByCccd_Invalid() {
        assertEquals("CCCD không hợp lệ!", diemThiBUS.getDiemThiByCccd(null).getMessage());
        assertEquals("CCCD không hợp lệ!", diemThiBUS.getDiemThiByCccd(" ").getMessage());
    }

    @Test
    public void testGetDiemThiByPhuongThuc_Invalid() {
        assertEquals("Phương thức không hợp lệ!", diemThiBUS.getDiemThiByPhuongThuc(null).getMessage());
        assertEquals("Phương thức không hợp lệ!", diemThiBUS.getDiemThiByPhuongThuc(" ").getMessage());
    }

    @Test
    public void testGetDiemThiByCccdAndPhuongThuc_Invalid() {
        assertEquals("CCCD không hợp lệ!", diemThiBUS.getDiemThiByCccdAndPhuongThuc(null, "100").getMessage());
        assertEquals("Phương thức không hợp lệ!", diemThiBUS.getDiemThiByCccdAndPhuongThuc("0123456789", null).getMessage());
    }

    @Test
    public void testAddDiemThi_InvalidData() {
        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(null);

        assertEquals("Thông tin điểm thi không hợp lệ!", result.getMessage());
        verify(diemThiDAO, never()).insert(any(DiemThi.class));
    }

    @Test
    public void testAddDiemThi_InvalidThiSinh() {
        DiemThi invalid = new DiemThi();
        invalid.setThiSinh(new ThiSinh2025());
        invalid.setPhuongThuc("100");

        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(invalid);

        assertEquals("Thông tin thí sinh không hợp lệ!", result.getMessage());
    }

    @Test
    public void testAddDiemThi_InvalidPhuongThuc() {
        DiemThi invalid = new DiemThi();
        invalid.setThiSinh(testThiSinh);

        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(invalid);

        assertEquals("Phương thức thi không được để trống!", result.getMessage());
    }

    @Test
    public void testAddDiemThi_Duplicate() {
        when(diemThiDAO.findByCccdAndPhuongThuc("0123456789", "100")).thenReturn(testDiemThi);

        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(testDiemThi);

        assertEquals("Điểm thi theo phương thức này đã tồn tại cho thí sinh!", result.getMessage());
    }

    @Test
    public void testAddDiemThi_Success() {
        when(diemThiDAO.findByCccdAndPhuongThuc("0123456789", "100")).thenReturn(null);
        when(diemThiDAO.insert(testDiemThi)).thenReturn(true);

        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(testDiemThi);

        assertEquals("Thêm điểm thi thành công!", result.getMessage());
    }

    @Test
    public void testAddDiemThi_Fail() {
        when(diemThiDAO.findByCccdAndPhuongThuc("0123456789", "100")).thenReturn(null);
        when(diemThiDAO.insert(testDiemThi)).thenReturn(false);

        BUSResult<DiemThi> result = diemThiBUS.addDiemThi(testDiemThi);

        assertEquals("Thêm điểm thi thất bại!", result.getMessage());
    }

    @Test
    public void testUpdateDiemThi_InvalidId() {
        DiemThi invalid = new DiemThi();
        invalid.setId(0);

        BUSResult<DiemThi> result = diemThiBUS.updateDiemThi(invalid);

        assertEquals("ID điểm thi không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateDiemThi_NotFound() {
        when(diemThiDAO.findById(1)).thenReturn(null);

        BUSResult<DiemThi> result = diemThiBUS.updateDiemThi(testDiemThi);

        assertEquals("Không tìm thấy điểm thi này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateDiemThi_Success() {
        DiemThi existing = new DiemThi();
        existing.setId(1);

        when(diemThiDAO.findById(1)).thenReturn(existing);
        when(diemThiDAO.update(existing)).thenReturn(true);

        BUSResult<DiemThi> result = diemThiBUS.updateDiemThi(testDiemThi);

        assertEquals("Cập nhật điểm thi thành công!", result.getMessage());
    }

    @Test
    public void testUpdateDiemThi_Fail() {
        DiemThi existing = new DiemThi();
        existing.setId(1);

        when(diemThiDAO.findById(1)).thenReturn(existing);
        when(diemThiDAO.update(existing)).thenReturn(false);

        BUSResult<DiemThi> result = diemThiBUS.updateDiemThi(testDiemThi);

        assertEquals("Cập nhật điểm thi thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteDiemThi_InvalidId() {
        DiemThi invalid = new DiemThi();
        invalid.setId(0);

        BUSResult<DiemThi> result = diemThiBUS.deleteDiemThi(invalid);

        assertEquals("ID điểm thi không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteDiemThi_NotFound() {
        when(diemThiDAO.findById(1)).thenReturn(null);

        BUSResult<DiemThi> result = diemThiBUS.deleteDiemThi(testDiemThi);

        assertEquals("Không tìm thấy điểm thi này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteDiemThi_Success() {
        when(diemThiDAO.findById(1)).thenReturn(testDiemThi);
        when(diemThiDAO.delete(testDiemThi)).thenReturn(true);

        BUSResult<DiemThi> result = diemThiBUS.deleteDiemThi(testDiemThi);

        assertEquals("Xóa điểm thi thành công!", result.getMessage());
    }

    @Test
    public void testDeleteDiemThi_Fail() {
        when(diemThiDAO.findById(1)).thenReturn(testDiemThi);
        when(diemThiDAO.delete(testDiemThi)).thenReturn(false);

        BUSResult<DiemThi> result = diemThiBUS.deleteDiemThi(testDiemThi);

        assertEquals("Xóa điểm thi thất bại!", result.getMessage());
    }
}
