package com.library.gui.dialogs;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.library.enums.AccountStatus;
import com.library.gui.Theme;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.IdGenerator;

/**
 * Dialog đăng ký hoặc cập nhật thành viên thư viện (Yêu cầu R5, R6).
 */
public class AddEditMemberDialog extends JDialog {
    private final Library library;
    private final Member editingMember;
    private boolean saved = false;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtStreet;
    private JTextField txtCity;
    private JTextField txtCardNumber;
    private JComboBox<AccountStatus> cbStatus;

    public AddEditMemberDialog(Frame owner, Library library, Member memberToEdit) {
        super(owner, memberToEdit == null ? "Đăng Ký Thành Viên Mới" : "Cập Nhật Thông Tin Thành Viên", true);
        this.library = library;
        this.editingMember = memberToEdit;

        initComponents();
        if (memberToEdit != null) {
            populateFields(memberToEdit);
        }
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Theme.BG_CARD);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel(editingMember == null ? "Đăng Ký Tài Khoản & Cấp Thẻ Thư Viện" : "Cập Nhật Hồ Sơ Thành Viên");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.PRIMARY);
        contentPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        txtId = new JTextField(editingMember == null ? IdGenerator.generateMemberId() : "", 20);
        if (editingMember == null) txtId.setEditable(false);
        txtName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);
        txtStreet = new JTextField("123 Đường Giải Phóng", 20);
        txtCity = new JTextField("Hà Nội", 20);
        txtCardNumber = new JTextField(editingMember == null ? IdGenerator.generateCardNumber() : "", 20);
        txtCardNumber.setEditable(false);
        cbStatus = new JComboBox<>(AccountStatus.values());

        addFormField(form, gbc, row++, "Mã Thành Viên (ID):", txtId);
        addFormField(form, gbc, row++, "Họ và Tên (*):", txtName);
        addFormField(form, gbc, row++, "Địa chỉ Email (*):", txtEmail);
        addFormField(form, gbc, row++, "Số điện thoại (*):", txtPhone);
        addFormField(form, gbc, row++, "Địa chỉ đường phố:", txtStreet);
        addFormField(form, gbc, row++, "Tỉnh / Thành phố:", txtCity);
        addFormField(form, gbc, row++, "Mã thẻ thư viện được cấp:", txtCardNumber);
        addFormField(form, gbc, row++, "Trạng thái tài khoản:", cbStatus);

        contentPanel.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.BG_CARD);

        JButton btnCancel = Theme.createButton("Hủy bỏ", Theme.BG_MAIN, Theme.TEXT_MAIN);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = Theme.createSuccessButton("💾 Lưu Hồ Sơ");
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

    private void populateFields(Member m) {
        txtId.setText(m.getId());
        txtId.setEditable(false);
        txtName.setText(m.getName());
        txtEmail.setText(m.getEmail());
        txtPhone.setText(m.getPhone());
        if (m.getAddress() != null) {
            txtStreet.setText(m.getAddress().getStreetAddress());
            txtCity.setText(m.getAddress().getCity());
        }
        if (m.getCard() != null) {
            txtCardNumber.setText(m.getCard().getCardNumber());
        }
        cbStatus.setSelectedItem(m.getStatus());
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và email!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Address addr = new Address(txtStreet.getText().trim(), txtCity.getText().trim());
        Person person = new Person(name, addr, email, phone);
        AccountStatus status = (AccountStatus) cbStatus.getSelectedItem();

        if (editingMember != null) {
            editingMember.setPerson(person);
            editingMember.setStatus(status);
            if (editingMember.getCard() != null) {
                editingMember.getCard().setActive(status == AccountStatus.ACTIVE);
            }
        } else {
            String memberId = txtId.getText().trim();
            String cardNum = txtCardNumber.getText().trim();
            LibraryCard card = new LibraryCard(cardNum, "BC-" + cardNum, LocalDate.now(), true);
            Member newMember = new Member(memberId, "123456", person, card, status, LocalDate.now());
            library.registerMember(newMember);
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
