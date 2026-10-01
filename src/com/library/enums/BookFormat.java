package com.library.enums;

/**
 * Cac dinh dang sach duoc ho tro trong thu vien.
 */
public enum BookFormat {
    HARDCOVER("Bia cung"),
    PAPERBACK("Bia mem"),
    AUDIO_BOOK("Sach noi (Audio Book)"),
    EBOOK("Sach dien tu (E-Book)"),
    NEWSPAPER("Bao"),
    MAGAZINE("Tap chi"),
    JOURNAL("Tap san khoa hoc");

    private final String displayName;

    BookFormat(String displayName) {
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
