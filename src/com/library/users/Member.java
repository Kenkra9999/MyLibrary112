package com.library.users;

import java.time.LocalDate;
import com.library.enums.AccountStatus;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.utils.Constants;
import com.library.utils.DateUtils;

/**
 * Class Member dai dien cho Thanh vien thu vien (Ke thua tu User).
 */
public class Member extends User {
    private static final long serialVersionUID = 1L;

    private LocalDate dateOfMembership;
    private int totalBooksCheckedOut;

    public Member() {
        super();
        this.dateOfMembership = LocalDate.now();
        this.totalBooksCheckedOut = 0;
    }

    public Member(String id, String password, Person person, LibraryCard card, AccountStatus status, LocalDate dateOfMembership) {
        super(id, password, person, card, status);
        this.dateOfMembership = dateOfMembership != null ? dateOfMembership : LocalDate.now();
        this.totalBooksCheckedOut = 0;
    }

    public Member(String id, String password, Person person, LibraryCard card) {
        this(id, password, person, card, AccountStatus.ACTIVE, LocalDate.now());
    }

    public LocalDate getDateOfMembership() {
        return dateOfMembership;
    }

    public void setDateOfMembership(LocalDate dateOfMembership) {
        this.dateOfMembership = dateOfMembership;
    }

    public int getTotalBooksCheckedOut() {
        return totalBooksCheckedOut;
    }

    public void setTotalBooksCheckedOut(int totalBooksCheckedOut) {
        this.totalBooksCheckedOut = totalBooksCheckedOut;
    }

    public boolean canCheckoutMoreBooks() {
        return isActive() && totalBooksCheckedOut < Constants.MAX_BOOKS_ISSUED_TO_A_USER;
    }

    public void incrementTotalBooksCheckedOut() {
        this.totalBooksCheckedOut++;
    }

    public void decrementTotalBooksCheckedOut() {
        if (this.totalBooksCheckedOut > 0) {
            this.totalBooksCheckedOut--;
        }
    }

    @Override
    public String getRole() {
        return "Thanh vien";
    }

    @Override
    public String toString() {
        return "[Thanh vien] " + getId() + " - " + getName() + " (Dang muon: " + totalBooksCheckedOut + "/" + Constants.MAX_BOOKS_ISSUED_TO_A_USER + ", Ngay gia nhap: " + DateUtils.formatDate(dateOfMembership) + ")";
    }
}
