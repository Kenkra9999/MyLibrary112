package com.library.search;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.library.models.Author;
import com.library.models.Book;
import com.library.models.BookItem;

/**
 * Class Catalog hiện thực hóa Interface Search (Đa hình / Polymorphism).
 * Quản lý danh mục sách tổng thể và các bản sao vật lý (BookItem) trong thư viện.
 */
public class Catalog implements Search, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Book> booksByIsbn = new LinkedHashMap<>();
    private final Map<String, BookItem> bookItemsByBarcode = new LinkedHashMap<>();

    private final Map<String, List<Book>> bookTitles = new HashMap<>();
    private final Map<String, List<Book>> bookAuthors = new HashMap<>();
    private final Map<String, List<Book>> bookSubjects = new HashMap<>();
    private final Map<LocalDate, List<Book>> bookPublicationDates = new HashMap<>();

    public Catalog() {}

    /**
     * Thêm sách mới vào danh mục và cập nhật chỉ mục tìm kiếm.
     */
    public synchronized void addBook(Book book) {
        if (book == null || book.getIsbn() == null) return;
        booksByIsbn.put(book.getIsbn(), book);
        rebuildIndices();
    }

    /**
     * Thêm một bản sao sách (BookItem) vào thư viện.
     */
    public synchronized void addBookItem(BookItem item) {
        if (item == null || item.getBarcode() == null) return;
        bookItemsByBarcode.put(item.getBarcode(), item);
        
        // Đồng thời cập nhật hoặc thêm Book tương ứng
        if (!booksByIsbn.containsKey(item.getIsbn())) {
            Book baseBook = new Book(item.getIsbn(), item.getTitle(), item.getSubject(),
                    item.getPublisher(), item.getLanguage(), item.getNumberOfPages(), item.getAuthors());
            booksByIsbn.put(item.getIsbn(), baseBook);
        }
        rebuildIndices();
    }

    /**
     * Xóa một bản sao sách (BookItem) khỏi danh mục theo mã vạch.
     */
    public synchronized boolean removeBookItem(String barcode) {
        if (barcode == null) return false;
        BookItem removed = bookItemsByBarcode.remove(barcode);
        if (removed != null) {
            rebuildIndices();
            return true;
        }
        return false;
    }

    /**
     * Cập nhật thông tin bản sao sách.
     */
    public synchronized void updateBookItem(BookItem item) {
        if (item != null && item.getBarcode() != null) {
            bookItemsByBarcode.put(item.getBarcode(), item);
            rebuildIndices();
        }
    }

    /**
     * Xây dựng lại các chỉ mục tra cứu nhanh (Indices).
     */
    public synchronized void rebuildIndices() {
        bookTitles.clear();
        bookAuthors.clear();
        bookSubjects.clear();
        bookPublicationDates.clear();

        for (Book book : booksByIsbn.values()) {
            // Index Title
            if (book.getTitle() != null) {
                String key = book.getTitle().toLowerCase().trim();
                bookTitles.computeIfAbsent(key, k -> new ArrayList<>()).add(book);
            }
            // Index Authors
            if (book.getAuthors() != null) {
                for (Author author : book.getAuthors()) {
                    if (author != null && author.getName() != null) {
                        String key = author.getName().toLowerCase().trim();
                        bookAuthors.computeIfAbsent(key, k -> new ArrayList<>()).add(book);
                    }
                }
            }
            // Index Subject
            if (book.getSubject() != null) {
                String key = book.getSubject().toLowerCase().trim();
                bookSubjects.computeIfAbsent(key, k -> new ArrayList<>()).add(book);
            }
        }

        for (BookItem item : bookItemsByBarcode.values()) {
            if (item.getPublicationDate() != null) {
                bookPublicationDates.computeIfAbsent(item.getPublicationDate(), k -> new ArrayList<>()).add(item);
            }
        }
    }

    @Override
    public List<Book> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>(booksByIsbn.values());
        }
        String q = title.toLowerCase().trim();
        return booksByIsbn.values().stream()
                .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.trim().isEmpty()) {
            return new ArrayList<>(booksByIsbn.values());
        }
        String q = authorName.toLowerCase().trim();
        return booksByIsbn.values().stream()
                .filter(b -> b.getAuthors() != null && b.getAuthors().stream()
                        .anyMatch(a -> a.getName() != null && a.getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBySubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return new ArrayList<>(booksByIsbn.values());
        }
        String q = subject.toLowerCase().trim();
        return booksByIsbn.values().stream()
                .filter(b -> b.getSubject() != null && b.getSubject().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByPubDate(LocalDate pubDate) {
        if (pubDate == null) {
            return new ArrayList<>(booksByIsbn.values());
        }
        Set<String> matchedIsbns = bookItemsByBarcode.values().stream()
                .filter(item -> item.getPublicationDate() != null && item.getPublicationDate().equals(pubDate))
                .map(Book::getIsbn)
                .collect(Collectors.toSet());

        return booksByIsbn.values().stream()
                .filter(b -> matchedIsbns.contains(b.getIsbn()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookItem> searchBookItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(bookItemsByBarcode.values());
        }
        String q = query.toLowerCase().trim();
        return bookItemsByBarcode.values().stream()
                .filter(item ->
                        (item.getBarcode() != null && item.getBarcode().toLowerCase().contains(q)) ||
                        (item.getTitle() != null && item.getTitle().toLowerCase().contains(q)) ||
                        (item.getIsbn() != null && item.getIsbn().toLowerCase().contains(q)) ||
                        (item.getSubject() != null && item.getSubject().toLowerCase().contains(q)) ||
                        (item.getAuthorsAsString() != null && item.getAuthorsAsString().toLowerCase().contains(q)) ||
                        (item.getFormat() != null && item.getFormat().toString().toLowerCase().contains(q)) ||
                        (item.getStatus() != null && item.getStatus().toString().toLowerCase().contains(q)) ||
                        (item.getPlacedAt() != null && item.getPlacedAt().toString().toLowerCase().contains(q))
                )
                .collect(Collectors.toList());
    }

    public Book getBookByIsbn(String isbn) {
        return booksByIsbn.get(isbn);
    }

    public BookItem getBookItemByBarcode(String barcode) {
        return bookItemsByBarcode.get(barcode);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(booksByIsbn.values());
    }

    public List<BookItem> getAllBookItems() {
        return new ArrayList<>(bookItemsByBarcode.values());
    }

    public List<BookItem> getBookItemsByIsbn(String isbn) {
        return bookItemsByBarcode.values().stream()
                .filter(item -> item.getIsbn().equalsIgnoreCase(isbn))
                .collect(Collectors.toList());
    }

    public int getTotalBooks() {
        return booksByIsbn.size();
    }

    public int getTotalBookItems() {
        return bookItemsByBarcode.size();
    }
}
