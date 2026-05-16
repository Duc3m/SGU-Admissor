/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.gui.components;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Duc3m
 */
public class AvatarIcon implements Icon {
    private String letter;
    private int size;
    private Color bgColor;
    private Color fgColor;

    public AvatarIcon(String name, int size, Color bgColor, Color fgColor) {
        this.letter = (name != null && !name.trim().isEmpty()) ? name.trim().substring(0, 1).toUpperCase() : "?";
        this.size = size;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g2.setColor(bgColor);
        g2.fill(new Ellipse2D.Double(x, y, size, size));


        g2.setColor(fgColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2 + 2)); // Chữ to bằng nửa vòng tròn
        FontMetrics fm = g2.getFontMetrics();
        

        int textWidth = fm.stringWidth(letter);
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString(letter, textX, textY);
        g2.dispose();
    }

    @Override
    public int getIconWidth() { return size; }

    @Override
    public int getIconHeight() { return size; }
}