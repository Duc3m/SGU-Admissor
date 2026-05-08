/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
import com.sgu.admissor.entity.NguyenVong;
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
public class NguyenVongBUSTest {

    @Mock
    private NguyenVongDAO nguyenVongDAO;

    @InjectMocks
    private NguyenVongBUS nguyenVongBUS;

    private NguyenVong testNv;
    private ThiSinh2025 testThiSinh;
    private Nganh testNganh;

    @BeforeEach
    public void setUp() {
        testThiSinh = new ThiSinh2025();
        testThiSinh.setCccd("0123456789");

        testNganh = new Nganh();
        testNganh.setMaNganh("7480201");

        testNv = new NguyenVong();
        testNv.setId(1);
        testNv.setThiSinh(testThiSinh);
        testNv.setNganh(testNganh);
        testNv.setPhuongThuc("100");
        testNv.setNvKey("0123456789_7480201_100");
    }

    @Test
    public void testGetAllNguyenVong() {
        when(nguyenVongDAO.findAll()).thenReturn(Arrays.asList(testNv));

        BUSResult<List<NguyenVong>> result = nguyenVongBUS.getAllNguyenVong();

        assertEquals("Lấy toàn bộ nguyện vọng thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetNguyenVongById_Invalid() {
        assertEquals("ID Nguyên vọng không hợp lệ!", nguyenVongBUS.getNguyenVongById(null).getMessage());
        assertEquals("ID Nguyên vọng không hợp lệ!", nguyenVongBUS.getNguyenVongById(0).getMessage());
    }

    @Test
    public void testGetNguyenVongsByCccd_Invalid() {
        assertEquals("CCCD không hợp lệ!", nguyenVongBUS.getNguyenVongsByCccd(null).getMessage());
        assertEquals("CCCD không hợp lệ!", nguyenVongBUS.getNguyenVongsByCccd(" ").getMessage());
    }

    @Test
    public void testAddNguyenVong_InvalidData() {
        NguyenVong invalid = new NguyenVong();
        invalid.setThiSinh(new ThiSinh2025());
        invalid.setNganh(new Nganh());

        BUSResult<NguyenVong> result = nguyenVongBUS.addNguyenVong(invalid);

        assertEquals("CCCD không hợp lệ!", result.getMessage());
        verify(nguyenVongDAO, never()).insert(any(NguyenVong.class));
    }

    @Test
    public void testAddNguyenVong_DuplicateKey() {
        when(nguyenVongDAO.findByNvKey("0123456789_7480201_100")).thenReturn(testNv);

        BUSResult<NguyenVong> result = nguyenVongBUS.addNguyenVong(testNv);

        assertEquals("Nguyện vọng này đã tồn tại (trùng CCCD, Ngành và Phương thức)!", result.getMessage());
        verify(nguyenVongDAO, never()).insert(any(NguyenVong.class));
    }

    @Test
    public void testAddNguyenVong_Success() {
        when(nguyenVongDAO.findByNvKey(any(String.class))).thenReturn(null);
        when(nguyenVongDAO.insert(testNv)).thenReturn(true);

        BUSResult<NguyenVong> result = nguyenVongBUS.addNguyenVong(testNv);

        assertEquals("Thêm nguyên vọng mới thành công!", result.getMessage());
    }

    @Test
    public void testAddNguyenVong_Fail() {
        when(nguyenVongDAO.findByNvKey(any(String.class))).thenReturn(null);
        when(nguyenVongDAO.insert(testNv)).thenReturn(false);

        BUSResult<NguyenVong> result = nguyenVongBUS.addNguyenVong(testNv);

        assertEquals("Thêm nguyên vọng thất bại!", result.getMessage());
    }

    @Test
    public void testUpdateNguyenVong_InvalidId() {
        NguyenVong invalid = new NguyenVong();
        invalid.setId(0);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(invalid);

        assertEquals("ID nguyện vọng không hợp lê!", result.getMessage());
    }

    @Test
    public void testUpdateNguyenVong_NotFound() {
        when(nguyenVongDAO.findById(1)).thenReturn(null);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Không tìm thấy nguyện vọng này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateNguyenVong_DuplicateKeyDifferentId() {
        NguyenVong existing = new NguyenVong();
        existing.setId(1);

        NguyenVong duplicate = new NguyenVong();
        duplicate.setId(2);

        when(nguyenVongDAO.findById(1)).thenReturn(existing);
        when(nguyenVongDAO.findByNvKey("0123456789_7480201_100")).thenReturn(duplicate);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Lỗi cập nhật: Nguyện vọng đã tồn tại!", result.getMessage());
        verify(nguyenVongDAO, never()).update(any(NguyenVong.class));
    }

    @Test
    public void testUpdateNguyenVong_Success() {
        NguyenVong existing = new NguyenVong();
        existing.setId(1);

        when(nguyenVongDAO.findById(1)).thenReturn(existing);
        when(nguyenVongDAO.findByNvKey(any(String.class))).thenReturn(null);
        when(nguyenVongDAO.update(existing)).thenReturn(true);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Cập nhật nguyện vọng thành công!", result.getMessage());
    }

    @Test
    public void testUpdateNguyenVong_Fail() {
        NguyenVong existing = new NguyenVong();
        existing.setId(1);

        when(nguyenVongDAO.findById(1)).thenReturn(existing);
        when(nguyenVongDAO.findByNvKey(any(String.class))).thenReturn(null);
        when(nguyenVongDAO.update(existing)).thenReturn(false);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Cập nhật nguyện vọng thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteNguyenVong_InvalidId() {
        NguyenVong invalid = new NguyenVong();
        invalid.setId(0);

        BUSResult result = nguyenVongBUS.deleteNguyenVong(invalid);

        assertEquals("ID nguyện vọng không hợp lê!", result.getMessage());
    }

    @Test
    public void testDeleteNguyenVong_NotFound() {
        when(nguyenVongDAO.findById(1)).thenReturn(null);

        BUSResult result = nguyenVongBUS.deleteNguyenVong(testNv);

        assertEquals("Không tìm thấy nguyện vọng này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteNguyenVong_Success() {
        when(nguyenVongDAO.findById(1)).thenReturn(testNv);
        when(nguyenVongDAO.delete(testNv)).thenReturn(true);

        BUSResult result = nguyenVongBUS.deleteNguyenVong(testNv);

        assertEquals("Xóa nguyện vọng thành công!", result.getMessage());
    }

    @Test
    public void testDeleteNguyenVong_Fail() {
        when(nguyenVongDAO.findById(1)).thenReturn(testNv);
        when(nguyenVongDAO.delete(testNv)).thenReturn(false);

        BUSResult result = nguyenVongBUS.deleteNguyenVong(testNv);

        assertEquals("Xóa nguyện vọng thất bại!", result.getMessage());
    }
}
