/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.RoleDetailDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.RoleDetail;
import com.sgu.admissor.entity.RoleDetailId;
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
public class RoleDetailBUSTest {

    @Mock
    private RoleDetailDAO roleDetailDAO;

    @InjectMocks
    private RoleDetailBUS roleDetailBUS;

    private RoleDetail testRoleDetail;
    private RoleDetailId testId;

    @BeforeEach
    public void setUp() {
        testId = new RoleDetailId(1, 2);

        testRoleDetail = new RoleDetail();
        testRoleDetail.setId(testId);
        testRoleDetail.setAction("READ");
    }

    // ==========================================
    // TESTS FOR getAllRoleDetail & getById/RoleId/FunctionId
    // ==========================================
    @Test
    public void testGetAllRoleDetail() {
        when(roleDetailDAO.findAll()).thenReturn(Arrays.asList(testRoleDetail));

        BUSResult<List<RoleDetail>> result = roleDetailBUS.getAllRoleDetail();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ chi tiết vai trò thành công!", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetRoleDetailById_InvalidId() {
        BUSResult<RoleDetail> result = roleDetailBUS.getRoleDetailById(null);
        assertFalse(result.isSuccess());
        assertEquals("ID chi tiết vai trò không hợp lệ!", result.getMessage());
    }

    @Test
    public void testGetRoleDetailsByRoleId_InvalidId() {
        BUSResult<List<RoleDetail>> result1 = roleDetailBUS.getRoleDetailsByRoleId(null);
        assertFalse(result1.isSuccess());
        assertEquals("Role ID không hợp lệ!", result1.getMessage());

        BUSResult<List<RoleDetail>> result2 = roleDetailBUS.getRoleDetailsByRoleId(0);
        assertFalse(result2.isSuccess());
        assertEquals("Role ID không hợp lệ!", result2.getMessage());
    }

    @Test
    public void testGetRoleDetailsByFunctionId_InvalidId() {
        BUSResult<List<RoleDetail>> result1 = roleDetailBUS.getRoleDetailsByFunctionId(null);
        assertFalse(result1.isSuccess());
        assertEquals("Function ID không hợp lệ!", result1.getMessage());

        BUSResult<List<RoleDetail>> result2 = roleDetailBUS.getRoleDetailsByFunctionId(0);
        assertFalse(result2.isSuccess());
        assertEquals("Function ID không hợp lệ!", result2.getMessage());
    }

    // ==========================================
    // TESTS FOR addRoleDetail
    // ==========================================
    @Test
    public void testAddRoleDetail_InvalidData() {
        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(null);
        assertEquals("Thông tin chi tiết vai trò không hợp lệ!", result.getMessage());
        verify(roleDetailDAO, never()).insert(any(RoleDetail.class));
    }

    @Test
    public void testAddRoleDetail_InvalidRoleId() {
        RoleDetail invalid = new RoleDetail();
        RoleDetailId id = new RoleDetailId();
        id.setRoleId(0);
        id.setFunctionId(1);
        invalid.setId(id);
        invalid.setAction("READ");

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(invalid);

        assertEquals("Role ID không hợp lệ!", result.getMessage());
        verify(roleDetailDAO, never()).insert(any(RoleDetail.class));
    }

    @Test
    public void testAddRoleDetail_InvalidFunctionId() {
        RoleDetail invalid = new RoleDetail();
        RoleDetailId id = new RoleDetailId();
        id.setRoleId(1);
        id.setFunctionId(0);
        invalid.setId(id);
        invalid.setAction("READ");

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(invalid);

        assertEquals("Function ID không hợp lệ!", result.getMessage());
        verify(roleDetailDAO, never()).insert(any(RoleDetail.class));
    }

    @Test
    public void testAddRoleDetail_InvalidAction() {
        RoleDetail invalid = new RoleDetail();
        invalid.setId(testId);
        invalid.setAction("   ");

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(invalid);

        assertEquals("Action không được để trống!", result.getMessage());
        verify(roleDetailDAO, never()).insert(any(RoleDetail.class));
    }

