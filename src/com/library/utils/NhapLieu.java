package com.library.utils;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Tien ich nhap lieu tu ban phim co kiem tra hop le (chi gom phuong thuc static).
 * Nhap sai kieu (vd go chu vao o so) se duoc yeu cau nhap lai thay vi lam vang chuong trinh.
 */
public final class NhapLieu {
    private NhapLieu() {}

    /** Nhap mot chuoi (co the rong). */
    public static String nhapChuoi(Scanner sc, String loiNhac) {
        System.out.print(loiNhac);
        if (!sc.hasNextLine()) {
            // Het du lieu dau vao (vd dong cua so / Ctrl+Z): du lieu da duoc luu sau moi thao tac
            System.out.println("\n[!] Ket thuc nhap lieu - thoat chuong trinh.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    /** Nhap chuoi bat buoc khong rong; ky tu '|' bi thay bang '/' vi '|' dung lam dau phan cach trong file text. */
    public static String nhapChuoiBatBuoc(Scanner sc, String loiNhac) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac).replace('|', '/');
            if (!s.isEmpty()) return s;
            System.out.println("  [!] Khong duoc de trong, vui long nhap lai.");
        }
    }

    /** Dung khi SUA: bam Enter de giu nguyen gia tri cu. */
    public static String nhapChuoiGiuNguyen(Scanner sc, String loiNhac, String giaTriCu) {
        String s = nhapChuoi(sc, loiNhac + " [" + giaTriCu + "]: ").replace('|', '/');
        return s.isEmpty() ? giaTriCu : s;
    }

    /** Nhap so nguyen trong doan [min, max]. */
    public static int nhapSoNguyen(Scanner sc, String loiNhac, int min, int max) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac);
            try {
                int so = Integer.parseInt(s);
                if (so >= min && so <= max) return so;
            } catch (NumberFormatException e) {
                // nhap lai ben duoi
            }
            System.out.println("  [!] Vui long nhap so nguyen tu " + min + " den " + max + ".");
        }
    }

    /** Dung khi SUA: nhap so nguyen, bam Enter de giu gia tri cu. */
    public static int nhapSoNguyenGiuNguyen(Scanner sc, String loiNhac, int giaTriCu, int min, int max) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac + " [" + giaTriCu + "]: ");
            if (s.isEmpty()) return giaTriCu;
            try {
                int so = Integer.parseInt(s);
                if (so >= min && so <= max) return so;
            } catch (NumberFormatException e) {
                // nhap lai ben duoi
            }
            System.out.println("  [!] Vui long nhap so nguyen tu " + min + " den " + max + ".");
        }
    }

    /** Dung khi SUA: nhap so thuc, bam Enter de giu gia tri cu. */
    public static double nhapSoThucGiuNguyen(Scanner sc, String loiNhac, double giaTriCu, double min) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac + " [" + String.format("%.0f", giaTriCu) + "]: ");
            if (s.isEmpty()) return giaTriCu;
            try {
                double so = Double.parseDouble(s);
                if (so >= min) return so;
            } catch (NumberFormatException e) {
                // nhap lai ben duoi
            }
            System.out.println("  [!] Vui long nhap so >= " + min + ".");
        }
    }

    /** Nhap so thuc >= min. */
    public static double nhapSoThuc(Scanner sc, String loiNhac, double min) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac);
            try {
                double so = Double.parseDouble(s);
                if (so >= min) return so;
            } catch (NumberFormatException e) {
                // nhap lai ben duoi
            }
            System.out.println("  [!] Vui long nhap so >= " + min + ".");
        }
    }

    /** Nhap ngay dd/MM/yyyy; cho phep bo trong (tra ve null) neu choPhepRong = true. */
    public static LocalDate nhapNgay(Scanner sc, String loiNhac, boolean choPhepRong) {
        while (true) {
            String s = nhapChuoi(sc, loiNhac);
            if (s.isEmpty() && choPhepRong) return null;
            LocalDate d = DateUtils.parseDate(s);
            if (d != null) return d;
            System.out.println("  [!] Ngay khong hop le, dinh dang dung: dd/MM/yyyy.");
        }
    }

    /** Hoi Co/Khong (y/n). */
    public static boolean nhapCoKhong(Scanner sc, String loiNhac) {
        String s = nhapChuoi(sc, loiNhac + " (y/n): ");
        return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("c");
    }
}
