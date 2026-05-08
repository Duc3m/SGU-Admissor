/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.BangQuyDoiDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.BangQuyDoi;
import java.util.List;

/**
 *
 * @author Duc3m
 */
public class BangQuyDoiBUS {
    
    private final BangQuyDoiDAO bangQuyDoiDAO;
    
    @Inject
    public BangQuyDoiBUS(BangQuyDoiDAO bangQuyDoiDAO) {
        this.bangQuyDoiDAO = bangQuyDoiDAO;
    }
    
    public BUSResult<List<BangQuyDoi>> getAllBangQuyDoi() {
        return BUSResult.successWithData("Lấy toàn bộ BangQuyDoi thành công!", bangQuyDoiDAO.findAll());
    }
    
    public BUSResult<BangQuyDoi> getBangQuyDoiByID(Integer id){
        if (id == null || id <= 0) {
            return BUSResult.error("ID BangQuyDoi không hợp lệ");
        }
        
        BangQuyDoi data = bangQuyDoiDAO.findById(id);
        return BUSResult.successWithData("Lấy BangQuyDoi thành công", data);
    }
    
    public BUSResult<BangQuyDoi> addBangQuyDoi(BangQuyDoi bangQuyDoi) {
        if (bangQuyDoi.getPhuongThuc() ==null || bangQuyDoi.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("Phương thức không được trống");
        }
        
        boolean isInserted = bangQuyDoiDAO.insert(bangQuyDoi);
        
        if(isInserted) {
            return BUSResult.success("Thêm BangQuyDoi thành công!");
        } else {
            return BUSResult.error("Lỗi gì đó ở phương thức addBangQuyDoi()");
        }
    }
    
    public BUSResult deleteBangQuyDoi(BangQuyDoi bangQuyDoi) {
        if(bangQuyDoi == null || bangQuyDoiDAO.findById(bangQuyDoi.getId()) == null) {
            return BUSResult.error("Không tìm thấy BangQuyDoi");
        }
        
        boolean isDeleted = bangQuyDoiDAO.delete(bangQuyDoi);
        
        if(isDeleted) {
            return BUSResult.success("Xoá BangQuyDoi thành công");
        } else {
            return BUSResult.error("Lỗi gì đó ở phương thức deleteBangQuyDoi()");
        }
    }
    
}
