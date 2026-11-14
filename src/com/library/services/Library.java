package com.library.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.library.collections.DanhSachDatCho;
import com.library.collections.DanhSachMuonTra;
import com.library.collections.DanhSachPhat;
import com.library.collections.DanhSachSach;
import com.library.collections.DanhSachThanhVien;
import com.library.collections.DanhSachThongBao;
import com.library.collections.DanhSachThuThu;
import com.library.collections.IDanhSach;
import com.library.enums.AccountStatus;
import com.library.enums.BookStatus;
import com.library.enums.ReservationStatus;
import com.library.models.Address;
import com.library.models.BookItem;
import com.library.models.BookLending;
import com.library.models.BookReservation;
import com.library.models.Fine;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.notifications.EmailNotification;
import com.library.notifications.Notification;
import com.library.notifications.PostalNotification;
import com.library.users.Librarian;
import com.library.users.Member;
import com.library.users.User;
import com.library.utils.Constants;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * Class Library: dieu phoi toan bo nghiep vu thu vien (muon, tra, gia han, dat giu, phat, thong bao).
 * Toan bo du lieu duoc luu trong cac LOP MANG DOI TUONG (DanhSachXxx), khong dung ArrayList / HashMap.
 */
public class Library {
    private String name;
    private Address address;

    private final DanhSachSach dsSach = new DanhSachSach();
    private final DanhSachThanhVien dsThanhVien = new DanhSachThanhVien();
    private final DanhSachThuThu dsThuThu = new DanhSachThuThu();
    private final DanhSachMuonTra dsMuonTra = new DanhSachMuonTra();
    private final DanhSachDatCho dsDatCho = new DanhSachDatCho();
    private final DanhSachPhat dsPhat = new DanhSachPhat();
    private final DanhSachThongBao dsThongBao = new DanhSachThongBao();

    public Library() {
        this(Constants.DEFAULT_LIBRARY_NAME,
                new Address(Constants.DEFAULT_LIBRARY_STREET, Constants.DEFAULT_LIBRARY_CITY));
    }

    public Library(String name, Address address) {
        this.name = name;
        this.address = address != null ? address : new Address();
    }

    // ===================== GETTER =====================

    public String getName() { return name; }
    public Address getAddress() { return address; }

    public DanhSachSach getDsSach() { return dsSach; }
    public DanhSachThanhVien getDsThanhVien() { return dsThanhVien; }
    public DanhSachThuThu getDsThuThu() { return dsThuThu; }
    public DanhSachMuonTra getDsMuonTra() { return dsMuonTra; }
    public DanhSachDatCho getDsDatCho() { return dsDatCho; }
    public DanhSachPhat getDsPhat() { return dsPhat; }
    public DanhSachThongBao getDsThongBao() { return dsThongBao; }

    /**
     * Tat ca lop mang duoi dang mang interface IDanhSach<?>[] (DA HINH):
     * noi goi chi can biet interface, khong can biet tung lop cu the.
     */
    public IDanhSach<?>[] getTatCaDanhSach() {
        return new IDanhSach<?>[] { dsSach, dsThanhVien, dsThuThu, dsMuonTra, dsDatCho, dsPhat, dsThongBao };
    }

    /**
     * Gop thu thu va thanh vien vao 1 mang kieu lop cha truu tuong User[] (DA HINH):
     * phan tu thuc te la Librarian hoac Member; goi u.getRole() / u.getQuyenHan() se chay ham cua lop con.
     */
    public User[] getTatCaNguoiDung() {
        Librarian[] tt = dsThuThu.getDanhSach();
        Member[] tv = dsThanhVien.getDanhSach();
        User[] tatCa = new User[tt.length + tv.length];
        int k = 0;
        for (Librarian l : tt) tatCa[k++] = l;   // upcasting Librarian -> User
        for (Member m : tv) tatCa[k++] = m;      // upcasting Member -> User
        return tatCa;
    }

