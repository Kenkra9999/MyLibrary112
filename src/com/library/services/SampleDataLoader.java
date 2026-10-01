package com.library.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.library.enums.AccountStatus;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.enums.ReservationStatus;
import com.library.models.*;
import com.library.notifications.EmailNotification;
import com.library.notifications.PostalNotification;
import com.library.users.Librarian;
import com.library.users.Member;

/**
 * Lop nap du lieu mau phong phu cho he thong quan ly thu vien.
 */
public class SampleDataLoader {

    public static Library createSampleLibrary() {
        Library lib = new Library("Thu Vien Cong Nghe & Tri Thuc CNTT", new Address("So 1 Dai Co Viet, Hai Ba Trung", "Ha Noi"));

        // 1. TAC GIA (AUTHORS)
        Author uncleBob = new Author("Robert C. Martin (Uncle Bob)", new Address("Chicago", "USA"), "unclebob@cleancoder.com", "0901234561", "Tac gia cua Clean Code va Clean Architecture");
        Author joshuaBloch = new Author("Joshua Bloch", new Address("California", "USA"), "jbloch@effectivejava.com", "0901234562", "Chuyen gia kien truc Java, tac gia Effective Java");
        Author martinFowler = new Author("Martin Fowler", new Address("Melbourn", "UK"), "fowler@refactoring.com", "0901234563", "Chuyen gia kien truc phan mem, tac gia Refactoring");
        Author nguyenNhatAnh = new Author("Nguyen Nhat Anh", new Address("Quan 1", "TP. Ho Chi Minh"), "nnanh@nxbtre.com.vn", "0901234564", "Nha van noi tieng voi nhieu tac pham van hoc");
        Author namCao = new Author("Nam Cao", new Address("Ly Nhan", "Ha Nam"), "namcao@vanhoc.vn", "0901234565", "Nha van hien thuc xuat sac");
        Author gangOfFour = new Author("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "Nhom GoF - Design Patterns");

        // 2. KE SACH (RACKS)
        Rack rackIT1 = new Rack(1, "Khu A - Tang 2 - Ke IT-01");
        Rack rackIT2 = new Rack(2, "Khu A - Tang 2 - Ke IT-02");
        Rack rackLit1 = new Rack(3, "Khu B - Tang 1 - Ke Van Hoc-01");
        Rack rackSci1 = new Rack(4, "Khu C - Tang 3 - Ke Khoa Hoc-01");

        // 3. SACH & BAN SAO (BOOKS & BOOK ITEMS)
        Book book1 = new Book("978-0132350884", "Clean Code: A Handbook of Agile Software Craftsmanship", "Cong nghe thong tin", "Prentice Hall", "Tieng Anh", 464, Arrays.asList(uncleBob));
        BookItem item1_1 = new BookItem("978-0132350884", book1.getTitle(), book1.getSubject(), book1.getPublisher(), "Tieng Anh", 464, book1.getAuthors(),
                "BC-CC-001", false, 450000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 1, 15), LocalDate.of(2008, 8, 1), rackIT1);
        BookItem item1_2 = new BookItem("978-0132350884", book1.getTitle(), book1.getSubject(), book1.getPublisher(), "Tieng Anh", 464, book1.getAuthors(),
                "BC-CC-002", false, 520000, BookFormat.HARDCOVER, BookStatus.LOANED, LocalDate.of(2023, 1, 15), LocalDate.of(2008, 8, 1), rackIT1);
        item1_2.setBorrowedDate(LocalDate.now().minusDays(5));
        item1_2.setDueDate(LocalDate.now().plusDays(10));

        Book book2 = new Book("978-0134685991", "Effective Java (3rd Edition)", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 412, Arrays.asList(joshuaBloch));
        BookItem item2_1 = new BookItem("978-0134685991", book2.getTitle(), book2.getSubject(), book2.getPublisher(), "Tieng Anh", 412, book2.getAuthors(),
                "BC-EJ-001", false, 600000, BookFormat.HARDCOVER, BookStatus.AVAILABLE, LocalDate.of(2023, 3, 10), LocalDate.of(2018, 1, 6), rackIT1);
        BookItem item2_2 = new BookItem("978-0134685991", book2.getTitle(), book2.getSubject(), book2.getPublisher(), "Tieng Anh", 412, book2.getAuthors(),
                "BC-EJ-002", true, 600000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 3, 10), LocalDate.of(2018, 1, 6), rackIT1);

