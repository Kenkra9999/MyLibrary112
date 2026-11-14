package com.library.collections;

import com.library.models.Address;
import com.library.notifications.EmailNotification;
import com.library.notifications.Notification;
import com.library.notifications.PostalNotification;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachThongBao: quan ly mang Notification[].
 *
 * DA HINH: mang co kieu lop cha truu tuong Notification nhung moi phan tu thuc te la
 * EmailNotification hoac PostalNotification. Khi goi n.getNotificationType() / n.getNoiNhanText()
 * Java tu chon dung ham cua lop con tuong ung (lien ket dong - dynamic binding).
 * File text: data/thongbao.txt
 */
public class DanhSachThongBao extends DanhSachCoBan<Notification> {
    private static final String LOAI_EMAIL = "Email";

    public DanhSachThongBao() {
        super(new Notification[SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(Notification n) {
        return n.getNotificationId();
    }

    @Override
    protected boolean khopTuKhoa(Notification n, String q) {
        return n.getNotificationId().toLowerCase().contains(q)
                || n.getRecipientMemberId().toLowerCase().contains(q)
                || n.getContent().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-10s | %-8s | %-8s | %-19s | %s", "MA TB", "LOAI", "MA TV", "THOI GIAN", "NOI DUNG");
    }

    @Override
    protected String dongBang(Notification n) {
        return String.format("%-10s | %-8s | %-8s | %-19s | %s", n.getNotificationId(), n.getNotificationType(),
                n.getRecipientMemberId(), DateUtils.formatDateTime(n.getCreatedOn()), rutGon(n.getContent(), 70));
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_TB|LOAI(Email/Buu dien)|MA_TV|THOI_GIAN|DA_GUI|NOI_NHAN(email hoac dia chi)|NOI_DUNG";
    }

    @Override
    protected String chuyenThanhDongText(Notification n) {
        // getNotificationType() va getNoiNhanText() la ham truu tuong -> goi dung ham cua lop con
        return text(n.getNotificationId()) + "|" + n.getNotificationType() + "|" + text(n.getRecipientMemberId()) + "|"
                + DateUtils.ngayGioRaText(n.getCreatedOn()) + "|" + n.isSent() + "|"
                + text(n.getNoiNhanText()) + "|" + text(n.getContent());
    }

    @Override
    protected Notification taoTuDongText(String[] t) {
        // Tuy cot LOAI ma tao dung doi tuong lop con, roi luu vao mang kieu lop cha Notification
        if (LOAI_EMAIL.equals(t[1])) {
            return new EmailNotification(t[0], DateUtils.parseDateTime(t[3]), t[6], t[2], Boolean.parseBoolean(t[4]), t[5]);
        }
        return new PostalNotification(t[0], DateUtils.parseDateTime(t[3]), t[6], t[2], Boolean.parseBoolean(t[4]),
                Address.tuDongText(t[5]));
    }

    /** Dem so thong bao theo loai (dua tren ham da hinh getNotificationType()). */
    public int demTheoLoai(String loai) {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getNotificationType().equalsIgnoreCase(loai)) dem++;
        }
        return dem;
    }
}