    /**
     * Sau khi doc du lieu tu file: ghi nhan moi ma da co de bo dem static trong IdGenerator
     * tang len bang ma lon nhat -> ma moi khong bao gio trung ma cu.
     */
    public void dongBoBoDemMa() {
        for (Member m : dsThanhVien.getDanhSach()) {
            IdGenerator.ghiNhanMaDaCo(m.getId());
            if (m.getCard() != null) IdGenerator.ghiNhanMaDaCo(m.getCard().getCardNumber());
        }
        for (Librarian l : dsThuThu.getDanhSach()) {
            IdGenerator.ghiNhanMaDaCo(l.getId());
            if (l.getCard() != null) IdGenerator.ghiNhanMaDaCo(l.getCard().getCardNumber());
        }
        for (BookItem b : dsSach.getDanhSach()) IdGenerator.ghiNhanMaDaCo(b.getBarcode());
        for (BookLending l : dsMuonTra.getDanhSach()) IdGenerator.ghiNhanMaDaCo(l.getLendingId());
        for (BookReservation r : dsDatCho.getDanhSach()) IdGenerator.ghiNhanMaDaCo(r.getReservationId());
        for (Fine f : dsPhat.getDanhSach()) IdGenerator.ghiNhanMaDaCo(f.getFineId());
        for (Notification n : dsThongBao.getDanhSach()) IdGenerator.ghiNhanMaDaCo(n.getNotificationId());
    }

    // ===================== QUAN LY SACH =====================

    public OperationResult themSach(BookItem item) {
        if (item == null || item.getBarcode() == null) return OperationResult.failure("Sach khong hop le!");
        if (!dsSach.them(item)) return OperationResult.failure("Ma vach " + item.getBarcode() + " da ton tai!");
        return OperationResult.success("Da them sach: " + item.getTitle() + " (Ma vach: " + item.getBarcode() + ")");
    }

    public OperationResult suaSach(String maVach, BookItem sachMoi) {
        if (!dsSach.sua(maVach, sachMoi)) return OperationResult.failure("Khong tim thay sach " + maVach);
        return OperationResult.success("Da cap nhat thong tin sach " + maVach);
    }

    public OperationResult xoaSach(String maVach) {
        BookItem item = dsSach.timKiemTheoMa(maVach);
        if (item == null) return OperationResult.failure("Khong tim thay sach " + maVach);
        if (item.getStatus() == BookStatus.LOANED) {
            return OperationResult.failure("Sach dang duoc muon, khong the xoa!");
        }
        if (dsDatCho.timConHieuLuc(maVach) != null) {
            return OperationResult.failure("Sach dang co phieu dat giu, hay huy dat giu truoc!");
        }
        dsSach.xoa(maVach);
        return OperationResult.success("Da xoa sach " + maVach + " - " + item.getTitle());
    }

    // ===================== QUAN LY THANH VIEN =====================

    public Member getMemberById(String memberId) {
        return dsThanhVien.timKiemTheoMa(memberId);
    }

    /** Dang ky thanh vien moi: tu sinh ma thanh vien va so the (khong trung ma cu). */
    public Member themThanhVien(String hoTen, String email, String sdt, Address diaChi) {
        String maTV;
        do {
            maTV = IdGenerator.taoMaThanhVien();
        } while (dsThanhVien.timKiemTheoMa(maTV) != null); // chot chan: chac chan khong trung
        String soThe = IdGenerator.taoSoThe();
        LibraryCard the = new LibraryCard(soThe, "BC-CARD-" + soThe.substring(3));
        Member m = new Member(maTV, Constants.DEFAULT_MEMBER_PASSWORD, new Person(hoTen, diaChi, email, sdt), the);
        dsThanhVien.them(m);
        return m;
    }

    /** Sua ho ten / email / sdt / dia chi: tao doi tuong moi giu nguyen ma, the, trang thai roi goi sua() cua mang. */
    public OperationResult suaThanhVien(String maTV, String hoTen, String email, String sdt, Address diaChi) {
        Member cu = dsThanhVien.timKiemTheoMa(maTV);
        if (cu == null) return OperationResult.failure("Khong tim thay thanh vien " + maTV);
        Member moi = new Member(cu.getId(), cu.getPassword(), new Person(hoTen, diaChi, email, sdt),
                cu.getCard(), cu.getStatus(), cu.getDateOfMembership());
        moi.setTotalBooksCheckedOut(cu.getTotalBooksCheckedOut());
        dsThanhVien.sua(maTV, moi);
        return OperationResult.success("Da cap nhat thanh vien " + maTV);
    }

