package com.library.enums;

/**
 * Trang thai cua mot ban sao sach (BookItem) trong thu vien.
 */
public enum BookStatus {
    AVAILABLE("Co san"),
    RESERVED("Da duoc dat truoc"),
    LOANED("Dang cho muon"),
    LOST("Bi mat");

    private final String displayName;

    BookStatus(String displayName) {
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
