package com.library.enums;

/**
 * Trang thai cua mot phieu dat giu sach (BookReservation).
 */
public enum ReservationStatus {
    WAITING("Dang cho"),
    PENDING("Dang xu ly / San sang lay"),
    COMPLETED("Da hoan thanh"),
    CANCELED("Da huy"),
    NONE("Khong xac dinh");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
