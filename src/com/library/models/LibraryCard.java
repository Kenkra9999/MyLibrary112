package com.library.models;

import java.io.Serializable;
import java.time.LocalDate;
import com.library.utils.DateUtils;

/**
 * Class LibraryCard bieu dien the thu vien cua nguoi dung (Yeu cau R6).
 */
public class LibraryCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cardNumber;
    private String barcode;
    private LocalDate issuedAt;
    private boolean active;

    public LibraryCard() {
        this.issuedAt = LocalDate.now();
        this.active = true;
    }

    public LibraryCard(String cardNumber, String barcode, LocalDate issuedAt, boolean active) {
        this.cardNumber = cardNumber;
        this.barcode = barcode;
        this.issuedAt = issuedAt != null ? issuedAt : LocalDate.now();
        this.active = active;
    }

    public LibraryCard(String cardNumber, String barcode) {
        this(cardNumber, barcode, LocalDate.now(), true);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "The TV: " + cardNumber + " [Cap: " + DateUtils.formatDate(issuedAt) + ", Trang thai: " + (active ? "Hoat dong" : "Khoa") + "]";
    }
}
