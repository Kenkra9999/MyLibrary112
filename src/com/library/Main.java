package com.library;

import com.library.cli.LibraryCLI;

/**
 * Diem khoi chay chinh cua Chuong trinh (Main Entry Point).
 * Chuong trinh chay tren Console (Terminal).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Dang khoi dong He thong Quan ly Thu vien (Console)...");
        LibraryCLI cli = new LibraryCLI();
        cli.start();
    }
}
