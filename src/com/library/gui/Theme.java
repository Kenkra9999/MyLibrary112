package com.library.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Hệ thống Theme & Styling cho giao diện Java Swing.
 * Cung cấp bảng màu hiện đại, font chữ tinh tế và các helper component.
 */
public class Theme {
    // Primary & Accent Colors
    public static final Color PRIMARY = new Color(30, 41, 59);       // Slate 800
    public static final Color PRIMARY_HOVER = new Color(51, 65, 85); // Slate 700
    public static final Color ACCENT = new Color(59, 130, 246);      // Blue 500
    public static final Color ACCENT_DARK = new Color(29, 78, 216);  // Blue 700
    public static final Color SUCCESS = new Color(16, 185, 129);     // Emerald 500
    public static final Color WARNING = new Color(245, 158, 11);     // Amber 500
    public static final Color DANGER = new Color(239, 68, 68);       // Red 500
    public static final Color PURPLE = new Color(139, 92, 246);      // Violet 500

    // Background & Surface
    public static final Color BG_MAIN = new Color(241, 245, 249);    // Slate 100
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);    // Slate 900
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200

    // Text Colors
    public static final Color TEXT_MAIN = new Color(15, 23, 42);     // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color TEXT_LIGHT = new Color(248, 250, 252); // Slate 50

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_KPI_NUM = new Font("Segoe UI", Font.BOLD, 26);

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, ACCENT, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, Color.WHITE);
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_MAIN);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBHEADER);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_MUTED);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }
}
