package com.sgu.admissor.util;

import java.awt.Frame;
import java.awt.Window;

/**
 * 
 * @author Duc3m
 */
public final class WindowUtil {

    private WindowUtil() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không thể khởi tạo thực thể!");
    }

    /**
     * Quét tìm cửa sổ (Frame) chính đang hiển thị của ứng dụng
     * @return Window cha đang mở, hoặc null nếu không tìm thấy
     */
    public static Window findMainWindow() {
        for (Frame frame : Frame.getFrames()) {
            if (frame.isVisible()) {
                return frame;
            }
        }
        return null;
    }
}