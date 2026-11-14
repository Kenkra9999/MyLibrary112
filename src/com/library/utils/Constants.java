package com.library.utils;

/**
 * Hang so cau hinh he thong Quan ly Thu vien (public static final).
 */
public final class Constants {
    private Constants() {}

    // R7: Moi thanh vien duoc phep muon toi da 10 cuon sach cung luc
    public static final int MAX_BOOKS_ISSUED_TO_A_USER = 10;

    // R8: Thoi gian muon toi da cho moi cuon sach la 15 ngay
    public static final int MAX_LENDING_DAYS = 15;

    // R11: So ngay duoc gia han them moi lan
    public static final int RENEW_EXTENSION_DAYS = 15;

    // Use case "Pay fine" (sang tao them): Phi phat tra muon cho moi ngay qua han (VND)
    public static final double FINE_PER_DAY = 5000.0;

    // Ten va dia chi thu vien
    public static final String DEFAULT_LIBRARY_NAME = "Thu Vien Cong Nghe & Tri Thuc CNTT";
    public static final String DEFAULT_LIBRARY_STREET = "So 1 Dai Co Viet, Hai Ba Trung";
    public static final String DEFAULT_LIBRARY_CITY = "Ha Noi";

    // Mat khau mac dinh cap cho thanh vien moi
    public static final String DEFAULT_MEMBER_PASSWORD = "123456";
}
