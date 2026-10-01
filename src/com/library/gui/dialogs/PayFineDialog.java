package com.library.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.gui.Theme;
import com.library.models.Fine;
import com.library.services.Library;
import com.library.utils.DateUtils;

/**
 * Dialog thanh toán tiền phạt quá hạn (Pay Fine - R15).
 */
public class PayFineDialog extends JDialog {
    private final Library library;
    private boolean completed = false;

    private JComboBox<String> cbUnpaidFines;
    private JLabel lblAmountInfo;

    public PayFineDialog(Frame owner, Library library, String preselectedFineId) {
        super(owner, "Thu Phí Phạt Quá Hạn (Pay Fine)", true);
        this.library = library;

        initComponents(preselectedFineId);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents(String preselectedFineId) {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Xác Nhận Thanh Toán Tiền Phạt");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        DefaultComboBoxModel<String> fineModel = new DefaultComboBoxModel<>();
        for (Fine f : library.getFines()) {
            if (!f.isPaid()) {
                fineModel.addElement(f.getFineId() + " | " + String.format("%,.0f VNĐ", f.getAmount()) + " - TV: " + f.getMemberId() + " (Sách: " + f.getItemBarcode() + ")");
            }
        }
        cbUnpaidFines = new JComboBox<>(fineModel);

        lblAmountInfo = new JLabel("Sau khi thanh toán, tài khoản thành viên sẽ được mở quyền mượn sách bình thường.");
        lblAmountInfo.setFont(Theme.FONT_SMALL);
        lblAmountInfo.setForeground(Theme.TEXT_MUTED);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Khoản Phạt Cần Đóng (*):"), gbc);
        gbc.gridx = 1; form.add(cbUnpaidFines, gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(lblAmountInfo, gbc);

        if (preselectedFineId != null) {
            for (int i = 0; i < cbUnpaidFines.getItemCount(); i++) {
                if (cbUnpaidFines.getItemAt(i).startsWith(preselectedFineId)) {
                    cbUnpaidFines.setSelectedIndex(i);
                    break;
                }
            }
        }

        contentPanel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Hủy", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnPay = Theme.createSuccessButton("💵 Xác Nhận Đã Thu Tiền");
        btnPay.addActionListener(e -> onPay());

        btnPanel.add(btnCancel);
        btnPanel.add(btnPay);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void onPay() {
        String selected = (String) cbUnpaidFines.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Không có khoản tiền phạt nào cần thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fineId = selected.split(" \\| ")[0].trim();
        Library.OperationResult result = library.payFine(fineId);
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            completed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCompleted() {
        return completed;
    }
}
