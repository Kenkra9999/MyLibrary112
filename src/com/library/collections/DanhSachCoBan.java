package com.library.collections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * LOP TRUU TUONG DanhSachCoBan<T>: lop cha cua tat ca LOP MANG DOI TUONG.
 *
 * - Luu tru bang MANG THUAN T[] danhSach + bien dem soLuong (khong dung ArrayList / HashMap).
 * - Cai dat san cac thao tac chung: them, them k, sua, xoa, tim kiem, xuat, doc/ghi file text.
 * - Phan khac nhau giua cac danh sach (lay ma, so khop tu khoa, dinh dang 1 dong file...)
 *   duoc khai bao la HAM TRUU TUONG, lop con bat buoc cai dat.
 *   Khi lop cha goi layMa(item), Java tu goi dung ham cua lop con -> DA HINH (template method).
 *
 * @param <T> kieu doi tuong trong mang
 */
public abstract class DanhSachCoBan<T> implements IDanhSach<T> {
    protected static final int SUC_CHUA_MAC_DINH = 10;

    protected T[] danhSach;   // mang doi tuong
    protected int soLuong;    // so phan tu thuc te dang dung

    /**
     * @param mangBanDau mang rong do lop con tao (vd new BookItem[10]).
     *                   Java khong cho tao truc tiep "new T[n]" nen lop con truyen mang vao.
     */
    protected DanhSachCoBan(T[] mangBanDau) {
        this.danhSach = mangBanDau;
        this.soLuong = 0;
    }

    // ===================== CAC HAM TRUU TUONG (lop con cai dat) =====================

    /** Lay ma dinh danh cua 1 phan tu (ma vach, ma thanh vien, ma phieu...). */
    protected abstract String layMa(T item);

    /** Phan tu co chua tu khoa (da chuyen chu thuong) hay khong. */
    protected abstract boolean khopTuKhoa(T item, String tuKhoa);

    /** Dong tieu de cua bang khi xuat ra man hinh. */
    protected abstract String tieuDeBang();

    /** 1 dong cua bang ung voi 1 phan tu. */
    protected abstract String dongBang(T item);

    /** Dong chu thich dau file text (bat dau bang #). */
    protected abstract String tieuDeFile();

    /** Chuyen 1 doi tuong thanh 1 dong text, cac truong cach nhau dau |. */
    protected abstract String chuyenThanhDongText(T item);

    /** Tao doi tuong tu cac truong da tach cua 1 dong text (sai dinh dang -> nem ngoai le). */
    protected abstract T taoTuDongText(String[] truong);

    // ===================== CAC THAO TAC TREN MANG =====================

    /** Gap doi kich thuoc mang khi day (Arrays.copyOf tao mang moi cung kieu va chep du lieu cu sang). */
    private void moRongMang() {
        int sucChuaMoi = Math.max(SUC_CHUA_MAC_DINH, danhSach.length * 2);
        danhSach = Arrays.copyOf(danhSach, sucChuaMoi);
    }

    /** Tim vi tri (chi so) cua phan tu co ma tuong ung; khong co -> -1. */
    protected int timViTri(String ma) {
        if (ma == null) return -1;
        for (int i = 0; i < soLuong; i++) {
            if (layMa(danhSach[i]).equalsIgnoreCase(ma.trim())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean them(T item) {
        if (item == null || layMa(item) == null || timViTri(layMa(item)) != -1) {
            return false; // null hoac trung ma -> khong them
        }
        if (soLuong == danhSach.length) {
            moRongMang();
        }
        danhSach[soLuong] = item;
        soLuong++;
        return true;
    }

    @Override
    public int themK(T[] items) {
        if (items == null) return 0;
        int dem = 0;
        for (T item : items) {
            if (them(item)) dem++;
        }
        return dem;
    }

    @Override
    public boolean sua(String ma, T itemMoi) {
        int viTri = timViTri(ma);
        if (viTri == -1 || itemMoi == null || !layMa(itemMoi).equalsIgnoreCase(ma.trim())) {
            return false; // khong tim thay, hoac doi tuong moi bi doi ma
        }
        danhSach[viTri] = itemMoi;
        return true;
    }

    @Override
    public boolean xoa(String ma) {
        int viTri = timViTri(ma);
        if (viTri == -1) return false;
        // Don cac phan tu phia sau len 1 o
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        return true;
    }

    @Override
    public T timKiemTheoMa(String ma) {
        int viTri = timViTri(ma);
        return viTri == -1 ? null : danhSach[viTri];
    }

    @Override
    public T[] timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getDanhSach();
        }
        String q = tuKhoa.trim().toLowerCase();
        T[] ketQua = Arrays.copyOf(danhSach, soLuong); // mang tam cung kieu, du cho
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (khopTuKhoa(danhSach[i], q)) {
                ketQua[dem] = danhSach[i];
                dem++;
            }
        }
        return Arrays.copyOf(ketQua, dem); // cat mang vua dung so ket qua
    }