    @Test
    public void testAddRoleDetail_Duplicate() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(testRoleDetail);

        assertEquals("Chi tiết vai trò này đã tồn tại!", result.getMessage());
        verify(roleDetailDAO, never()).insert(any(RoleDetail.class));
    }

    @Test
    public void testAddRoleDetail_Success() {
        when(roleDetailDAO.findById(testId)).thenReturn(null);
        when(roleDetailDAO.insert(testRoleDetail)).thenReturn(true);

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(testRoleDetail);

        assertEquals("Thêm chi tiết vai trò thành công!", result.getMessage());
    }

    @Test
    public void testAddRoleDetail_Fail() {
        when(roleDetailDAO.findById(testId)).thenReturn(null);
        when(roleDetailDAO.insert(testRoleDetail)).thenReturn(false);

        BUSResult<RoleDetail> result = roleDetailBUS.addRoleDetail(testRoleDetail);

        assertEquals("Thêm chi tiết vai trò thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR updateRoleDetail
    // ==========================================
    @Test
    public void testUpdateRoleDetail_InvalidData() {
        BUSResult<RoleDetail> result = roleDetailBUS.updateRoleDetail(null);
        assertEquals("Thông tin chi tiết vai trò không hợp lệ!", result.getMessage());
    }

    @Test
    public void testUpdateRoleDetail_NotFound() {
        when(roleDetailDAO.findById(testId)).thenReturn(null);

        BUSResult<RoleDetail> result = roleDetailBUS.updateRoleDetail(testRoleDetail);
        assertEquals("Không tìm thấy chi tiết vai trò này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testUpdateRoleDetail_InvalidAction() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);

        RoleDetail invalid = new RoleDetail();
        invalid.setId(testId);
        invalid.setAction("   ");

        BUSResult<RoleDetail> result = roleDetailBUS.updateRoleDetail(invalid);
        assertEquals("Action không được để trống!", result.getMessage());
    }

    @Test
    public void testUpdateRoleDetail_Success() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);
        when(roleDetailDAO.update(testRoleDetail)).thenReturn(true);

        BUSResult<RoleDetail> result = roleDetailBUS.updateRoleDetail(testRoleDetail);
        assertEquals("Cập nhật chi tiết vai trò thành công!", result.getMessage());
    }

    @Test
    public void testUpdateRoleDetail_Fail() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);
        when(roleDetailDAO.update(testRoleDetail)).thenReturn(false);

        BUSResult<RoleDetail> result = roleDetailBUS.updateRoleDetail(testRoleDetail);
        assertEquals("Cập nhật chi tiết vai trò thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteRoleDetail
    // ==========================================
    @Test
    public void testDeleteRoleDetail_InvalidData() {
        BUSResult<RoleDetail> result = roleDetailBUS.deleteRoleDetail(null);
        assertEquals("Thông tin chi tiết vai trò không hợp lệ!", result.getMessage());
    }

    @Test
    public void testDeleteRoleDetail_NotFound() {
        when(roleDetailDAO.findById(testId)).thenReturn(null);

        BUSResult<RoleDetail> result = roleDetailBUS.deleteRoleDetail(testRoleDetail);
        assertEquals("Không tìm thấy chi tiết vai trò này trong hệ thống!", result.getMessage());
    }

    @Test
    public void testDeleteRoleDetail_Success() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);
        when(roleDetailDAO.delete(testRoleDetail)).thenReturn(true);

        BUSResult<RoleDetail> result = roleDetailBUS.deleteRoleDetail(testRoleDetail);
        assertEquals("Xóa chi tiết vai trò thành công!", result.getMessage());
    }

    @Test
    public void testDeleteRoleDetail_Fail() {
        when(roleDetailDAO.findById(testId)).thenReturn(testRoleDetail);
        when(roleDetailDAO.delete(testRoleDetail)).thenReturn(false);

        BUSResult<RoleDetail> result = roleDetailBUS.deleteRoleDetail(testRoleDetail);
        assertEquals("Xóa chi tiết vai trò thất bại!", result.getMessage());
    }
}
