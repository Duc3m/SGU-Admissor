/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.BangQuyDoiDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.BangQuyDoi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Duc3m
 */
@ExtendWith(MockitoExtension.class)
public class BangQuyDoiBUSTest {

    // QUY TẮC 4: Mock DAO, không đụng vào Database thật
    @Mock
    private BangQuyDoiDAO bangQuyDoiDAO;

    private BangQuyDoiBUS bangQuyDoiBUS;

    @BeforeEach
    void setUp() {
        // QUY TẮC 4: Khởi tạo qua Constructor và truyền Mock DAO vào (Không dùng @InjectMocks)
        bangQuyDoiBUS = new BangQuyDoiBUS(bangQuyDoiDAO);
    }

    // =========================================================
    // TEST CASES FOR getAllBangQuyDoi()
    // =========================================================
    @Test
    void getAllBangQuyDoi_ShouldReturnSuccessWithData() {
        // Arrange
        List<BangQuyDoi> mockList = Arrays.asList(new BangQuyDoi(), new BangQuyDoi());
        when(bangQuyDoiDAO.findAll()).thenReturn(mockList);

        // Act
        BUSResult<List<BangQuyDoi>> result = bangQuyDoiBUS.getAllBangQuyDoi();

        // Assert
        assertNotNull(result, "BUSResult không được trả về null");
        assertEquals("Lấy toàn bộ BangQuyDoi thành công!", result.getMessage(), "Message không khớp khi lấy danh sách thành công");
        assertEquals(mockList, result.getData(), "Data trả về phải khớp với dữ liệu từ DAO");
        verify(bangQuyDoiDAO, times(1)).findAll(); // Đảm bảo DAO được gọi đúng 1 lần
    }

    // =========================================================
    // TEST CASES FOR getBangQuyDoiByID(Integer id)
    // =========================================================
    @Test
    void getBangQuyDoiByID_WhenIdIsNull_ShouldReturnError() {
        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.getBangQuyDoiByID(null);

        // Assert
        assertEquals("ID BangQuyDoi không hợp lệ", result.getMessage(), "Phải báo lỗi khi ID là null");
        verify(bangQuyDoiDAO, never()).findById(anyInt()); // DAO không được phép gọi
    }

    @Test
    void getBangQuyDoiByID_WhenIdIsNegativeOrZero_ShouldReturnError() {
        // Act
        BUSResult<BangQuyDoi> resultZero = bangQuyDoiBUS.getBangQuyDoiByID(0);
        BUSResult<BangQuyDoi> resultNegative = bangQuyDoiBUS.getBangQuyDoiByID(-5);

        // Assert
        assertEquals("ID BangQuyDoi không hợp lệ", resultZero.getMessage(), "Phải báo lỗi khi ID <= 0");
        assertEquals("ID BangQuyDoi không hợp lệ", resultNegative.getMessage(), "Phải báo lỗi khi ID <= 0");
        verify(bangQuyDoiDAO, never()).findById(anyInt());
    }

    @Test
    void getBangQuyDoiByID_WhenIdIsValid_ShouldReturnSuccess() {
        // Arrange
        Integer validId = 1;
        BangQuyDoi mockData = new BangQuyDoi();
        when(bangQuyDoiDAO.findById(validId)).thenReturn(mockData);

        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.getBangQuyDoiByID(validId);

        // Assert
        assertEquals("Lấy BangQuyDoi thành công", result.getMessage(), "Message không khớp khi ID hợp lệ");
        assertEquals(mockData, result.getData(), "Dữ liệu trả về không khớp với Mock Object");
        verify(bangQuyDoiDAO, times(1)).findById(validId);
    }

    // =========================================================
    // TEST CASES FOR addBangQuyDoi(BangQuyDoi bangQuyDoi)
    // =========================================================
    @Test
    void addBangQuyDoi_WhenPhuongThucIsNull_ShouldReturnError() {
        // Arrange
        BangQuyDoi invalidData = new BangQuyDoi();
        invalidData.setPhuongThuc(null);

        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(invalidData);

        // Assert
        assertEquals("Phương thức không được trống", result.getMessage(), "Phải báo lỗi khi phương thức null");
        verify(bangQuyDoiDAO, never()).insert(any(BangQuyDoi.class));
    }

