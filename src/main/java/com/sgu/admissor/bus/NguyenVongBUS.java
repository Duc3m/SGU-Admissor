/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.bus;

import com.google.inject.Inject;
import com.sgu.admissor.dao.NguyenVongDAO;
import com.sgu.admissor.dto.BUSResult;
import com.sgu.admissor.entity.NguyenVong;
import java.util.List;

/**
 *
 * @author Admin
 */
public class NguyenVongBUS {
    private final NguyenVongDAO nguyenVongDAO;
    
    @Inject
    public NguyenVongBUS(NguyenVongDAO nguyenVongDAO){
        this.nguyenVongDAO = nguyenVongDAO;
    }
    
    public BUSResult<List<NguyenVong>> getAllNguyenVong(){
        return BUSResult.successWithData("Lấy toàn bộ nguyện vọng thành công!", nguyenVongDAO.findAll());
    }
    
    public BUSResult<NguyenVong> getNguyenVongById(Integer id){
        if(id == null || id <= 0){
            return BUSResult.error("ID Nguyên vọng không hợp lệ!");
        }
        
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findById(id));
    }
    
    public BUSResult<List<NguyenVong>> getNguyenVongsByCccd(String cccd){
        if(cccd == null || cccd.trim().isEmpty()){
            return BUSResult.error("CCCD không hợp lệ!");
        }
        
        return BUSResult.successWithData("Lấy nguyện vọng thành công!", nguyenVongDAO.findByCccd(cccd));
    }
    
    public BUSResult<NguyenVong> addNguyenVong(NguyenVong newNv) {
        if (newNv.getThiSinh().getCccd() == null || newNv.getThiSinh().getCccd().trim().isEmpty()){
            return BUSResult.error("CCCD không hợp lệ!");
        }
        if (newNv.getNganh().getMaNganh() == null || newNv.getNganh().getMaNganh().trim().isEmpty()){
            return BUSResult.error("Mã ngành không hợp lệ!");
        }
        if (newNv.getPhuongThuc() == null || newNv.getPhuongThuc().trim().isEmpty()){
            return BUSResult.error("Phương thức không hợp lệ!");
        }
        
        // TẠO NV_KEY: cccd_manganh_phuongthuc
        String newNvKey = newNv.getThiSinh().getCccd() + "_" + newNv.getNganh().getMaNganh() + "_" + newNv.getPhuongThuc();
        newNv.setNvKey(newNvKey);
        
        if (nguyenVongDAO.findByNvKey(newNvKey) != null){
            return BUSResult.error("Nguyện vọng này đã tồn tại (trùng CCCD, Ngành và Phương thức)!");
        }
        
        boolean isInserted = nguyenVongDAO.insert(newNv);
        
        if(isInserted){
            return BUSResult.success("Thêm nguyên vọng mới thành công!");
        } else {
            return BUSResult.error("Thêm nguyên vọng thất bại!");
        }
    }
    
    public BUSResult<NguyenVong> updateNguyenVong(NguyenVong nv){
        if (nv == null || nv.getId() <= 0){
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        
        NguyenVong existingNv = nguyenVongDAO.findById(nv.getId());
        if(existingNv == null){
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        
        // Kiểm tra các trường cấu thành nv_key không được để trống khi update
        if (nv.getThiSinh().getCccd() == null || nv.getThiSinh().getCccd().trim().isEmpty() ||
            nv.getNganh().getMaNganh() == null || nv.getNganh().getMaNganh().trim().isEmpty() ||
            nv.getPhuongThuc() == null || nv.getPhuongThuc().trim().isEmpty()) {
            return BUSResult.error("CCCD, Mã ngành và Phương thức không được để trống!");
        }

        // TẠO NV_KEY MỚI PHÒNG TRƯỜNG HỢP USER SỬA THÔNG TIN
        String newNvKey = nv.getThiSinh().getCccd().trim() + "_" + nv.getNganh().getMaNganh().trim() + "_" + nv.getPhuongThuc().trim();
        
        // Kiểm tra xem key mới có vô tình trùng với 1 nguyện vọng KHÁC hay không
        NguyenVong duplicateCheck = nguyenVongDAO.findByNvKey(newNvKey);
        if (duplicateCheck != null && !duplicateCheck.getId().equals(existingNv.getId())) {
            return BUSResult.error("Lỗi cập nhật: Nguyện vọng đã tồn tại!");
        }
        
        // Cập nhật các trường (bạn có thể bổ sung thêm các setter khác như diemThxt, thuTu... nếu cần)
        existingNv.setThiSinh(nv.getThiSinh());
        existingNv.setNganh(nv.getNganh());
        existingNv.setPhuongThuc(nv.getPhuongThuc());
        existingNv.setNvKey(newNvKey); // Cập nhật lại key mới
        // existingNv.setThuTu(nv.getThuTu()); ...
        
        boolean isUpdated = nguyenVongDAO.update(existingNv);
        if(isUpdated){
            return BUSResult.success("Cập nhật nguyện vọng thành công!");
        } else {
            return BUSResult.error("Cập nhật nguyện vọng thất bại!");
        }
    }
    
    public BUSResult deleteNguyenVong(NguyenVong nv){
        if (nv == null || nv.getId() <= 0){
            return BUSResult.error("ID nguyện vọng không hợp lê!");
        }
        
        NguyenVong nvToDelete = nguyenVongDAO.findById(nv.getId());
        if(nvToDelete == null){
            return BUSResult.error("Không tìm thấy nguyện vọng này trong hệ thống!");
        }
        
        // Mình giữ nguyên tên biến isUpdated giống trong mẫu hàm delete của bạn
        boolean isUpdated = nguyenVongDAO.delete(nvToDelete); 
        if(isUpdated){
            return BUSResult.success("Xóa nguyện vọng thành công!");
        } else {
            return BUSResult.error("Xóa nguyện vọng thất bại!");
        }
    }
}
