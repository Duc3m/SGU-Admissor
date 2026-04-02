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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author Admin
 */
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

    // ==========================================
    // TESTS FOR getAllUser
    // ==========================================
    @Test
    public void testGetAllUser() {
        when(userDAO.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> result = userBUS.getAllUser();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("duc3m", result.get(0).getUsername());
        verify(userDAO, times(1)).findAll();
    }

    // ==========================================
    // TESTS FOR getUserById
    // ==========================================
    @Test
    public void testGetUserById_NullId() {
        User result = userBUS.getUserById(null);
        assertNull(result, "Truyền ID null thì phải trả về null");
    }

    @Test
    public void testGetUserById_NegativeOrZeroId() {
        User result = userBUS.getUserById(0);
        assertNull(result, "Truyền ID <= 0 thì phải trả về null");
    }

    @Test
    public void testGetUserById_ValidId() {
        when(userDAO.findById(1)).thenReturn(testUser);

        User result = userBUS.getUserById(1);

        assertNotNull(result);
        assertEquals("duc3m", result.getUsername());
    }

    // ==========================================
    // TESTS FOR getUsersByRoleId
    // ==========================================
    @Test
    public void testGetUsersByRoleId_NullId() {
        List<User> result = userBUS.getUsersByRoleId(null);
        assertNull(result);
    }

    @Test
    public void testGetUsersByRoleId_NegativeOrZeroId() {
        List<User> result = userBUS.getUsersByRoleId(-1);
        assertNull(result);
    }

    @Test
    public void testGetUsersByRoleId_ValidId() {
        when(userDAO.findByRoleId(1)).thenReturn(Arrays.asList(testUser));

        List<User> result = userBUS.getUsersByRoleId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==========================================
    // TESTS FOR addUser
    // ==========================================
    @Test
    public void testAddUser_NullOrEmptyUsername() {
        User emptyUser = new User();
        emptyUser.setUsername("   ");

        BUSResult<User> result = userBUS.addUser(emptyUser);

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
    public void testAddUser_NullOrEmptyPassword() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        
        User noPasswordUser = new User();
        noPasswordUser.setUsername("duc3m");
        noPasswordUser.setPassword("");

        BUSResult<User> result = userBUS.addUser(noPasswordUser);

        assertEquals("Password không được để trống!", result.getMessage());
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    public void testAddUser_InsertSuccess() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        when(userDAO.insert(testUser)).thenReturn(true);

        BUSResult<User> result = userBUS.addUser(testUser);

        assertEquals("Thêm user mới thành công!", result.getMessage());
    }

    @Test
    public void testAddUser_InsertFail() {
        when(userDAO.findByUsername("duc3m")).thenReturn(null);
        when(userDAO.insert(testUser)).thenReturn(false);

        BUSResult<User> result = userBUS.addUser(testUser);

        assertEquals("Thêm user thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR updateUser
    // ==========================================
    @Test
    public void testUpdateUser_NullUserOrInvalidId() {
        User invalidUser = new User();
        invalidUser.setId(0);

        BUSResult<User> result = userBUS.updateUser(invalidUser);

        assertEquals("ID user không hợp lê!", result.getMessage());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userDAO.findById(1)).thenReturn(null);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Không tìm thấy user này trong hệ thống!", result.getMessage());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setRole(new Role());

        when(userDAO.findById(1)).thenReturn(existingUser);
        when(userDAO.update(existingUser)).thenReturn(true);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Cập nhật user thành công!", result.getMessage());
        assertEquals(testRole, existingUser.getRole()); // Đảm bảo role đã được set lại
    }

    @Test
    public void testUpdateUser_Fail() {
        User existingUser = new User();
        existingUser.setId(1);

        when(userDAO.findById(1)).thenReturn(existingUser);
        when(userDAO.update(existingUser)).thenReturn(false);

        BUSResult<User> result = userBUS.updateUser(testUser);

        assertEquals("Cập nhật user thất bại!", result.getMessage());
    }

    // ==========================================
    // TESTS FOR deleteUser
    // ==========================================
    @Test
    public void testDeleteUser_NullUserOrInvalidId() {
        User invalidUser = new User();
        invalidUser.setId(-1);

        BUSResult result = userBUS.deleteUser(invalidUser);

        assertEquals("ID user không hợp lê!", result.getMessage());
        verify(userDAO, never()).delete(any(User.class));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
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