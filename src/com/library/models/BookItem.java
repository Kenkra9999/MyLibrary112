package com.library.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.utils.DateUtils;

/**
 * Class BookItem ke thua tu Book (Ke thua / Inheritance).
 * Dai dien cho mot ban sao vat ly cu the trong thu vien (Yeu cau R2, R4).
 */
public class BookItem extends Book {
    private static final long serialVersionUID = 1L;

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
        this.publicationDate = LocalDate.now();
        this.placedAt = new Rack(1, "A1");
    }

    public BookItem(String isbn, String title, String subject, String publisher, String language,
                    int numberOfPages, List<Author> authors, String barcode, boolean isReferenceOnly,
                    double price, BookFormat format, BookStatus status, LocalDate dateOfPurchase,
                    LocalDate publicationDate, Rack placedAt) {
        super(isbn, title, subject, publisher, language, numberOfPages, authors);
        this.barcode = barcode;
        this.isReferenceOnly = isReferenceOnly;
        this.price = price;
        this.format = format != null ? format : BookFormat.PAPERBACK;
        this.status = status != null ? status : BookStatus.AVAILABLE;
        this.dateOfPurchase = dateOfPurchase != null ? dateOfPurchase : LocalDate.now();
        this.publicationDate = publicationDate != null ? publicationDate : LocalDate.now();
        this.placedAt = placedAt != null ? placedAt : new Rack(1, "A1");
    }

    public BookItem(Book book, String barcode, double price, BookFormat format, Rack placedAt) {
        super(book.getIsbn(), book.getTitle(), book.getSubject(), book.getPublisher(),
              book.getLanguage(), book.getNumberOfPages(), book.getAuthors());
        this.barcode = barcode;
        this.isReferenceOnly = false;
        this.price = price;
        this.format = format != null ? format : BookFormat.PAPERBACK;
        this.status = BookStatus.AVAILABLE;
        this.dateOfPurchase = LocalDate.now();
        this.publicationDate = LocalDate.of(2023, 1, 1);
        this.placedAt = placedAt != null ? placedAt : new Rack(1, "A1");
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isReferenceOnly() {
        return isReferenceOnly;
    }

    public void setReferenceOnly(boolean referenceOnly) {
        isReferenceOnly = referenceOnly;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BookFormat getFormat() {
        return format;
    }

    public void setFormat(BookFormat format) {
        this.format = format;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public LocalDate getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(LocalDate dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Rack getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Rack placedAt) {
        this.placedAt = placedAt;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE && !isReferenceOnly;
    }

    @Override
    public String toString() {
        return "[" + barcode + "] " + getTitle() + " (" + format + ") - " + status + " @ " + placedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookItem bookItem = (BookItem) o;
        return Objects.equals(barcode, bookItem.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }
}
