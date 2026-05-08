/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.sgu.admissor.bus;

import com.sgu.admissor.dao.UserDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.Role;
import com.sgu.admissor.entity.User;
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
public class UserBUSTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserBUS userBUS;

    private User testUser;
    private Role testRole;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("Admin");

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("duc3m");
        testUser.setPassword("123456");
        testUser.setRole(testRole);
    }

    @Test
    public void testGetAllUser() {
        when(userDAO.findAll()).thenReturn(Arrays.asList(testUser));

        BUSResult<List<User>> result = userBUS.getAllUser();

        assertTrue(result.isSuccess());
        assertEquals("Lấy toàn bộ user thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testGetUserById_InvalidId() {
        BUSResult<User> result1 = userBUS.getUserById(null);
        BUSResult<User> result2 = userBUS.getUserById(0);

        assertEquals("User ID không hợp lê!", result1.getMessage());
        assertEquals("User ID không hợp lê!", result2.getMessage());
    }

    @Test
    public void testGetUserById_Valid() {
        when(userDAO.findById(1)).thenReturn(testUser);

        BUSResult<User> result = userBUS.getUserById(1);

        assertEquals("Lấy user thành công!", result.getMessage());
        assertEquals("duc3m", result.getData().getUsername());
    }

    @Test
    public void testGetUsersByRoleId_InvalidId() {
        BUSResult<List<User>> result1 = userBUS.getUsersByRoleId(null);
        BUSResult<List<User>> result2 = userBUS.getUsersByRoleId(-1);

        assertEquals("Role ID không hợp lê!", result1.getMessage());
        assertEquals("Role ID không hợp lê!", result2.getMessage());
    }

    @Test
    public void testGetUsersByRoleId_Valid() {
        when(userDAO.findByRoleId(1)).thenReturn(Arrays.asList(testUser));

        BUSResult<List<User>> result = userBUS.getUsersByRoleId(1);

        assertEquals("Lấy user thành công!", result.getMessage());
        assertEquals(1, result.getData().size());
    }

    @Test
    public void testAddUser_InvalidUsername() {
        User invalid = new User();
        invalid.setUsername(" ");

        BUSResult<User> result = userBUS.addUser(invalid);

        assertEquals("Username không hơp lệ!", result.getMessage());
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    public void testAddUser_DuplicateUsername() {
        when(userDAO.findByUsername("duc3m")).thenReturn(testUser);

        BUSResult<User> result = userBUS.addUser(testUser);

        assertEquals("Username đã tồn tại!", result.getMessage());
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    public void testAddUser_InvalidPassword() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        User invalid = new User();
        invalid.setUsername("duc3m");
        invalid.setPassword(" ");

        BUSResult<User> result = userBUS.addUser(invalid);

        assertEquals("Password không được để trống!", result.getMessage());
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    public void testAddUser_Success() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        when(userDAO.insert(testUser)).thenReturn(true);

        BUSResult<User> result = userBUS.addUser(testUser);

        assertEquals("Thêm user mới thành công!", result.getMessage());
    }

    @Test
    public void testAddUser_Fail() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        when(userDAO.insert(testUser)).thenReturn(false);

        BUSResult<User> result = userBUS.addUser(testUser);

        assertEquals("Thêm user thất bại!", result.getMessage());
    }

    @Test
    public void testUpdateUser_Invalid() {
        User invalid = new User();
        invalid.setId(0);

        BUSResult<User> result = userBUS.updateUser(invalid);

        assertEquals("ID user không hợp lê!", result.getMessage());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        when(userDAO.findById(1)).thenReturn(null);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Không tìm thấy user này trong hệ thống!", result.getMessage());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUser_Success() {
        User existing = new User();
        existing.setId(1);

        when(userDAO.findById(1)).thenReturn(existing);
        when(userDAO.update(existing)).thenReturn(true);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Cập nhật user thành công!", result.getMessage());
    }

    @Test
    public void testUpdateUser_Fail() {
        User existing = new User();
        existing.setId(1);

        when(userDAO.findById(1)).thenReturn(existing);
        when(userDAO.update(existing)).thenReturn(false);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Cập nhật user thất bại!", result.getMessage());
    }

    @Test
    public void testDeleteUser_Invalid() {
        User invalid = new User();
        invalid.setId(0);

        BUSResult result = userBUS.deleteUser(invalid);

        assertEquals("ID user không hợp lê!", result.getMessage());
        verify(userDAO, never()).delete(any(User.class));
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userDAO.findById(1)).thenReturn(null);

        BUSResult result = userBUS.deleteUser(testUser);

        assertEquals("Không tìm thấy user này trong hệ thống!", result.getMessage());
        verify(userDAO, never()).delete(any(User.class));
    }

    @Test
    public void testDeleteUser_Success() {
        when(userDAO.findById(1)).thenReturn(testUser);
        when(userDAO.delete(testUser)).thenReturn(true);

        BUSResult result = userBUS.deleteUser(testUser);

        assertEquals("Xóa user thành công!", result.getMessage());
    }

    @Test
    public void testDeleteUser_Fail() {
        when(userDAO.findById(1)).thenReturn(testUser);
        when(userDAO.delete(testUser)).thenReturn(false);

        BUSResult result = userBUS.deleteUser(testUser);

        assertEquals("Xóa user thất bại!", result.getMessage());
    }
}
