/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

//import com.sgu.admissor.dao.SysFuntionDAO;

import com.google.inject.Inject;
import com.sgu.admissor.dao.SysFunctionDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.SysFunction;
import java.util.List;


/**
 *
 * @author Admin
 */
public class SysFunctionBUS {
    private final SysFunctionDAO sysFunctionDAO;
    
    @Inject
    public SysFunctionBUS(SysFunctionDAO sysFunctionDAO){
        this.sysFunctionDAO = sysFunctionDAO;
    }
    
    public List<SysFunction> getAllSysFunction(){
        return sysFunctionDAO.findAll();
    }
    
    public SysFunction getSysFunctionByID(Integer id){
        if (id == null || id <= 0){
            System.out.println("Function ID không hợp lê!");
            return null;
        }
        
        return sysFunctionDAO.findById(id);
    }
    
    public BUSResult<SysFunction> addSysFunction(SysFunction sysFunction) {
        if(sysFunction.getName() == null || sysFunction.getName().trim().isEmpty()){
            return BUSResult.error("Tên Function không được để trống!");
        }
        
        if(sysFunctionDAO.findByName(sysFunction.getName()) != null){
            return BUSResult.error("Tên Function '" + sysFunction.getName() + "' đã tồn tại!");
        }
        
        boolean isInserted = sysFunctionDAO.insert(sysFunction);
        
        if(isInserted){
            return BUSResult.success("Thêm Function '" + sysFunction.getName() + "' thành công!");
        } else {
            return BUSResult.error("Thêm Function thất bại!");
        }
    }
    
    public BUSResult deleteSysFunction(SysFunction sysFunction){
        if ( sysFunction == null || sysFunction.getId() == null) {
            return BUSResult.error("Không tìm thấy Function!");
        }
        
        boolean isDeleted = sysFunctionDAO.delete(sysFunction);
        
        if(isDeleted){
            return BUSResult.success("Xóa Function '" + sysFunction.getName() + "' thành công!");
        } else {
            return BUSResult.error("Xóa Function thất bại!");
        }
    }
}
