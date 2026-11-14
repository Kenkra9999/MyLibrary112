package com.library.models;

import java.time.LocalDate;
import java.util.Scanner;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.utils.DateUtils;
import com.library.utils.NhapLieu;

/**
 * Class BookItem KE THUA tu Book: mot BAN SAO vat ly cu the cua dau sach (Yeu cau R2, R4).
 * Moi ban sao co ma vach (barcode) rieng, gia, dinh dang, trang thai va vi tri ke.
 */
public class BookItem extends Book {
    private String barcode;
    private boolean isReferenceOnly;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private double price;
    private BookFormat format;
    private BookStatus status;
    private LocalDate dateOfPurchase;
    private LocalDate publicationDate;
    private Rack placedAt;

    public BookItem() {
        super();
        this.status = BookStatus.AVAILABLE;
        this.format = BookFormat.PAPERBACK;
        this.dateOfPurchase = LocalDate.now();
        this.placedAt = new Rack(1, "Khu A - Ke 1");
    }

    public BookItem(String isbn, String title, String subject, String publisher, String language,
                    int numberOfPages, Author[] authors, String barcode, boolean isReferenceOnly,
                    double price, BookFormat format, BookStatus status, LocalDate dateOfPurchase,
                    LocalDate publicationDate, Rack placedAt) {
        super(isbn, title, subject, publisher, language, numberOfPages, authors);
        this.barcode = barcode;
        this.isReferenceOnly = isReferenceOnly;
        this.price = price;
        this.format = format != null ? format : BookFormat.PAPERBACK;
        this.status = status != null ? status : BookStatus.AVAILABLE;
        this.dateOfPurchase = dateOfPurchase != null ? dateOfPurchase : LocalDate.now();
        this.publicationDate = publicationDate;
        this.placedAt = placedAt != null ? placedAt : new Rack(1, "Khu A - Ke 1");
    }

    /** Tao ban sao tu mot dau sach co san. */
    public BookItem(Book book, String barcode, double price, BookFormat format, Rack placedAt) {
        this(book.getIsbn(), book.getTitle(), book.getSubject(), book.getPublisher(), book.getLanguage(),
                book.getNumberOfPages(), book.getAuthors(), barcode, false, price, format,
                BookStatus.AVAILABLE, LocalDate.now(), null, placedAt);
    }

    /**
     * GHI DE (override) ham nhap cua lop cha Book:
     * goi super.nhap(sc) de nhap phan thong tin dau sach, sau do nhap them thong tin rieng cua ban sao.
     * Ma vach do he thong tu sinh, khong nhap tay.
     */
    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        this.price = NhapLieu.nhapSoThuc(sc, "  Gia bia (VND, chi go so, vd 120000): ", 0);
        BookFormat[] dsDinhDang = BookFormat.values();
        for (int i = 0; i < dsDinhDang.length; i++) {
            System.out.print("  " + (i + 1) + "." + dsDinhDang[i].getDisplayName() + (i < dsDinhDang.length - 1 ? " |" : "\n"));
        }
        this.format = dsDinhDang[NhapLieu.nhapSoNguyen(sc, "  Chon dinh dang: ", 1, dsDinhDang.length) - 1];
        int soKe = NhapLieu.nhapSoNguyen(sc, "  So ke sach: ", 1, 999);
        String viTri = NhapLieu.nhapChuoiBatBuoc(sc, "  Vi tri ke (vd: Khu A - Tang 2): ");
        this.placedAt = new Rack(soKe, viTri);
        this.publicationDate = NhapLieu.nhapNgay(sc, "  Ngay xuat ban dd/MM/yyyy (Enter = bo qua): ", true);
        this.isReferenceOnly = NhapLieu.nhapCoKhong(sc, "  Sach chi doc tai cho?");
        this.status = BookStatus.AVAILABLE;
        this.dateOfPurchase = LocalDate.now();
    }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public boolean isReferenceOnly() { return isReferenceOnly; }
    public void setReferenceOnly(boolean referenceOnly) { isReferenceOnly = referenceOnly; }

    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public BookFormat getFormat() { return format; }
    public void setFormat(BookFormat format) { this.format = format; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public LocalDate getDateOfPurchase() { return dateOfPurchase; }
    public void setDateOfPurchase(LocalDate dateOfPurchase) { this.dateOfPurchase = dateOfPurchase; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public Rack getPlacedAt() { return placedAt; }
    public void setPlacedAt(Rack placedAt) { this.placedAt = placedAt; }

    /** GHI DE toString() cua Book: hien thi them ma vach, trang thai, vi tri. */
    @Override
    public String toString() {
        String han = (dueDate != null) ? " | Han tra: " + DateUtils.formatDate(dueDate) : "";
        return "[" + barcode + "] " + getTitle() + " - " + getAuthorsAsString() + " (" + format + ") - "
                + status + (isReferenceOnly ? " - Chi doc tai cho" : "") + " @ " + placedAt + han;
    }
}
