package com.library.search;

import java.time.LocalDate;
import com.library.models.BookItem;

/**
 * INTERFACE Search: cac chuc nang tim kiem sach trong thu vien (Yeu cau R14).
 * Duoc cai dat boi lop DanhSachSach. Moi ham tra ve MANG ket qua.
 */
public interface Search {
    /** Tim theo tua de (gan dung, khong phan biet hoa thuong). */
    BookItem[] searchByTitle(String title);

    /** Tim theo ten tac gia. */
    BookItem[] searchByAuthor(String authorName);

    /** Tim theo the loai / chu de. */
    BookItem[] searchBySubject(String subject);

    /** Tim theo ngay xuat ban. */
    BookItem[] searchByPubDate(LocalDate pubDate);
}
