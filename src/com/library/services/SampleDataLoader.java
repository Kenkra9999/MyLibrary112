package com.library.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.library.enums.AccountStatus;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.enums.ReservationStatus;
import com.library.models.Address;
import com.library.models.Author;
import com.library.models.BookItem;
import com.library.models.BookLending;
import com.library.models.BookReservation;
import com.library.models.Fine;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.models.Rack;
import com.library.notifications.EmailNotification;
import com.library.notifications.PostalNotification;
import com.library.users.Librarian;
import com.library.users.Member;

/**
 * Tao du lieu mau cho lan chay dau tien (khi thu muc data/ chua co file text).
 */
public final class SampleDataLoader {

    private SampleDataLoader() {
        // Lop tien ich chi gom phuong thuc static
    }

    public static Library createSampleLibrary() {
        Library lib = new Library();
        LocalDate homNay = LocalDate.now();

        // 1. TAC GIA
        Author[] uncleBob = { new Author("Robert C. Martin", "Tac gia Clean Code") };
        Author[] bloch = { new Author("Joshua Bloch", "Tac gia Effective Java") };
        Author[] gof = { new Author("Erich Gamma", ""), new Author("Richard Helm", ""),
                new Author("Ralph Johnson", ""), new Author("John Vlissides", "") };
        Author[] nna = { new Author("Nguyen Nhat Anh", "Nha van") };
        Author[] namCao = { new Author("Nam Cao", "Nha van hien thuc") };
        Author[] fowler = { new Author("Martin Fowler", "Tac gia Refactoring") };

        // 2. KE SACH
        Rack keIT1 = new Rack(1, "Khu A - Tang 2 - Ke IT-01");
        Rack keIT2 = new Rack(2, "Khu A - Tang 2 - Ke IT-02");
        Rack keVH1 = new Rack(3, "Khu B - Tang 1 - Ke Van Hoc-01");

        // 3. BAN SAO SACH
        BookItem[] sach = {
            new BookItem("978-0132350884", "Clean Code", "Cong nghe thong tin", "Prentice Hall", "Tieng Anh", 464, uncleBob,
                    "BC-CC-001", false, 450000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 1, 15), LocalDate.of(2008, 8, 1), keIT1),
            new BookItem("978-0132350884", "Clean Code", "Cong nghe thong tin", "Prentice Hall", "Tieng Anh", 464, uncleBob,
                    "BC-CC-002", false, 520000, BookFormat.HARDCOVER, BookStatus.LOANED, LocalDate.of(2023, 1, 15), LocalDate.of(2008, 8, 1), keIT1),
            new BookItem("978-0134685991", "Effective Java (3rd Edition)", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 412, bloch,
                    "BC-EJ-001", false, 600000, BookFormat.HARDCOVER, BookStatus.AVAILABLE, LocalDate.of(2023, 3, 10), LocalDate.of(2018, 1, 6), keIT1),
            new BookItem("978-0134685991", "Effective Java (3rd Edition)", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 412, bloch,
                    "BC-EJ-002", true, 600000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 3, 10), LocalDate.of(2018, 1, 6), keIT1),
            new BookItem("978-0201633610", "Design Patterns", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 395, gof,
                    "BC-DP-001", false, 550000, BookFormat.HARDCOVER, BookStatus.AVAILABLE, LocalDate.of(2023, 2, 20), LocalDate.of(1994, 10, 31), keIT2),
            new BookItem("978-0201633610", "Design Patterns", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 395, gof,
                    "BC-DP-002", false, 480000, BookFormat.PAPERBACK, BookStatus.LOANED, LocalDate.of(2023, 2, 20), LocalDate.of(1994, 10, 31), keIT2),
            new BookItem("978-604-1-08888-8", "Cho Toi Xin Mot Ve Di Tuoi Tho", "Van hoc Viet Nam", "NXB Tre", "Tieng Viet", 210, nna,
                    "BC-VN-001", false, 95000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 4, 5), LocalDate.of(2008, 1, 1), keVH1),
            new BookItem("978-604-1-08888-8", "Cho Toi Xin Mot Ve Di Tuoi Tho", "Van hoc Viet Nam", "NXB Tre", "Tieng Viet", 210, nna,
                    "BC-VN-002", false, 120000, BookFormat.HARDCOVER, BookStatus.RESERVED, LocalDate.of(2023, 4, 5), LocalDate.of(2008, 1, 1), keVH1),
            new BookItem("978-604-2-01234-5", "Chi Pheo va cac tac pham tieu bieu", "Van hoc hien thuc", "NXB Van Hoc", "Tieng Viet", 320, namCao,
                    "BC-CP-001", false, 85000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 5, 12), LocalDate.of(1941, 2, 1), keVH1),
            new BookItem("978-0134757599", "Refactoring", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 448, fowler,
                    "BC-RF-001", false, 650000, BookFormat.HARDCOVER, BookStatus.LOANED, LocalDate.of(2023, 6, 18), LocalDate.of(2018, 11, 20), keIT2)
        };
        lib.getDsSach().themK(sach); // them k phan tu vao mang

        // 4. THU THU
        lib.getDsThuThu().them(new Librarian("LIB-101", "admin123",
                new Person("Tran Thi Minh Trang", new Address("Cau Giay", "Ha Noi"), "trang.tran@lms.edu.vn", "0912345678"),
                new LibraryCard("LC-LIB-001", "BC-CARD-LIB-01", LocalDate.of(2022, 1, 1), true),
                AccountStatus.ACTIVE, "Truong Ban Thu Vien", LocalDate.of(2022, 1, 1)));

        // 5. THANH VIEN (thanh vien MB-1004 khong co email -> nhan thong bao qua buu dien)
        Member an = new Member("MB-1001", "123456",
                new Person("Nguyen Van An", new Address("Thanh Xuan", "Ha Noi"), "an.nguyen@student.edu.vn", "0987654321"),
                new LibraryCard("LC-5001", "BC-CARD-5001", LocalDate.of(2023, 1, 10), true), AccountStatus.ACTIVE, LocalDate.of(2023, 1, 10));
        Member bach = new Member("MB-1002", "123456",
                new Person("Le Hoang Bach", new Address("Dong Da", "Ha Noi"), "bach.le@student.edu.vn", "0978123456"),
                new LibraryCard("LC-5002", "BC-CARD-5002", LocalDate.of(2023, 2, 15), true), AccountStatus.ACTIVE, LocalDate.of(2023, 2, 15));
        Member ha = new Member("MB-1003", "123456",
                new Person("Pham Thu Ha", new Address("Binh Thanh", "TP. Ho Chi Minh"), "ha.pham@student.edu.vn", "0965432198"),
                new LibraryCard("LC-5003", "BC-CARD-5003", LocalDate.of(2023, 3, 20), true), AccountStatus.ACTIVE, LocalDate.of(2023, 3, 20));
        Member dung = new Member("MB-1004", "123456",
                new Person("Hoang Van Dung", new Address("Ninh Kieu", "Can Tho"), "", "0911222333"),
                new LibraryCard("LC-5004", "BC-CARD-5004", LocalDate.of(2023, 5, 2), true), AccountStatus.ACTIVE, LocalDate.of(2023, 5, 2));
        lib.getDsThanhVien().themK(new Member[] { an, bach, ha, dung });

        // 6. LUOT MUON (khop voi trang thai LOANED cua sach o tren)
        muonMau(lib, "BC-CC-002", an, homNay.minusDays(5), homNay.plusDays(10));    // dang muon, con han
        muonMau(lib, "BC-DP-002", bach, homNay.minusDays(20), homNay.minusDays(5)); // qua han 5 ngay
        muonMau(lib, "BC-RF-001", dung, homNay.minusDays(17), homNay.minusDays(2)); // qua han 2 ngay

        // 7. DAT GIU: sach BC-VN-002 dang giu cho MB-1003 den nhan
        lib.getDsDatCho().them(new BookReservation("RES-0001", "BC-VN-002", "MB-1003",
                LocalDateTime.now().minusHours(4), ReservationStatus.PENDING));

        // 8. TIEN PHAT (khoan phat cu chua dong cua MB-1002)
        lib.getDsPhat().them(new Fine("FINE-0001", 25000, homNay.minusDays(30), "BC-EJ-001", "MB-1002", false, null));

        // 9. THONG BAO (mang Notification[] chua ca Email va Buu dien)
        lib.getDsThongBao().them(new EmailNotification("Chao mung ban Nguyen Van An tham gia thu vien! The TV: LC-5001",
                "MB-1001", "an.nguyen@student.edu.vn"));
        lib.getDsThongBao().them(new PostalNotification("The thu vien LC-5004 da duoc gui toi dia chi cua ban.",
                "MB-1004", dung.getAddress()));

        lib.dongBoBoDemMa();
        return lib;
    }

    private static void muonMau(Library lib, String maVach, Member m, LocalDate ngayMuon, LocalDate hanTra) {
        BookLending l = new BookLending(maVach, m.getId());
        l.setCreationDate(ngayMuon);
        l.setDueDate(hanTra);
        lib.getDsMuonTra().them(l);
        m.incrementTotalBooksCheckedOut();
        BookItem b = lib.getDsSach().timKiemTheoMa(maVach);
        b.setBorrowedDate(ngayMuon);
        b.setDueDate(hanTra);
    }
}
