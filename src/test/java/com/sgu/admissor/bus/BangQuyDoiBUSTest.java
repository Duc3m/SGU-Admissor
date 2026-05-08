/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.BangQuyDoiDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.BangQuyDoi;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BangQuyDoiBUSTest {

    @Mock
    private BangQuyDoiDAO bangQuyDoiDAO;

    private BangQuyDoiBUS bangQuyDoiBUS;

    @BeforeEach
    void setUp() {
        bangQuyDoiBUS = new BangQuyDoiBUS(bangQuyDoiDAO);
    }

    @Test
    void getAllBangQuyDoi_ShouldReturnSuccessWithData() {
        List<BangQuyDoi> mockList = Arrays.asList(new BangQuyDoi(), new BangQuyDoi());
        when(bangQuyDoiDAO.findAll()).thenReturn(mockList);

        BUSResult<List<BangQuyDoi>> result = bangQuyDoiBUS.getAllBangQuyDoi();

        assertEquals("Lấy toàn bộ BangQuyDoi thành công!", result.getMessage());
        assertEquals(mockList, result.getData());
        verify(bangQuyDoiDAO, times(1)).findAll();
    }

    @Test
    void getBangQuyDoiByID_WhenIdIsNull_ShouldReturnError() {
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.getBangQuyDoiByID(null);

        assertEquals("ID BangQuyDoi không hợp lệ", result.getMessage());
        verify(bangQuyDoiDAO, never()).findById(anyInt());
    }

    @Test
    void getBangQuyDoiByID_WhenIdIsNegativeOrZero_ShouldReturnError() {
        BUSResult<BangQuyDoi> resultZero = bangQuyDoiBUS.getBangQuyDoiByID(0);
        BUSResult<BangQuyDoi> resultNegative = bangQuyDoiBUS.getBangQuyDoiByID(-5);

        assertEquals("ID BangQuyDoi không hợp lệ", resultZero.getMessage());
        assertEquals("ID BangQuyDoi không hợp lệ", resultNegative.getMessage());
        verify(bangQuyDoiDAO, never()).findById(anyInt());
    }

    @Test
    void getBangQuyDoiByID_WhenIdIsValid_ShouldReturnSuccess() {
        Integer validId = 1;
        BangQuyDoi mockData = new BangQuyDoi();
        when(bangQuyDoiDAO.findById(validId)).thenReturn(mockData);

        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.getBangQuyDoiByID(validId);

        assertEquals("Lấy BangQuyDoi thành công", result.getMessage());
        assertEquals(mockData, result.getData());
        verify(bangQuyDoiDAO, times(1)).findById(validId);
    }

    @Test
    void addBangQuyDoi_WhenPhuongThucIsNull_ShouldReturnError() {
        BangQuyDoi invalidData = new BangQuyDoi();
        invalidData.setPhuongThuc(null);

        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(invalidData);

        assertEquals("Phương thức không được trống", result.getMessage());
        verify(bangQuyDoiDAO, never()).insert(any(BangQuyDoi.class));
    }

    @Test
    void addBangQuyDoi_WhenPhuongThucIsEmpty_ShouldReturnError() {
        BangQuyDoi invalidData = new BangQuyDoi();
        invalidData.setPhuongThuc("   ");

        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(invalidData);

        assertEquals("Phương thức không được trống", result.getMessage());
        verify(bangQuyDoiDAO, never()).insert(any(BangQuyDoi.class));
    }

    @Test
    void addBangQuyDoi_WhenInsertSucceeds_ShouldReturnSuccess() {
        BangQuyDoi validData = new BangQuyDoi();
        validData.setPhuongThuc("Xet Tuyen Diem Thi");
        when(bangQuyDoiDAO.insert(validData)).thenReturn(true);

        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(validData);

        assertEquals("Thêm BangQuyDoi thành công!", result.getMessage());
        verify(bangQuyDoiDAO, times(1)).insert(validData);
    }

    @Test
    void addBangQuyDoi_WhenInsertFails_ShouldReturnError() {
        BangQuyDoi validData = new BangQuyDoi();
        validData.setPhuongThuc("Xet Tuyen Học Bạ");
        when(bangQuyDoiDAO.insert(validData)).thenReturn(false);

        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(validData);

        assertEquals("Lỗi gì đó ở phương thức addBangQuyDoi()", result.getMessage());
        verify(bangQuyDoiDAO, times(1)).insert(validData);
    }

    @Test
    void deleteBangQuyDoi_WhenInputIsNull_ShouldReturnError() {
        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(null);

        assertEquals("Không tìm thấy BangQuyDoi", result.getMessage());
        verify(bangQuyDoiDAO, never()).delete(any());
    }

    @Test
    void deleteBangQuyDoi_WhenEntityNotFoundInDB_ShouldReturnError() {
        BangQuyDoi nonExistentData = new BangQuyDoi();
        nonExistentData.setId(99);
        when(bangQuyDoiDAO.findById(99)).thenReturn(null);

        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(nonExistentData);

        assertEquals("Không tìm thấy BangQuyDoi", result.getMessage());
        verify(bangQuyDoiDAO, never()).delete(any());
    }

    @Test
    void deleteBangQuyDoi_WhenDeleteSucceeds_ShouldReturnSuccess() {
        BangQuyDoi validData = new BangQuyDoi();
        validData.setId(1);
        when(bangQuyDoiDAO.findById(1)).thenReturn(validData);
        when(bangQuyDoiDAO.delete(validData)).thenReturn(true);

        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(validData);

        assertEquals("Xoá BangQuyDoi thành công", result.getMessage());
        verify(bangQuyDoiDAO, times(1)).delete(validData);
    }

    @Test
    void deleteBangQuyDoi_WhenDeleteFails_ShouldReturnError() {
        BangQuyDoi validData = new BangQuyDoi();
        validData.setId(1);
        when(bangQuyDoiDAO.findById(1)).thenReturn(validData);
        when(bangQuyDoiDAO.delete(validData)).thenReturn(false);

        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(validData);

        assertEquals("Lỗi gì đó ở phương thức deleteBangQuyDoi()", result.getMessage());
        verify(bangQuyDoiDAO, times(1)).delete(validData);
    }
}
