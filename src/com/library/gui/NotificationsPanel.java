package com.library.gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.library.notifications.EmailNotification;
import com.library.notifications.Notification;
import com.library.notifications.PostalNotification;
import com.library.services.Library;
import com.library.users.Member;
import com.library.utils.DateUtils;

/**
 * Panel Trung Tâm Thông Báo & Nhật Ký Gửi Tin (Notifications Outbox - R12).
 */
public class NotificationsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Library library;

    private JTable tableNotifications;
    private DefaultTableModel tableModel;

    public NotificationsPanel(MainFrame mainFrame, Library library) {
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

        JLabel lblTitle = new JLabel("🔔 Trung Tâm Thông Báo & Hộp Thư Tự Động (Notifications Outbox)");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.PRIMARY);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JLabel lblDesc = new JLabel("Ghi nhận toàn bộ thông báo Email và Bưu điện tự động gửi đến thành viên (nhắc nhở quá hạn, sách có sẵn, cấp thẻ).");
        lblDesc.setFont(Theme.FONT_BODY);
        lblDesc.setForeground(Theme.TEXT_MUTED);
        topPanel.add(lblDesc, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: Table
        JPanel tableCard = Theme.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] columns = {"Mã Thông Báo", "Hình Thức", "Người Nhận (Mã TV)", "Họ Tên Thành Viên", "Nội Dung Thông Báo", "Thời Gian Gửi", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableNotifications = new JTable(tableModel);
        Theme.styleTable(tableNotifications);
        tableNotifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableNotifications.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableNotifications.getColumnModel().getColumn(1).setPreferredWidth(90);
        tableNotifications.getColumnModel().getColumn(2).setPreferredWidth(110);
        tableNotifications.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableNotifications.getColumnModel().getColumn(4).setPreferredWidth(350);
        tableNotifications.getColumnModel().getColumn(5).setPreferredWidth(130);
        tableNotifications.getColumnModel().getColumn(6).setPreferredWidth(90);

        tableCard.add(new JScrollPane(tableNotifications), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // SOUTH: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(Theme.BG_MAIN);

        JButton btnCheckOverdue = Theme.createButton("🔔 Quét & Báo Quá Hạn Tự Động", Theme.WARNING, Color.WHITE);
        btnCheckOverdue.addActionListener(e -> {
            int count = library.checkOverdueBooksAndNotify();
            mainFrame.saveLibraryData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã quét và phát hiện/gửi thông báo cho " + count + " lượt mượn quá hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnSendTest = Theme.createPrimaryButton("✉️ Gửi Thông Báo Tùy Chỉnh");
        btnSendTest.addActionListener(e -> onSendCustomNotification());

        bottomPanel.add(btnCheckOverdue);
        bottomPanel.add(btnSendTest);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        List<Notification> list = library.getNotifications();
        tableModel.setRowCount(0);
        for (Notification n : list) {
            Member member = library.getMemberById(n.getRecipientMemberId());
            String memName = member != null ? member.getName() : "N/A";

            tableModel.addRow(new Object[]{
                    n.getNotificationId(),
                    n.getNotificationType(),
                    n.getRecipientMemberId(),
                    memName,
                    n.getContent(),
                    DateUtils.formatDateTime(n.getCreatedOn()),
                    n.isSent() ? "Đã gửi" : "Chờ gửi"
            });
        }
    }

    private void onSendCustomNotification() {
        List<Member> members = library.getAllMembers();
        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có thành viên nào trong thư viện!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] memberNames = members.stream().map(m -> m.getId() + " - " + m.getName()).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Chọn thành viên nhận thông báo:", "Gửi Thông Báo", JOptionPane.PLAIN_MESSAGE, null, memberNames, memberNames[0]);
        if (selected == null) return;

        String memberId = selected.split(" - ")[0].trim();
        Member member = library.getMemberById(memberId);

        String[] types = {"Email", "Bưu điện"};
        String type = (String) JOptionPane.showInputDialog(this, "Chọn hình thức gửi:", "Hình thức", JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (type == null) return;

        String message = JOptionPane.showInputDialog(this, "Nhập nội dung thông báo:", "Nội dung", JOptionPane.QUESTION_MESSAGE);
        if (message == null || message.trim().isEmpty()) return;

        Notification notif;
        if ("Email".equalsIgnoreCase(type)) {
            notif = new EmailNotification(message.trim(), memberId, member.getEmail());
        } else {
            notif = new PostalNotification(message.trim(), memberId, member.getAddress());
        }

        library.sendNotification(notif);
        mainFrame.saveLibraryData();
        refreshData();
        JOptionPane.showMessageDialog(this, "Đã gửi thông báo thành công tới " + member.getName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
