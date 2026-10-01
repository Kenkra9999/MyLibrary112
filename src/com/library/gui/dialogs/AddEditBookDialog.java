package com.library.gui.dialogs;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.gui.Theme;
import com.library.models.Author;
import com.library.models.BookItem;
import com.library.models.Rack;
import com.library.services.Library;
import com.library.utils.IdGenerator;

/**
 * Dialog thêm mới hoặc chỉnh sửa thông tin sách và bản sao sách (BookItem).
 */
public class AddEditBookDialog extends JDialog {
    private final Library library;
    private final BookItem editingItem;
    private boolean saved = false;

    private JTextField txtIsbn;
    private JTextField txtTitle;
    private JTextField txtAuthors;
    private JComboBox<String> cbSubject;
    private JTextField txtPublisher;
    private JTextField txtLanguage;
    private JSpinner spPages;
    private JTextField txtBarcode;
    private JSpinner spPrice;
    private JComboBox<BookFormat> cbFormat;
    private JComboBox<BookStatus> cbStatus;
    private JSpinner spRackNumber;
    private JTextField txtRackLoc;
    private JCheckBox chkReferenceOnly;

    public AddEditBookDialog(Frame owner, Library library, BookItem itemToEdit) {
        super(owner, itemToEdit == null ? "Thêm Sách Mới" : "Chỉnh Sửa Thông Tin Sách", true);
        this.library = library;
        this.editingItem = itemToEdit;

        initComponents();
        if (itemToEdit != null) {
            populateFields(itemToEdit);
        }
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JLabel lblHeader = new JLabel(editingItem == null ? "Thêm Sách & Bản Sao Vào Thư Viện" : "Cập Nhật Chi Tiết Sách");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        // Form Fields (GridBagLayout)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        txtIsbn = new JTextField("978-604-0-" + (int)(Math.random() * 90000 + 10000), 20);
        txtTitle = new JTextField(20);
        txtAuthors = new JTextField(20);
        cbSubject = new JComboBox<>(new String[]{
                "Công nghệ thông tin", "Khoa học máy tính", "Toán học & Giải thuật",
                "Văn học Việt Nam", "Văn học Nước ngoài", "Kinh tế & Quản trị", "Kỹ năng sống", "Khoa học tự nhiên"
        });
        cbSubject.setEditable(true);
        txtPublisher = new JTextField("NXB Giáo Dục", 20);
        txtLanguage = new JTextField("Tiếng Việt", 20);
        spPages = new JSpinner(new SpinnerNumberModel(250, 10, 5000, 10));

        txtBarcode = new JTextField(IdGenerator.generateBarcode("BOOK", (int)(Math.random() * 900 + 100)), 20);
        spPrice = new JSpinner(new SpinnerNumberModel(150000, 0, 50000000, 10000));
        cbFormat = new JComboBox<>(BookFormat.values());
        cbStatus = new JComboBox<>(BookStatus.values());
        spRackNumber = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        txtRackLoc = new JTextField("Khu A - Tầng 1 - Kệ IT", 20);
        chkReferenceOnly = new JCheckBox("Sách tham khảo (Chỉ đọc tại chỗ, không cho mượn về)");
        chkReferenceOnly.setBackground(Theme.BG_CARD);

        addFormField(form, gbc, row++, "Mã ISBN (*):", txtIsbn);
        addFormField(form, gbc, row++, "Tựa đề sách (*):", txtTitle);
        addFormField(form, gbc, row++, "Tác giả (*):", txtAuthors);
        addFormField(form, gbc, row++, "Thể loại / Chủ đề:", cbSubject);
        addFormField(form, gbc, row++, "Nhà xuất bản:", txtPublisher);
        addFormField(form, gbc, row++, "Ngôn ngữ:", txtLanguage);
        addFormField(form, gbc, row++, "Số trang:", spPages);

        JSeparator sep = new JSeparator();
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(sep, gbc);
        gbc.gridwidth = 1;

        addFormField(form, gbc, row++, "Mã vạch bản sao (Barcode) (*):", txtBarcode);
        addFormField(form, gbc, row++, "Giá bìa (VNĐ):", spPrice);
        addFormField(form, gbc, row++, "Định dạng sách:", cbFormat);
        addFormField(form, gbc, row++, "Trạng thái:", cbStatus);
        addFormField(form, gbc, row++, "Số thứ tự kệ (Rack #):", spRackNumber);
        addFormField(form, gbc, row++, "Vị trí kệ cụ thể:", txtRackLoc);

        gbc.gridx = 1; gbc.gridy = row++;
        form.add(chkReferenceOnly, gbc);

        contentPanel.add(new JScrollPane(form), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Hủy bỏ", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = Theme.createSuccessButton("💾 Lưu Thông Tin");
        btnSave.addActionListener(e -> onSave());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_SUBHEADER);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void populateFields(BookItem item) {
        txtIsbn.setText(item.getIsbn());
        txtIsbn.setEditable(false);
        txtTitle.setText(item.getTitle());
        txtAuthors.setText(item.getAuthorsAsString());
        cbSubject.setSelectedItem(item.getSubject());
        txtPublisher.setText(item.getPublisher());
        txtLanguage.setText(item.getLanguage());
        spPages.setValue(item.getNumberOfPages());

        txtBarcode.setText(item.getBarcode());
        txtBarcode.setEditable(false);
        spPrice.setValue((int)item.getPrice());
        cbFormat.setSelectedItem(item.getFormat());
        cbStatus.setSelectedItem(item.getStatus());
        if (item.getPlacedAt() != null) {
            spRackNumber.setValue(item.getPlacedAt().getNumber());
            txtRackLoc.setText(item.getPlacedAt().getLocationIdentifier());
        }
        chkReferenceOnly.setSelected(item.isReferenceOnly());
    }

    private void onSave() {
        String isbn = txtIsbn.getText().trim();
        String title = txtTitle.getText().trim();
        String authorStr = txtAuthors.getText().trim();
        String barcode = txtBarcode.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ISBN, Tựa đề và Mã vạch!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Author> authors = new ArrayList<>();
        if (!authorStr.isEmpty()) {
            for (String part : authorStr.split(",")) {
                if (!part.trim().isEmpty()) {
                    authors.add(new Author(part.trim(), ""));
                }
            }
        }
        if (authors.isEmpty()) {
            authors.add(new Author("Chưa rõ", ""));
        }

        String subject = (String) cbSubject.getSelectedItem();
        String publisher = txtPublisher.getText().trim();
        String language = txtLanguage.getText().trim();
        int pages = (Integer) spPages.getValue();
        double price = ((Number) spPrice.getValue()).doubleValue();
        BookFormat format = (BookFormat) cbFormat.getSelectedItem();
        BookStatus status = (BookStatus) cbStatus.getSelectedItem();
        int rackNum = (Integer) spRackNumber.getValue();
        String rackLoc = txtRackLoc.getText().trim();
        Rack rack = new Rack(rackNum, rackLoc);
        boolean isRef = chkReferenceOnly.isSelected();

        BookItem item;
        if (editingItem != null) {
            item = editingItem;
            item.setTitle(title);
            item.setAuthors(authors);
            item.setSubject(subject);
            item.setPublisher(publisher);
            item.setLanguage(language);
            item.setNumberOfPages(pages);
            item.setPrice(price);
            item.setFormat(format);
            item.setStatus(status);
            item.setPlacedAt(rack);
            item.setReferenceOnly(isRef);
            library.getCatalog().updateBookItem(item);
        } else {
            item = new BookItem(isbn, title, subject, publisher, language, pages, authors, barcode, isRef,
                    price, format, status, LocalDate.now(), LocalDate.now(), rack);
            library.getCatalog().addBookItem(item);
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
