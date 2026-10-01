package com.library;

import javax.swing.*;
import com.library.cli.LibraryCLI;
import com.library.gui.MainFrame;

/**
 * Diem khoi chay chinh cua Chuong trinh (Main Entry Point).
 * Ho tro 2 che do:
 * 1. Giao dien Do hoa (Swing GUI - Mac dinh)
 * 2. Giao dien Console (CLI - Them tham so: --cli)
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && ("--cli".equalsIgnoreCase(args[0]) || "-c".equalsIgnoreCase(args[0]) || "cli".equalsIgnoreCase(args[0]))) {
            System.out.println("Dang khoi dong He thong Quan ly Thu vien o che do Console (CLI)...");
            LibraryCLI cli = new LibraryCLI();
            cli.start();
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback default
        }

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Khong the khoi dong giao dien do hoa: " + e.getMessage(), "Loi khoi dong", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
