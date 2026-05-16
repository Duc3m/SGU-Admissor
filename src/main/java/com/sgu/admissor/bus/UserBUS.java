/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import jakarta.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sgu.admissor.dao.UserDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.User;
import com.sgu.admissor.util.PasswordUtil;
import java.util.List;

/**
 *
 * @author Admin
 */
public class UserBUS {

    private final UserDAO userDAO;

    @Inject
    public UserBUS(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public BUSResult<List<User>> getAllUser() {
        return BUSResult.successWithData("Lấy toàn bộ user thành công!", userDAO.findAll());
    }

    public BUSResult<User> getUserById(Integer id) {
        if (id == null || id <= 0) {
            return BUSResult.error("User ID không hợp lê!");
        }
        return BUSResult.successWithData("Lấy user thành công!", userDAO.findById(id));
    }

    public BUSResult<List<User>> getUsersByRoleId(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            return BUSResult.error("Role ID không hợp lê!");
        }
        return BUSResult.successWithData("Lấy user thành công!", userDAO.findByRoleId(roleId));
    }

    @Transactional
    public BUSResult<User> addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return BUSResult.error("Username không hơp lệ!");
        }
        if (userDAO.findByUsername(user.getUsername()) != null) {
            return BUSResult.error("Username đã tồn tại!");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return BUSResult.error("Password không được để trống!");
        }
        if (userDAO.insert(user)) {
            return BUSResult.success("Thêm user mới thành công!");
        }
        return BUSResult.error("Thêm user thất bại!");
    }
    
    @Transactional
    public BUSResult<User> register(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return BUSResult.error("Tên đăng nhập không hợp lệ!");
        }
        if (user.getPassword() == null || !PasswordUtil.isValid(user.getPassword())) {
            return BUSResult.error("Mật khẩu phải có ít nhất 6 ký tự và không được chứa khoảng trắng!");
        }

        if (userDAO.findByUsername(user.getUsername()) != null) {
            return BUSResult.error("Tên đăng nhập đã tồn tại trong hệ thống!");
        }

        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user.setIsActive(true);
        if (userDAO.insert(user)) {
            return BUSResult.successWithData("Đăng ký tài khoản thành công!", user);
        }
        return BUSResult.error("Đăng ký thất bại, vui lòng thử lại sau!");
    }
    
    @Transactional
    public BUSResult<User> login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return BUSResult.error("Vui lòng nhập đầy đủ thông tin!");
        }
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return BUSResult.error("Tên đăng nhập không tồn tại!");
        }

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            return BUSResult.error("Mật khẩu không chính xác!");
        }

        if (user.getIsActive() != null && !user.getIsActive()) {
            return BUSResult.error("Tài khoản của bạn đã bị khóa!");
        }

        return BUSResult.successWithData("Đăng nhập thành công!", user);
    }
    
    @Transactional
    public BUSResult changePassword(int userId, String oldPassword, String newPassword) {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                return BUSResult.error("Tài khoản không tồn tại hoặc đã bị xóa!");
            }

            boolean isMatch = PasswordUtil.checkPassword(oldPassword, user.getPassword());
            if (!isMatch) {
                return BUSResult.error("Mật khẩu cũ không chính xác!");
            }

            String hashedNewPassword = PasswordUtil.hashPassword(newPassword);

            user.setPassword(hashedNewPassword);
            userDAO.update(user);

            return BUSResult.success("Đổi mật khẩu thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            return BUSResult.error("Lỗi hệ thống khi đổi mật khẩu: " + e.getMessage());
        }
    }

    @Transactional
    public BUSResult<User> updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            return BUSResult.error("ID user không hợp lê!");
        }
        User existing = userDAO.findById(user.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy user này trong hệ thống!");
        }
        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());
        existing.setIsActive(user.getIsActive());

        if (userDAO.update(existing)) {
            return BUSResult.success("Cập nhật user thành công!");
        }
        return BUSResult.error("Cập nhật user thất bại!");
    }

    @Transactional
    public BUSResult deleteUser(User user) {
        if (user == null || user.getId() <= 0) {
            return BUSResult.error("ID user không hợp lê!");
        }
        User existing = userDAO.findById(user.getId());
        if (existing == null) {
            return BUSResult.error("Không tìm thấy user này trong hệ thống!");
        }
        if (userDAO.delete(existing)) {
            return BUSResult.success("Xóa user thành công!");
        }
        return BUSResult.error("Xóa user thất bại!");
    }
}
