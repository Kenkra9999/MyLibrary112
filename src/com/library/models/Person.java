package com.library.models;

import java.io.Serializable;

/**
 * Class Person lưu trữ thông tin cá nhân cơ bản (Họ tên, Địa chỉ, Email, Số điện thoại).
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Address address;
    private String email;
    private String phone;

    public Person() {
        this.address = new Address();
    }

    public Person(String name, Address address, String email, String phone) {
        this.name = name;
        this.address = address != null ? address : new Address();
        this.email = email;
        this.phone = phone;
    }

    public Person(String name, String email, String phone) {
        this(name, new Address(), email, phone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + email + ", " + phone + ")";
    }
}
