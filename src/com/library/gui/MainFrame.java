package com.library.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.library.gui.dialogs.*;
import com.library.models.BookItem;
import com.library.users.Member;
import com.library.services.DataStorageService;
import com.library.services.Library;
import com.library.services.SampleDataLoader;

/**
 * Cửa sổ giao diện chính của Hệ thống Quản lý Thư viện (MainFrame).
 * Quản lý thanh điều hướng Sidebar, điều phối chuyển đổi màn hình và lưu trữ dữ liệu.
 */
public class MainFrame extends JFrame {
    private Library library;

    private JPanel mainContentCardPanel;
    private CardLayout cardLayout;

    private DashboardPanel dashboardPanel;
    private BooksPanel booksPanel;
    private CirculationPanel circulationPanel;
    private ReservationPanel reservationPanel;
    private MembersPanel membersPanel;
    private FinesPanel finesPanel;
    private NotificationsPanel notificationsPanel;

    private JButton[] navButtons;
    private String[] cardNames = {"DASHBOARD", "BOOKS", "CIRCULATION", "RESERVATIONS", "MEMBERS", "FINES", "NOTIFICATIONS"};

    public MainFrame() {
        super("Hệ Thống Quản Lý Thư Viện Hướng Đối Tượng (OOP Library Management System)");

        initData();
        initUI();
        refreshAllPanels();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveLibraryData();
                System.exit(0);
            }
        });
    }

    private void initData() {
        Library loaded = DataStorageService.loadData();
        if (loaded != null) {
            this.library = loaded;
        } else {
            this.library = SampleDataLoader.createSampleLibrary();
            DataStorageService.saveData(this.library);
        }
    }

    public synchronized void saveLibraryData() {
        DataStorageService.saveData(this.library);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_MAIN);

        // 1. TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JLabel lblLogo = new JLabel("📖 " + library.getName());
        lblLogo.setFont(Theme.FONT_TITLE);
        lblLogo.setForeground(Theme.PRIMARY);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userInfoPanel.setBackground(Theme.BG_CARD);

        JLabel lblUser = new JLabel("👤 Thủ thư: Trần Thị Minh Trang [LIB-101]");
        lblUser.setFont(Theme.FONT_BODY_BOLD);
        lblUser.setForeground(Theme.PRIMARY);

        JButton btnResetData = Theme.createButton("🔄 Khôi Phục Dữ Liệu Mẫu", Theme.BG_MAIN, Theme.TEXT_MUTED);
        btnResetData.setFont(Theme.FONT_SMALL);
        btnResetData.addActionListener(e -> onResetData());

        userInfoPanel.add(lblUser);
        userInfoPanel.add(btnResetData);

        topBar.add(lblLogo, BorderLayout.WEST);
        topBar.add(userInfoPanel, BorderLayout.EAST);
        rootPanel.add(topBar, BorderLayout.NORTH);

        // 2. SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblNavHeader = new JLabel("DANH MỤC CHỨC NĂNG");
        lblNavHeader.setFont(Theme.FONT_SMALL);
        lblNavHeader.setForeground(new Color(148, 163, 184));
        lblNavHeader.setBorder(new EmptyBorder(0, 12, 15, 12));
        sidebar.add(lblNavHeader);

        String[] navLabels = {
                "📊 Bảng Điều Khiển",
                "📚 Danh Mục Sách",
                "🔄 Mượn / Trả Sách",
                "📑 Hàng Đợi Đặt Giữ",
                "👥 Quản Lý Thành Viên",
                "💰 Quản Lý Tiền Phạt",
                "🔔 Hộp Thư Thông Báo"
        };

        navButtons = new JButton[navLabels.length];
        for (int i = 0; i < navLabels.length; i++) {
            final int index = i;
            JButton btn = new JButton(navLabels[i]);
            btn.setFont(Theme.FONT_BODY_BOLD);
            btn.setForeground(new Color(203, 213, 225));
            btn.setBackground(Theme.BG_SIDEBAR);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(220, 42));
            btn.setPreferredSize(new Dimension(220, 42));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(8, 14, 8, 14));

            btn.addActionListener(e -> switchCard(index));
            navButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel lblVersion = new JLabel("LMS OOP v2.0 - Java 21");
        lblVersion.setFont(Theme.FONT_SMALL);
        lblVersion.setForeground(new Color(100, 116, 139));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblVersion);

        rootPanel.add(sidebar, BorderLayout.WEST);

        // 3. CENTER: CardLayout content panels
        cardLayout = new CardLayout();
        mainContentCardPanel = new JPanel(cardLayout);
        mainContentCardPanel.setBackground(Theme.BG_MAIN);

        dashboardPanel = new DashboardPanel(this, library);
        booksPanel = new BooksPanel(this, library);
        circulationPanel = new CirculationPanel(this, library);
        reservationPanel = new ReservationPanel(this, library);
        membersPanel = new MembersPanel(this, library);
        finesPanel = new FinesPanel(this, library);
        notificationsPanel = new NotificationsPanel(this, library);

        mainContentCardPanel.add(dashboardPanel, cardNames[0]);
        mainContentCardPanel.add(booksPanel, cardNames[1]);
        mainContentCardPanel.add(circulationPanel, cardNames[2]);
        mainContentCardPanel.add(reservationPanel, cardNames[3]);
        mainContentCardPanel.add(membersPanel, cardNames[4]);
        mainContentCardPanel.add(finesPanel, cardNames[5]);
        mainContentCardPanel.add(notificationsPanel, cardNames[6]);

        rootPanel.add(mainContentCardPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
        switchCard(0); // Show Dashboard first
    }

    public void switchCard(int index) {
        if (index >= 0 && index < cardNames.length) {
            cardLayout.show(mainContentCardPanel, cardNames[index]);
            for (int i = 0; i < navButtons.length; i++) {
                if (i == index) {
                    navButtons[i].setBackground(Theme.ACCENT);
                    navButtons[i].setForeground(Color.WHITE);
                } else {
                    navButtons[i].setBackground(Theme.BG_SIDEBAR);
                    navButtons[i].setForeground(new Color(203, 213, 225));
                }
            }
            refreshAllPanels();
        }
    }

    public void refreshAllPanels() {
        if (dashboardPanel != null) dashboardPanel.refreshData();
        if (booksPanel != null) booksPanel.refreshData();
        if (circulationPanel != null) circulationPanel.refreshData();
        if (reservationPanel != null) reservationPanel.refreshData();
        if (membersPanel != null) membersPanel.refreshData();
        if (finesPanel != null) finesPanel.refreshData();
        if (notificationsPanel != null) notificationsPanel.refreshData();
    }

    private void onResetData() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thiết lập lại toàn bộ dữ liệu mẫu ban đầu?", "Khôi phục dữ liệu", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.library = SampleDataLoader.createSampleLibrary();
            saveLibraryData();
            // Re-initialize panels
            getContentPane().removeAll();
            initUI();
            revalidate();
            repaint();
            JOptionPane.showMessageDialog(this, "Đã nạp lại dữ liệu mẫu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ==========================================
    // DIALOG OPENERS
    // ==========================================
    public void openAddBookDialog() {
        AddEditBookDialog dialog = new AddEditBookDialog(this, library, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            saveLibraryData();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Thêm sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openEditBookDialog(BookItem item) {
        AddEditBookDialog dialog = new AddEditBookDialog(this, library, item);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            saveLibraryData();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Cập nhật sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openAddMemberDialog() {
        AddEditMemberDialog dialog = new AddEditMemberDialog(this, library, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            saveLibraryData();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Đăng ký thành viên và cấp thẻ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openEditMemberDialog(Member member) {
        AddEditMemberDialog dialog = new AddEditMemberDialog(this, library, member);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            saveLibraryData();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Cập nhật thành viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openCheckoutDialog(String barcode, String memberId) {
        CheckoutDialog dialog = new CheckoutDialog(this, library, barcode, memberId);
        dialog.setVisible(true);
        if (dialog.isCompleted()) {
            saveLibraryData();
            refreshAllPanels();
        }
    }

    public void openReturnDialog(String barcode, String memberId) {
        ReturnDialog dialog = new ReturnDialog(this, library, barcode, memberId);
        dialog.setVisible(true);
        if (dialog.isCompleted()) {
            saveLibraryData();
            refreshAllPanels();
        }
    }

    public void openReserveDialog(String barcode, String memberId) {
        ReserveDialog dialog = new ReserveDialog(this, library, barcode, memberId);
        dialog.setVisible(true);
        if (dialog.isCompleted()) {
            saveLibraryData();
            refreshAllPanels();
        }
    }

    public void openPayFineDialog(String fineId) {
        PayFineDialog dialog = new PayFineDialog(this, library, fineId);
        dialog.setVisible(true);
        if (dialog.isCompleted()) {
            saveLibraryData();
            refreshAllPanels();
        }
    }
}
