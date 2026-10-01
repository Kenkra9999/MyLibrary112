package com.library.collections;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.models.Author;
import com.library.models.BookItem;
import com.library.models.Rack;

/**
 * Lop mang cac doi tuong Sach (DanhSachSach) quan ly bang mang BookItem[].
 * Thoa man tieu chi: (2d) Lop mang cac doi tuong (array), Nhap/Xuat, Them 1/Them k, Sua, Xoa, Tim kiem, Thong ke, Doc/Ghi file text.
 */
public class DanhSachSach implements IDanhSach<BookItem>, Serializable {
    private static final long serialVersionUID = 1L;

    private BookItem[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 10;

    public DanhSachSach() {
        this.danhSach = new BookItem[SUC_CHUA_MAC_DINH];
        this.soLuong = 0;
    }

    public DanhSachSach(int sucChua) {
        if (sucChua <= 0) sucChua = SUC_CHUA_MAC_DINH;
        this.danhSach = new BookItem[sucChua];
        this.soLuong = 0;
    }

    public DanhSachSach(BookItem[] initialArray) {
        if (initialArray != null) {
            this.danhSach = Arrays.copyOf(initialArray, initialArray.length);
            this.soLuong = initialArray.length;
        } else {
            this.danhSach = new BookItem[SUC_CHUA_MAC_DINH];
            this.soLuong = 0;
        }
    }

    private void moRongMang() {
        int sucChuaMoi = danhSach.length * 2;
        danhSach = Arrays.copyOf(danhSach, sucChuaMoi);
    }

    @Override
    public void them(BookItem item) {
        if (item == null) return;
        if (soLuong >= danhSach.length) {
            moRongMang();
        }
        danhSach[soLuong++] = item;
    }

    @Override
    public void themK(BookItem[] items) {
        if (items == null || items.length == 0) return;
        for (BookItem item : items) {
            if (item != null) {
                them(item);
            }
        }
    }

    public void nhapDanhSach(int n, Scanner sc) {
        System.out.println("--- NHAP " + n + " CUON SACH MOI VAO MANG ---");
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap thong tin cuon sach thu " + (i + 1) + ":");
            System.out.print("  Ma vach (Barcode): ");
            String barcode = sc.nextLine().trim();
            System.out.print("  ISBN: ");
            String isbn = sc.nextLine().trim();
            System.out.print("  Tua de: ");
            String title = sc.nextLine().trim();
            System.out.print("  Tac gia: ");
            String author = sc.nextLine().trim();
            System.out.print("  The loai: ");
            String subject = sc.nextLine().trim();
            System.out.print("  Nha xuat ban: ");
            String publisher = sc.nextLine().trim();
            System.out.print("  Gia bia (VND): ");
            double price = 100000;
            try { price = Double.parseDouble(sc.nextLine().trim()); } catch (Exception ignored) {}

            List<Author> authors = new ArrayList<>();
            authors.add(new Author(author, ""));
            Rack rack = new Rack(1, "Khu A - Ke 1");
            BookItem item = new BookItem(isbn, title, subject, publisher, "Tieng Viet", 200, authors,
                    barcode, false, price, BookFormat.PAPERBACK, BookStatus.AVAILABLE,
                    LocalDate.now(), LocalDate.now(), rack);
            them(item);
            System.out.println("  [+] Da them cuon sach: " + title);
        }
    }

