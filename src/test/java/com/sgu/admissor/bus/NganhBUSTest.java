/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.NganhDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Nganh;
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
public class NganhBUSTest {

    @Mock
    private NganhDAO nganhDAO;

    @InjectMocks
    private NganhBUS nganhBUS;

    private Nganh testNganh;

    @BeforeEach
    public void setUp() {
        testNganh = new Nganh();
        testNganh.setId(1);
        testNganh.setMaNganh("7480201");
        testNganh.setTenNganh("Cong nghe thong tin");
    }

    @Test
    public void testGetAllNganh() {
        when(nganhDAO.findAll()).thenReturn(Arrays.asList(testNganh));

        BUSResult<List<Nganh>> result = nganhBUS.getAllNganh();

        assertEquals("Lấy toàn bộ ngành thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetNganhById_InvalidId() {
        assertEquals("ID ngành không hợp lệ!", nganhBUS.getNganhById(null).getMessage());
        assertEquals("ID ngành không hợp lệ!", nganhBUS.getNganhById(0).getMessage());
    }

    @Test
    public void testGetNganhByMaNganh_Invalid() {
        assertEquals("Mã ngành không hợp lệ!", nganhBUS.getNganhByMaNganh(null).getMessage());
        assertEquals("Mã ngành không hợp lệ!", nganhBUS.getNganhByMaNganh(" ").getMessage());
    }

    @Test
    public void testGetNganhByTenNganh_Invalid() {
        assertEquals("Tên ngành không hợp lệ!", nganhBUS.getNganhByTenNganh(null).getMessage());
        assertEquals("Tên ngành không hợp lệ!", nganhBUS.getNganhByTenNganh(" ").getMessage());
    }

    @Test
    public void testGetNganhTuyenThang() {
        when(nganhDAO.findByTuyenThang(true)).thenReturn(Arrays.asList(testNganh));

        BUSResult<List<Nganh>> result = nganhBUS.getNganhTuyenThang();

        assertEquals("Lấy ngành tuyển thẳng thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testAddNganh_InvalidData() {
        BUSResult<Nganh> result = nganhBUS.addNganh(null);

        assertEquals("Thông tin ngành không hợp lệ!", result.getMessage());
        verify(nganhDAO, never()).insert(any(Nganh.class));
    }

    @Test
    public void testAddNganh_InvalidMaNganh() {
        Nganh invalid = new Nganh();
        invalid.setTenNganh("Cong nghe thong tin");

        BUSResult<Nganh> result = nganhBUS.addNganh(invalid);

        assertEquals("Mã ngành không được để trống!", result.getMessage());
    }

    @Test
    public void testAddNganh_InvalidTenNganh() {
        Nganh invalid = new Nganh();
        invalid.setMaNganh("7480201");

        BUSResult<Nganh> result = nganhBUS.addNganh(invalid);

        assertEquals("Tên ngành không được để trống!", result.getMessage());
    }

    @Test
    public void testAddNganh_Duplicate() {
        when(nganhDAO.findByMaNganh("7480201")).thenReturn(testNganh);

        BUSResult<Nganh> result = nganhBUS.addNganh(testNganh);

        assertEquals("Mã ngành đã tồn tại trong hệ thống!", result.getMessage());
    }

    @Test
    public void testAddNganh_Success() {
        when(nganhDAO.findByMaNganh("7480201")).thenReturn(null);
        when(nganhDAO.insert(testNganh)).thenReturn(true);

        BUSResult<Nganh> result = nganhBUS.addNganh(testNganh);

        assertEquals("Thêm ngành mới thành công!", result.getMessage());
    }

    @Test
    public void testAddNganh_Fail() {
        when(nganhDAO.findByMaNganh("7480201")).thenReturn(null);
        when(nganhDAO.insert(testNganh)).thenReturn(false);

        BUSResult<Nganh> result = nganhBUS.addNganh(testNganh);

        assertEquals("Thêm ngành thất bại!", result.getMessage());
    }

    @Test
    public void testUpdateNganh_InvalidId() {
        Nganh invalid = new Nganh();
        invalid.setId(0);

        BUSResult<Nganh> result = nganhBUS.updateNganh(invalid);

        assertEquals("ID ngành không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateNganh_NotFound() {
        when(nganhDAO.findById(1)).thenReturn(null);

        BUSResult<Nganh> result = nganhBUS.updateNganh(testNganh);

        assertEquals("Không tìm thấy ngành này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateNganh_Success() {
        Nganh existing = new Nganh();
        existing.setId(1);

        when(nganhDAO.findById(1)).thenReturn(existing);
        when(nganhDAO.update(existing)).thenReturn(true);

        BUSResult<Nganh> result = nganhBUS.updateNganh(testNganh);

        assertEquals("Cập nhật ngành thành công!", result.getMessage());
    }

    @Test
    public void testUpdateNganh_Fail() {
        Nganh existing = new Nganh();
        existing.setId(1);

        when(nganhDAO.findById(1)).thenReturn(existing);
        when(nganhDAO.update(existing)).thenReturn(false);

        BUSResult<Nganh> result = nganhBUS.updateNganh(testNganh);

        assertEquals("Cập nhật ngành thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteNganh_InvalidId() {
        Nganh invalid = new Nganh();
        invalid.setId(0);

        BUSResult<Nganh> result = nganhBUS.deleteNganh(invalid);

        assertEquals("ID ngành không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteNganh_NotFound() {
        when(nganhDAO.findById(1)).thenReturn(null);

        BUSResult<Nganh> result = nganhBUS.deleteNganh(testNganh);

        assertEquals("Không tìm thấy ngành này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteNganh_Success() {
        when(nganhDAO.findById(1)).thenReturn(testNganh);
        when(nganhDAO.delete(testNganh)).thenReturn(true);

        BUSResult<Nganh> result = nganhBUS.deleteNganh(testNganh);

        assertEquals("Xóa ngành thành công!", result.getMessage());
    }

    @Test
    public void testDeleteNganh_Fail() {
        when(nganhDAO.findById(1)).thenReturn(testNganh);
        when(nganhDAO.delete(testNganh)).thenReturn(false);

        BUSResult<Nganh> result = nganhBUS.deleteNganh(testNganh);

        assertEquals("Xóa ngành thất bại!", result.getMessage());
    }
}
