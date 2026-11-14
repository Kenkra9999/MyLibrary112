package com.library.collections;

/**
 * INTERFACE IDanhSach: dinh nghia cac thao tac chuan cua mot LOP MANG DOI TUONG.
 * Moi lop DanhSachXxx (sach, thanh vien, muon tra, phat...) deu cai dat interface nay,
 * nho vay co the duyet chung trong mang IDanhSach<?>[] (vd: ghi/doc tat ca file text) -> DA HINH.
 *
 * @param <T> kieu doi tuong duoc quan ly trong mang
 */
public interface IDanhSach<T> {
    /** Them 1 phan tu (tu mo rong mang khi day). Tra ve false neu null hoac trung ma. */
    boolean them(T item);

    /** Them k phan tu; tra ve so phan tu them thanh cong. */
    int themK(T[] items);

    /** Thay phan tu co ma tuong ung bang doi tuong moi (ma giu nguyen). */
    boolean sua(String ma, T itemMoi);

    /** Xoa phan tu theo ma (don cac phan tu phia sau len). */
    boolean xoa(String ma);

    /** Tim chinh xac theo ma; khong co -> null. */
    T timKiemTheoMa(String ma);

    /** Tim gan dung theo tu khoa; tra ve mang ket qua. */
    T[] timKiem(String tuKhoa);

    /** In toan bo danh sach ra man hinh dang bang. */
    void xuatDanhSach();

    /** So phan tu thuc te dang co trong mang. */
    int getSoLuong();

    /** Ban sao mang chua dung soLuong phan tu thuc te. */
    T[] getDanhSach();

    /** Xoa het phan tu trong mang. */
    void xoaTatCa();

    /** Doc du lieu tu file text (xoa du lieu cu truoc khi doc). Tra ve so dong doc thanh cong. */
    int docFileText(String duongDan);

    /** Ghi toan bo mang ra file text. Tra ve true neu ghi thanh cong. */
    boolean ghiFileText(String duongDan);
}
