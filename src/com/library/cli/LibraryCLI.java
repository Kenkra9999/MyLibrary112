package com.library.cli;

import java.time.LocalDate;
import java.util.Scanner;

import com.library.collections.DanhSachSach;
import com.library.collections.DanhSachThanhVien;
import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.Book;
import com.library.models.BookItem;
import com.library.models.BookLending;
import com.library.models.Fine;
import com.library.models.Rack;
import com.library.services.Library;
import com.library.services.OperationResult;
import com.library.services.SampleDataLoader;
import com.library.services.TextFileStorageService;
import com.library.users.Member;
import com.library.users.User;
import com.library.utils.NhapLieu;

/**
 * Giao dien dong lenh (Console) cua chuong trinh Quan ly Thu vien.
 * Van ban dung tieng Viet khong dau de khong loi font tren Windows CMD.
 * Moi thao tac lam thay doi du lieu deu duoc ghi ngay ra file text trong thu muc data/.
 */
public class LibraryCLI {
    private Library library;
    private final Scanner scanner;

    public LibraryCLI() {
        this.scanner = new Scanner(System.in);
        napDuLieu();
    }

    /** Lan dau chay (chua co file) -> tao du lieu mau va ghi ra file; cac lan sau -> doc tu file text. */
    private void napDuLieu() {
        library = new Library();
        if (TextFileStorageService.coDuLieu()) {
            int soBanGhi = TextFileStorageService.docTatCa(library);
            System.out.println("[+] Da doc " + soBanGhi + " ban ghi tu cac file text trong thu muc data/");
        } else {
            library = SampleDataLoader.createSampleLibrary();
            luu();
            System.out.println("[+] Lan chay dau tien: da tao du lieu mau va ghi ra thu muc data/");
        }
    }

    private void luu() {
        if (!TextFileStorageService.ghiTatCa(library)) {
            System.out.println("[!] Co loi khi ghi file, du lieu co the chua duoc luu!");
        }
    }

    /** In ket qua nghiep vu; neu thanh cong thi luu file ngay. */
    private void xuLyKetQua(OperationResult kq) {
        System.out.println(kq);
        if (kq.isSuccess()) luu();
    }

    private String nhap(String loiNhac) {
        return NhapLieu.nhapChuoi(scanner, loiNhac);
    }

    // ============================== MENU CHINH ==============================

