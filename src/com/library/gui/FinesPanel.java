package com.library.gui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.models.BookItem;
import com.library.models.Fine;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.DateUtils;

/**
 * Panel Quản lý và Thu Tiền Phạt Quá Hạn (Fine Management - R15 / Pay Fine).
 */
public class FinesPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JComboBox<String> cbFilter;
    private JTable tableFines;
    private DefaultTableModel tableModel;
    private List<Fine> currentDisplayedFines;

    public FinesPanel(MainFrame mainFrame, Library library) {
        this.mainFrame = mainFrame;
        this.library = library;

        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // TOP: Header
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Theme.BG_MAIN);

        JLabel lblTitle = new JLabel("💰 Quản Lý Thu Tiền Phạt Quá Hạn Mượn Sách (Fine Management)");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel filterCard = Theme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        cbFilter = new JComboBox<>(new String[]{"Chưa thanh toán (Cần thu)", "Đã thanh toán", "Tất cả biên lai"});
        cbFilter.setFont(Theme.FONT_BODY);
        cbFilter.addActionListener(e -> performFilter());

        filterCard.add(new JLabel("Lọc danh sách biên lai phạt:"));
        filterCard.add(cbFilter);

        topPanel.add(filterCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Phạt", "Mã Thành Viên", "Họ Tên Thành Viên", "Mã Sách Trễ", "Tựa Đề Sách", "Số Tiền Phạt (VNĐ)", "Ngày Lập", "Ngày Thu", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableFines = new JTable(tableModel);
        Theme.styleTable(tableFines);
        tableFines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableCard.add(new JScrollPane(tableFines), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnPayFine = Theme.createSuccessButton("💵 Xác Nhận Thu Tiền Phạt (Pay Fine)");
        btnPayFine.addActionListener(e -> onPayFineSelected());

        bottomPanel.add(btnPayFine);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        performFilter();
    }

    private void performFilter() {
        int idx = cbFilter.getSelectedIndex();
        List<Fine> list = library.getFines();

        if (idx == 0) {
            list = list.stream().filter(f -> !f.isPaid()).collect(Collectors.toList());
        } else if (idx == 1) {
            list = list.stream().filter(Fine::isPaid).collect(Collectors.toList());
        }

        currentDisplayedFines = list;
        updateTable(list);
    }

    private void updateTable(List<Fine> list) {
        tableModel.setRowCount(0);
        for (Fine f : list) {
            Member member = library.getMemberById(f.getMemberId());
            BookItem item = library.getCatalog().getBookItemByBarcode(f.getItemBarcode());

            String memName = member != null ? member.getName() : "N/A";
            String title = item != null ? item.getTitle() : "N/A";

            tableModel.addRow(new Object[]{
                    f.getFineId(),
                    f.getMemberId(),
                    memName,
                    f.getItemBarcode(),
                    title,
                    String.format("%,.0f VNĐ", f.getAmount()),
                    DateUtils.formatDate(f.getCreationDate()),
                    f.getPaidDate() != null ? DateUtils.formatDate(f.getPaidDate()) : "--",
                    f.isPaid() ? "Đã đóng" : "🔴 CHƯA ĐÓNG"
            });
        }
    }

    private Fine getSelectedFine() {
        int row = tableFines.getSelectedRow();
        if (row >= 0 && row < currentDisplayedFines.size()) {
            return currentDisplayedFines.get(row);
        }
        return null;
    }

    private void onPayFineSelected() {
        Fine fine = getSelectedFine();
        if (fine != null) {
            if (fine.isPaid()) {
                JOptionPane.showMessageDialog(this, "Khoản phạt này đã được thanh toán trước đó!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainFrame.openPayFineDialog(fine.getFineId());
        } else {
            mainFrame.openPayFineDialog(null);
        }
    }
}
