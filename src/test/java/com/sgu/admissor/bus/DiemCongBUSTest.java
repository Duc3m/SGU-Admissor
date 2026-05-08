/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.DiemCongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.DiemCong;
import com.sgu.admissor.entity.Nganh;
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
public class DiemCongBUSTest {

    @Mock
    private DiemCongDAO diemCongDAO;

    @InjectMocks
    private DiemCongBUS diemCongBUS;

    private DiemCong testDiemCong;
    private ThiSinh2025 testThiSinh;
    private Nganh testNganh;

    @BeforeEach
    public void setUp() {
        testThiSinh = new ThiSinh2025();
        testThiSinh.setCccd("0123456789");

        testNganh = new Nganh();
        testNganh.setMaNganh("7480201");

        testDiemCong = new DiemCong();
        testDiemCong.setId(1);
        testDiemCong.setThiSinh(testThiSinh);
        testDiemCong.setNganh(testNganh);
        testDiemCong.setPhuongThuc("100");
        testDiemCong.setDcKey("0123456789_7480201_100");
    }

    @Test
    public void testGetAllDiemCong() {
        when(diemCongDAO.findAll()).thenReturn(Arrays.asList(testDiemCong));

        BUSResult<List<DiemCong>> result = diemCongBUS.getAllDiemCong();

        assertEquals("Lấy toàn bộ điểm cộng thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetDiemCongById_InvalidId() {
        assertEquals("ID điểm cộng không hợp lệ!", diemCongBUS.getDiemCongById(null).getMessage());
        assertEquals("ID điểm cộng không hợp lệ!", diemCongBUS.getDiemCongById(0).getMessage());
    }

    @Test
    public void testGetDiemCongByCccd_Invalid() {
        assertEquals("CCCD không hợp lệ!", diemCongBUS.getDiemCongByCccd(null).getMessage());
        assertEquals("CCCD không hợp lệ!", diemCongBUS.getDiemCongByCccd(" ").getMessage());
    }

    @Test
    public void testGetDiemCongByDcKey_Invalid() {
        assertEquals("DC key không hợp lệ!", diemCongBUS.getDiemCongByDcKey(null).getMessage());
        assertEquals("DC key không hợp lệ!", diemCongBUS.getDiemCongByDcKey(" ").getMessage());
    }

    @Test
    public void testGetDiemCongByMaNganh_Invalid() {
        assertEquals("Mã ngành không hợp lệ!", diemCongBUS.getDiemCongByMaNganh(null).getMessage());
        assertEquals("Mã ngành không hợp lệ!", diemCongBUS.getDiemCongByMaNganh(" ").getMessage());
    }

    @Test
    public void testAddDiemCong_InvalidData() {
        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(null);

        assertEquals("Thông tin điểm cộng không hợp lệ!", result.getMessage());
        verify(diemCongDAO, never()).insert(any(DiemCong.class));
    }

    @Test
    public void testAddDiemCong_InvalidThiSinh() {
        DiemCong invalid = new DiemCong();
        invalid.setThiSinh(new ThiSinh2025());
        invalid.setNganh(testNganh);

        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(invalid);

        assertEquals("Thông tin thí sinh không hợp lệ!", result.getMessage());
    }

    @Test
    public void testAddDiemCong_InvalidMaNganh() {
        DiemCong invalid = new DiemCong();
        invalid.setThiSinh(testThiSinh);
        invalid.setNganh(new Nganh());

        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(invalid);

        assertEquals("Mã ngành không được để trống!", result.getMessage());
    }

    @Test
    public void testAddDiemCong_DuplicateDcKey() {
        when(diemCongDAO.findByDcKey("0123456789_7480201_100")).thenReturn(testDiemCong);

        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(testDiemCong);

        assertEquals("Điểm cộng này đã tồn tại (trùng dc_key)!", result.getMessage());
    }

    @Test
    public void testAddDiemCong_Success() {
        when(diemCongDAO.insert(testDiemCong)).thenReturn(true);

        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(testDiemCong);

        assertEquals("Thêm điểm cộng thành công!", result.getMessage());
    }

    @Test
    public void testAddDiemCong_Fail() {
        when(diemCongDAO.insert(testDiemCong)).thenReturn(false);

        BUSResult<DiemCong> result = diemCongBUS.addDiemCong(testDiemCong);

        assertEquals("Thêm điểm cộng thất bại!", result.getMessage());
    }

    @Test
    public void testUpdateDiemCong_InvalidId() {
        DiemCong invalid = new DiemCong();
        invalid.setId(0);

        BUSResult<DiemCong> result = diemCongBUS.updateDiemCong(invalid);

        assertEquals("ID điểm cộng không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateDiemCong_NotFound() {
        when(diemCongDAO.findById(1)).thenReturn(null);

        BUSResult<DiemCong> result = diemCongBUS.updateDiemCong(testDiemCong);

        assertEquals("Không tìm thấy điểm cộng này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateDiemCong_Success() {
        DiemCong existing = new DiemCong();
        existing.setId(1);

        when(diemCongDAO.findById(1)).thenReturn(existing);
        when(diemCongDAO.update(existing)).thenReturn(true);

        BUSResult<DiemCong> result = diemCongBUS.updateDiemCong(testDiemCong);

        assertEquals("Cập nhật điểm cộng thành công!", result.getMessage());
    }

    @Test
    public void testUpdateDiemCong_Fail() {
        DiemCong existing = new DiemCong();
        existing.setId(1);

        when(diemCongDAO.findById(1)).thenReturn(existing);
        when(diemCongDAO.update(existing)).thenReturn(false);

        BUSResult<DiemCong> result = diemCongBUS.updateDiemCong(testDiemCong);

        assertEquals("Cập nhật điểm cộng thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteDiemCong_InvalidId() {
        DiemCong invalid = new DiemCong();
        invalid.setId(0);

        BUSResult<DiemCong> result = diemCongBUS.deleteDiemCong(invalid);

        assertEquals("ID điểm cộng không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteDiemCong_NotFound() {
        when(diemCongDAO.findById(1)).thenReturn(null);

        BUSResult<DiemCong> result = diemCongBUS.deleteDiemCong(testDiemCong);

        assertEquals("Không tìm thấy điểm cộng này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteDiemCong_Success() {
        when(diemCongDAO.findById(1)).thenReturn(testDiemCong);
        when(diemCongDAO.delete(testDiemCong)).thenReturn(true);

        BUSResult<DiemCong> result = diemCongBUS.deleteDiemCong(testDiemCong);

        assertEquals("Xóa điểm cộng thành công!", result.getMessage());
    }

    @Test
    public void testDeleteDiemCong_Fail() {
        when(diemCongDAO.findById(1)).thenReturn(testDiemCong);
        when(diemCongDAO.delete(testDiemCong)).thenReturn(false);

        BUSResult<DiemCong> result = diemCongBUS.deleteDiemCong(testDiemCong);

        assertEquals("Xóa điểm cộng thất bại!", result.getMessage());
    }
}
