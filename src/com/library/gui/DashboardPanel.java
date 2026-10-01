package com.library.gui;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.library.enums.BookStatus;
import com.library.enums.ReservationStatus;
import com.library.models.BookItem;
import com.library.models.BookLending;
import com.library.models.Fine;
import com.library.services.Library;
import com.library.utils.DateUtils;

/**
 * Panel Tổng Quan & Thống Kê (Dashboard) với các thẻ KPI trực quan.
 */
public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JLabel lblTotalBooks;
    private JLabel lblTotalItems;
    private JLabel lblActiveLendings;
    private JLabel lblOverdueCount;
    private JLabel lblActiveMembers;
    private JLabel lblPendingReservations;
    private JLabel lblUnpaidFines;
    private DefaultListModel<String> activityListModel;

    public DashboardPanel(MainFrame mainFrame, Library library) {
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.BG_MAIN);

        JLabel lblTitle = new JLabel("📊 Bảng Điều Khiển & Thống Kê Thư Viện");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);

        JLabel lblSubtitle = new JLabel("Hệ thống Quản lý Thư viện Hướng Đối Tượng (OOP Library Management System)");
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_MUTED);

        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: KPI Cards & Quick Actions
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Theme.BG_MAIN);

        // 6 KPI Cards Grid
        JPanel kpiGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        kpiGrid.setBackground(Theme.BG_MAIN);

        lblTotalItems = new JLabel("0");
        lblActiveLendings = new JLabel("0");
        lblOverdueCount = new JLabel("0");
        lblActiveMembers = new JLabel("0");
        lblPendingReservations = new JLabel("0");
        lblUnpaidFines = new JLabel("0đ");

        kpiGrid.add(createKpiCard("📚 Tổng Bản Sao Sách", lblTotalItems, "Trong danh mục", Theme.ACCENT));
        kpiGrid.add(createKpiCard("📖 Đang Cho Mượn", lblActiveLendings, "Sách ngoài thư viện", Theme.PURPLE));
        kpiGrid.add(createKpiCard("⚠️ Quá Hạn Cần Thu", lblOverdueCount, "Trễ hạn quy định", Theme.DANGER));
        kpiGrid.add(createKpiCard("👥 Thành Viên Đang Hoạt Động", lblActiveMembers, "Thẻ hợp lệ", Theme.SUCCESS));
        kpiGrid.add(createKpiCard("📑 Phiếu Đặt Giữ Chờ", lblPendingReservations, "Đang xếp hàng", Theme.WARNING));
        kpiGrid.add(createKpiCard("💰 Tiền Phạt Chưa Thu", lblUnpaidFines, "Phí phạt quá hạn", new Color(234, 88, 12)));

        centerPanel.add(kpiGrid, BorderLayout.NORTH);

        // Quick Actions & Recent Activities
        JPanel lowerPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        lowerPanel.setBackground(Theme.BG_MAIN);

        // Quick Actions Panel
        JPanel actionsCard = Theme.createCardPanel();
        actionsCard.setLayout(new BorderLayout(10, 10));

        JLabel lblActionsHeader = new JLabel("⚡ Thao Tác Nghiệp Vụ Nhanh");
        lblActionsHeader.setFont(Theme.FONT_HEADER);
        lblActionsHeader.setForeground(Theme.PRIMARY);
        actionsCard.add(lblActionsHeader, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new GridLayout(4, 1, 10, 10));
        actionButtons.setBackground(Theme.BG_CARD);

        JButton btnQuickCheckout = Theme.createPrimaryButton("➕ Cấp Phát / Mượn Sách Mới (Checkout)");
        btnQuickCheckout.addActionListener(e -> mainFrame.openCheckoutDialog(null, null));

        JButton btnQuickReturn = Theme.createSuccessButton("📥 Tiếp Nhận Trả Sách (Return Book)");
        btnQuickReturn.addActionListener(e -> mainFrame.openReturnDialog(null, null));

        JButton btnQuickCheckOverdue = Theme.createButton("🔔 Quét & Gửi Thông Báo Quá Hạn Tự Động", Theme.WARNING, Color.WHITE);
        btnQuickCheckOverdue.addActionListener(e -> {
            int count = library.checkOverdueBooksAndNotify();
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã quét và phát hiện/gửi thông báo cho " + count + " lượt mượn quá hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnAddBook = Theme.createButton("📚 Thêm Sách Mới Vào Thư Viện", Theme.PRIMARY, Color.WHITE);
        btnAddBook.addActionListener(e -> mainFrame.openAddBookDialog());

        actionButtons.add(btnQuickCheckout);
        actionButtons.add(btnQuickReturn);
        actionButtons.add(btnQuickCheckOverdue);
        actionButtons.add(btnAddBook);
        actionsCard.add(actionButtons, BorderLayout.CENTER);

        // Recent Activity Feed Panel
        JPanel activityCard = Theme.createCardPanel();
        activityCard.setLayout(new BorderLayout(10, 10));

        JLabel lblActivityHeader = new JLabel("🔔 Cảnh Báo & Hoạt Động Gần Đây");
        lblActivityHeader.setFont(Theme.FONT_HEADER);
        lblActivityHeader.setForeground(Theme.PRIMARY);
        activityCard.add(lblActivityHeader, BorderLayout.NORTH);

        activityListModel = new DefaultListModel<>();
        JList<String> activityList = new JList<>(activityListModel);
        activityList.setFont(Theme.FONT_BODY);
        activityList.setSelectionBackground(new Color(241, 245, 249));
        activityList.setSelectionForeground(Theme.TEXT_MAIN);
        activityCard.add(new JScrollPane(activityList), BorderLayout.CENTER);

        lowerPanel.add(actionsCard);
        lowerPanel.add(activityCard);

        centerPanel.add(lowerPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_SUBHEADER);
        lblTitle.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(Theme.FONT_KPI_NUM);
        valueLabel.setForeground(accentColor);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    public void refreshData() {
        int totalItems = library.getCatalog().getTotalBookItems();
        long activeLendings = library.getLendings().stream().filter(l -> !l.isReturned()).count();
        long overdueCount = library.getLendings().stream().filter(l -> !l.isReturned() && l.isOverdue()).count();
        long activeMembers = library.getAllMembers().stream().filter(m -> m.isActive()).count();
        long pendingRes = library.getReservations().stream().filter(r -> r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING).count();

        double unpaidFinesSum = library.getFines().stream()
                .filter(f -> !f.isPaid())
                .mapToDouble(Fine::getAmount)
                .sum();

        lblTotalItems.setText(String.valueOf(totalItems));
        lblActiveLendings.setText(String.valueOf(activeLendings));
        lblOverdueCount.setText(String.valueOf(overdueCount));
        lblActiveMembers.setText(String.valueOf(activeMembers));
        lblPendingReservations.setText(String.valueOf(pendingRes));
        lblUnpaidFines.setText(String.format("%,.0fđ", unpaidFinesSum));

        // Update activity feed
        activityListModel.clear();
        if (overdueCount > 0) {
            activityListModel.addElement("🔴 CẢNH BÁO: Có " + overdueCount + " sách đang mượn đã quá hạn cần thu hồi!");
        }
        for (BookLending l : library.getLendings()) {
            if (!l.isReturned() && l.isOverdue()) {
                activityListModel.addElement("⚠️ [Quá hạn " + l.getOverdueDays() + " ngày] Sách " + l.getItemBarcode() + " do TV " + l.getMemberId() + " mượn (Hạn: " + DateUtils.formatDate(l.getDueDate()) + ")");
            }
        }
        if (pendingRes > 0) {
            activityListModel.addElement("📌 Có " + pendingRes + " yêu cầu đặt trước sách đang chờ phục vụ.");
        }
        if (unpaidFinesSum > 0) {
            activityListModel.addElement("💰 Tổng tiền phạt quá hạn chưa thanh toán: " + String.format("%,.0f VNĐ", unpaidFinesSum));
        }
        activityListModel.addElement("✅ Hệ thống hoạt động bình thường lúc " + DateUtils.formatDate(LocalDate.now()));
    }
}
