/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.RoleDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;
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
    public void testAddRole_EmptyName() {
        Role emptyRole = new Role();
        emptyRole.setName("   ");

        BUSResult<Role> result = roleBUS.addRole(emptyRole);

        assertEquals("Tên Role không được để trống!", result.getMessage());
        verify(roleDAO, never()).insert(any(Role.class));
    }

    @Test
    public void testAddRole_DuplicateName() {
        when(roleDAO.findByName("Admin")).thenReturn(testRole);

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertEquals("Tên Role 'Admin' đã tồn tại!", result.getMessage());
        verify(roleDAO, never()).insert(any(Role.class));
    }

    @Test
    public void testAddRole_InsertSuccess() {
        when(roleDAO.findByName("Admin")).thenReturn(null);

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertEquals("Thêm role thành công", result.getMessage());
        verify(roleDAO).insert(testRole);
    }

    @Test
    public void testAddRole_InsertException() {
        when(roleDAO.findByName("Admin")).thenReturn(null);
        when(roleDAO.insert(testRole)).thenThrow(new RuntimeException("db error"));

        BUSResult<Role> result = roleBUS.addRole(testRole);

        assertEquals("Lỗi phương thức addRole()", result.getMessage());
    }
}
