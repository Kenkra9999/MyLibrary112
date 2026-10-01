package com.library.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Book dai dien cho thong tin khai niem chung cua mot cuon sach (Yeu cau R3).
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;
    private String title;
    private String subject;
    private String publisher;
    private String language;
    private int numberOfPages;
    private List<Author> authors;

    public Book() {
        this.authors = new ArrayList<>();
        this.language = "Tieng Viet";
    }

    public Book(String isbn, String title, String subject, String publisher, String language, int numberOfPages, List<Author> authors) {
        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.authors = authors != null ? authors : new ArrayList<>();
    }

    public Book(String isbn, String title, String subject, String publisher, String authorName) {
        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = "Tieng Viet";
        this.numberOfPages = 250;
        this.authors = new ArrayList<>();
        if (authorName != null && !authorName.isEmpty()) {
            this.authors.add(new Author(authorName, ""));
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        if (author != null && !this.authors.contains(author)) {
            this.authors.add(author);
        }
    }

    public String getAuthorsAsString() {
        if (authors == null || authors.isEmpty()) return "Chua ro tac gia";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(authors.get(i).getName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "[" + isbn + "] " + title + " - Tac gia: " + getAuthorsAsString() + " (" + subject + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