    public OperationResult xoaThanhVien(String maTV) {
        Member m = dsThanhVien.timKiemTheoMa(maTV);
        if (m == null) return OperationResult.failure("Khong tim thay thanh vien " + maTV);
        if (m.getTotalBooksCheckedOut() > 0) {
            return OperationResult.failure("Thanh vien dang muon " + m.getTotalBooksCheckedOut() + " cuon, khong the xoa!");
        }
        if (dsPhat.coPhatChuaDong(maTV)) {
            return OperationResult.failure("Thanh vien con tien phat chua dong, khong the xoa!");
        }
        if (dsDatCho.demConHieuLucCuaThanhVien(maTV) > 0) {
            return OperationResult.failure("Thanh vien con phieu dat giu, hay huy truoc khi xoa!");
        }
        dsThanhVien.xoa(maTV);
        return OperationResult.success("Da xoa thanh vien " + maTV + " - " + m.getName());
    }

    /** Thu thu (phai dang nhap dung mat khau) khoa hoac mo khoa the thanh vien. */
    public OperationResult khoaMoKhoaThanhVien(String maThuThu, String matKhau, String maTV, boolean khoa) {
        Librarian thuThu = dsThuThu.dangNhap(maThuThu, matKhau);
        if (thuThu == null) return OperationResult.failure("Sai ma thu thu hoac mat khau!");
        Member m = dsThanhVien.timKiemTheoMa(maTV);
        if (m == null) return OperationResult.failure("Khong tim thay thanh vien " + maTV);
        if (khoa) {
            thuThu.blockMember(m);
        } else {
            thuThu.unblockMember(m);
        }
        return OperationResult.success("Thu thu " + thuThu.getName() + " da " + (khoa ? "KHOA" : "MO KHOA")
                + " the cua " + m.getName() + " (" + m.getStatus().getDisplayName() + ")");
    }

    // ===================== MUON SACH (R7, R8) =====================

    public OperationResult checkoutBook(String maVach, String maTV) {
        Member member = dsThanhVien.timKiemTheoMa(maTV);
        if (member == null) return OperationResult.failure("Khong tim thay thanh vien: " + maTV);
        if (member.getStatus() != AccountStatus.ACTIVE) {
            return OperationResult.failure("Tai khoan thanh vien khong hoat dong (" + member.getStatus() + ")");
        }
        if (member.getTotalBooksCheckedOut() >= Constants.MAX_BOOKS_ISSUED_TO_A_USER) {
            return OperationResult.failure("Thanh vien da muon toi da " + Constants.MAX_BOOKS_ISSUED_TO_A_USER + " cuon!");
        }
        if (dsPhat.coPhatChuaDong(maTV)) {
            return OperationResult.failure("Thanh vien con tien phat chua dong. Vui long nop phat truoc khi muon tiep.");
        }

        BookItem item = dsSach.timKiemTheoMa(maVach);
        if (item == null) return OperationResult.failure("Khong tim thay sach voi ma vach: " + maVach);
        if (item.isReferenceOnly()) return OperationResult.failure("Sach tham khao chi doc tai cho, khong cho muon!");
        if (item.getStatus() == BookStatus.LOANED || item.getStatus() == BookStatus.LOST) {
            return OperationResult.failure("Sach hien khong co san (" + item.getStatus() + ")");
        }

        BookReservation datCho = dsDatCho.timConHieuLuc(maVach);
        if (datCho != null && !datCho.getMemberId().equalsIgnoreCase(maTV)) {
            return OperationResult.failure("Sach dang duoc thanh vien " + datCho.getMemberId() + " dat giu!");
        }
        if (datCho == null && item.getStatus() != BookStatus.AVAILABLE) {
            return OperationResult.failure("Sach hien khong co san (" + item.getStatus() + ")");
        }
        if (datCho != null) {
            datCho.setStatus(ReservationStatus.COMPLETED); // nguoi dat giu den nhan sach
        }

        LocalDate homNay = LocalDate.now();
        LocalDate hanTra = homNay.plusDays(Constants.MAX_LENDING_DAYS);
        item.setStatus(BookStatus.LOANED);
        item.setBorrowedDate(homNay);
        item.setDueDate(hanTra);

        BookLending luotMuon = new BookLending(item.getBarcode(), member.getId()); // luu ma chuan, khong luu chu nguoi dung go
        luotMuon.setDueDate(hanTra);
        dsMuonTra.them(luotMuon);
        member.incrementTotalBooksCheckedOut();

        return OperationResult.success("Muon sach thanh cong! Ma muon: " + luotMuon.getLendingId()
                + " | Han tra: " + DateUtils.formatDate(hanTra));
    }

    // ===================== TRA SACH (R1, R10) & TINH PHAT (use case Pay fine) =====================

