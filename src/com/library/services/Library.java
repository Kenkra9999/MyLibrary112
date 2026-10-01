package com.library.services;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.library.enums.AccountStatus;
import com.library.enums.BookStatus;
import com.library.enums.ReservationStatus;
import com.library.models.*;
import com.library.notifications.EmailNotification;
import com.library.notifications.Notification;
import com.library.notifications.PostalNotification;
import com.library.search.Catalog;
import com.library.users.Librarian;
import com.library.users.Member;
import com.library.utils.Constants;
import com.library.utils.DateUtils;

/**
 * Class Library - Dieu phoi toan bo nghiep vu cua he thong thu vien.
 * Ap dung mo hinh Facade va quan ly tap trung cac thuc the.
 */
public class Library implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Address address;
    private Catalog catalog;
    private Map<String, Member> members = new LinkedHashMap<>();
    private Map<String, Librarian> librarians = new LinkedHashMap<>();
    private List<BookLending> lendings = new ArrayList<>();
    private List<BookReservation> reservations = new ArrayList<>();
    private List<Fine> fines = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    public Library() {
        this.name = Constants.DEFAULT_LIBRARY_NAME;
        this.address = new Address("So 1 Dai Co Viet", "Ha Noi", "Ha Noi", "100000", "Viet Nam");
        this.catalog = new Catalog();
    }

    public Library(String name, Address address) {
        this.name = name;
        this.address = address != null ? address : new Address();
        this.catalog = new Catalog();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Catalog getCatalog() { return catalog; }
    public void setCatalog(Catalog catalog) { this.catalog = catalog; }

    public Map<String, Member> getMembers() { return members; }
    public Map<String, Librarian> getLibrarians() { return librarians; }
    public List<BookLending> getLendings() { return lendings; }
    public List<BookReservation> getReservations() { return reservations; }
    public List<Fine> getFines() { return fines; }
    public List<Notification> getNotifications() { return notifications; }

    public synchronized void registerMember(Member member) {
        if (member != null && member.getId() != null) {
            members.put(member.getId(), member);
        }
    }

    public synchronized void registerLibrarian(Librarian librarian) {
        if (librarian != null && librarian.getId() != null) {
            librarians.put(librarian.getId(), librarian);
        }
    }

    public Member getMemberById(String memberId) {
        return members.get(memberId);
    }

    public Librarian getLibrarianById(String librarianId) {
        return librarians.get(librarianId);
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    public List<Librarian> getAllLibrarians() {
        return new ArrayList<>(librarians.values());
    }

    public synchronized boolean deleteMember(String memberId) {
        Member m = members.get(memberId);
        if (m != null) {
            if (m.getTotalBooksCheckedOut() > 0) {
                return false;
            }
            members.remove(memberId);
            return true;
        }
        return false;
    }

    // CHECKOUT BOOK (R7, R8)
    public synchronized OperationResult checkoutBook(String barcode, String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            return OperationResult.failure("Khong tim thay thanh vien voi ma: " + memberId);
        }

        if (member.getStatus() != AccountStatus.ACTIVE) {
            return OperationResult.failure("Tai khoan thanh vien khong o trang thai hoat dong (" + member.getStatus() + ")");
        }

        if (member.getTotalBooksCheckedOut() >= Constants.MAX_BOOKS_ISSUED_TO_A_USER) {
            return OperationResult.failure("Thanh vien da muon toi da " + Constants.MAX_BOOKS_ISSUED_TO_A_USER + " cuon sach!");
        }

        long unpaidFinesCount = fines.stream()
                .filter(f -> f.getMemberId().equals(memberId) && !f.isPaid())
                .count();
        if (unpaidFinesCount > 0) {
            return OperationResult.failure("Thanh vien co khoan tien phat chua thanh toan. Vui long nop phat truoc khi muon tiep.");
        }

        BookItem item = catalog.getBookItemByBarcode(barcode);
        if (item == null) {
            return OperationResult.failure("Khong tim thay sach voi ma vach: " + barcode);
        }

        if (item.isReferenceOnly()) {
            return OperationResult.failure("Cuon sach nay la sach tham khao chi doc tai cho!");
        }

        BookReservation activeReservation = getActiveReservationForItem(barcode);
        if (activeReservation != null) {
            if (!activeReservation.getMemberId().equals(memberId)) {
                return OperationResult.failure("Sach nay dang duoc dat truoc boi thanh vien khac (" + activeReservation.getMemberId() + ")!");
            } else {
                activeReservation.setStatus(ReservationStatus.COMPLETED);
            }
        } else if (item.getStatus() != BookStatus.AVAILABLE) {
            return OperationResult.failure("Sach hien khong co san de muon (Trang thai: " + item.getStatus() + ")");
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(Constants.MAX_LENDING_DAYS);

        item.setStatus(BookStatus.LOANED);
        item.setBorrowedDate(today);
        item.setDueDate(dueDate);
        catalog.updateBookItem(item);

        BookLending lending = new BookLending(barcode, memberId);
        lending.setCreationDate(today);
        lending.setDueDate(dueDate);
        lendings.add(lending);

        member.incrementTotalBooksCheckedOut();

        return OperationResult.success("Muon sach thanh cong! Han tra: " + DateUtils.formatDate(dueDate), lending);
    }

    // RETURN BOOK (R1, R12, R15)
    public synchronized OperationResult returnBook(String barcode, String memberId) {
        BookItem item = catalog.getBookItemByBarcode(barcode);
        if (item == null) {
            return OperationResult.failure("Khong tim thay sach voi ma vach: " + barcode);
        }

        Member member = members.get(memberId);
        if (member == null) {
            return OperationResult.failure("Khong tim thay thanh vien: " + memberId);
        }

        BookLending activeLending = lendings.stream()
                .filter(l -> l.getItemBarcode().equalsIgnoreCase(barcode) && l.getMemberId().equalsIgnoreCase(memberId) && !l.isReturned())
                .findFirst()
                .orElse(null);

        if (activeLending == null) {
            return OperationResult.failure("Khong tim thay thong tin muon hop le cho sach nay cua thanh vien " + memberId);
        }

        LocalDate today = LocalDate.now();
        activeLending.setReturned(true);
        activeLending.setReturnDate(today);

        member.decrementTotalBooksCheckedOut();

        double fineAmount = 0.0;
        Fine fineObj = null;
        if (today.isAfter(activeLending.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(activeLending.getDueDate(), today);
            fineAmount = overdueDays * Constants.FINE_PER_DAY;
            fineObj = new Fine(fineAmount, barcode, memberId);
            fines.add(fineObj);

            sendNotification(new EmailNotification(
                    "Sach [" + item.getTitle() + "] tra tre " + overdueDays + " ngay. Phi phat: " + String.format("%,.0f VND", fineAmount),
                    memberId,
                    member.getEmail()
            ));
        }

        BookReservation waitingReservation = reservations.stream()
                .filter(r -> r.getItemBarcode().equalsIgnoreCase(barcode) && r.getStatus() == ReservationStatus.WAITING)
                .findFirst()
                .orElse(null);

        if (waitingReservation != null) {
            waitingReservation.setStatus(ReservationStatus.PENDING);
            item.setStatus(BookStatus.RESERVED);
            item.setBorrowedDate(null);
            item.setDueDate(null);

            Member resMember = members.get(waitingReservation.getMemberId());
            if (resMember != null) {
                sendNotification(new EmailNotification(
                        "Cuon sach ban dat giu [" + item.getTitle() + "] hien da co san tai thu vien. Vui long den nhan trong 2 ngay!",
                        resMember.getId(),
                        resMember.getEmail()
                ));
            }
        } else {
            item.setStatus(BookStatus.AVAILABLE);
            item.setBorrowedDate(null);
            item.setDueDate(null);
        }

        catalog.updateBookItem(item);

        String msg = "Tra sach thanh cong!";
        if (fineAmount > 0) {
            msg += " Phat hien qua han! Phi phat: " + String.format("%,.0f VND", fineAmount);
        }
        return OperationResult.success(msg, fineObj);
    }

    // RENEW BOOK (R11)
    public synchronized OperationResult renewBook(String barcode, String memberId) {
        BookItem item = catalog.getBookItemByBarcode(barcode);
        if (item == null) {
            return OperationResult.failure("Khong tim thay sach voi ma: " + barcode);
        }

        BookReservation activeReservation = getActiveReservationForItem(barcode);
        if (activeReservation != null && !activeReservation.getMemberId().equals(memberId)) {
            return OperationResult.failure("Khong the gia han: Sach da duoc thanh vien khac dat giu!");
        }

        BookLending activeLending = lendings.stream()
                .filter(l -> l.getItemBarcode().equalsIgnoreCase(barcode) && l.getMemberId().equalsIgnoreCase(memberId) && !l.isReturned())
                .findFirst()
                .orElse(null);

        if (activeLending == null) {
            return OperationResult.failure("Khong tim thay thong tin muon dang hoat dong cua sach nay!");
        }

        if (activeLending.isOverdue()) {
            return OperationResult.failure("Khong the gia han: Sach da qua han tra! Vui long hoan tra va nop phi phat.");
        }

        LocalDate newDueDate = activeLending.getDueDate().plusDays(Constants.RENEW_EXTENSION_DAYS);
        activeLending.setDueDate(newDueDate);
        item.setDueDate(newDueDate);
        catalog.updateBookItem(item);

        return OperationResult.success("Gia han thanh cong them " + Constants.RENEW_EXTENSION_DAYS + " ngay! Han tra moi: " + DateUtils.formatDate(newDueDate), activeLending);
    }

    // RESERVE BOOK (R9, R13)
    public synchronized OperationResult reserveBook(String barcode, String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            return OperationResult.failure("Khong tim thay thanh vien: " + memberId);
        }

        if (member.getStatus() != AccountStatus.ACTIVE) {
            return OperationResult.failure("Tai khoan thanh vien khong o trang thai hoat dong!");
        }

        BookItem item = catalog.getBookItemByBarcode(barcode);
        if (item == null) {
            return OperationResult.failure("Khong tim thay sach: " + barcode);
        }

        if (item.getStatus() == BookStatus.AVAILABLE) {
            return OperationResult.failure("Sach hien dang co san tren ke " + item.getPlacedAt() + ", ban co the muon truc tiep!");
        }

        BookReservation existing = reservations.stream()
                .filter(r -> r.getItemBarcode().equalsIgnoreCase(barcode) &&
                        (r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            if (existing.getMemberId().equalsIgnoreCase(memberId)) {
                return OperationResult.failure("Ban da dat giu cuon sach nay roi!");
            }
            return OperationResult.failure("Cuon sach nay hien da co nguoi khac dat giu!");
        }

        BookReservation reservation = new BookReservation(barcode, memberId);
        reservations.add(reservation);

        return OperationResult.success("Dat giu sach thanh cong! He thong se thong bao khi sach co san.", reservation);
    }

    public synchronized OperationResult cancelReservation(String reservationId) {
        BookReservation res = reservations.stream()
                .filter(r -> r.getReservationId().equalsIgnoreCase(reservationId))
                .findFirst()
                .orElse(null);

        if (res == null) {
            return OperationResult.failure("Khong tim thay phieu dat giu: " + reservationId);
        }

        res.setStatus(ReservationStatus.CANCELED);
        BookItem item = catalog.getBookItemByBarcode(res.getItemBarcode());
        if (item != null && item.getStatus() == BookStatus.RESERVED) {
            item.setStatus(BookStatus.AVAILABLE);
            catalog.updateBookItem(item);
        }

        return OperationResult.success("Huy dat giu thanh cong!", res);
    }

    // PAY FINE (R15)
    public synchronized OperationResult payFine(String fineId) {
        Fine fine = fines.stream()
                .filter(f -> f.getFineId().equalsIgnoreCase(fineId))
                .findFirst()
                .orElse(null);

        if (fine == null) {
            return OperationResult.failure("Khong tim thay bien lai phat: " + fineId);
        }

        if (fine.isPaid()) {
            return OperationResult.failure("Khoan phat nay da duoc thanh toan truoc do!");
        }

        fine.payFine();
        return OperationResult.success("Thanh toan tien phat thanh cong (" + String.format("%,.0f VND", fine.getAmount()) + ")!", fine);
    }

    public synchronized void sendNotification(Notification notification) {
        if (notification != null) {
            notification.sendNotification();
            notifications.add(notification);
        }
    }

    public synchronized int checkOverdueBooksAndNotify() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (BookLending lending : lendings) {
            if (!lending.isReturned() && today.isAfter(lending.getDueDate())) {
                Member member = members.get(lending.getMemberId());
                BookItem item = catalog.getBookItemByBarcode(lending.getItemBarcode());
                if (member != null && item != null) {
                    long overdueDays = ChronoUnit.DAYS.between(lending.getDueDate(), today);
                    sendNotification(new EmailNotification(
                            "Nhac nho: Sach [" + item.getTitle() + "] da qua han " + overdueDays + " ngay. Vui long hoan tra som de tranh phi phat!",
                            member.getId(),
                            member.getEmail()
                    ));
                    count++;
                }
            }
        }
        return count;
    }

    private BookReservation getActiveReservationForItem(String barcode) {
        return reservations.stream()
                .filter(r -> r.getItemBarcode().equalsIgnoreCase(barcode) &&
                        (r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING))
                .findFirst()
                .orElse(null);
    }

    public List<BookLending> getActiveLendingsForMember(String memberId) {
        return lendings.stream()
                .filter(l -> l.getMemberId().equalsIgnoreCase(memberId) && !l.isReturned())
                .collect(Collectors.toList());
    }

    public List<BookLending> getLendingHistoryForMember(String memberId) {
        return lendings.stream()
                .filter(l -> l.getMemberId().equalsIgnoreCase(memberId))
                .collect(Collectors.toList());
    }

    public List<Fine> getUnpaidFinesForMember(String memberId) {
        return fines.stream()
                .filter(f -> f.getMemberId().equalsIgnoreCase(memberId) && !f.isPaid())
                .collect(Collectors.toList());
    }

    public List<BookReservation> getReservationsForMember(String memberId) {
        return reservations.stream()
                .filter(r -> r.getMemberId().equalsIgnoreCase(memberId))
                .collect(Collectors.toList());
    }

    public static class OperationResult implements Serializable {
        private static final long serialVersionUID = 1L;
        private final boolean success;
        private final String message;
        private final Object data;

        private OperationResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static OperationResult success(String message, Object data) {
            return new OperationResult(true, message, data);
        }

        public static OperationResult success(String message) {
            return new OperationResult(true, message, null);
        }

        public static OperationResult failure(String message) {
            return new OperationResult(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
}
