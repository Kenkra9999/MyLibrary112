package com.library.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.gui.Theme;
import com.library.models.BookLending;
import com.library.services.Library;
import com.library.utils.DateUtils;

/**
 * Dialog thực hiện trả sách, phát hiện trễ hạn và tự động tạo biên lai phạt (Return Book - R1, R15).
 */
public class ReturnDialog extends JDialog {
    private final Library library;
    private boolean completed = false;

    private JComboBox<String> cbActiveLendings;
    private JLabel lblDetails;

    public ReturnDialog(Frame owner, Library library, String preselectedBarcode, String preselectedMemberId) {
        super(owner, "Thực Hiện Trả Sách (Return Book)", true);
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

        JLabel lblHeader = new JLabel("Tiếp Nhận Sách Trả Về Thư Viện");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        DefaultComboBoxModel<String> lendingModel = new DefaultComboBoxModel<>();
        for (BookLending l : library.getLendings()) {
            if (!l.isReturned()) {
                String overdueText = l.isOverdue() ? " [QUÁ HẠN " + l.getOverdueDays() + " NGÀY!]" : " [Còn hạn đến " + DateUtils.formatDate(l.getDueDate()) + "]";
                lendingModel.addElement(l.getItemBarcode() + " | Thành viên: " + l.getMemberId() + overdueText);
            }
        }
        cbActiveLendings = new JComboBox<>(lendingModel);

        lblDetails = new JLabel("Hệ thống sẽ tự động tính phí phạt quá hạn (5.000đ/ngày) và kích hoạt giữ sách (nếu có).");
        lblDetails.setFont(Theme.FONT_SMALL);
        lblDetails.setForeground(Theme.TEXT_MUTED);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Chọn Lượt Mượn (*):"), gbc);
        gbc.gridx = 1; form.add(cbActiveLendings, gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(lblDetails, gbc);

        // Preselect
        if (preselectedBarcode != null) {
            for (int i = 0; i < cbActiveLendings.getItemCount(); i++) {
                if (cbActiveLendings.getItemAt(i).startsWith(preselectedBarcode)) {
                    cbActiveLendings.setSelectedIndex(i);
                    break;
                }
            }
        }

        contentPanel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Đóng", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnReturn = Theme.createSuccessButton("📥 Xác Nhận Trả Sách");
        btnReturn.addActionListener(e -> onReturn());

        btnPanel.add(btnCancel);
        btnPanel.add(btnReturn);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void onReturn() {
        String selected = (String) cbActiveLendings.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Không có sách nào đang mượn cần trả!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] parts = selected.split(" \\| Thành viên: ");
        String barcode = parts[0].trim();
        String memberId = parts[1].split(" \\[")[0].trim();

        Library.OperationResult result = library.returnBook(barcode, memberId);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Hoàn tất trả sách", JOptionPane.INFORMATION_MESSAGE);
            completed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi trả sách", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCompleted() {
        return completed;
    }
}
