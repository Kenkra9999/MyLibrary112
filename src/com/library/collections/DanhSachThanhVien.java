package com.library.collections;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.users.Member;

/**
 * Lop mang cac doi tuong Thanh Vien (DanhSachThanhVien) quan ly bang mang Member[].
 * Thoa man tieu chi: (2d) Lop mang cac doi tuong (array), Nhap/Xuat, Them, Sua, Xoa, Tim kiem, Doc/Ghi file text.
 */
public class DanhSachThanhVien implements IDanhSach<Member>, Serializable {
    private static final long serialVersionUID = 1L;

    private Member[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 10;

    public DanhSachThanhVien() {
        this.danhSach = new Member[SUC_CHUA_MAC_DINH];
        this.soLuong = 0;
    }

    public DanhSachThanhVien(int sucChua) {
        if (sucChua <= 0) sucChua = SUC_CHUA_MAC_DINH;
        this.danhSach = new Member[sucChua];
        this.soLuong = 0;
    }

    private void moRongMang() {
        int sucChuaMoi = danhSach.length * 2;
        danhSach = Arrays.copyOf(danhSach, sucChuaMoi);
    }

    @Override
    public void them(Member item) {
        if (item == null) return;
        if (soLuong >= danhSach.length) {
            moRongMang();
        }
        danhSach[soLuong++] = item;
    }

    @Override
    public void themK(Member[] items) {
        if (items == null || items.length == 0) return;
        for (Member m : items) {
            if (m != null) them(m);
        }
    }

    public void nhapDanhSach(int n, Scanner sc) {
        System.out.println("--- NHAP " + n + " THANH VIEN MOI VAO MANG ---");
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap thong tin thanh vien thu " + (i + 1) + ":");
            System.out.print("  Ma thanh vien (ID): ");
            String id = sc.nextLine().trim();
            System.out.print("  Ho va ten: ");
            String name = sc.nextLine().trim();
            System.out.print("  Email: ");
            String email = sc.nextLine().trim();
            System.out.print("  So dien thoai: ");
            String phone = sc.nextLine().trim();
            System.out.print("  Dia chi: ");
            String addr = sc.nextLine().trim();

            LibraryCard card = new LibraryCard("LC-" + id, "BC-LC-" + id);
            Person p = new Person(name, new Address(addr, "Ha Noi"), email, phone);
            Member m = new Member(id, "123456", p, card);
            them(m);
            System.out.println("  [+] Da them thanh vien: " + name);
        }
    }

    @Override
    public boolean sua(String id, Member newM) {
        if (id == null || newM == null) return false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getId().equalsIgnoreCase(id)) {
                danhSach[i] = newM;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String id) {
        if (id == null) return false;
        int index = -1;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getId().equalsIgnoreCase(id)) {
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
    public Member timKiemTheoMa(String id) {
        if (id == null) return null;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getId().equalsIgnoreCase(id)) {
                return danhSach[i];
            }
        }
        return null;
    }

    @Override
    public Member[] timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getDanhSach();
        }
        String q = tuKhoa.toLowerCase().trim();
        List<Member> matched = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            Member m = danhSach[i];
            if (m != null) {
                if (m.getId().toLowerCase().contains(q) ||
                    m.getName().toLowerCase().contains(q) ||
                    m.getEmail().toLowerCase().contains(q) ||
                    m.getPhone().toLowerCase().contains(q)) {
                    matched.add(m);
                }
            }
        }
        return matched.toArray(new Member[0]);
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================================");
        System.out.printf("%-10s | %-24s | %-22s | %-12s | %-10s | %-15s\n",
                "MA TV", "HO VA TEN", "EMAIL", "DIEN THOAI", "DANG MUON", "TRANG THAI");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            Member m = danhSach[i];
            if (m != null) {
                System.out.printf("%-10s | %-24s | %-22s | %-12s | %-10d | %-15s\n",
                        m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                        m.getTotalBooksCheckedOut(), m.getStatus().getDisplayName());
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Tong cong: " + soLuong + " thanh vien trong mang.");
    }

    @Override
    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public Member[] getDanhSach() {
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
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String phone = parts[3].trim();
                    String cardNum = parts.length > 4 ? parts[4].trim() : "LC-" + id;
                    String statusStr = parts.length > 5 ? parts[5].trim() : "ACTIVE";

                    LibraryCard card = new LibraryCard(cardNum, "BC-" + cardNum);
                    Person p = new Person(name, email, phone);
                    Member m = new Member(id, "123456", p, card, AccountStatus.valueOf(statusStr), LocalDate.now());
                    them(m);
                }
            }
            System.out.println("[+] Da doc " + soLuong + " thanh vien tu file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi doc file text thanh vien: " + e.getMessage());
        }
    }

    @Override
    public void ghiFileText(String filePath) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("# ID|NAME|EMAIL|PHONE|CARD_NUMBER|STATUS\n");
                for (int i = 0; i < soLuong; i++) {
                    Member m = danhSach[i];
                    if (m != null) {
                        String cardNum = m.getCard() != null ? m.getCard().getCardNumber() : "N/A";
                        bw.write(String.format("%s|%s|%s|%s|%s|%s\n",
                                m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                                cardNum, m.getStatus().name()));
                    }
                }
            }
            System.out.println("[+] Da ghi " + soLuong + " thanh vien ra file text: " + filePath);
        } catch (Exception e) {
            System.err.println("[-] Loi ghi file text thanh vien: " + e.getMessage());
        }
    }
}
