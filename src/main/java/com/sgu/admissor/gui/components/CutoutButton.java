/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 *
 * @author Duc3m
 */
public class CutoutButton extends JButton {
    private int cornerToCut;
    private int cutRadius = 75;
    private Color borderColor;

    public CutoutButton(String text, int cornerToCut, Color borderColor) {
        super(text);
        this.cornerToCut = cornerToCut;
        this.borderColor = borderColor;
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        
        setIconTextGap(20);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int inset = 5;

        Area buttonArea = new Area(new RoundRectangle2D.Double(inset, inset, w - inset * 2, h - inset * 2, 30, 30));
        int cx = 0, cy = 0;
        switch (cornerToCut) {
            case 1: cx = 0; cy = 0; break;
            case 2: cx = w; cy = 0; break;
            case 3: cx = 0; cy = h; break;
            case 4: cx = w; cy = h; break;
        }
        Area cutCircle = new Area(new Ellipse2D.Double(cx - cutRadius, cy - cutRadius, cutRadius * 2, cutRadius * 2));
        buttonArea.subtract(cutCircle);

        // Vẽ Shadow
        g2.translate(3, 4); // Dịch bóng xéo xuống góc phải dưới (x=3, y=4)
        g2.setColor(new Color(0, 0, 0, 15)); // Màu đen với độ đục Alpha = 15 (rất mờ)
        g2.fill(buttonArea);
        g2.translate(-3, -4); // Trả trục tọa độ về lại vị trí ban đầu để vẽ nút chính

        // Vẽ nền nút lúc Hover và bình thường
        if (getModel().isRollover()) {
            // Khi di chuột vào: Nền đậm, Chữ/Icon trắng
            g2.setColor(borderColor);
            g2.fill(buttonArea);
            
            setForeground(Color.WHITE);
            
            if (getIcon() instanceof com.formdev.flatlaf.extras.FlatSVGIcon) {
                ((com.formdev.flatlaf.extras.FlatSVGIcon) getIcon())
                    .setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(color -> Color.WHITE));
            }
        } else {
            // Trạng thái bình thường: Màu nền Pastel nhẹ
            // Lấy màu viền, ép thêm thông số Alpha = 20 (Độ đục khoảng 8%)
            Color pastelBackground = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 20);
            
            g2.setColor(pastelBackground);
            g2.fill(buttonArea);
            
            setForeground(borderColor);
            
            if (getIcon() instanceof com.formdev.flatlaf.extras.FlatSVGIcon) {
                ((com.formdev.flatlaf.extras.FlatSVGIcon) getIcon())
                    .setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(color -> borderColor));
            }
        }

        // Vẽ Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(buttonArea);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        int cx = 0, cy = 0;
        switch (cornerToCut) {
            case 1: cx = 0; cy = 0; break;
            case 2: cx = getWidth(); cy = 0; break;
            case 3: cx = 0; cy = getHeight(); break;
            case 4: cx = getWidth(); cy = getHeight(); break;
        }
        if (Math.pow(x - cx, 2) + Math.pow(y - cy, 2) <= Math.pow(cutRadius, 2)) {
            return false;
        }
        return super.contains(x, y);
    }
}