package com.library.gui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.models.BookItem;
import com.library.services.Library;
import com.library.utils.DateUtils;

/**
 * Panel Quản lý Danh mục Sách & Bản sao sách (Books & Catalog Management - R2, R3, R4, R14).
 */
public class BooksPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JTextField txtSearch;
    private JComboBox<String> cbSearchType;
    private JComboBox<String> cbFilterStatus;
    private JTable tableBooks;
    private DefaultTableModel tableModel;
    private List<BookItem> currentDisplayedItems;

    public BooksPanel(MainFrame mainFrame, Library library) {
        this.mainFrame = mainFrame;
        this.library = library;

        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // TOP: Header & Search Toolbar
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Theme.BG_MAIN);

        JLabel lblTitle = new JLabel("📚 Quản Lý Sách & Tra Cứu Danh Mục (Catalog Search)");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Search Bar Card
        JPanel searchCard = Theme.createCardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(Theme.FONT_SUBHEADER);

        txtSearch = new JTextField(20);
        txtSearch.setFont(Theme.FONT_BODY);

        cbSearchType = new JComboBox<>(new String[]{"Tất cả (Từ khóa)", "Theo Tựa Đề", "Theo Tác Giả", "Theo Thể Loại", "Theo Mã Vạch"});
        cbSearchType.setFont(Theme.FONT_BODY);

        cbFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Có sẵn (AVAILABLE)", "Đang mượn (LOANED)", "Đã đặt giữ (RESERVED)"});
        cbFilterStatus.setFont(Theme.FONT_BODY);

        JButton btnSearch = Theme.createPrimaryButton("Tìm Kiếm");
        btnSearch.addActionListener(e -> performSearch());

        JButton btnReset = Theme.createButton("Đặt lại", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbSearchType.setSelectedIndex(0);
            cbFilterStatus.setSelectedIndex(0);
            refreshData();
        });

        searchCard.add(lblSearch);
        searchCard.add(txtSearch);
        searchCard.add(new JLabel("Tiêu chí:"));
        searchCard.add(cbSearchType);
        searchCard.add(new JLabel("Trạng thái:"));
        searchCard.add(cbFilterStatus);
        searchCard.add(btnSearch);
        searchCard.add(btnReset);

        topPanel.add(searchCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Books Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Vạch", "Tựa Đề Sách", "Tác Giả", "Thể Loại / Chủ Đề", "Định Dạng", "Vị Trí Kệ", "Giá Bìa", "Trạng Thái", "Ghi Chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBooks = new JTable(tableModel);
        Theme.styleTable(tableBooks);
        tableBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableBooks.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableBooks.getColumnModel().getColumn(1).setPreferredWidth(260);
        tableBooks.getColumnModel().getColumn(2).setPreferredWidth(160);
        tableBooks.getColumnModel().getColumn(3).setPreferredWidth(140);
        tableBooks.getColumnModel().getColumn(4).setPreferredWidth(90);
        tableBooks.getColumnModel().getColumn(5).setPreferredWidth(140);
        tableBooks.getColumnModel().getColumn(6).setPreferredWidth(90);
        tableBooks.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableBooks.getColumnModel().getColumn(8).setPreferredWidth(90);

        tableCard.add(new JScrollPane(tableBooks), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnAddBook = Theme.createSuccessButton("➕ Thêm Sách Mới");
        btnAddBook.addActionListener(e -> mainFrame.openAddBookDialog());

        JButton btnEditBook = Theme.createButton("✏️ Sửa Sách", Theme.ACCENT, Color.WHITE);
        btnEditBook.addActionListener(e -> onEditSelected());

        JButton btnDeleteBook = Theme.createDangerButton("🗑️ Xóa Bản Sao");
        btnDeleteBook.addActionListener(e -> onDeleteSelected());

        JButton btnBorrow = Theme.createButton("⚡ Mượn Sách Này", Theme.PURPLE, Color.WHITE);
        btnBorrow.addActionListener(e -> onBorrowSelected());

        JButton btnReserve = Theme.createButton("📌 Đặt Giữ Sách", new Color(234, 88, 12), Color.WHITE);
        btnReserve.addActionListener(e -> onReserveSelected());

        bottomPanel.add(btnAddBook);
        bottomPanel.add(btnEditBook);
        bottomPanel.add(btnDeleteBook);
        bottomPanel.add(btnBorrow);
        bottomPanel.add(btnReserve);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        currentDisplayedItems = library.getCatalog().getAllBookItems();
        updateTable(currentDisplayedItems);
    }

    private void performSearch() {
        String query = txtSearch.getText().trim();
        int searchType = cbSearchType.getSelectedIndex();
        int statusFilter = cbFilterStatus.getSelectedIndex();

        List<BookItem> results;
        if (query.isEmpty()) {
            results = library.getCatalog().getAllBookItems();
        } else {
            switch (searchType) {
                case 1: // Title
                    results = library.getCatalog().getAllBookItems().stream()
                            .filter(i -> i.getTitle() != null && i.getTitle().toLowerCase().contains(query.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                case 2: // Author
                    results = library.getCatalog().getAllBookItems().stream()
                            .filter(i -> i.getAuthorsAsString() != null && i.getAuthorsAsString().toLowerCase().contains(query.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                case 3: // Subject
                    results = library.getCatalog().getAllBookItems().stream()
                            .filter(i -> i.getSubject() != null && i.getSubject().toLowerCase().contains(query.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                case 4: // Barcode
                    results = library.getCatalog().getAllBookItems().stream()
                            .filter(i -> i.getBarcode() != null && i.getBarcode().toLowerCase().contains(query.toLowerCase()))
                            .collect(Collectors.toList());
                    break;
                default: // All
                    results = library.getCatalog().searchBookItems(query);
                    break;
            }
        }

        // Apply Status Filter
        if (statusFilter == 1) {
            results = results.stream().filter(i -> i.getStatus() == BookStatus.AVAILABLE).collect(Collectors.toList());
        } else if (statusFilter == 2) {
            results = results.stream().filter(i -> i.getStatus() == BookStatus.LOANED).collect(Collectors.toList());
        } else if (statusFilter == 3) {
            results = results.stream().filter(i -> i.getStatus() == BookStatus.RESERVED).collect(Collectors.toList());
        }

        currentDisplayedItems = results;
        updateTable(results);
    }

    private void updateTable(List<BookItem> items) {
        tableModel.setRowCount(0);
        for (BookItem item : items) {
            tableModel.addRow(new Object[]{
                    item.getBarcode(),
                    item.getTitle(),
                    item.getAuthorsAsString(),
                    item.getSubject(),
                    item.getFormat().getDisplayName(),
                    item.getPlacedAt() != null ? item.getPlacedAt().toString() : "N/A",
                    String.format("%,.0fđ", item.getPrice()),
                    item.getStatus().getDisplayName(),
                    item.isReferenceOnly() ? "Chỉ đọc tại chỗ" : "Cho mượn"
            });
        }
    }

    private BookItem getSelectedItem() {
        int row = tableBooks.getSelectedRow();
        if (row >= 0 && row < currentDisplayedItems.size()) {
            return currentDisplayedItems.get(row);
        }
        return null;
    }

    private void onEditSelected() {
        BookItem item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn cuốn sách cần chỉnh sửa từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.openEditBookDialog(item);
    }

    private void onDeleteSelected() {
        BookItem item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn cuốn sách cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (item.getStatus() == BookStatus.LOANED) {
            JOptionPane.showMessageDialog(this, "Không thể xóa sách đang được mượn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản sao [" + item.getBarcode() + "] " + item.getTitle() + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            library.getCatalog().removeBookItem(item.getBarcode());
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã xóa sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onBorrowSelected() {
        BookItem item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách để mượn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.openCheckoutDialog(item.getBarcode(), null);
    }

    private void onReserveSelected() {
        BookItem item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách để đặt giữ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.openReserveDialog(item.getBarcode(), null);
    }
}
