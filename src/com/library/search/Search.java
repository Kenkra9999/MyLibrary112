package com.library.search;

import java.time.LocalDate;
import java.util.List;
import com.library.models.Book;
import com.library.models.BookItem;

/**
 * Interface Search định nghĩa các phương thức tìm kiếm sách trong danh mục (Yêu cầu R14).
 * (Tính trừu tượng / Abstraction & Giao diện / Interface).
 */
public interface Search {
    /**
     * Tìm kiếm sách theo tiêu đề hoặc từ khóa trong tiêu đề.
     */
    List<Book> searchByTitle(String title);

    /**
     * Tìm kiếm sách theo tên tác giả.
     */
    List<Book> searchByAuthor(String authorName);

    /**
     * Tìm kiếm sách theo chủ đề / thể loại.
     */
    List<Book> searchBySubject(String subject);

    /**
     * Tìm kiếm sách theo ngày xuất bản.
     */
    List<Book> searchByPubDate(LocalDate pubDate);

    /**
     * Tìm kiếm chi tiết các bản sao sách (BookItem) theo từ khóa tổng hợp.
     */
    List<BookItem> searchBookItems(String query);
}
