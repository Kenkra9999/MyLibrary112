package com.library.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.enums.BookStatus;
import com.library.gui.Theme;
import com.library.models.BookItem;
import com.library.services.Library;
import com.library.users.Member;

/**
 * Dialog đặt giữ sách khi sách đang không có sẵn (Book Reservation - R9, R13).
 */
public class ReserveDialog extends JDialog {
    private final Library library;
    private boolean completed = false;

    private JComboBox<String> cbMembers;
    private JComboBox<String> cbUnavailableBooks;

    public ReserveDialog(Frame owner, Library library, String preselectedBarcode, String preselectedMemberId) {
        super(owner, "Đặt Giữ Sách (Reserve Book)", true);
        this.library = library;

        initComponents(preselectedBarcode, preselectedMemberId);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents(String preselectedBarcode, String preselectedMemberId) {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Lập Phiếu Đặt Giữ Sách Đang Mượn / Hết");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        DefaultComboBoxModel<String> memModel = new DefaultComboBoxModel<>();
        for (Member m : library.getAllMembers()) {
            memModel.addElement(m.getId() + " - " + m.getName());
        }
        cbMembers = new JComboBox<>(memModel);

        DefaultComboBoxModel<String> bookModel = new DefaultComboBoxModel<>();
        for (BookItem item : library.getCatalog().getAllBookItems()) {
            // Cho phép đặt trước cả sách đang mượn hoặc sách khác
            bookModel.addElement(item.getBarcode() + " | " + item.getTitle() + " [" + item.getStatus() + "]");
        }
        cbUnavailableBooks = new JComboBox<>(bookModel);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Thành Viên Đặt (*):"), gbc);
        gbc.gridx = 1; form.add(cbMembers, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Sách Cần Đặt Giữ (*):"), gbc);
        gbc.gridx = 1; form.add(cbUnavailableBooks, gbc);

        JLabel lblHelp = new JLabel("<html><i>* Lưu ý: Mỗi cuốn sách chỉ được giữ bởi 1 người tại một thời điểm (R9).<br>Khi sách được trả về, bạn sẽ nhận được thông báo ưu tiên mượn (R12, R13).</i></html>");
        lblHelp.setFont(Theme.FONT_SMALL);
        lblHelp.setForeground(Theme.TEXT_MUTED);
        gbc.gridx = 1; gbc.gridy = 2; form.add(lblHelp, gbc);

        if (preselectedBarcode != null) {
            for (int i = 0; i < cbUnavailableBooks.getItemCount(); i++) {
                if (cbUnavailableBooks.getItemAt(i).startsWith(preselectedBarcode)) {
                    cbUnavailableBooks.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (preselectedMemberId != null) {
            for (int i = 0; i < cbMembers.getItemCount(); i++) {
                if (cbMembers.getItemAt(i).startsWith(preselectedMemberId)) {
                    cbMembers.setSelectedIndex(i);
                    break;
                }
            }
        }

        contentPanel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Hủy bỏ", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnReserve = Theme.createButton("📌 Đặt Giữ Ngay", Theme.PURPLE, Color.WHITE);
        btnReserve.addActionListener(e -> onReserve());

        btnPanel.add(btnCancel);
        btnPanel.add(btnReserve);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void onReserve() {
        String selectedMem = (String) cbMembers.getSelectedItem();
        String selectedBook = (String) cbUnavailableBooks.getSelectedItem();

        if (selectedMem == null || selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông tin hợp lệ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String memberId = selectedMem.split(" - ")[0].trim();
        String barcode = selectedBook.split(" \\| ")[0].trim();

        Library.OperationResult result = library.reserveBook(barcode, memberId);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Đặt giữ thành công", JOptionPane.INFORMATION_MESSAGE);
            completed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isCompleted() {
        return completed;
    }
}
