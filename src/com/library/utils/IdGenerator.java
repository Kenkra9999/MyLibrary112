package com.library.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Bộ sinh mã định danh tự động cho sách, thẻ thư viện, giao dịch mượn, thông báo.
 */
public final class IdGenerator {
    private static final AtomicLong MEMBER_COUNTER = new AtomicLong(1000);
    private static final AtomicLong LIBRARIAN_COUNTER = new AtomicLong(100);
    private static final AtomicLong CARD_COUNTER = new AtomicLong(5000);
    private static final AtomicLong NOTIF_COUNTER = new AtomicLong(1);
    private static final AtomicLong FINE_COUNTER = new AtomicLong(1);

    private IdGenerator() {}

    public static String generateMemberId() {
        return "MB-" + MEMBER_COUNTER.incrementAndGet();
    }

    public static String generateLibrarianId() {
        return "LIB-" + LIBRARIAN_COUNTER.incrementAndGet();
    }

    public static String generateCardNumber() {
        return "LC-" + CARD_COUNTER.incrementAndGet();
    }

    public static String generateBarcode(String isbn, int copyNumber) {
        String cleanIsbn = (isbn != null ? isbn.replaceAll("[^0-9A-Za-z]", "") : "BOOK");
        return "BC-" + cleanIsbn + "-" + String.format("%03d", copyNumber);
    }

    public static String generateNotificationId() {
        return "NOTIF-" + NOTIF_COUNTER.incrementAndGet();
    }

    public static String generateFineId() {
        return "FINE-" + FINE_COUNTER.incrementAndGet();
    }

    public static String generateShortUuid() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
