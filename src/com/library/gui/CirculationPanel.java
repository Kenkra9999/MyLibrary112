package com.library.gui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.models.BookItem;
import com.library.models.BookLending;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.DateUtils;

/**
 * Panel Quản lý Lưu thông Sách: Mượn, Trả, Gia hạn (Circulation System - R1, R7, R8, R10, R11, R15).
 */
public class CirculationPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JComboBox<String> cbFilter;
    private JTextField txtSearch;
    private JTable tableLendings;
    private DefaultTableModel tableModel;
    private List<BookLending> currentDisplayedLendings;

    public CirculationPanel(MainFrame mainFrame, Library library) {
        this.mainFrame = mainFrame;
        this.library = library;

        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // TOP: Header & Filters
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Theme.BG_MAIN);

        JLabel lblTitle = new JLabel("🔄 Quản Lý Lưu Thông: Mượn, Trả & Gia Hạn Sách (Circulation)");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel filterCard = Theme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        cbFilter = new JComboBox<>(new String[]{"Đang mượn (Chưa trả)", "Quá hạn (Cần thu hồi)", "Đã hoàn trả (Lịch sử)", "Tất cả các lượt"});
        cbFilter.setFont(Theme.FONT_BODY);
        cbFilter.addActionListener(e -> performFilter());

        txtSearch = new JTextField(18);
        txtSearch.setFont(Theme.FONT_BODY);

        JButton btnSearch = Theme.createPrimaryButton("Lọc / Tìm");
        btnSearch.addActionListener(e -> performFilter());

        filterCard.add(new JLabel("Bộ lọc trạng thái:"));
        filterCard.add(cbFilter);
        filterCard.add(new JLabel("Tìm mã sách / mã TV:"));
        filterCard.add(txtSearch);
        filterCard.add(btnSearch);

        topPanel.add(filterCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Phiếu Mượn", "Mã Vạch Sách", "Tựa Đề Sách", "Mã Thành Viên", "Họ Tên Thành Viên", "Ngày Mượn", "Hạn Trả", "Ngày Trả Thực", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLendings = new JTable(tableModel);
        Theme.styleTable(tableLendings);
        tableLendings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableCard.add(new JScrollPane(tableLendings), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnCheckout = Theme.createPrimaryButton("➕ Cho Mượn Sách (Checkout)");
        btnCheckout.addActionListener(e -> mainFrame.openCheckoutDialog(null, null));

        JButton btnReturn = Theme.createSuccessButton("📥 Trả Sách (Return)");
        btnReturn.addActionListener(e -> {
            BookLending selected = getSelectedLending();
            if (selected != null && !selected.isReturned()) {
                mainFrame.openReturnDialog(selected.getItemBarcode(), selected.getMemberId());
            } else {
                mainFrame.openReturnDialog(null, null);
            }
        });

        JButton btnRenew = Theme.createButton("⏳ Gia Hạn Mượn (+15 ngày)", Theme.PURPLE, Color.WHITE);
        btnRenew.addActionListener(e -> onRenewSelected());

        JButton btnNotifyOverdue = Theme.createButton("🔔 Quét & Báo Quá Hạn", Theme.WARNING, Color.WHITE);
        btnNotifyOverdue.addActionListener(e -> {
            int count = library.checkOverdueBooksAndNotify();
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã gửi thông báo cho " + count + " lượt mượn quá hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        bottomPanel.add(btnCheckout);
        bottomPanel.add(btnReturn);
        bottomPanel.add(btnRenew);
        bottomPanel.add(btnNotifyOverdue);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        performFilter();
    }

    private void performFilter() {
        int filterIndex = cbFilter.getSelectedIndex();
        String query = txtSearch.getText().trim().toLowerCase();

        List<BookLending> list = library.getLendings();

        if (filterIndex == 0) { // Đang mượn
            list = list.stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
        } else if (filterIndex == 1) { // Quá hạn
            list = list.stream().filter(l -> !l.isReturned() && l.isOverdue()).collect(Collectors.toList());
        } else if (filterIndex == 2) { // Đã trả
            list = list.stream().filter(BookLending::isReturned).collect(Collectors.toList());
        }

        if (!query.isEmpty()) {
            list = list.stream().filter(l ->
                    l.getItemBarcode().toLowerCase().contains(query) ||
                    l.getMemberId().toLowerCase().contains(query) ||
                    l.getLendingId().toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        currentDisplayedLendings = list;
        updateTable(list);
    }

    private void updateTable(List<BookLending> list) {
        tableModel.setRowCount(0);
        for (BookLending l : list) {
            BookItem item = library.getCatalog().getBookItemByBarcode(l.getItemBarcode());
            Member member = library.getMemberById(l.getMemberId());

            String title = item != null ? item.getTitle() : "N/A";
            String memName = member != null ? member.getName() : "N/A";

            String statusStr;
            if (l.isReturned()) {
                statusStr = "Đã trả (" + DateUtils.formatDate(l.getReturnDate()) + ")";
            } else if (l.isOverdue()) {
                statusStr = "⚠️ QUÁ HẠN (" + l.getOverdueDays() + " ngày)";
            } else {
                statusStr = "Đang mượn";
            }

            tableModel.addRow(new Object[]{
                    l.getLendingId(),
                    l.getItemBarcode(),
                    title,
                    l.getMemberId(),
                    memName,
                    DateUtils.formatDate(l.getCreationDate()),
                    DateUtils.formatDate(l.getDueDate()),
                    l.getReturnDate() != null ? DateUtils.formatDate(l.getReturnDate()) : "--",
                    statusStr
            });
        }
    }

    private BookLending getSelectedLending() {
        int row = tableLendings.getSelectedRow();
        if (row >= 0 && row < currentDisplayedLendings.size()) {
            return currentDisplayedLendings.get(row);
        }
        return null;
    }

    private void onRenewSelected() {
        BookLending lending = getSelectedLending();
        if (lending == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lượt mượn trong bảng để gia hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (lending.isReturned()) {
            JOptionPane.showMessageDialog(this, "Cuốn sách này đã được trả lại, không thể gia hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Library.OperationResult result = library.renewBook(lending.getItemBarcode(), lending.getMemberId());
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Gia hạn thành công", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.saveLibraryData();
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Không thể gia hạn", JOptionPane.ERROR_MESSAGE);
        }
    }
}
