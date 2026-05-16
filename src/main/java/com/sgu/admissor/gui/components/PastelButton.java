/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author Duc3m
 */
public class PastelButton extends JButton {
    private Color themeColor;

    public PastelButton(String text, String iconPath, Color themeColor) {
        super(text);
        this.themeColor = themeColor;
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        setIconTextGap(10);

        try {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 22, 22);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            setIcon(icon);
        } catch (Exception e) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int inset = 4;
        int arc = 20;

        RoundRectangle2D rect = new RoundRectangle2D.Double(inset, inset, w - inset * 2, h - inset * 2, arc, arc);

        if (getModel().isRollover()) {
            g2.setColor(themeColor);
            g2.fill(rect);
            setForeground(Color.WHITE);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));
            }
        } else {
            Color pastelBackground = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 20);
            g2.setColor(pastelBackground);
            g2.fill(rect);
            setForeground(themeColor);
            if (getIcon() instanceof FlatSVGIcon) {
                ((FlatSVGIcon) getIcon()).setColorFilter(new FlatSVGIcon.ColorFilter(color -> themeColor));
            }
        }

        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(rect);

        g2.dispose();
        super.paintComponent(g);
    }
}