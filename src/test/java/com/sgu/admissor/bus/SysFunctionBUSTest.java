/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.SysFunctionDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.SysFunction;
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

/**
 *
 * @author Admin
 */
@ExtendWith(MockitoExtension.class)
public class SysFunctionBUSTest {

    @Mock
    private SysFunctionDAO sysFunctionDAO;

    @InjectMocks
    private SysFunctionBUS sysFunctionBUS;

    private SysFunction testFunction;

    @BeforeEach
    public void setUp() {
        testFunction = new SysFunction();
        testFunction.setId(1);
        testFunction.setName("ManageUsers");
    }

    // ==========================================
    // TESTS FOR getALlSysFunction
    // ==========================================
    @Test
    public void testGetAllSysFunction() {
        when(sysFunctionDAO.findAll()).thenReturn(Arrays.asList(testFunction));

        BUSResult<List<SysFunction>> result = sysFunctionBUS.getAllSysFunction();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ function thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("ManageUsers", result.getData().get(0).getName());
        verify(sysFunctionDAO, times(1)).findAll();
    }

    // ==========================================
    // TESTS FOR getSysFunctionByID
    // ==========================================
    @Test
    public void testGetSysFunctionByID_NullId() {
        BUSResult<SysFunction> result = sysFunctionBUS.getSysFunctionByID(null);
        assertFalse(result.isSuccess());
        assertEquals("Function ID không hợp lê!", result.getMessage());
    }

    @Test
    public void testGetSysFunctionByID_NegativeOrZeroId() {
        BUSResult<SysFunction> result1 = sysFunctionBUS.getSysFunctionByID(0);
        assertFalse(result1.isSuccess());
        assertEquals("Function ID không hợp lê!", result1.getMessage());

        BUSResult<SysFunction> result2 = sysFunctionBUS.getSysFunctionByID(-5);
        assertFalse(result2.isSuccess());
        assertEquals("Function ID không hợp lê!", result2.getMessage());
    }

    @Test
    public void testGetSysFunctionByID_ValidId() {
        when(sysFunctionDAO.findById(1)).thenReturn(testFunction);

        BUSResult<SysFunction> result = sysFunctionBUS.getSysFunctionByID(1);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy function thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("ManageUsers", result.getData().getName());
    }

    // ==========================================
    // TESTS FOR addSysFunction
    // ==========================================
    @Test
    public void testAddSysFunction_NullOrEmptyName() {
        SysFunction emptyFunc = new SysFunction();
        emptyFunc.setName("   "); // Tên toàn khoảng trắng

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(emptyFunc);

        assertEquals("Tên Function không được để trống!", result.getMessage());
        // Sử dụng verify never() để đảm bảo db không bị rác
        verify(sysFunctionDAO, never()).insert(any(SysFunction.class));
    }

    @Test
    public void testAddSysFunction_DuplicateName() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(testFunction);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Tên Function 'ManageUsers' đã tồn tại!", result.getMessage());
        verify(sysFunctionDAO, never()).insert(any(SysFunction.class));
    }

    @Test
    public void testAddSysFunction_InsertSuccess() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(null);
        when(sysFunctionDAO.insert(testFunction)).thenReturn(true);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Thêm Function 'ManageUsers' thành công!", result.getMessage());
    }

    @Test
    public void testAddSysFunction_InsertFail() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(null);
        when(sysFunctionDAO.insert(testFunction)).thenReturn(false);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Thêm Function thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteSysFuntion
    // ==========================================
    @Test
    public void testDeleteSysFunction_NullFunction() {
        // Lưu ý: Gọi đúng tên hàm deleteSysFuntion (thiếu chữ c) theo code của bạn
        BUSResult result = sysFunctionBUS.deleteSysFunction(null);

        assertEquals("Không tìm thấy Function!", result.getMessage());
        verify(sysFunctionDAO, never()).delete(any(SysFunction.class));
    }

    @Test
    public void testDeleteSysFunction_NullId() {
        SysFunction noIdFunc = new SysFunction();
        noIdFunc.setName("NoIdFunc");
        // Không set ID

        BUSResult result = sysFunctionBUS.deleteSysFunction(noIdFunc);

        assertEquals("Không tìm thấy Function!", result.getMessage());
        verify(sysFunctionDAO, never()).delete(any(SysFunction.class));
    }

    @Test
    public void testDeleteSysFunction_Success() {
        when(sysFunctionDAO.delete(testFunction)).thenReturn(true);

        BUSResult result = sysFunctionBUS.deleteSysFunction(testFunction);

        assertEquals("Xóa Function 'ManageUsers' thành công!", result.getMessage());
    }

    @Test
    public void testDeleteSysFunction_Fail() {
        when(sysFunctionDAO.delete(testFunction)).thenReturn(false);

        BUSResult result = sysFunctionBUS.deleteSysFunction(testFunction);

        assertEquals("Xóa Function thất bại!", result.getMessage());
    }
}
