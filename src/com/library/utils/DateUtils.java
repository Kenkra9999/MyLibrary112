package com.library.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Tien ich xu ly ngay thang (chi gom phuong thuc static).
 * Dinh dang hien thi va luu file: dd/MM/yyyy (ngay) va dd/MM/yyyy HH:mm:ss (ngay gio).
 */
public final class DateUtils {
    // STRICT: ngay khong ton tai (vd 31/02/2020) bi bao loi thay vi tu lam tron thanh 29/02
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    private DateUtils() {}

    /** Dinh dang ngay de HIEN THI (null -> "N/A"). */
    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DATE_FORMATTER);
    }

    /** Dinh dang ngay gio de HIEN THI (null -> "N/A"). */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /** Dinh dang ngay de GHI FILE (null -> chuoi rong). */
    public static String ngayRaText(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    /** Dinh dang ngay gio de GHI FILE (null -> chuoi rong). */
    public static String ngayGioRaText(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMATTER);
    }

    /** Doc ngay dd/MM/yyyy; chuoi rong hoac sai dinh dang -> null. */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Doc ngay gio dd/MM/yyyy HH:mm:ss; chuoi rong hoac sai dinh dang -> null. */
    public static LocalDateTime parseDateTime(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(str.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