    @Override
    public boolean sua(String barcode, BookItem newItem) {
        if (barcode == null || newItem == null) return false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getBarcode().equalsIgnoreCase(barcode)) {
                danhSach[i] = newItem;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String barcode) {
        if (barcode == null) return false;
        int index = -1;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getBarcode().equalsIgnoreCase(barcode)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            for (int i = index; i < soLuong - 1; i++) {
                danhSach[i] = danhSach[i + 1];
            }
            danhSach[soLuong - 1] = null;
            soLuong--;
            return true;
        }
        return false;
    }

    @Override
    public BookItem timKiemTheoMa(String barcode) {
        if (barcode == null) return null;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getBarcode().equalsIgnoreCase(barcode)) {
                return danhSach[i];
            }
        }
        return null;
    }

    @Override
    public BookItem[] timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getDanhSach();
        }
        String q = tuKhoa.toLowerCase().trim();
        List<BookItem> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            BookItem item = danhSach[i];
            if (item != null) {
                if (item.getBarcode().toLowerCase().contains(q) ||
                    item.getTitle().toLowerCase().contains(q) ||
                    item.getAuthorsAsString().toLowerCase().contains(q) ||
                    item.getSubject().toLowerCase().contains(q)) {
                    matched.add(item);
                }
            }
        }
        return matched.toArray(new BookItem[0]);
    }

    public BookItem[] timKiemTheoTuaDe(String tuaDe) {
        if (tuaDe == null || tuaDe.trim().isEmpty()) return getDanhSach();
        String q = tuaDe.toLowerCase().trim();
        List<BookItem> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getTitle().toLowerCase().contains(q)) {
                matched.add(danhSach[i]);
            }
        }
        return matched.toArray(new BookItem[0]);
    }

    public BookItem[] timKiemTheoTacGia(String tacGia) {
        if (tacGia == null || tacGia.trim().isEmpty()) return getDanhSach();
        String q = tacGia.toLowerCase().trim();
        List<BookItem> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getAuthorsAsString().toLowerCase().contains(q)) {
                matched.add(danhSach[i]);
            }
        }
        return matched.toArray(new BookItem[0]);
    }

    public BookItem[] timKiemTheoTheLoai(String theLoai) {
        if (theLoai == null || theLoai.trim().isEmpty()) return getDanhSach();
        String q = theLoai.toLowerCase().trim();
        List<BookItem> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getSubject().toLowerCase().contains(q)) {
                matched.add(danhSach[i]);
            }
        }
        return matched.toArray(new BookItem[0]);
    }

    public Map<String, Integer> thongKeTheoTheLoai() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null) {
                String sub = danhSach[i].getSubject();
                map.put(sub, map.getOrDefault(sub, 0) + 1);
            }
        }
        return map;
    }

    public Map<BookStatus, Integer> thongKeTheoTrangThai() {
        Map<BookStatus, Integer> map = new HashMap<>();
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null) {
                BookStatus st = danhSach[i].getStatus();
                map.put(st, map.getOrDefault(st, 0) + 1);
            }
        }
        return map;
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================================");
        System.out.printf("%-12s | %-32s | %-20s | %-16s | %-12s | %-12s\n",
                "MA VACH", "TUA DE SACH", "TAC GIA", "THE LOAI", "GIA BIA", "TRANG THAI");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            BookItem item = danhSach[i];
            if (item != null) {
                System.out.printf("%-12s | %-32s | %-20s | %-16s | %,-12.0f | %-12s\n",
                        item.getBarcode(),
                        rutGon(item.getTitle(), 32),
                        rutGon(item.getAuthorsAsString(), 20),
                        rutGon(item.getSubject(), 16),
                        item.getPrice(),
                        item.getStatus().getDisplayName());
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Tong cong: " + soLuong + " ban sao sach trong mang.");
    }

    private String rutGon(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }

    @Override
    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public BookItem[] getDanhSach() {
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
                if (parts.length >= 7) {
                    String barcode = parts[0].trim();
                    String isbn = parts[1].trim();
                    String title = parts[2].trim();
                    String authorsStr = parts[3].trim();
                    String subject = parts[4].trim();
                    String publisher = parts[5].trim();
                    double price = Double.parseDouble(parts[6].trim());
                    String statusStr = parts.length > 7 ? parts[7].trim() : "AVAILABLE";

                    List<Author> authors = new ArrayList<>();
                    authors.add(new Author(authorsStr, ""));
                    BookStatus status = BookStatus.valueOf(statusStr);
                    BookItem item = new BookItem(isbn, title, subject, publisher, "Tieng Viet", 250, authors,
                            barcode, false, price, BookFormat.PAPERBACK, status,
                            LocalDate.now(), LocalDate.now(), new Rack(1, "Ke 1"));
                    them(item);
                }
            }
            System.out.println("[+] Da doc " + soLuong + " cuon sach tu file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi doc file text sach: " + e.getMessage());
        }
    }

    @Override
    public void ghiFileText(String filePath) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("# BARCODE|ISBN|TITLE|AUTHORS|SUBJECT|PUBLISHER|PRICE|STATUS\n");
                for (int i = 0; i < soLuong; i++) {
                    BookItem b = danhSach[i];
                    if (b != null) {
                        bw.write(String.format("%s|%s|%s|%s|%s|%s|%.0f|%s\n",
                                b.getBarcode(), b.getIsbn(), b.getTitle(),
                                b.getAuthorsAsString(), b.getSubject(), b.getPublisher(),
                                b.getPrice(), b.getStatus().name()));
                    }
                }
            }
            System.out.println("[+] Da ghi " + soLuong + " cuon sach ra file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi ghi file text sach: " + e.getMessage());
        }
    }
}
