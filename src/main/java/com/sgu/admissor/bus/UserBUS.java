/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.UserDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.User;
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
    
    public BUSResult<List<User>> getAllUser(){
        return BUSResult.successWithData("Lấy toàn bộ user thành công!", userDAO.findAll());
    }
    
    public BUSResult<User> getUserById(Integer id){
        if (id == null || id <= 0){
            return BUSResult.error("User ID không hợp lê!");
        }
        
        return BUSResult.successWithData("Lấy user thành công!", userDAO.findById(id));
    }
    
    public BUSResult<List<User>> getUsersByRoleId(Integer roleId){
        if (roleId == null || roleId <= 0) {
            return BUSResult.error("Role ID không hợp lê!");
        }
        
        return BUSResult.successWithData("Lấy user thành công!", userDAO.findByRoleId(roleId));
    }
    
    public BUSResult<User> addUser(User newUser){
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()){
            return BUSResult.error("Username không hơp lệ!");
        }
        
        if (userDAO.findByUsername(newUser.getUsername()) != null){
            return BUSResult.error("Username đã tồn tại!");
        }
        
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()){
            return BUSResult.error("Password không được để trống!");
        }
        
        boolean isInserted = userDAO.insert(newUser);
        
        if(isInserted){
            return BUSResult.success("Thêm user mới thành công!");
        } else {
            return BUSResult.error("Thêm user thất bại!");
        }
    }
    
    public BUSResult<User> updateUser(User user){
        if (user == null || user.getId() <= 0){
            return BUSResult.error("ID user không hợp lê!");
        }
        
        User existingUser = userDAO.findById(user.getId());
        if(existingUser == null){
            return BUSResult.error("Không tìm thấy user này trong hệ thống!");
        }
        
        existingUser.setRole(user.getRole());
        
        boolean isUpdated = userDAO.update(existingUser);
        if(isUpdated){
            return BUSResult.success("Cập nhật user thành công!");
        } else {
            return BUSResult.error("Cập nhật user thất bại!");
        }
    }
    
    public BUSResult deleteUser(User user){
        if (user == null || user.getId() <= 0){
            return BUSResult.error("ID user không hợp lê!");
        }
        
        User userToDelete = userDAO.findById(user.getId());
        if(userToDelete == null){
            return BUSResult.error("Không tìm thấy user này trong hệ thống!");
        }
        
        boolean isDeleted = userDAO.delete(userToDelete);
        if(isDeleted){
            return BUSResult.success("Xóa user thành công!");
        } else {
            return BUSResult.error("Xóa user thất bại!");
        }
    }
}
