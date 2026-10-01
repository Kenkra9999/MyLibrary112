package com.library.gui;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.enums.ReservationStatus;
import com.library.models.BookItem;
import com.library.models.BookReservation;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.DateUtils;

/**
 * Panel Quản lý Đặt Giữ Sách (Book Reservations - R9, R10, R13).
 */
public class ReservationPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JComboBox<String> cbStatusFilter;
    private JTable tableReservations;
    private DefaultTableModel tableModel;
    private List<BookReservation> currentDisplayedReservations;

    public ReservationPanel(MainFrame mainFrame, Library library) {
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

        JLabel lblTitle = new JLabel("📑 Quản Lý Hàng Đợi & Đặt Giữ Sách (Book Reservations)");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel filterCard = Theme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        cbStatusFilter = new JComboBox<>(new String[]{"Đang chờ & Sẵn sàng lấy", "Đang chờ sách trả (WAITING)", "Đã sẵn sàng nhận (PENDING)", "Đã hoàn thành (COMPLETED)", "Đã hủy (CANCELED)", "Tất cả"});
        cbStatusFilter.setFont(Theme.FONT_BODY);
        cbStatusFilter.addActionListener(e -> performFilter());

        filterCard.add(new JLabel("Lọc theo trạng thái đặt giữ:"));
        filterCard.add(cbStatusFilter);

        topPanel.add(filterCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Đặt Giữ", "Mã Sách", "Tựa Đề Sách", "Mã Thành Viên", "Họ Tên", "Thời Điểm Đặt", "Trạng Thái Đặt Giữ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableReservations = new JTable(tableModel);
        Theme.styleTable(tableReservations);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableCard.add(new JScrollPane(tableReservations), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnNewReserve = Theme.createButton("➕ Đặt Giữ Sách Mới", Theme.PURPLE, Color.WHITE);
        btnNewReserve.addActionListener(e -> mainFrame.openReserveDialog(null, null));

        JButton btnIssueToReserved = Theme.createPrimaryButton("⚡ Cấp Phát Cho Người Đặt");
        btnIssueToReserved.addActionListener(e -> onIssueToReserved());

        JButton btnCancelReserve = Theme.createDangerButton("❌ Hủy Đặt Giữ");
        btnCancelReserve.addActionListener(e -> onCancelReserve());

        bottomPanel.add(btnNewReserve);
        bottomPanel.add(btnIssueToReserved);
        bottomPanel.add(btnCancelReserve);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        performFilter();
    }

    private void performFilter() {
        int idx = cbStatusFilter.getSelectedIndex();
        List<BookReservation> list = library.getReservations();

        if (idx == 0) {
            list = list.stream().filter(r -> r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING).collect(Collectors.toList());
        } else if (idx == 1) {
            list = list.stream().filter(r -> r.getStatus() == ReservationStatus.WAITING).collect(Collectors.toList());
        } else if (idx == 2) {
            list = list.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).collect(Collectors.toList());
        } else if (idx == 3) {
            list = list.stream().filter(r -> r.getStatus() == ReservationStatus.COMPLETED).collect(Collectors.toList());
        } else if (idx == 4) {
            list = list.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELED).collect(Collectors.toList());
        }

        currentDisplayedReservations = list;
        updateTable(list);
    }

    private void updateTable(List<BookReservation> list) {
        tableModel.setRowCount(0);
        for (BookReservation r : list) {
            BookItem item = library.getCatalog().getBookItemByBarcode(r.getItemBarcode());
            Member member = library.getMemberById(r.getMemberId());

            String title = item != null ? item.getTitle() : "N/A";
            String memName = member != null ? member.getName() : "N/A";

            tableModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getItemBarcode(),
                    title,
                    r.getMemberId(),
                    memName,
                    DateUtils.formatDateTime(r.getCreationDate()),
                    r.getStatus().getDisplayName()
            });
        }
    }

    private BookReservation getSelectedReservation() {
        int row = tableReservations.getSelectedRow();
        if (row >= 0 && row < currentDisplayedReservations.size()) {
            return currentDisplayedReservations.get(row);
        }
        return null;
    }

    private void onIssueToReserved() {
        BookReservation r = getSelectedReservation();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu đặt giữ từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (r.getStatus() == ReservationStatus.COMPLETED || r.getStatus() == ReservationStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Phiếu đặt giữ này đã hoàn tất hoặc đã bị hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.openCheckoutDialog(r.getItemBarcode(), r.getMemberId());
    }

    private void onCancelReserve() {
        BookReservation r = getSelectedReservation();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu đặt giữ để hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (r.getStatus() == ReservationStatus.COMPLETED || r.getStatus() == ReservationStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Phiếu này đã kết thúc trước đó!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy phiếu đặt giữ " + r.getReservationId() + "?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            library.cancelReservation(r.getReservationId());
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã hủy phiếu đặt giữ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
