/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.RoleDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;
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
 * @author Duc3m
 */

@ExtendWith(MockitoExtension.class)
public class RoleBUSTest {
    
    @Mock
    private RoleDAO roleDAO;

    @InjectMocks
    private RoleBUS roleBUS;

    private Role testRole;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("Admin");
    }

    @Test
    public void testGetAllRoles() {
        when(roleDAO.findAll()).thenReturn(Arrays.asList(testRole));

        List<Role> result = roleBUS.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Admin", result.get(0).getName());
        verify(roleDAO, times(1)).findAll();
    }

    @Test
    public void testGetRoleById_NullId() {
        Role result = roleBUS.getRoleById(null);
        assertNull(result, "Truyền ID null thì phải trả về null");
    }

    @Test
    public void testGetRoleById_NegativeId() {
        Role result = roleBUS.getRoleById(-5);
        assertNull(result, "Truyền ID âm thì phải trả về null");
    }

    @Test
    public void testGetRoleById_ValidId() {
        when(roleDAO.findById(1)).thenReturn(testRole);
        
        Role result = roleBUS.getRoleById(1);
        
        assertNotNull(result);
        assertEquals("Admin", result.getName());
    }

    @Test
    public void testAddRole_EmptyName() {
        Role emptyRole = new Role();
        emptyRole.setName("   "); // Tên toàn khoảng trắng

        BUSResult<Role> result = roleBUS.addRole(emptyRole);

        assertTrue(result.getMessage().contains("Tên Role không được để trống!"));
        verify(roleDAO, never()).insert(any(Role.class));
    }

    @Test
    public void testAddRole_DuplicateName() {
        when(roleDAO.findByName("Admin")).thenReturn(testRole);

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertTrue(result.getMessage().contains("đã tồn tại"));
        verify(roleDAO, never()).insert(any(Role.class));
    }

    @Test
    public void testAddRole_InsertSuccess() {
        when(roleDAO.findByName("Admin")).thenReturn(null);
        when(roleDAO.insert(testRole)).thenReturn(true);

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertTrue(result.getMessage().contains("thành công"));
    }

    @Test
    public void testAddRole_InsertFail() {
        when(roleDAO.findByName("Admin")).thenReturn(null);
        when(roleDAO.insert(testRole)).thenReturn(false);

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertTrue(result.getMessage().contains("Lỗi gì đó ở phương thức addRole()"));
    }

    @Test
    public void testDeleteRole_NullRole() {
        BUSResult result = roleBUS.deleteRole(null);
        assertTrue(result.getMessage().contains("Không tìm thấy role!"));
    }

    @Test
    public void testDeleteRole_NullRoleId() {
        Role roleNoId = new Role();
        BUSResult result = roleBUS.deleteRole(roleNoId);
        assertTrue(result.getMessage().contains("Không tìm thấy role!"));
    }

    @Test
    public void testDeleteRole_Success() {
        when(roleDAO.delete(testRole)).thenReturn(true);

        BUSResult result = roleBUS.deleteRole(testRole);

        assertTrue(result.getMessage().contains("thành công"));
    }

    @Test
    public void testDeleteRole_Fail() {
        when(roleDAO.delete(testRole)).thenReturn(false);

        BUSResult result = roleBUS.deleteRole(testRole);

        assertTrue(result.getMessage().contains("Lỗi gì đó ở phương thức deleteRole()"));
    }
    
}
