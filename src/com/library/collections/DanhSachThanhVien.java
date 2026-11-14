package com.library.collections;

import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.users.Member;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachThanhVien: quan ly mang Member[].
 * File text: data/thanhvien.txt (luu day du: mat khau, dia chi, the, trang thai, ngay gia nhap, so sach dang muon).
 */
public class DanhSachThanhVien extends DanhSachCoBan<Member> {

    public DanhSachThanhVien() {
        super(new Member[SUC_CHUA_MAC_DINH]);
    }

    public DanhSachThanhVien(int sucChua) {
        super(new Member[sucChua > 0 ? sucChua : SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(Member m) {
        return m.getId();
    }

    @Override
    protected boolean khopTuKhoa(Member m, String q) {
        return m.getId().toLowerCase().contains(q)
                || m.getName().toLowerCase().contains(q)
                || m.getEmail().toLowerCase().contains(q)
                || m.getPhone().toLowerCase().contains(q)
                || (m.getCard() != null && m.getCard().getCardNumber().toLowerCase().contains(q));
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-8s | %-20s | %-26s | %-11s | %-8s | %-22s | %-10s | %s",
                "MA TV", "HO TEN", "EMAIL", "SDT", "SO THE", "TRANG THAI", "GIA NHAP", "DANG MUON");
    }

    @Override
    protected String dongBang(Member m) {
        return String.format("%-8s | %-20s | %-26s | %-11s | %-8s | %-22s | %-10s | %d",
                m.getId(), rutGon(m.getName(), 20), rutGon(m.getEmail(), 26), m.getPhone(),
                m.getCard() != null ? m.getCard().getCardNumber() : "-", m.getStatus().getDisplayName(),
                DateUtils.formatDate(m.getDateOfMembership()), m.getTotalBooksCheckedOut());
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_TV|MAT_KHAU|HO_TEN|EMAIL|SDT|DIA_CHI(duong;tp;tinh;zip;quoc gia)|SO_THE|MA_VACH_THE"
                + "|NGAY_CAP_THE|THE_HOAT_DONG|TRANG_THAI|NGAY_GIA_NHAP|SO_SACH_DANG_MUON";
    }

    @Override
    protected String chuyenThanhDongText(Member m) {
        LibraryCard c = m.getCard();
        return text(m.getId()) + "|" + text(m.getPassword()) + "|" + text(m.getName()) + "|"
                + text(m.getEmail()) + "|" + text(m.getPhone()) + "|" + text(m.getAddress().toDongText()) + "|"
                + (c != null ? text(c.getCardNumber()) + "|" + text(c.getBarcode()) + "|"
                        + DateUtils.ngayRaText(c.getIssuedAt()) + "|" + c.isActive() : "|||false") + "|"
                + m.getStatus().name() + "|" + DateUtils.ngayRaText(m.getDateOfMembership()) + "|"
                + m.getTotalBooksCheckedOut();
    }

    @Override
    protected Member taoTuDongText(String[] t) {
        Person p = new Person(t[2], Address.tuDongText(t[5]), t[3], t[4]);
        LibraryCard card = t[6].isEmpty() ? null
                : new LibraryCard(t[6], t[7], DateUtils.parseDate(t[8]), Boolean.parseBoolean(t[9]));
        Member m = new Member(t[0], t[1], p, card, AccountStatus.valueOf(t[10]), DateUtils.parseDate(t[11]));
        m.setTotalBooksCheckedOut(Integer.parseInt(t[12]));
        return m;
    }

    /** Dem so thanh vien theo trang thai tai khoan. */
    public int demTheoTrangThai(AccountStatus trangThai) {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getStatus() == trangThai) dem++;
        }
        return dem;
    }
}
