package com.library.services;

import java.io.File;
import com.library.collections.IDanhSach;

/**
 * Doc / ghi toan bo du lieu thu vien ra cac FILE TEXT trong thu muc data/ (noi luu tru chinh cua chuong trinh).
 *
 * DA HINH: lay tat ca lop mang duoi dang IDanhSach<?>[] roi goi chung ghiFileText()/docFileText();
 * moi phan tu tu dung ham cua lop cu the (DanhSachSach, DanhSachThanhVien, ...).
 */
public final class TextFileStorageService {
    public static final String THU_MUC = "data";

    /** Ten file theo DUNG THU TU cua Library.getTatCaDanhSach(). */
    private static final String[] TEN_FILE = {
            "sach.txt", "thanhvien.txt", "thuthu.txt", "muontra.txt", "datcho.txt", "phat.txt", "thongbao.txt"
    };

    private TextFileStorageService() {
        // Lop tien ich chi gom phuong thuc static
    }

    public static String duongDan(int i) {
        return THU_MUC + "/" + TEN_FILE[i];
    }

    /** Da co du lieu luu truoc do chua (dua vao file sach.txt). */
    public static boolean coDuLieu() {
        return new File(duongDan(0)).exists();
    }

    /** Ghi tat ca danh sach ra file text. */
    public static boolean ghiTatCa(Library library) {
        IDanhSach<?>[] tatCa = library.getTatCaDanhSach();
        boolean thanhCong = true;
        for (int i = 0; i < tatCa.length; i++) {
            if (!tatCa[i].ghiFileText(duongDan(i))) {
                thanhCong = false;
            }
        }
        return thanhCong;
    }

    /** Doc tat ca file text vao cac danh sach (du lieu cu trong mang bi thay the). Tra ve tong so ban ghi. */
    public static int docTatCa(Library library) {
        IDanhSach<?>[] tatCa = library.getTatCaDanhSach();
        int tong = 0;
        for (int i = 0; i < tatCa.length; i++) {
            tong += tatCa[i].docFileText(duongDan(i));
        }
        library.dongBoBoDemMa();
        return tong;
    }

    /** In bang: ten file - so ban ghi trong mang tuong ung. */
    public static void inThongTinFile(Library library) {
        IDanhSach<?>[] tatCa = library.getTatCaDanhSach();
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-20s | %-22s | %s%n", "FILE", "LOP MANG", "SO BAN GHI");
        System.out.println("-------------------------------------------------------------");
        for (int i = 0; i < tatCa.length; i++) {
            System.out.printf("%-20s | %-22s | %d%n", duongDan(i), tatCa[i].getClass().getSimpleName(), tatCa[i].getSoLuong());
        }
        System.out.println("-------------------------------------------------------------");
    }
}
