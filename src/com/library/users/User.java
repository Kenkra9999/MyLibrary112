package com.library.users;

import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;

/**
 * Abstract Class User dai dien cho nguoi dung truu tuong trong he thong (Yeu cau R5, R6).
 */
public abstract class User {

    private String id;
    private String password;
    private Person person;
    private LibraryCard card;
    private AccountStatus status;

    public User() {
        this.person = new Person();
        this.status = AccountStatus.ACTIVE;
    }

    public User(String id, String password, Person person, LibraryCard card, AccountStatus status) {
        this.id = id;
        this.password = password;
        this.person = person != null ? person : new Person();
        this.card = card;
        this.status = status != null ? status : AccountStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LibraryCard getCard() {
        return card;
    }

    public void setCard(LibraryCard card) {
        this.card = card;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getName() {
        return person.getName() != null ? person.getName() : "N/A";
    }

    public String getEmail() {
        return person.getEmail() != null ? person.getEmail() : "";
    }

    public String getPhone() {
        return person.getPhone() != null ? person.getPhone() : "";
    }

    public Address getAddress() {
        return person.getAddress() != null ? person.getAddress() : new Address();
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    /** HAM TRUU TUONG: moi loai nguoi dung tu tra ve vai tro cua minh. */
    public abstract String getRole();

    /** HAM TRUU TUONG: mo ta quyen han rieng cua tung loai nguoi dung (da hinh khi duyet mang User[]). */
    public abstract String getQuyenHan();

    @Override
    public String toString() {
        return "[" + getRole() + "] " + id + " - " + getName() + " (" + status + ")";
    }
}
