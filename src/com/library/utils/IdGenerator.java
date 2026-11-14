package com.library.utils;

/**
 * Bo sinh ma dinh danh tu dong (ma thanh vien, so the, ma vach sach, ma phieu...).
 *
 * Tieu chi "thuoc tinh static, phuong thuc static":
 *  - Cac bien dem la THUOC TINH STATIC: dung chung cho toan chuong trinh, khong thuoc rieng doi tuong nao.
 *  - Cac ham sinh ma la PHUONG THUC STATIC: goi truc tiep IdGenerator.taoMaThanhVien() ma khong can tao doi tuong.
 *
 * Khi khoi dong, chuong trinh doc du lieu tu file roi goi ghiNhanMaDaCo(...) cho tung ma da ton tai
 * de bien dem tang len bang ma lon nhat -> ma moi sinh ra khong bao gio trung voi ma cu.
 */
public final class IdGenerator {
    // ===== THUOC TINH STATIC (bien dem dung chung) =====
    private static int demThanhVien = 1000;   // MB-1001, MB-1002, ...
    private static int demThe = 5000;         // LC-5001, ...
    private static int demSach = 0;           // BC-0001, ...
    private static int demMuon = 0;           // LEN-0001, ...
    private static int demDatCho = 0;         // RES-0001, ...
    private static int demPhat = 0;           // FINE-0001, ...
    private static int demThongBao = 0;       // NOTIF-0001, ...

    private IdGenerator() {
        // Lop tien ich: khong cho tao doi tuong
    }

    // ===== PHUONG THUC STATIC SINH MA =====
    public static String taoMaThanhVien() {
        demThanhVien++;
        return "MB-" + demThanhVien;
    }

    public static String taoSoThe() {
        demThe++;
        return "LC-" + demThe;
    }

    public static String taoMaVachSach() {
        demSach++;
        return "BC-" + String.format("%04d", demSach);
    }

    public static String taoMaMuon() {
        demMuon++;
        return "LEN-" + String.format("%04d", demMuon);
    }

    public static String taoMaDatCho() {
        demDatCho++;
        return "RES-" + String.format("%04d", demDatCho);
    }

    public static String taoMaPhat() {
        demPhat++;
        return "FINE-" + String.format("%04d", demPhat);
    }

    public static String taoMaThongBao() {
        demThongBao++;
        return "NOTIF-" + String.format("%04d", demThongBao);
    }

    /**
     * Ghi nhan mot ma da ton tai (vd doc tu file) de cap nhat bien dem tuong ung.
     * Vi du: ghiNhanMaDaCo("MB-1003") -> demThanhVien = max(demThanhVien, 1003).
     * Ma khong dung dinh dang so (vd "BC-CC-001") se duoc bo qua.
     */
    public static void ghiNhanMaDaCo(String ma) {
        if (ma == null) return;
        if (ma.startsWith("MB-")) demThanhVien = Math.max(demThanhVien, layPhanSo(ma, "MB-"));
        else if (ma.startsWith("LC-")) demThe = Math.max(demThe, layPhanSo(ma, "LC-"));
        else if (ma.startsWith("BC-")) demSach = Math.max(demSach, layPhanSo(ma, "BC-"));
        else if (ma.startsWith("LEN-")) demMuon = Math.max(demMuon, layPhanSo(ma, "LEN-"));
        else if (ma.startsWith("RES-")) demDatCho = Math.max(demDatCho, layPhanSo(ma, "RES-"));
        else if (ma.startsWith("FINE-")) demPhat = Math.max(demPhat, layPhanSo(ma, "FINE-"));
        else if (ma.startsWith("NOTIF-")) demThongBao = Math.max(demThongBao, layPhanSo(ma, "NOTIF-"));
    }

    /** Lay phan so phia sau tien to; tra ve 0 neu khong phai so. */
    private static int layPhanSo(String ma, String tienTo) {
        try {
            return Integer.parseInt(ma.substring(tienTo.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