        Book book3 = new Book("978-0201633610", "Design Patterns: Elements of Reusable Object-Oriented Software", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 395, Arrays.asList(gangOfFour));
        BookItem item3_1 = new BookItem("978-0201633610", book3.getTitle(), book3.getSubject(), book3.getPublisher(), "Tieng Anh", 395, book3.getAuthors(),
                "BC-DP-001", false, 550000, BookFormat.HARDCOVER, BookStatus.AVAILABLE, LocalDate.of(2023, 2, 20), LocalDate.of(1994, 10, 31), rackIT2);
        BookItem item3_2 = new BookItem("978-0201633610", book3.getTitle(), book3.getSubject(), book3.getPublisher(), "Tieng Anh", 395, book3.getAuthors(),
                "BC-DP-002", false, 480000, BookFormat.PAPERBACK, BookStatus.LOANED, LocalDate.of(2023, 2, 20), LocalDate.of(1994, 10, 31), rackIT2);
        item3_2.setBorrowedDate(LocalDate.now().minusDays(20));
        item3_2.setDueDate(LocalDate.now().minusDays(5)); // QUA HAN 5 NGAY

        Book book4 = new Book("978-604-1-08888-8", "Cho Toi Xin Mot Ve Di Tuoi Tho", "Van hoc Viet Nam", "NXB Tre", "Tieng Viet", 210, Arrays.asList(nguyenNhatAnh));
        BookItem item4_1 = new BookItem("978-604-1-08888-8", book4.getTitle(), book4.getSubject(), book4.getPublisher(), "Tieng Viet", 210, book4.getAuthors(),
                "BC-VN-001", false, 95000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 4, 5), LocalDate.of(2008, 1, 1), rackLit1);
        BookItem item4_2 = new BookItem("978-604-1-08888-8", book4.getTitle(), book4.getSubject(), book4.getPublisher(), "Tieng Viet", 210, book4.getAuthors(),
                "BC-VN-002", false, 120000, BookFormat.HARDCOVER, BookStatus.RESERVED, LocalDate.of(2023, 4, 5), LocalDate.of(2008, 1, 1), rackLit1);

        Book book5 = new Book("978-604-2-01234-5", "Chi Pheo va cac tac pham tieu bieu", "Van hoc hien thuc", "NXB Van Hoc", "Tieng Viet", 320, Arrays.asList(namCao));
        BookItem item5_1 = new BookItem("978-604-2-01234-5", book5.getTitle(), book5.getSubject(), book5.getPublisher(), "Tieng Viet", 320, book5.getAuthors(),
                "BC-CP-001", false, 85000, BookFormat.PAPERBACK, BookStatus.AVAILABLE, LocalDate.of(2023, 5, 12), LocalDate.of(1941, 2, 1), rackLit1);

        Book book6 = new Book("978-0134757599", "Refactoring: Improving the Design of Existing Code", "Cong nghe thong tin", "Addison-Wesley", "Tieng Anh", 448, Arrays.asList(martinFowler));
        BookItem item6_1 = new BookItem("978-0134757599", book6.getTitle(), book6.getSubject(), book6.getPublisher(), "Tieng Anh", 448, book6.getAuthors(),
                "BC-RF-001", false, 650000, BookFormat.HARDCOVER, BookStatus.AVAILABLE, LocalDate.of(2023, 6, 18), LocalDate.of(2018, 11, 20), rackIT2);

        lib.getCatalog().addBook(book1);
        lib.getCatalog().addBook(book2);
        lib.getCatalog().addBook(book3);
        lib.getCatalog().addBook(book4);
        lib.getCatalog().addBook(book5);
        lib.getCatalog().addBook(book6);

        lib.getCatalog().addBookItem(item1_1);
        lib.getCatalog().addBookItem(item1_2);
        lib.getCatalog().addBookItem(item2_1);
        lib.getCatalog().addBookItem(item2_2);
        lib.getCatalog().addBookItem(item3_1);
        lib.getCatalog().addBookItem(item3_2);
        lib.getCatalog().addBookItem(item4_1);
        lib.getCatalog().addBookItem(item4_2);
        lib.getCatalog().addBookItem(item5_1);
        lib.getCatalog().addBookItem(item6_1);

        // 4. THU THU (LIBRARIANS)
        LibraryCard libCard1 = new LibraryCard("LC-LIB-001", "BC-CARD-LIB-01", LocalDate.of(2022, 1, 1), true);
        Librarian libAdmin = new Librarian("LIB-101", "admin123",
                new Person("Tran Thi Minh Trang", new Address("Cau Giay", "Ha Noi"), "trang.tran@lms.edu.vn", "0912345678"),
                libCard1, AccountStatus.ACTIVE, "Truong Ban Thu Vien", LocalDate.of(2022, 1, 1));
        lib.registerLibrarian(libAdmin);

        // 5. THANH VIEN (MEMBERS)
        LibraryCard memCard1 = new LibraryCard("LC-5001", "BC-CARD-5001", LocalDate.of(2023, 1, 10), true);
        Member mem1 = new Member("MB-1001", "123456",
                new Person("Nguyen Van An", new Address("Thanh Xuan", "Ha Noi"), "an.nguyen@student.edu.vn", "0987654321"),
                memCard1, AccountStatus.ACTIVE, LocalDate.of(2023, 1, 10));
        mem1.setTotalBooksCheckedOut(1);

        LibraryCard memCard2 = new LibraryCard("LC-5002", "BC-CARD-5002", LocalDate.of(2023, 2, 15), true);
        Member mem2 = new Member("MB-1002", "123456",
                new Person("Le Hoang Bach", new Address("Dong Da", "Ha Noi"), "bach.le@student.edu.vn", "0978123456"),
                memCard2, AccountStatus.ACTIVE, LocalDate.of(2023, 2, 15));
        mem2.setTotalBooksCheckedOut(1);

        LibraryCard memCard3 = new LibraryCard("LC-5003", "BC-CARD-5003", LocalDate.of(2023, 3, 20), true);
        Member mem3 = new Member("MB-1003", "123456",
                new Person("Pham Thu Ha", new Address("Binh Thanh", "TP. Ho Chi Minh"), "ha.pham@student.edu.vn", "0965432198"),
                memCard3, AccountStatus.ACTIVE, LocalDate.of(2023, 3, 20));

        lib.registerMember(mem1);
        lib.registerMember(mem2);
        lib.registerMember(mem3);

        // 6. LUOT MUON (BOOK LENDINGS)
        BookLending lending1 = new BookLending("BC-CC-002", "MB-1001");
        lending1.setCreationDate(LocalDate.now().minusDays(5));
        lending1.setDueDate(LocalDate.now().plusDays(10));
        lib.getLendings().add(lending1);

        BookLending lending2 = new BookLending("BC-DP-002", "MB-1002");
        lending2.setCreationDate(LocalDate.now().minusDays(20));
        lending2.setDueDate(LocalDate.now().minusDays(5));
        lib.getLendings().add(lending2);

        // 7. DAT GIU SACH (RESERVATIONS)
        BookReservation res1 = new BookReservation("RES-SAMPLE-01", "BC-VN-002", "MB-1003", LocalDateTime.now().minusHours(4), ReservationStatus.PENDING);
        lib.getReservations().add(res1);

        // 8. TIEN PHAT (FINES)
        Fine fine1 = new Fine("FINE-001", 25000, LocalDate.now().minusDays(1), "BC-DP-002", "MB-1002", false, null);
        lib.getFines().add(fine1);

        // 9. THONG BAO (NOTIFICATIONS)
        lib.getNotifications().add(new EmailNotification("Chao mung ban Nguyen Van An tham gia thu vien! The TV cua ban: LC-5001", "MB-1001", "an.nguyen@student.edu.vn"));
        lib.getNotifications().add(new EmailNotification("Canh bao qua han: Cuon sach [Design Patterns] da tre han 5 ngay!", "MB-1002", "bach.le@student.edu.vn"));
        lib.getNotifications().add(new PostalNotification("The thu vien moi duoc phat hanh gui toi dia chi cua ban.", "MB-1003", mem3.getAddress()));

        return lib;
    }
}
