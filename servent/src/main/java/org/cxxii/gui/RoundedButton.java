package org.cxxii.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private static final int ARC_WIDTH = 20;
    private static final int ARC_HEIGHT = 20;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false); // Make the button's content area transparent
        setFocusPainted(false); // Remove the focus border
        setOpaque(true); // Make the button opaque so that background color is visible
        setBackground(new Color(0xA56AE1)); // Custom background color
        setForeground(Color.WHITE);
        setBorderPainted(false); // Remove border painting to avoid additional outlines
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding inside the button
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getModel().isArmed() ? getBackground().darker() : getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);

        g2d.setColor(getForeground());
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(getText(), g2d);
        int x = (getWidth() - (int) r.getWidth()) / 2;
        int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(getText(), x, y);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the button's border
        g2d.setColor(getBackground().darker());
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
    }
}