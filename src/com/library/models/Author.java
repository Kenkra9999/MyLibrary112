package com.library.models;

/**
 * Class Author ke thua tu Person, bo sung mo ta tieu su cua tac gia.
 */
public class Author extends Person {
    private static final long serialVersionUID = 1L;

    private String description;

    public Author() {
        super();
    }

    public Author(String name, String description) {
        super(name, "", "");
        this.description = description;
    }

    public Author(String name, Address address, String email, String phone, String description) {
        super(name, address, email, phone);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getName() + (description != null && !description.isEmpty() ? " [" + description + "]" : "");
    }
}