    public OperationResult returnBook(String maVach, String maTV) {
        BookItem item = dsSach.timKiemTheoMa(maVach);
        if (item == null) return OperationResult.failure("Khong tim thay sach voi ma vach: " + maVach);
        Member member = dsThanhVien.timKiemTheoMa(maTV);
        if (member == null) return OperationResult.failure("Khong tim thay thanh vien: " + maTV);

        BookLending luotMuon = dsMuonTra.timDangMuon(maVach, maTV);
        if (luotMuon == null) {
            return OperationResult.failure("Thanh vien " + maTV + " khong muon cuon sach " + maVach);
        }

        LocalDate homNay = LocalDate.now();
        luotMuon.setReturned(true);
        luotMuon.setReturnDate(homNay);
        member.decrementTotalBooksCheckedOut();

        String thongBao = "Tra sach thanh cong!";
        if (homNay.isAfter(luotMuon.getDueDate())) {
            long soNgayTre = ChronoUnit.DAYS.between(luotMuon.getDueDate(), homNay);
            double tienPhat = soNgayTre * Constants.FINE_PER_DAY;
            Fine phat = new Fine(tienPhat, item.getBarcode(), member.getId());
            dsPhat.them(phat);
            thongBao += String.format(" Tre %d ngay -> phat %,.0f VND (Ma phat: %s)", soNgayTre, tienPhat, phat.getFineId());
            sendNotification(taoThongBao(member, "Sach [" + item.getTitle() + "] tra tre " + soNgayTre
                    + " ngay. Phi phat: " + String.format("%,.0f VND", tienPhat)));
        }

        // Neu co nguoi dang cho dat giu -> giu sach cho ho va bao cho ho den nhan
        BookReservation datCho = dsDatCho.timConHieuLuc(maVach);
        item.setBorrowedDate(null);
        item.setDueDate(null);
        if (datCho != null && datCho.getStatus() == ReservationStatus.WAITING) {
            datCho.setStatus(ReservationStatus.PENDING);
            item.setStatus(BookStatus.RESERVED);
            Member nguoiDat = dsThanhVien.timKiemTheoMa(datCho.getMemberId());
            if (nguoiDat != null) {
                sendNotification(taoThongBao(nguoiDat, "Sach ban dat giu [" + item.getTitle()
                        + "] da co san. Vui long den nhan trong 2 ngay!"));
            }
            thongBao += " Sach duoc giu cho thanh vien " + datCho.getMemberId() + ".";
        } else {
            item.setStatus(BookStatus.AVAILABLE);
        }
        return OperationResult.success(thongBao);
    }

    // ===================== GIA HAN (R11) =====================

    public OperationResult renewBook(String maVach, String maTV) {
        Member member = dsThanhVien.timKiemTheoMa(maTV);
        if (member == null) return OperationResult.failure("Khong tim thay thanh vien: " + maTV);
        if (member.getStatus() != AccountStatus.ACTIVE) {
            return OperationResult.failure("Tai khoan thanh vien khong hoat dong, khong the gia han!");
        }
        BookLending luotMuon = dsMuonTra.timDangMuon(maVach, maTV);
        if (luotMuon == null) return OperationResult.failure("Khong tim thay luot muon dang hoat dong cua sach nay!");
        BookReservation datCho = dsDatCho.timConHieuLuc(maVach);
        if (datCho != null && !datCho.getMemberId().equalsIgnoreCase(maTV)) {
            return OperationResult.failure("Khong the gia han: sach da duoc thanh vien khac dat giu!");
        }
        if (luotMuon.isOverdue()) {
            return OperationResult.failure("Khong the gia han: sach da qua han! Vui long tra sach va nop phat.");
        }
        LocalDate hanMoi = luotMuon.getDueDate().plusDays(Constants.RENEW_EXTENSION_DAYS);
        luotMuon.setDueDate(hanMoi);
        BookItem item = dsSach.timKiemTheoMa(maVach);
        if (item != null) item.setDueDate(hanMoi);
        return OperationResult.success("Gia han them " + Constants.RENEW_EXTENSION_DAYS + " ngay! Han tra moi: "
                + DateUtils.formatDate(hanMoi));
    }

    // ===================== DAT GIU (R9, R13) =====================

