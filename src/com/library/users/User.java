package com.library.users;

import java.io.Serializable;
import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;

/**
 * Abstract Class User dai dien cho nguoi dung truu tuong trong he thong (Yeu cau R5, R6).
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

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
        return person != null ? person.getName() : "N/A";
    }

    public String getEmail() {
        return person != null ? person.getEmail() : "";
    }

    public String getPhone() {
        return person != null ? person.getPhone() : "";
    }

    public Address getAddress() {
        return person != null ? person.getAddress() : null;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean checkPassword(String inputPassword) {
        if (this.password == null) return true;
        return this.password.equals(inputPassword);
    }

    public boolean resetPassword(String oldPass, String newPass) {
        if (checkPassword(oldPass)) {
            this.password = newPass;
            return true;
        }
        return false;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return "[" + getRole() + "] " + id + " - " + getName() + " (" + status + ")";
    }
}