    @Test
    void addBangQuyDoi_WhenPhuongThucIsEmpty_ShouldReturnError() {
        // Arrange
        BangQuyDoi invalidData = new BangQuyDoi();
        invalidData.setPhuongThuc("   ");

        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(invalidData);

        // Assert
        assertEquals("Phương thức không được trống", result.getMessage(), "Phải báo lỗi khi phương thức chỉ chứa khoảng trắng");
        verify(bangQuyDoiDAO, never()).insert(any(BangQuyDoi.class));
    }

    @Test
    void addBangQuyDoi_WhenInsertSucceeds_ShouldReturnSuccess() {
        // Arrange
        BangQuyDoi validData = new BangQuyDoi();
        validData.setPhuongThuc("Xet Tuyen Diem Thi");
        when(bangQuyDoiDAO.insert(validData)).thenReturn(true);

        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(validData);

        // Assert
        assertEquals("Thêm BangQuyDoi thành công!", result.getMessage(), "Message không khớp khi insert thành công");
        verify(bangQuyDoiDAO, times(1)).insert(validData);
    }

    @Test
    void addBangQuyDoi_WhenInsertFails_ShouldReturnError() {
        // Arrange
        BangQuyDoi validData = new BangQuyDoi();
        validData.setPhuongThuc("Xet Tuyen Học Bạ");
        when(bangQuyDoiDAO.insert(validData)).thenReturn(false);

        // Act
        BUSResult<BangQuyDoi> result = bangQuyDoiBUS.addBangQuyDoi(validData);

        // Assert
        assertEquals("Lỗi gì đó ở phương thức addBangQuyDoi()", result.getMessage(), "Message báo lỗi không khớp khi DAO insert thất bại");
        verify(bangQuyDoiDAO, times(1)).insert(validData);
    }

    // =========================================================
    // TEST CASES FOR deleteBangQuyDoi(BangQuyDoi bangQuyDoi)
    // =========================================================
    @Test
    void deleteBangQuyDoi_WhenInputIsNull_ShouldReturnError() {
        // Act
        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(null);

        // Assert
        assertEquals("Không tìm thấy BangQuyDoi", result.getMessage(), "Phải báo lỗi khi truyền vào đối tượng null");
        verify(bangQuyDoiDAO, never()).delete(any());
    }

    @Test
    void deleteBangQuyDoi_WhenEntityNotFoundInDB_ShouldReturnError() {
        // Arrange
        BangQuyDoi nonExistentData = new BangQuyDoi();
        // Giả sử class có setter cho ID
        nonExistentData.setId(99); 
        when(bangQuyDoiDAO.findById(99)).thenReturn(null);

        // Act
        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(nonExistentData);

        // Assert
        assertEquals("Không tìm thấy BangQuyDoi", result.getMessage(), "Phải báo lỗi nếu ID không tồn tại trong DB");
        verify(bangQuyDoiDAO, never()).delete(any());
    }

    @Test
    void deleteBangQuyDoi_WhenDeleteSucceeds_ShouldReturnSuccess() {
        // Arrange
        BangQuyDoi validData = new BangQuyDoi();
        validData.setId(1);
        when(bangQuyDoiDAO.findById(1)).thenReturn(validData);
        when(bangQuyDoiDAO.delete(validData)).thenReturn(true);

        // Act
        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(validData);

        // Assert
        assertEquals("Xoá BangQuyDoi thành công", result.getMessage(), "Message không khớp khi delete thành công");
        verify(bangQuyDoiDAO, times(1)).delete(validData);
    }

    @Test
    void deleteBangQuyDoi_WhenDeleteFails_ShouldReturnError() {
        // Arrange
        BangQuyDoi validData = new BangQuyDoi();
        validData.setId(1);
        when(bangQuyDoiDAO.findById(1)).thenReturn(validData);
        when(bangQuyDoiDAO.delete(validData)).thenReturn(false);

        // Act
        BUSResult result = bangQuyDoiBUS.deleteBangQuyDoi(validData);

        // Assert
        assertEquals("Lỗi gì đó ở phương thức deleteBangQuyDoi()", result.getMessage(), "Message báo lỗi không khớp khi DAO delete thất bại");
        verify(bangQuyDoiDAO, times(1)).delete(validData);
    }
}