    @Override
    public void xuatDanhSach() {
        xuatMang(getDanhSach());
    }

    /** In mot mang bat ky (vd ket qua tim kiem) theo dinh dang bang cua danh sach nay. */
    public void xuatMang(T[] mang) {
        String tieuDe = tieuDeBang();
        String gach = "-".repeat(tieuDe.length());
        System.out.println(gach);
        System.out.println(tieuDe);
        System.out.println(gach);
        if (mang.length == 0) {
            System.out.println("  (Khong co du lieu)");
        }
        for (T item : mang) {
            System.out.println(dongBang(item));
        }
        System.out.println(gach);
        System.out.println("Tong cong: " + mang.length + " phan tu.");
    }

    @Override
    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public T[] getDanhSach() {
        return Arrays.copyOf(danhSach, soLuong);
    }

    @Override
    public void xoaTatCa() {
        for (int i = 0; i < soLuong; i++) {
            danhSach[i] = null;
        }
        soLuong = 0;
    }

    // ===================== DOC / GHI FILE TEXT =====================

    @Override
    public int docFileText(String duongDan) {
        xoaTatCa(); // doc lai tu dau -> khong bi nhan doi du lieu
        File file = new File(duongDan);
        if (!file.exists()) return 0;

        int soDong = 0;
        int soDongLoi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue; // bo dong trong va dong chu thich
                try {
                    T item = taoTuDongText(dong.split("\\|", -1));
                    if (them(item)) soDong++;
                    else soDongLoi++;  // trung ma
                } catch (RuntimeException e) {
                    soDongLoi++;       // sai dinh dang -> bo qua dong nay
                }
            }
        } catch (IOException e) {
            System.out.println("[-] Loi doc file " + duongDan + ": " + e.getMessage());
        }
        if (soDongLoi > 0) {
            System.out.println("[!] " + duongDan + ": bo qua " + soDongLoi + " dong sai dinh dang hoac trung ma.");
        }
        return soDong;
    }

    @Override
    public boolean ghiFileText(String duongDan) {
        File file = new File(duongDan);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write(tieuDeFile());
            bw.newLine();
            for (int i = 0; i < soLuong; i++) {
                bw.write(chuyenThanhDongText(danhSach[i]));
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("[-] Loi ghi file " + duongDan + ": " + e.getMessage());
            return false;
        }
    }

    // ===================== HAM HO TRO DUNG CHUNG CHO LOP CON =====================

    /** Lam sach chuoi truoc khi ghi file: null -> "", bo ky tu | va xuong dong. */
    protected static String text(String s) {
        if (s == null) return "";
        return s.replace('|', '/').replace('\n', ' ').replace('\r', ' ');
    }

    /** So thuc -> text gon (450000.0 -> "450000"). */
    protected static String soRaText(double so) {
        if (so == Math.floor(so)) return String.valueOf((long) so);
        return String.valueOf(so);
    }

    /** Cat ngan chuoi qua dai khi in bang. */
    protected static String rutGon(String s, int doDai) {
        if (s == null) return "";
        return s.length() <= doDai ? s : s.substring(0, doDai - 3) + "...";
    }
}
