package com.library.collections;

/**
 * Interface IDanhSach dinh nghia cac thao tac chuan cho lop mang doi tuong (Array).
 * Dap ung tieu chi cham do an OOP: Interface, Mang doi tuong, Da hinh, Doc/Ghi file text.
 *
 * @param <T> Kieu doi tuong quan ly trong mang
 */
public interface IDanhSach<T> {
    /**
     * Them 1 phan tu vao mang (tu dong tang kich thuoc mang neu day).
     */
    void them(T item);

    /**
     * Them k phan tu vao mang.
     */
    void themK(T[] items);

    /**
     * Sua thong tin phan tu theo ma dinh danh.
     */
    boolean sua(String ma, T newItem);

    /**
     * Xoa phan tu khoi mang theo ma dinh danh.
     */
    boolean xoa(String ma);

    /**
     * Tim kiem chinh xac phan tu theo ma.
     */
    T timKiemTheoMa(String ma);

    /**
     * Tim kiem gan dung theo tu khoa.
     */
    T[] timKiem(String tuKhoa);

    /**
     * Xuat toan bo danh sach doi tuong ra man hinh console.
     */
    void xuatDanhSach();

    /**
     * Lay so luong phan tu thuc te dang co trong mang.
     */
    int getSoLuong();

    /**
     * Lay mang chua cac phan tu thuc te.
     */
    T[] getDanhSach();

    /**
     * Doc du lieu tu tap tin van ban (Plain Text File).
     */
    void docFileText(String filePath);

    /**
     * Ghi du lieu ra tap tin van ban (Plain Text File).
     */
    void ghiFileText(String filePath);
}
