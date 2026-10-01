package com.library.collections;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import com.library.models.BookLending;
import com.library.utils.DateUtils;

/**
 * Lop mang cac doi tuong Muon Tra (DanhSachMuonTra) quan ly bang mang BookLending[].
 */
public class DanhSachMuonTra implements IDanhSach<BookLending>, Serializable {
    private static final long serialVersionUID = 1L;

    private BookLending[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 10;

    public DanhSachMuonTra() {
        this.danhSach = new BookLending[SUC_CHUA_MAC_DINH];
        this.soLuong = 0;
    }

    public DanhSachMuonTra(int sucChua) {
        if (sucChua <= 0) sucChua = SUC_CHUA_MAC_DINH;
        this.danhSach = new BookLending[sucChua];
        this.soLuong = 0;
    }

    private void moRongMang() {
        danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
    }

    @Override
    public void them(BookLending item) {
        if (item == null) return;
        if (soLuong >= danhSach.length) moRongMang();
        danhSach[soLuong++] = item;
    }

    @Override
    public void themK(BookLending[] items) {
        if (items == null) return;
        for (BookLending l : items) {
            if (l != null) them(l);
        }
    }

    @Override
    public boolean sua(String lendingId, BookLending newLending) {
        if (lendingId == null || newLending == null) return false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getLendingId().equalsIgnoreCase(lendingId)) {
                danhSach[i] = newLending;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String lendingId) {
        if (lendingId == null) return false;
        int index = -1;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getLendingId().equalsIgnoreCase(lendingId)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            for (int i = index; i < soLuong - 1; i++) {
                danhSach[i] = danhSach[i + 1];
            }
            danhSach[--soLuong] = null;
            return true;
        }
        return false;
    }

    @Override
    public BookLending timKiemTheoMa(String lendingId) {
        if (lendingId == null) return null;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getLendingId().equalsIgnoreCase(lendingId)) {
                return danhSach[i];
            }
        }
        return null;
    }

    @Override
    public BookLending[] timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return getDanhSach();
        String q = tuKhoa.toLowerCase().trim();
        List<BookLending> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            BookLending l = danhSach[i];
            if (l != null) {
                if (l.getLendingId().toLowerCase().contains(q) ||
                    l.getItemBarcode().toLowerCase().contains(q) ||
                    l.getMemberId().toLowerCase().contains(q)) {
                    matched.add(l);
                }
            }
        }
        return matched.toArray(new BookLending[0]);
    }

    public int thongKeSoLuotQuaHan() {
        int count = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && !danhSach[i].isReturned() && danhSach[i].isOverdue()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================================");
        System.out.printf("%-12s | %-12s | %-10s | %-12s | %-12s | %-12s | %-15s\n",
                "MA MUON", "MA SACH", "MA TV", "NGAY MUON", "HAN TRA", "NGAY TRA", "TRANG THAI");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            BookLending l = danhSach[i];
            if (l != null) {
                String st = l.isReturned() ? "Da tra" : (l.isOverdue() ? "QUA HAN (" + l.getOverdueDays() + "n)" : "Dang muon");
                System.out.printf("%-12s | %-12s | %-10s | %-12s | %-12s | %-12s | %-15s\n",
                        l.getLendingId(), l.getItemBarcode(), l.getMemberId(),
                        DateUtils.formatDate(l.getCreationDate()),
                        DateUtils.formatDate(l.getDueDate()),
                        l.getReturnDate() != null ? DateUtils.formatDate(l.getReturnDate()) : "--",
                        st);
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Tong cong: " + soLuong + " luot muon tra trong mang.");
    }

    @Override
    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public BookLending[] getDanhSach() {
        return Arrays.copyOf(danhSach, soLuong);
    }

    @Override
    public void docFileText(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String barcode = parts[1].trim();
                    String memId = parts[2].trim();
                    LocalDate createDate = DateUtils.parseDate(parts[3].trim());
                    LocalDate dueDate = DateUtils.parseDate(parts[4].trim());
                    LocalDate returnDate = (parts.length > 5 && !parts[5].trim().equals("null")) ? DateUtils.parseDate(parts[5].trim()) : null;
                    boolean isRet = parts.length > 6 && Boolean.parseBoolean(parts[6].trim());

                    BookLending l = new BookLending(id, barcode, memId, createDate, dueDate, returnDate, isRet);
                    them(l);
                }
            }
            System.out.println("[+] Da doc " + soLuong + " luot muon tra tu file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi doc file text muon tra: " + e.getMessage());
        }
    }

    @Override
    public void ghiFileText(String filePath) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("# ID|BARCODE|MEMBER_ID|BORROW_DATE|DUE_DATE|RETURN_DATE|IS_RETURNED\n");
                for (int i = 0; i < soLuong; i++) {
                    BookLending l = danhSach[i];
                    if (l != null) {
                        String retStr = l.getReturnDate() != null ? DateUtils.formatDate(l.getReturnDate()) : "null";
                        bw.write(String.format("%s|%s|%s|%s|%s|%s|%b\n",
                                l.getLendingId(), l.getItemBarcode(), l.getMemberId(),
                                DateUtils.formatDate(l.getCreationDate()),
                                DateUtils.formatDate(l.getDueDate()),
                                retStr, l.isReturned()));
                    }
                }
            }
            System.out.println("[+] Da ghi " + soLuong + " luot muon tra ra file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi ghi file text muon tra: " + e.getMessage());
        }
    }
}
