package com.library;

import com.library.cli.LibraryCLI;

/**
 * Diem khoi chay chuong trinh Quan ly Thu vien (Console).
 */
public class Main {
    private Main() {
        // Lop chi chua ham main, khong can tao doi tuong
    }

    public static void main(String[] args) {
        LibraryCLI cli = new LibraryCLI();
        cli.start();
    }
}
