/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Duc3m
 */
public class RoundButton extends JButton {
    private Color themeColor;

    public RoundButton(String iconPath, Color themeColor) {
        super();
        this.themeColor = themeColor;
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setText(null);

        try {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 44, 44);

            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            setIcon(icon);
        } catch (Exception e) {
            System.err.println("Lỗi icon: " + e.getMessage());
        }
        
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
//        setIconTextGap(8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int inset = 4; 

        Ellipse2D circle = new Ellipse2D.Double(inset, inset, w - inset * 2 - 2, h - inset * 2 - 2);

        // Vẽ Shadow
        g2.translate(3, 4);
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fill(circle);
        g2.translate(-3, -4);

        // Vẽ nền
        if (getModel().isRollover()) {
            // Khi Hover: Đổ nền màu đậm, icon trắng
            g2.setColor(themeColor);
            g2.fill(circle);
            setForeground(Color.WHITE);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
            }
        } else {
            // Trạng thái bình thường: Màu nền Pastel
            // Ép thêm Alpha = 20 (Độ đục khoảng 8%) vào màu chủ đạo
            Color pastelBackground = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 20);
            
            g2.setColor(pastelBackground);
            g2.fill(circle);
            
            setForeground(themeColor);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            }
        }

        // Vẽ viền
        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(circle);

        g2.dispose();
        super.paintComponent(g);
    }
}