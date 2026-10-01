package com.library.enums;

/**
 * Trang thai cua tai khoan nguoi dung trong he thong.
 */
public enum AccountStatus {
    ACTIVE("Dang hoat dong"),
    CLOSED("Da dong"),
    CANCELED("Da huy"),
    BLACKLISTED("Bi khoa / Danh sach den"),
    NONE("Khong xac dinh");

    private final String displayName;

    AccountStatus(String displayName) {
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
