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
public class RoundStatisticButton extends JButton {
    private Color themeColor;

    public RoundStatisticButton(String iconPath, Color themeColor) {
        super();
        this.themeColor = themeColor;
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        try {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 32, 32);

            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            setIcon(icon);
        } catch (Exception e) {
            System.err.println("Lỗi icon thống kê: " + e.getMessage());
        }
        
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setIconTextGap(8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int inset = 2;

        Ellipse2D circle = new Ellipse2D.Double(inset, inset, w - inset * 2, h - inset * 2);

        if (getModel().isRollover()) {
            g2.setColor(themeColor);
            g2.fill(circle);
            setForeground(Color.WHITE);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
            }
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(circle);
            setForeground(themeColor);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            }
        }

        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(circle);

        g2.dispose();
        super.paintComponent(g);
    }
}