    public OperationResult reserveBook(String maVach, String maTV) {
        Member member = dsThanhVien.timKiemTheoMa(maTV);
        if (member == null) return OperationResult.failure("Khong tim thay thanh vien: " + maTV);
        if (member.getStatus() != AccountStatus.ACTIVE) {
            return OperationResult.failure("Tai khoan thanh vien khong hoat dong!");
        }
        BookItem item = dsSach.timKiemTheoMa(maVach);
        if (item == null) return OperationResult.failure("Khong tim thay sach: " + maVach);
        if (item.isReferenceOnly()) return OperationResult.failure("Sach chi doc tai cho, khong the dat giu!");
        if (item.getStatus() == BookStatus.AVAILABLE) {
            return OperationResult.failure("Sach dang co san tren " + item.getPlacedAt() + ", ban co the muon truc tiep!");
        }
        if (dsMuonTra.timDangMuon(maVach, maTV) != null) {
            return OperationResult.failure("Ban dang muon chinh cuon sach nay!");
        }
        BookReservation daCo = dsDatCho.timConHieuLuc(maVach);
        if (daCo != null) {
            return OperationResult.failure(daCo.getMemberId().equalsIgnoreCase(maTV)
                    ? "Ban da dat giu cuon sach nay roi!" : "Cuon sach nay da co nguoi khac dat giu (R9)!");
        }
        BookReservation datCho = new BookReservation(item.getBarcode(), member.getId());
        dsDatCho.them(datCho);
        return OperationResult.success("Dat giu thanh cong! Ma dat giu: " + datCho.getReservationId()
                + ". He thong se thong bao khi sach co san.");
    }

    public OperationResult cancelReservation(String maDatCho) {
        BookReservation r = dsDatCho.timKiemTheoMa(maDatCho);
        if (r == null) return OperationResult.failure("Khong tim thay phieu dat giu: " + maDatCho);
        if (r.getStatus() != ReservationStatus.WAITING && r.getStatus() != ReservationStatus.PENDING) {
            return OperationResult.failure("Phieu dang o trang thai '" + r.getStatus().getDisplayName() + "', khong the huy!");
        }
        r.setStatus(ReservationStatus.CANCELED);
        BookItem item = dsSach.timKiemTheoMa(r.getItemBarcode());
        if (item != null && item.getStatus() == BookStatus.RESERVED) {
            item.setStatus(BookStatus.AVAILABLE); // sach dang giu cho nguoi nay -> tra ve ke
        }
        return OperationResult.success("Da huy phieu dat giu " + maDatCho);
    }

    // ===================== TIEN PHAT (use case Pay fine) =====================

    public OperationResult payFine(String maPhat) {
        Fine phat = dsPhat.timKiemTheoMa(maPhat);
        if (phat == null) return OperationResult.failure("Khong tim thay bien lai phat: " + maPhat);
        if (!phat.payFine()) return OperationResult.failure("Khoan phat nay da duoc thanh toan truoc do!");
        return OperationResult.success(String.format("Thanh toan thanh cong %,.0f VND (Ma phat: %s)", phat.getAmount(), maPhat));
    }

    // ===================== THONG BAO (R12) =====================

    /**
     * Chon kenh thong bao: thanh vien co email -> EmailNotification, khong co -> PostalNotification.
     * Kieu tra ve la lop cha Notification (DA HINH).
     */
    private Notification taoThongBao(Member m, String noiDung) {
        if (m.getEmail() != null && !m.getEmail().isEmpty()) {
            return new EmailNotification(noiDung, m.getId(), m.getEmail());
        }
        return new PostalNotification(noiDung, m.getId(), m.getAddress());
    }

    /** Gui thong bao: notification.sendNotification() tu chay dung ham cua Email hoac Buu dien. */
    public void sendNotification(Notification notification) {
        if (notification != null) {
            notification.sendNotification();
            dsThongBao.them(notification);
        }
    }

    /** Quet cac luot muon qua han va gui thong bao nhac nho. */
    public int checkOverdueBooksAndNotify() {
        int dem = 0;
        LocalDate homNay = LocalDate.now();
        for (BookLending l : dsMuonTra.timQuaHan()) {
            Member m = dsThanhVien.timKiemTheoMa(l.getMemberId());
            BookItem item = dsSach.timKiemTheoMa(l.getItemBarcode());
            if (m != null && item != null) {
                long soNgay = ChronoUnit.DAYS.between(l.getDueDate(), homNay);
                sendNotification(taoThongBao(m, "Nhac nho: sach [" + item.getTitle() + "] da qua han " + soNgay
                        + " ngay. Vui long tra som de tranh phat them!"));
                dem++;
            }
        }
        return dem;
    }
}
