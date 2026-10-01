package com.library.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.gui.Theme;
import com.library.models.BookItem;
import com.library.services.Library;
import com.library.users.Member;

/**
 * Dialog thực hiện cấp phát và mượn sách cho thành viên (Checkout Book - R7, R8).
 */
public class CheckoutDialog extends JDialog {
    private final Library library;
    private boolean completed = false;

    private JComboBox<String> cbMembers;
    private JComboBox<String> cbBookItems;
    private JLabel lblMemberInfo;
    private JLabel lblBookInfo;

    public CheckoutDialog(Frame owner, Library library, String preselectedBarcode, String preselectedMemberId) {
        super(owner, "Thực Hiện Cho Mượn Sách (Checkout)", true);
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

        JLabel lblHeader = new JLabel("Lập Phiếu Mượn Sách Cho Thành Viên");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Populate Members
        DefaultComboBoxModel<String> memModel = new DefaultComboBoxModel<>();
        for (Member m : library.getAllMembers()) {
            memModel.addElement(m.getId() + " - " + m.getName() + " (Đang mượn: " + m.getTotalBooksCheckedOut() + "/10)");
        }
        cbMembers = new JComboBox<>(memModel);

        // Populate Available Book Items
        DefaultComboBoxModel<String> bookModel = new DefaultComboBoxModel<>();
        for (BookItem item : library.getCatalog().getAllBookItems()) {
            bookModel.addElement(item.getBarcode() + " | " + item.getTitle() + " [" + item.getStatus() + "]");
        }
        cbBookItems = new JComboBox<>(bookModel);

        lblMemberInfo = new JLabel(" ");
        lblMemberInfo.setFont(Theme.FONT_SMALL);
        lblMemberInfo.setForeground(Theme.TEXT_MUTED);

        lblBookInfo = new JLabel(" ");
        lblBookInfo.setFont(Theme.FONT_SMALL);
        lblBookInfo.setForeground(Theme.TEXT_MUTED);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Chọn Thành Viên (*):"), gbc);
        gbc.gridx = 1; form.add(cbMembers, gbc);
        gbc.gridx = 1; gbc.gridy = ++row; form.add(lblMemberInfo, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; form.add(new JLabel("Chọn Sách Mượn (*):"), gbc);
        gbc.gridx = 1; form.add(cbBookItems, gbc);
        gbc.gridx = 1; gbc.gridy = ++row; form.add(lblBookInfo, gbc);

        // Select preselected if any
        if (preselectedMemberId != null) {
            for (int i = 0; i < cbMembers.getItemCount(); i++) {
                if (cbMembers.getItemAt(i).startsWith(preselectedMemberId)) {
                    cbMembers.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (preselectedBarcode != null) {
            for (int i = 0; i < cbBookItems.getItemCount(); i++) {
                if (cbBookItems.getItemAt(i).startsWith(preselectedBarcode)) {
                    cbBookItems.setSelectedIndex(i);
                    break;
                }
            }
        }

        contentPanel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Hủy bỏ", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnCheckout = Theme.createPrimaryButton("⚡ Xác Nhận Mượn Sách");
        btnCheckout.addActionListener(e -> onCheckout());

        btnPanel.add(btnCancel);
        btnPanel.add(btnCheckout);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void onCheckout() {
        String selectedMem = (String) cbMembers.getSelectedItem();
        String selectedBook = (String) cbBookItems.getSelectedItem();

        if (selectedMem == null || selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên và sách cần mượn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String memberId = selectedMem.split(" - ")[0].trim();
        String barcode = selectedBook.split(" \\| ")[0].trim();

        Library.OperationResult result = library.checkoutBook(barcode, memberId);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            completed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Không thể mượn sách", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCompleted() {
        return completed;
    }
}
