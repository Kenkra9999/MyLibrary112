package com.library.models;

import java.util.Scanner;
import com.library.utils.NhapLieu;

/**
 * Class Book: thong tin chung cua mot DAU SACH (ISBN, tua de, tac gia...) - Yeu cau R3.
 * Lop cha cua BookItem (ban sao vat ly cu the).
 */
public class Book {
    private String isbn;
    private String title;
    private String subject;
    private String publisher;
    private String language;
    private int numberOfPages;
    private Author[] authors;   // mang doi tuong tac gia

    public Book() {
        this.language = "Tieng Viet";
        this.authors = new Author[0];
    }

    public Book(String isbn, String title, String subject, String publisher, String language,
                int numberOfPages, Author[] authors) {
        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.authors = authors != null ? authors : new Author[0];
    }

    /** Nhap thong tin dau sach tu ban phim. Lop con BookItem ghi de (override) va goi lai super.nhap(sc). */
    public void nhap(Scanner sc) {
        this.isbn = NhapLieu.nhapChuoiBatBuoc(sc, "  ISBN: ");
        this.title = NhapLieu.nhapChuoiBatBuoc(sc, "  Tua de: ");
        String tacGia = NhapLieu.nhapChuoiBatBuoc(sc, "  Tac gia (nhieu tac gia cach nhau dau ;): ");
        this.authors = taoMangTacGia(tacGia);
        this.subject = NhapLieu.nhapChuoiBatBuoc(sc, "  The loai: ");
        this.publisher = NhapLieu.nhapChuoiBatBuoc(sc, "  Nha xuat ban: ");
        String ngonNgu = NhapLieu.nhapChuoi(sc, "  Ngon ngu (Enter = Tieng Viet): ").replace('|', '/');
        this.language = ngonNgu.isEmpty() ? "Tieng Viet" : ngonNgu;
        this.numberOfPages = NhapLieu.nhapSoNguyen(sc, "  So trang: ", 1, 100000);
    }

    /** Tao mang tac gia tu chuoi "Ten A; Ten B" (dung khi nhap ban phim va khi doc file). */
    public static Author[] taoMangTacGia(String chuoi) {
        if (chuoi == null || chuoi.trim().isEmpty()) return new Author[0];
        String[] ten = chuoi.split(";");
        int dem = 0;
        Author[] kq = new Author[ten.length];
        for (String t : ten) {
            if (!t.trim().isEmpty()) {
                kq[dem++] = new Author(t.trim(), "");
            }
        }
        Author[] gon = new Author[dem];
        for (int i = 0; i < dem; i++) gon[i] = kq[i];
        return gon;
    }

    /** Chuoi tac gia de HIEN THI: "A, B". */
    public String getAuthorsAsString() {
        if (authors == null || authors.length == 0) return "Chua ro tac gia";
        return noiTenTacGia(", ");
    }

    /** Chuoi tac gia de GHI FILE: "A;B". */
    public String getAuthorsAsText() {
        return noiTenTacGia(";");
    }

    private String noiTenTacGia(String dauNoi) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.length; i++) {
            if (i > 0) sb.append(dauNoi);
            sb.append(authors[i].getName());
        }
        return sb.toString();
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(int numberOfPages) { this.numberOfPages = numberOfPages; }

    public Author[] getAuthors() { return authors; }
    public void setAuthors(Author[] authors) { this.authors = authors != null ? authors : new Author[0]; }

    @Override
    public String toString() {
        return "[" + isbn + "] " + title + " - Tac gia: " + getAuthorsAsString() + " (" + subject + ")";
    }
}
