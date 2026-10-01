package com.library.collections;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import com.library.models.Fine;
import com.library.utils.DateUtils;

/**
 * Lop mang cac doi tuong Tien Phat (DanhSachPhat) quan ly bang mang Fine[].
 */
public class DanhSachPhat implements IDanhSach<Fine>, Serializable {
    private static final long serialVersionUID = 1L;

    private Fine[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 10;

    public DanhSachPhat() {
        this.danhSach = new Fine[SUC_CHUA_MAC_DINH];
        this.soLuong = 0;
    }

    public DanhSachPhat(int sucChua) {
        if (sucChua <= 0) sucChua = SUC_CHUA_MAC_DINH;
        this.danhSach = new Fine[sucChua];
        this.soLuong = 0;
    }

    private void moRongMang() {
        danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
    }

    @Override
    public void them(Fine item) {
        if (item == null) return;
        if (soLuong >= danhSach.length) moRongMang();
        danhSach[soLuong++] = item;
    }

    @Override
    public void themK(Fine[] items) {
        if (items == null) return;
        for (Fine f : items) {
            if (f != null) them(f);
        }
    }

    @Override
    public boolean sua(String fineId, Fine newFine) {
        if (fineId == null || newFine == null) return false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getFineId().equalsIgnoreCase(fineId)) {
                danhSach[i] = newFine;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String fineId) {
        if (fineId == null) return false;
        int index = -1;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getFineId().equalsIgnoreCase(fineId)) {
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
    public Fine timKiemTheoMa(String fineId) {
        if (fineId == null) return null;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getFineId().equalsIgnoreCase(fineId)) {
                return danhSach[i];
            }
        }
        return null;
    }

    @Override
    public Fine[] timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return getDanhSach();
        String q = tuKhoa.toLowerCase().trim();
        List<Fine> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            Fine f = danhSach[i];
            if (f != null) {
                if (f.getFineId().toLowerCase().contains(q) ||
                    f.getMemberId().toLowerCase().contains(q) ||
                    f.getItemBarcode().toLowerCase().contains(q)) {
                    matched.add(f);
                }
            }
        }
        return matched.toArray(new Fine[0]);
    }

    public double thongKeTongTienPhat() {
        double sum = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null) sum += danhSach[i].getAmount();
        }
        return sum;
    }

    public double thongKeTienPhatChuaThu() {
        double sum = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && !danhSach[i].isPaid()) sum += danhSach[i].getAmount();
        }
        return sum;
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================================");
        System.out.printf("%-10s | %-12s | %-10s | %-15s | %-12s | %-12s | %-12s\n",
                "MA PHAT", "SO TIEN", "MA TV", "MA SACH", "NGAY TAO", "NGAY THU", "TRANG THAI");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            Fine f = danhSach[i];
            if (f != null) {
                System.out.printf("%-10s | %,-12.0f | %-10s | %-15s | %-12s | %-12s | %-12s\n",
                        f.getFineId(), f.getAmount(), f.getMemberId(), f.getItemBarcode(),
                        DateUtils.formatDate(f.getCreationDate()),
                        f.getPaidDate() != null ? DateUtils.formatDate(f.getPaidDate()) : "--",
                        f.isPaid() ? "Da dong" : "CHUA DONG");
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Tong cong: " + soLuong + " bien lai phat trong mang.");
    }

    @Override
    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public Fine[] getDanhSach() {
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
                    double amount = Double.parseDouble(parts[1].trim());
                    String barcode = parts[2].trim();
                    String memId = parts[3].trim();
                    LocalDate createDate = DateUtils.parseDate(parts[4].trim());
                    boolean isPaid = parts.length > 5 && Boolean.parseBoolean(parts[5].trim());
                    LocalDate paidDate = (parts.length > 6 && !parts[6].trim().equals("null")) ? DateUtils.parseDate(parts[6].trim()) : null;

                    Fine f = new Fine(id, amount, createDate, barcode, memId, isPaid, paidDate);
                    them(f);
                }
            }
            System.out.println("[+] Da doc " + soLuong + " bien lai phat tu file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi doc file text tien phat: " + e.getMessage());
        }
    }

    @Override
    public void ghiFileText(String filePath) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("# ID|AMOUNT|BARCODE|MEMBER_ID|CREATED_DATE|IS_PAID|PAID_DATE\n");
                for (int i = 0; i < soLuong; i++) {
                    Fine f = danhSach[i];
                    if (f != null) {
                        String paidStr = f.getPaidDate() != null ? DateUtils.formatDate(f.getPaidDate()) : "null";
                        bw.write(String.format("%s|%.0f|%s|%s|%s|%b|%s\n",
                                f.getFineId(), f.getAmount(), f.getItemBarcode(), f.getMemberId(),
                                DateUtils.formatDate(f.getCreationDate()),
                                f.isPaid(), paidStr));
                    }
                }
            }
            System.out.println("[+] Da ghi " + soLuong + " bien lai phat ra file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi ghi file text tien phat: " + e.getMessage());
        }
    }
}
