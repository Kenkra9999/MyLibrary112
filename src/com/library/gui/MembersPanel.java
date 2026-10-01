package com.library.gui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.enums.AccountStatus;
import com.library.models.BookLending;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.Constants;
import com.library.utils.DateUtils;

/**
 * Panel Quản lý Thành Viên Thư Viện (Member Management - R5, R6).
 */
public class MembersPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private JTable tableMembers;
    private DefaultTableModel tableModel;
    private List<Member> currentDisplayedMembers;

    public MembersPanel(MainFrame mainFrame, Library library) {
        this.mainFrame = mainFrame;
        this.library = library;

        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // TOP: Header & Search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Theme.BG_MAIN);

        JLabel lblTitle = new JLabel("👥 Quản Lý Hồ Sơ Thành Viên & Cấp Thẻ Thư Viện");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel searchCard = Theme.createCardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtSearch = new JTextField(20);
        txtSearch.setFont(Theme.FONT_BODY);

        cbStatusFilter = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động (ACTIVE)", "Bị khóa / Blacklist (BLACKLISTED)"});
        cbStatusFilter.setFont(Theme.FONT_BODY);
        cbStatusFilter.addActionListener(e -> performSearch());

        JButton btnSearch = Theme.createPrimaryButton("🔍 Tìm Kiếm");
        btnSearch.addActionListener(e -> performSearch());

        searchCard.add(new JLabel("Tìm tên / Email / Mã TV:"));
        searchCard.add(txtSearch);
        searchCard.add(new JLabel("Trạng thái:"));
        searchCard.add(cbStatusFilter);
        searchCard.add(btnSearch);

        topPanel.add(searchCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Thành Viên", "Họ và Tên", "Email Liên Hệ", "Số Điện Thoại", "Địa Chỉ", "Số Thẻ TV", "Sách Đang Mượn", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMembers = new JTable(tableModel);
        Theme.styleTable(tableMembers);
        tableMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableCard.add(new JScrollPane(tableMembers), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnAddMember = Theme.createSuccessButton("➕ Đăng Ký Thành Viên Mới");
        btnAddMember.addActionListener(e -> mainFrame.openAddMemberDialog());

        JButton btnEditMember = Theme.createButton("✏️ Sửa Thông Tin", Theme.ACCENT, Color.WHITE);
        btnEditMember.addActionListener(e -> onEditSelected());

        JButton btnToggleBlock = Theme.createButton("🔒 Khóa / Mở Khóa", Theme.WARNING, Color.WHITE);
        btnToggleBlock.addActionListener(e -> onToggleBlockSelected());

        JButton btnHistory = Theme.createButton("📜 Lịch Sử Mượn Trả", Theme.PURPLE, Color.WHITE);
        btnHistory.addActionListener(e -> onViewHistorySelected());

        JButton btnDelete = Theme.createDangerButton("🗑️ Xóa Thành Viên");
        btnDelete.addActionListener(e -> onDeleteSelected());

        bottomPanel.add(btnAddMember);
        bottomPanel.add(btnEditMember);
        bottomPanel.add(btnToggleBlock);
        bottomPanel.add(btnHistory);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        performSearch();
    }

    private void performSearch() {
        String query = txtSearch.getText().trim().toLowerCase();
        int statusIdx = cbStatusFilter.getSelectedIndex();

        List<Member> list = library.getAllMembers();

        if (statusIdx == 1) {
            list = list.stream().filter(m -> m.getStatus() == AccountStatus.ACTIVE).collect(Collectors.toList());
        } else if (statusIdx == 2) {
            list = list.stream().filter(m -> m.getStatus() == AccountStatus.BLACKLISTED).collect(Collectors.toList());
        }

        if (!query.isEmpty()) {
            list = list.stream().filter(m ->
                    m.getId().toLowerCase().contains(query) ||
                    m.getName().toLowerCase().contains(query) ||
                    m.getEmail().toLowerCase().contains(query) ||
                    m.getPhone().toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        currentDisplayedMembers = list;
        updateTable(list);
    }

    private void updateTable(List<Member> list) {
        tableModel.setRowCount(0);
        for (Member m : list) {
            String cardNum = m.getCard() != null ? m.getCard().getCardNumber() : "Chưa có";
            String addrStr = m.getAddress() != null ? m.getAddress().toString() : "--";
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getEmail(),
                    m.getPhone(),
                    addrStr,
                    cardNum,
                    m.getTotalBooksCheckedOut() + " / " + Constants.MAX_BOOKS_ISSUED_TO_A_USER + " cuốn",
                    m.getStatus().getDisplayName()
            });
        }
    }

    private Member getSelectedMember() {
        int row = tableMembers.getSelectedRow();
        if (row >= 0 && row < currentDisplayedMembers.size()) {
            return currentDisplayedMembers.get(row);
        }
        return null;
    }

    private void onEditSelected() {
        Member m = getSelectedMember();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên cần chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.openEditMemberDialog(m);
    }

    private void onToggleBlockSelected() {
        Member m = getSelectedMember();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để khóa/mở khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (m.getStatus() == AccountStatus.ACTIVE) {
            m.setStatus(AccountStatus.BLACKLISTED);
            if (m.getCard() != null) m.getCard().setActive(false);
            JOptionPane.showMessageDialog(this, "Đã khóa thẻ và cấm tài khoản của thành viên " + m.getName(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            m.setStatus(AccountStatus.ACTIVE);
            if (m.getCard() != null) m.getCard().setActive(true);
            JOptionPane.showMessageDialog(this, "Đã kích hoạt lại tài khoản cho thành viên " + m.getName(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        mainFrame.saveLibraryData();
        refreshData();
    }

    private void onViewHistorySelected() {
        Member m = getSelectedMember();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để xem lịch sử mượn trả!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<BookLending> history = library.getLendingHistoryForMember(m.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Lịch sử mượn trả của: ").append(m.getName()).append(" (Mã: ").append(m.getId()).append(")\n");
        sb.append("===================================================\n\n");

        if (history.isEmpty()) {
            sb.append("Chưa có giao dịch mượn trả nào.");
        } else {
            for (BookLending l : history) {
                sb.append("• ").append(l.fetchLendingDetails()).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString(), 15, 45);
        textArea.setFont(Theme.FONT_BODY);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Lịch sử mượn sách", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDeleteSelected() {
        Member m = getSelectedMember();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (m.getTotalBooksCheckedOut() > 0) {
            JOptionPane.showMessageDialog(this, "Không thể xóa thành viên đang mượn " + m.getTotalBooksCheckedOut() + " cuốn sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thành viên " + m.getName() + " (" + m.getId() + ")?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            library.deleteMember(m.getId());
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã xóa thành viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
