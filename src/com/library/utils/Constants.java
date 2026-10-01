package com.library.utils;

/**
 * Hang so cau hinh he thong Quan ly Thu vien (Library Management System).
 * Dap ung cac yeu cau nghiep vu R7, R8, R15.
 */
public final class Constants {
    private Constants() {}

    // R7: Moi thanh vien duoc phep muon toi da 10 cuon sach cung luc
    public static final int MAX_BOOKS_ISSUED_TO_A_USER = 10;

    // R8: Thoi gian muon toi da cho moi cuon sach la 15 ngay
    public static final int MAX_LENDING_DAYS = 15;

    // Gia han them ngay khi renew
    public static final int RENEW_EXTENSION_DAYS = 15;

    // Phi phat tra muon cho moi ngay qua han (VND)
    public static final double FINE_PER_DAY = 5000.0;

    // Ten thu vien mac dinh
    public static final String DEFAULT_LIBRARY_NAME = "Thu Vien Trung Tam CNTT - Antigravity LMS";

    // Duong dan luu tru du lieu
    public static final String DATA_FILE_PATH = "data/library_state.dat";
}
