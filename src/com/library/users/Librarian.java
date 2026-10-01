package com.library.users;

import java.time.LocalDate;
import com.library.enums.AccountStatus;
import com.library.models.LibraryCard;
import com.library.models.Person;

/**
 * Class Librarian dai dien cho Thu thu / Quan tri vien thu vien (Ke thua tu User).
 */
public class Librarian extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private LocalDate hireDate;

    public Librarian() {
        super();
        this.department = "Quan ly luu thong & Danh muc";
        this.hireDate = LocalDate.now();
    }

    public Librarian(String id, String password, Person person, LibraryCard card, AccountStatus status, String department, LocalDate hireDate) {
        super(id, password, person, card, status);
        this.department = department != null ? department : "Quan ly chung";
        this.hireDate = hireDate != null ? hireDate : LocalDate.now();
    }

    public Librarian(String id, String password, Person person, LibraryCard card) {
        this(id, password, person, card, AccountStatus.ACTIVE, "Quan ly chung", LocalDate.now());
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void blockMember(Member member) {
        if (member != null) {
            member.setStatus(AccountStatus.BLACKLISTED);
        }
    }

    public void unblockMember(Member member) {
        if (member != null) {
            member.setStatus(AccountStatus.ACTIVE);
        }
    }

    @Override
    public String getRole() {
        return "Thu thu";
    }

    @Override
    public String toString() {
        return "[Thu thu] " + getId() + " - " + getName() + " (Phong ban: " + department + ")";
    }
}
