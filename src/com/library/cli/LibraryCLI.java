package com.library.cli;

import java.util.List;
import java.util.Scanner;

import com.library.enums.BookFormat;
import com.library.models.*;
import com.library.notifications.Notification;
import com.library.services.DataStorageService;
import com.library.services.Library;
import com.library.services.SampleDataLoader;
import com.library.users.Member;
import com.library.utils.IdGenerator;

/**
 * Giao dien Dong lenh (CLI Interactive Menu) cho Do an Quan ly Thu vien OOP.
 * Toan bo van ban duoc chuan hoa Tieng Viet khong dau, khong dung emoji
 * de tranh bi loi font chu '?' tren Windows Command Prompt.
 */
public class LibraryCLI {
    private final Library library;
    private final Scanner scanner;

    public LibraryCLI() {
        Library loaded = DataStorageService.loadData();
        if (loaded != null) {
            this.library = loaded;
        } else {
            this.library = SampleDataLoader.createSampleLibrary();
            DataStorageService.saveData(this.library);
        }
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printBanner();
            printMainMenu();
            System.out.print(">> Nhap lua chon cua ban (0 - 8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    menuCatalogAndSearch();
                    break;
                case "2":
                    menuBorrowBook();
                    break;
                case "3":
                    menuReturnBook();
                    break;
                case "4":
                    menuRenewBook();
                    break;
                case "5":
                    menuReserveBook();
                    break;
                case "6":
                    menuMemberManagement();
                    break;
                case "7":
                    menuFineManagement();
                    break;
                case "8":
                    menuNotifications();
                    break;
                case "0":
                    DataStorageService.saveData(library);
                    System.out.println("\n[+] Da luu toan bo du lieu. Tam biet!\n");
                    return;
                default:
                    System.out.println("\n[-] Lua chon khong hop le. Vui long thu lai!\n");
            }
        }
    }

    private void printBanner() {
        System.out.println("\n========================================================================");
        System.out.println("   HE THONG QUAN LY THU VIEN HUONG DOI TUONG (OOP LIBRARY LMS)");
        System.out.println("   " + library.getName());
        System.out.println("========================================================================");
    }

    private void printMainMenu() {
        System.out.println(" 1. Tra cuu & Quan ly Danh muc Sach (Catalog Search & Management)");
        System.out.println(" 2. Cho muon sach (Checkout Book - R7, R8)");
        System.out.println(" 3. Tra sach & Tinh tien phat qua han (Return Book & Penalty - R1, R15)");
        System.out.println(" 4. Gia han thoi gian muon sach (Renew Book - R11)");
        System.out.println(" 5. Dat giu sach khi het ban sao (Reserve Book - R9, R13)");
        System.out.println(" 6. Quan ly thanh vien & Cap the thu vien (Member & Library Card - R5, R6)");
        System.out.println(" 7. Quan ly & Thu tien phat qua han (Fine Management - R15)");
        System.out.println(" 8. Hop thu thong bao qua han / Dat sach (Notifications - R12)");
        System.out.println(" 0. Luu du lieu & Thoat chuong trinh");
        System.out.println("------------------------------------------------------------------------");
    }

    private void menuCatalogAndSearch() {
        System.out.println("\n--- [1] TRA CUU & QUAN LY DANH MUC SACH ---");
        System.out.println("1. Tim sach theo Tua de");
        System.out.println("2. Tim sach theo Tac gia");
        System.out.println("3. Tim sach theo The loai / Chu de");
        System.out.println("4. Xem toan bo cac ban sao sach (BookItem)");
        System.out.println("5. Them sach moi vao thu vien");
        System.out.println("0. Quay lai Menu chinh");
        System.out.print(">> Chon: ");
        String subChoice = scanner.nextLine().trim();

        switch (subChoice) {
            case "1":
                System.out.print("Nhap tua de can tim: ");
                String title = scanner.nextLine().trim();
                List<Book> booksByTitle = library.getCatalog().searchByTitle(title);
                displayBooks(booksByTitle);
                break;
            case "2":
                System.out.print("Nhap ten tac gia can tim: ");
                String author = scanner.nextLine().trim();
                List<Book> booksByAuthor = library.getCatalog().searchByAuthor(author);
                displayBooks(booksByAuthor);
                break;
            case "3":
                System.out.print("Nhap the loai / chu de: ");
                String subject = scanner.nextLine().trim();
                List<Book> booksBySubject = library.getCatalog().searchBySubject(subject);
                displayBooks(booksBySubject);
                break;
            case "4":
                displayBookItems(library.getCatalog().getAllBookItems());
                break;
            case "5":
                addNewBookItemCLI();
                break;
            default:
                break;
        }
    }

    private void displayBooks(List<Book> list) {
        System.out.println("\n--- KET QUA TIM KIEM SACH (" + list.size() + " dau sach) ---");
        if (list.isEmpty()) {
            System.out.println("Khong tim thay sach nao phu hop.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Book b = list.get(i);
            System.out.println((i + 1) + ". " + b.toString());
        }
    }

    private void displayBookItems(List<BookItem> list) {
        System.out.println("\n--- DANH SACH CAC BAN SAO SACH (" + list.size() + " cuon) ---");
        for (BookItem item : list) {
            System.out.printf("[%s] %s | %s | %s | Gia: %,.0f VND | Trang thai: %s | Ke: %s\n",
                    item.getBarcode(), item.getTitle(), item.getAuthorsAsString(),
                    item.getFormat().getDisplayName(), item.getPrice(),
                    item.getStatus().getDisplayName(), item.getPlacedAt());
        }
    }

    private void addNewBookItemCLI() {
        System.out.println("\n--- THEM SACH MOI ---");
        System.out.print("Nhap ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Nhap Tua de sach: ");
        String title = scanner.nextLine().trim();
        System.out.print("Nhap Ten tac gia: ");
        String authorName = scanner.nextLine().trim();
        System.out.print("Nhap The loai (CNTT / Van hoc / ...): ");
        String subject = scanner.nextLine().trim();
        System.out.print("Nhap Nha xuat ban: ");
        String publisher = scanner.nextLine().trim();

        String barcode = IdGenerator.generateBarcode(isbn, 1);
        Rack rack = new Rack(1, "Khu A - Ke 1");

        Book baseBook = new Book(isbn, title, subject, publisher, authorName);
        BookItem item = new BookItem(baseBook, barcode, 150000, BookFormat.PAPERBACK, rack);

        library.getCatalog().addBookItem(item);
        DataStorageService.saveData(library);
        System.out.println("[+] Da them sach moi thanh cong! Ma vach: " + barcode);
    }

    private void menuBorrowBook() {
        System.out.println("\n--- [2] CAP PHAT / MUON SACH (CHECKOUT) ---");
        System.out.print("Nhap Ma Thanh Vien (VD: MB-1001): ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Nhap Ma Vach Sach Can Muon (VD: BC-CC-001): ");
        String barcode = scanner.nextLine().trim();

        Library.OperationResult result = library.checkoutBook(barcode, memberId);
        if (result.isSuccess()) {
            System.out.println("[+] " + result.getMessage());
            DataStorageService.saveData(library);
        } else {
            System.out.println("[-] " + result.getMessage());
        }
    }

    private void menuReturnBook() {
        System.out.println("\n--- [3] TRA SACH & TINH TIEN PHAT ---");
        System.out.print("Nhap Ma Thanh Vien Tra Sach: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Nhap Ma Vach Sach Hoan Tra: ");
        String barcode = scanner.nextLine().trim();

        Library.OperationResult result = library.returnBook(barcode, memberId);
        if (result.isSuccess()) {
            System.out.println("[+] " + result.getMessage());
            DataStorageService.saveData(library);
        } else {
            System.out.println("[-] " + result.getMessage());
        }
    }

    private void menuRenewBook() {
        System.out.println("\n--- [4] GIA HAN THOI GIAN MUON SACH ---");
        System.out.print("Nhap Ma Thanh Vien: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Nhap Ma Vach Sach Can Gia Han: ");
        String barcode = scanner.nextLine().trim();

        Library.OperationResult result = library.renewBook(barcode, memberId);
        if (result.isSuccess()) {
            System.out.println("[+] " + result.getMessage());
            DataStorageService.saveData(library);
        } else {
            System.out.println("[-] " + result.getMessage());
        }
    }

    private void menuReserveBook() {
        System.out.println("\n--- [5] DAT GIU SACH (RESERVATION) ---");
        System.out.print("Nhap Ma Thanh Vien Dat Giu: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Nhap Ma Vach Sach Can Dat Giu: ");
        String barcode = scanner.nextLine().trim();

        Library.OperationResult result = library.reserveBook(barcode, memberId);
        if (result.isSuccess()) {
            System.out.println("[+] " + result.getMessage());
            DataStorageService.saveData(library);
        } else {
            System.out.println("[-] " + result.getMessage());
        }
    }

    private void menuMemberManagement() {
        System.out.println("\n--- [6] QUAN LY THANH VIEN & THE THU VIEN ---");
        System.out.println("1. Xem danh sach toan bo thanh vien");
        System.out.println("2. Dang ky thanh vien moi & Cap the thu vien");
        System.out.println("3. Xem lich su muon tra cua thanh vien");
        System.out.println("0. Quay lai");
        System.out.print(">> Chon: ");
        String sub = scanner.nextLine().trim();

        if ("1".equals(sub)) {
            for (Member m : library.getAllMembers()) {
                System.out.println(m.toString() + " | The: " + (m.getCard() != null ? m.getCard().getCardNumber() : "N/A"));
            }
        } else if ("2".equals(sub)) {
            System.out.print("Nhap Ho va Ten thanh vien: ");
            String name = scanner.nextLine().trim();
            System.out.print("Nhap Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Nhap So dien thoai: ");
            String phone = scanner.nextLine().trim();

            String memberId = IdGenerator.generateMemberId();
            String cardNumber = IdGenerator.generateCardNumber();
            LibraryCard card = new LibraryCard(cardNumber, "BC-" + cardNumber);
            Member newMember = new Member(memberId, "123456", new Person(name, email, phone), card);

            library.registerMember(newMember);
            DataStorageService.saveData(library);
            System.out.println("[+] Dang ky thanh cong! Ma thanh vien: " + memberId + " | So the: " + cardNumber);
        } else if ("3".equals(sub)) {
            System.out.print("Nhap Ma Thanh Vien: ");
            String mid = scanner.nextLine().trim();
            List<BookLending> hist = library.getLendingHistoryForMember(mid);
            System.out.println("\n--- LICH SU MUON TRA CUA " + mid + " ---");
            for (BookLending l : hist) {
                System.out.println(l.fetchLendingDetails());
            }
        }
    }

    private void menuFineManagement() {
        System.out.println("\n--- [7] QUAN LY TIEN PHAT QUA HAN ---");
        List<Fine> fines = library.getFines();
        if (fines.isEmpty()) {
            System.out.println("Hien khong co khoan tien phat nao trong he thong.");
            return;
        }
        for (Fine f : fines) {
            System.out.println(f.toString());
        }

        System.out.print("\nNhap Ma Tien Phat muon thanh toan (hoac bam Enter de bo qua): ");
        String fineId = scanner.nextLine().trim();
        if (!fineId.isEmpty()) {
            Library.OperationResult res = library.payFine(fineId);
            if (res.isSuccess()) {
                System.out.println("[+] " + res.getMessage());
                DataStorageService.saveData(library);
            } else {
                System.out.println("[-] " + res.getMessage());
            }
        }
    }

    private void menuNotifications() {
        System.out.println("\n--- [8] HOP THU THONG BAO ---");
        System.out.println("1. Xem nhat ky tat ca thong bao");
        System.out.println("2. Quet & Gui thong bao qua han tu dong");
        System.out.print(">> Chon: ");
        String sub = scanner.nextLine().trim();

        if ("1".equals(sub)) {
            for (Notification n : library.getNotifications()) {
                System.out.println(n.toString());
            }
        } else if ("2".equals(sub)) {
            int count = library.checkOverdueBooksAndNotify();
            DataStorageService.saveData(library);
            System.out.println("[+] Da gui thong bao cho " + count + " luot muon qua han.");
        }
    }
}
