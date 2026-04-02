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
import static org.mockito.Mockito.times;
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
        // Khởi tạo các object lồng nhau để tránh lỗi NullPointerException
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

    // ==========================================
    // TESTS FOR getAllNguyenVong & getById/Cccd
    // ==========================================
    @Test
    public void testGetAllNguyenVong() {
        when(nguyenVongDAO.findAll()).thenReturn(Arrays.asList(testNv));
        List<NguyenVong> result = nguyenVongBUS.getAllNguyenVong();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetNguyenVongById_InvalidId() {
        assertNull(nguyenVongBUS.getNguyenVongById(null));
        assertNull(nguyenVongBUS.getNguyenVongById(0));
    }

    @Test
    public void testGetNguyenVongsByCccd_InvalidCccd() {
        assertNull(nguyenVongBUS.getNguyenVongsByCccd(null));
        assertNull(nguyenVongBUS.getNguyenVongsByCccd("   "));
    }

    // ==========================================
    // TESTS FOR addNguyenVong
    // ==========================================
    @Test
    public void testAddNguyenVong_InvalidData() {
        NguyenVong invalidNv = new NguyenVong();
        invalidNv.setThiSinh(new ThiSinh2025()); // CCCD null
        invalidNv.setNganh(new Nganh());

        BUSResult<NguyenVong> result = nguyenVongBUS.addNguyenVong(invalidNv);

        assertEquals("CCCD không hợp lệ!", result.getMessage());
        verify(nguyenVongDAO, never()).insert(any(NguyenVong.class));
    }

    @Test
    public void testAddNguyenVong_DuplicateKey() {
        // Giả lập DB đã tồn tại nvKey này
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

    // ==========================================
    // TESTS FOR updateNguyenVong
    // ==========================================
    @Test
    public void testUpdateNguyenVong_InvalidId() {
        NguyenVong invalidNv = new NguyenVong();
        invalidNv.setId(0);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(invalidNv);
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
        NguyenVong existingNv = new NguyenVong();
        existingNv.setId(1);

        NguyenVong duplicateNv = new NguyenVong();
        duplicateNv.setId(2); // Một nguyện vọng khác trong DB đang chiếm giữ Key này

        when(nguyenVongDAO.findById(1)).thenReturn(existingNv);
        when(nguyenVongDAO.findByNvKey("0123456789_7480201_100")).thenReturn(duplicateNv);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Lỗi cập nhật: Nguyện vọng đã tồn tại!", result.getMessage());
        verify(nguyenVongDAO, never()).update(any(NguyenVong.class));
    }

    @Test
    public void testUpdateNguyenVong_Success() {
        NguyenVong existingNv = new NguyenVong();
        existingNv.setId(1);

        when(nguyenVongDAO.findById(1)).thenReturn(existingNv);
        // Trả về null nghĩa là Key mới không bị ai chiếm
        when(nguyenVongDAO.findByNvKey(any(String.class))).thenReturn(null); 
        when(nguyenVongDAO.update(existingNv)).thenReturn(true);

        BUSResult<NguyenVong> result = nguyenVongBUS.updateNguyenVong(testNv);

        assertEquals("Cập nhật nguyện vọng thành công!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteNguyenVong
    // ==========================================
    @Test
    public void testDeleteNguyenVong_Success() {
        when(nguyenVongDAO.findById(1)).thenReturn(testNv);
        when(nguyenVongDAO.delete(testNv)).thenReturn(true);

        BUSResult result = nguyenVongBUS.deleteNguyenVong(testNv);

        assertEquals("Xóa nguyện vọng thành công!", result.getMessage());
    }
}