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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    public void testGetAllSysFunction() {
        when(sysFunctionDAO.findAll()).thenReturn(Arrays.asList(testFunction));

        BUSResult<List<SysFunction>> result = sysFunctionBUS.getAllSysFunction();

        assertEquals("Lấy toàn bộ function thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetSysFunctionByID_InvalidId() {
        assertEquals("Function ID không hợp lê!", sysFunctionBUS.getSysFunctionByID(null).getMessage());
        assertEquals("Function ID không hợp lê!", sysFunctionBUS.getSysFunctionByID(0).getMessage());
    }

    @Test
    public void testGetSysFunctionByID_ValidId() {
        when(sysFunctionDAO.findById(1)).thenReturn(testFunction);

        BUSResult<SysFunction> result = sysFunctionBUS.getSysFunctionByID(1);

        assertEquals("Lấy function thành công!", result.getMessage());
        assertEquals("ManageUsers", result.getData().getName());
    }

    @Test
    public void testAddSysFunction_InvalidName() {
        SysFunction invalid = new SysFunction();
        invalid.setName(" ");

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(invalid);

        assertEquals("Tên Function không được để trống!", result.getMessage());
        verify(sysFunctionDAO, never()).insert(any(SysFunction.class));
    }

    @Test
    public void testAddSysFunction_Duplicate() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(testFunction);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Tên Function 'ManageUsers' đã tồn tại!", result.getMessage());
        verify(sysFunctionDAO, never()).insert(any(SysFunction.class));
    }

    @Test
    public void testAddSysFunction_Success() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(null);
        when(sysFunctionDAO.insert(testFunction)).thenReturn(true);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Thêm Function 'ManageUsers' thành công!", result.getMessage());
    }

    @Test
    public void testAddSysFunction_Fail() {
        when(sysFunctionDAO.findByName("ManageUsers")).thenReturn(null);
        when(sysFunctionDAO.insert(testFunction)).thenReturn(false);

        BUSResult<SysFunction> result = sysFunctionBUS.addSysFunction(testFunction);

        assertEquals("Thêm Function thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteSysFunction_Invalid() {
        assertEquals("Không tìm thấy Function!", sysFunctionBUS.deleteSysFunction(null).getMessage());

        SysFunction invalid = new SysFunction();
        invalid.setName("NoId");

        assertEquals("Không tìm thấy Function!", sysFunctionBUS.deleteSysFunction(invalid).getMessage());
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