    public void start() {
        while (true) {
            System.out.println("\n========================================================================");
            System.out.println("   HE THONG QUAN LY THU VIEN - " + library.getName());
            System.out.println("========================================================================");
            System.out.println(" 1. Quan ly sach (them / sua / xoa / tim kiem / sap xep / thong ke)");
            System.out.println(" 2. Muon sach");
            System.out.println(" 3. Tra sach & tinh tien phat qua han");
            System.out.println(" 4. Gia han muon sach");
            System.out.println(" 5. Dat giu sach");
            System.out.println(" 6. Quan ly thanh vien & thu thu");
            System.out.println(" 7. Quan ly tien phat");
            System.out.println(" 8. Thong bao (Email / Buu dien)");
            System.out.println(" 9. Du lieu file text & thong ke tong hop");
            System.out.println(" 0. Luu du lieu & thoat");
            System.out.println("------------------------------------------------------------------------");
            String chon = nhap(">> Chon (0-9): ");
            switch (chon) {
                case "1": menuSach(); break;
                case "2": menuMuonTraGiaHan("MUON SACH", 2); break;
                case "3": menuMuonTraGiaHan("TRA SACH", 3); break;
                case "4": menuMuonTraGiaHan("GIA HAN SACH", 4); break;
                case "5": menuDatGiu(); break;
                case "6": menuThanhVien(); break;
                case "7": menuTienPhat(); break;
                case "8": menuThongBao(); break;
                case "9": menuFileText(); break;
                case "0":
                    luu();
                    System.out.println("\n[+] Da luu toan bo du lieu ra file text. Tam biet!");
                    return;
                default:
                    System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    // ============================== 1. SACH ==============================

    private void menuSach() {
        DanhSachSach ds = library.getDsSach();
        while (true) {
            System.out.println("\n--- [1] QUAN LY SACH (mang BookItem[] trong DanhSachSach) ---");
            System.out.println(" 1. Xuat danh sach sach            2. Tim kiem theo tu khoa");
            System.out.println(" 3. Tim theo tua de                4. Tim theo tac gia");
            System.out.println(" 5. Tim theo the loai              6. Tim theo ngay xuat ban");
            System.out.println(" 7. Them 1 sach moi                8. Nhap n sach moi");
            System.out.println(" 9. Sua thong tin sach            10. Xoa sach");
            System.out.println("11. Sap xep theo tua de (A-Z)     12. Sap xep theo gia (giam dan)");
            System.out.println("13. Thong ke kho sach              0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1": ds.xuatDanhSach(); System.out.println("(* = sach chi doc tai cho)"); break;
                case "2": ds.xuatMang(ds.timKiem(nhap("Nhap tu khoa (ma vach/ISBN/tua/tac gia/the loai/NXB): "))); break;
                case "3": ds.xuatMang(ds.searchByTitle(nhap("Nhap tua de: "))); break;
                case "4": ds.xuatMang(ds.searchByAuthor(nhap("Nhap ten tac gia: "))); break;
                case "5": ds.xuatMang(ds.searchBySubject(nhap("Nhap the loai: "))); break;
                case "6":
                    LocalDate ngay = NhapLieu.nhapNgay(scanner, "Nhap ngay xuat ban (dd/MM/yyyy): ", false);
                    ds.xuatMang(ds.searchByPubDate(ngay));
                    break;
                case "7": themMotSach(); break;
                case "8":
                    int n = NhapLieu.nhapSoNguyen(scanner, "Nhap so luong sach n: ", 1, 100);
                    int daThem = ds.nhapDanhSach(n, scanner);
                    luu();
                    System.out.println("[+] Da them " + daThem + "/" + n + " sach vao mang.");
                    break;
                case "9": suaSach(); break;
                case "10":
                    xuLyKetQua(library.xoaSach(NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma vach sach can xoa: ")));
                    break;
                case "11": ds.sapXepTheoTuaDe(); luu(); ds.xuatDanhSach(); break;
                case "12": ds.sapXepTheoGiaGiamDan(); luu(); ds.xuatDanhSach(); break;
                case "13": ds.inThongKe(); break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    private void themMotSach() {
        System.out.println("--- THEM SACH MOI (ma vach tu sinh) ---");
        BookItem b = new BookItem();
        b.nhap(scanner);
        b.setBarcode(library.getDsSach().taoMaVachMoi());
        xuLyKetQua(library.themSach(b));
    }

    private void suaSach() {
        String ma = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma vach sach can sua: ");
        BookItem cu = library.getDsSach().timKiemTheoMa(ma);
        if (cu == null) {
            System.out.println("[-] Khong tim thay sach " + ma);
            return;
        }
        System.out.println("Thong tin hien tai: " + cu);
        System.out.println("(Bam Enter de giu nguyen gia tri trong ngoac)");
        String tuaDe = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Tua de", cu.getTitle());
        String tacGia = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Tac gia (cach nhau ;)", cu.getAuthorsAsText());
        String theLoai = NhapLieu.nhapChuoiGiuNguyen(scanner, "  The loai", cu.getSubject());
        String nxb = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Nha xuat ban", cu.getPublisher());
        String ngonNgu = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Ngon ngu", cu.getLanguage());
        int soTrang = NhapLieu.nhapSoNguyenGiuNguyen(scanner, "  So trang", cu.getNumberOfPages(), 1, 100000);
        double gia = NhapLieu.nhapSoThucGiuNguyen(scanner, "  Gia bia", cu.getPrice(), 0);
        int soKe = NhapLieu.nhapSoNguyenGiuNguyen(scanner, "  So ke", cu.getPlacedAt().getNumber(), 1, 999);
        String viTri = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Vi tri ke", cu.getPlacedAt().getLocationIdentifier());

        // Tao doi tuong moi (giu nguyen ma vach, trang thai, ngay muon...) roi thay vao mang bang sua()
        BookItem moi = new BookItem(cu.getIsbn(), tuaDe, theLoai, nxb, ngonNgu, soTrang, Book.taoMangTacGia(tacGia),
                cu.getBarcode(), cu.isReferenceOnly(), gia, cu.getFormat(), cu.getStatus(),
                cu.getDateOfPurchase(), cu.getPublicationDate(), new Rack(soKe, viTri));
        moi.setBorrowedDate(cu.getBorrowedDate());
        moi.setDueDate(cu.getDueDate());
        xuLyKetQua(library.suaSach(ma, moi));
    }

    // ============================== 2-4. MUON / TRA / GIA HAN ==============================

    private void menuMuonTraGiaHan(String tieuDe, int loai) {
        System.out.println("\n--- [" + loai + "] " + tieuDe + " ---");
        String maTV = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien (vd MB-1001): ");
        String maVach = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma vach sach (vd BC-CC-001): ");
        OperationResult kq;
        if (loai == 2) kq = library.checkoutBook(maVach, maTV);
        else if (loai == 3) kq = library.returnBook(maVach, maTV);
        else kq = library.renewBook(maVach, maTV);
        xuLyKetQua(kq);
    }

    // ============================== 5. DAT GIU ==============================

    private void menuDatGiu() {
        while (true) {
            System.out.println("\n--- [5] DAT GIU SACH (mang BookReservation[]) ---");
            System.out.println(" 1. Dat giu sach   2. Huy phieu dat giu   3. Xuat danh sach phieu dat giu   0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1":
                    String maTV = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien: ");
                    String maVach = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma vach sach can dat giu: ");
                    xuLyKetQua(library.reserveBook(maVach, maTV));
                    break;
                case "2":
                    xuLyKetQua(library.cancelReservation(NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma phieu dat giu: ")));
                    break;
                case "3": library.getDsDatCho().xuatDanhSach(); break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    // ============================== 6. THANH VIEN & THU THU ==============================

    private void menuThanhVien() {
        DanhSachThanhVien ds = library.getDsThanhVien();
        while (true) {
            System.out.println("\n--- [6] QUAN LY THANH VIEN (mang Member[]) & THU THU (mang Librarian[]) ---");
            System.out.println(" 1. Xuat danh sach thanh vien      2. Tim kiem thanh vien");
            System.out.println(" 3. Them thanh vien moi (cap the)  4. Sua thong tin thanh vien");
            System.out.println(" 5. Xoa thanh vien                 6. Lich su muon tra cua thanh vien");
            System.out.println(" 7. Khoa / mo khoa the (thu thu dang nhap)");
            System.out.println(" 8. Xuat danh sach thu thu         9. Xem tat ca nguoi dung (mang User[] - da hinh)");
            System.out.println(" 0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1": ds.xuatDanhSach(); break;
                case "2": ds.xuatMang(ds.timKiem(nhap("Nhap tu khoa (ma / ten / email / sdt / so the): "))); break;
                case "3": themThanhVien(); break;
                case "4": suaThanhVien(); break;
                case "5": xuLyKetQua(library.xoaThanhVien(NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien can xoa: "))); break;
                case "6":
                    String ma = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien: ");
                    BookLending[] lichSu = library.getDsMuonTra().timTheoThanhVien(ma);
                    library.getDsMuonTra().xuatMang(lichSu);
                    break;
                case "7": khoaMoKhoa(); break;
                case "8": library.getDsThuThu().xuatDanhSach(); break;
                case "9": xemTatCaNguoiDung(); break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    private void themThanhVien() {
        System.out.println("--- DANG KY THANH VIEN MOI ---");
        String hoTen = NhapLieu.nhapChuoiBatBuoc(scanner, "  Ho ten: ");
        String email = nhap("  Email (Enter neu khong co -> nhan thong bao qua buu dien): ").replace('|', '/');
        String sdt = NhapLieu.nhapChuoiBatBuoc(scanner, "  So dien thoai: ");
        String duong = NhapLieu.nhapChuoiBatBuoc(scanner, "  Dia chi (so nha, duong / quan): ");
        String thanhPho = NhapLieu.nhapChuoiBatBuoc(scanner, "  Tinh / thanh pho: ");
        Member m = library.themThanhVien(hoTen, email, sdt, new Address(duong, thanhPho));
        luu();
        System.out.println("[+] Dang ky thanh cong! Ma thanh vien: " + m.getId() + " | So the: "
                + m.getCard().getCardNumber() + " | Mat khau mac dinh: " + m.getPassword());
    }

    private void suaThanhVien() {
        String ma = NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien can sua: ");
        Member cu = library.getMemberById(ma);
        if (cu == null) {
            System.out.println("[-] Khong tim thay thanh vien " + ma);
            return;
        }
        System.out.println("(Bam Enter de giu nguyen gia tri trong ngoac)");
        String hoTen = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Ho ten", cu.getName());
        String email = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Email", cu.getEmail());
        String sdt = NhapLieu.nhapChuoiGiuNguyen(scanner, "  So dien thoai", cu.getPhone());
        String duong = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Dia chi", cu.getAddress().getStreetAddress());
        String thanhPho = NhapLieu.nhapChuoiGiuNguyen(scanner, "  Tinh / thanh pho", cu.getAddress().getCity());
        xuLyKetQua(library.suaThanhVien(ma, hoTen, email, sdt, new Address(duong, thanhPho)));
    }

    private void khoaMoKhoa() {
        String maTT = NhapLieu.nhapChuoiBatBuoc(scanner, "Ma thu thu (vd LIB-101): ");
        String matKhau = nhap("Mat khau: ");
        String maTV = NhapLieu.nhapChuoiBatBuoc(scanner, "Ma thanh vien can khoa / mo khoa: ");
        boolean khoa = NhapLieu.nhapSoNguyen(scanner, "1. Khoa the   2. Mo khoa the: ", 1, 2) == 1;
        xuLyKetQua(library.khoaMoKhoaThanhVien(maTT, matKhau, maTV, khoa));
    }

    /** DA HINH: mang kieu lop cha truu tuong User[] chua ca Librarian va Member. */
    private void xemTatCaNguoiDung() {
        User[] tatCa = library.getTatCaNguoiDung();
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-22s | %-10s | %s%n", "MA", "HO TEN", "VAI TRO", "QUYEN HAN");
        System.out.println("-------------------------------------------------------------------------------------------");
        for (User u : tatCa) {
            // getRole() va getQuyenHan() la ham truu tuong: Java tu goi ham cua Member hoac Librarian
            System.out.printf("%-8s | %-22s | %-10s | %s%n", u.getId(), u.getName(), u.getRole(), u.getQuyenHan());
        }
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("Tong: " + tatCa.length + " nguoi dung.");
    }

    // ============================== 7. TIEN PHAT ==============================

    private void menuTienPhat() {
        while (true) {
            System.out.println("\n--- [7] QUAN LY TIEN PHAT (mang Fine[]) ---");
            System.out.println(" 1. Xuat danh sach & thong ke   2. Thanh toan tien phat   3. Phat chua dong cua 1 thanh vien   0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1":
                    library.getDsPhat().xuatDanhSach();
                    System.out.printf("* Tong tien phat: %,.0f VND | Chua thu: %,.0f VND%n",
                            library.getDsPhat().tongTienPhat(), library.getDsPhat().tongTienChuaThu());
                    break;
                case "2": xuLyKetQua(library.payFine(NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma phat (vd FINE-0001): "))); break;
                case "3":
                    Fine[] chuaDong = library.getDsPhat().timChuaDongCuaThanhVien(NhapLieu.nhapChuoiBatBuoc(scanner, "Nhap ma thanh vien: "));
                    library.getDsPhat().xuatMang(chuaDong);
                    break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    // ============================== 8. THONG BAO ==============================

    private void menuThongBao() {
        while (true) {
            System.out.println("\n--- [8] THONG BAO (mang Notification[] chua Email + Buu dien - da hinh) ---");
            System.out.println(" 1. Xuat tat ca thong bao   2. Quet sach qua han & gui nhac nho   3. Thong ke theo loai   0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1": library.getDsThongBao().xuatDanhSach(); break;
                case "2":
                    int dem = library.checkOverdueBooksAndNotify();
                    luu();
                    System.out.println("[+] Da gui nhac nho cho " + dem + " luot muon qua han.");
                    break;
                case "3":
                    System.out.println("* Email   : " + library.getDsThongBao().demTheoLoai("Email"));
                    System.out.println("* Buu dien: " + library.getDsThongBao().demTheoLoai("Buu dien"));
                    break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    // ============================== 9. FILE TEXT & THONG KE ==============================

    private void menuFileText() {
        while (true) {
            System.out.println("\n--- [9] DU LIEU FILE TEXT (thu muc data/) & THONG KE ---");
            System.out.println(" 1. Ghi toan bo du lieu ra file text");
            System.out.println(" 2. Doc lai du lieu tu file text (vd sau khi sua file bang Notepad)");
            System.out.println(" 3. Xem danh sach file & so ban ghi");
            System.out.println(" 4. Thong ke tong hop");
            System.out.println(" 5. Khoi phuc du lieu mau ban dau (ghi de file)");
            System.out.println(" 0. Quay lai");
            String chon = nhap(">> Chon: ");
            switch (chon) {
                case "1":
                    luu();
                    System.out.println("[+] Da ghi du lieu ra thu muc data/");
                    TextFileStorageService.inThongTinFile(library);
                    break;
                case "2":
                    int soBanGhi = TextFileStorageService.docTatCa(library);
                    System.out.println("[+] Da doc lai " + soBanGhi + " ban ghi tu file text.");
                    TextFileStorageService.inThongTinFile(library);
                    break;
                case "3": TextFileStorageService.inThongTinFile(library); break;
                case "4": thongKeTongHop(); break;
                case "5":
                    if (NhapLieu.nhapCoKhong(scanner, "Toan bo du lieu hien tai se bi thay bang du lieu mau. Tiep tuc?")) {
                        library = SampleDataLoader.createSampleLibrary();
                        luu();
                        System.out.println("[+] Da khoi phuc du lieu mau.");
                    }
                    break;
                case "0": return;
                default: System.out.println("[-] Lua chon khong hop le!");
            }
        }
    }

    private void thongKeTongHop() {
        library.getDsSach().inThongKe();
        System.out.println("\n===== THANH VIEN & MUON TRA =====");
        System.out.println("* So thanh vien: " + library.getDsThanhVien().getSoLuong()
                + " (hoat dong: " + library.getDsThanhVien().demTheoTrangThai(AccountStatus.ACTIVE)
                + ", bi khoa: " + library.getDsThanhVien().demTheoTrangThai(AccountStatus.BLACKLISTED) + ")");
        System.out.println("* So thu thu: " + library.getDsThuThu().getSoLuong());
        System.out.println("* Luot muon: " + library.getDsMuonTra().getSoLuong()
                + " (dang muon: " + library.getDsMuonTra().demDangMuon()
                + ", qua han: " + library.getDsMuonTra().timQuaHan().length + ")");
        System.out.printf("* Tien phat: tong %,.0f VND, chua thu %,.0f VND%n",
                library.getDsPhat().tongTienPhat(), library.getDsPhat().tongTienChuaThu());
    }
}
