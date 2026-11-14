package com.library.collections;

import com.library.enums.AccountStatus;
import com.library.models.Address;
import com.library.models.LibraryCard;
import com.library.models.Person;
import com.library.users.Librarian;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachThuThu: quan ly mang Librarian[].
 * File text: data/thuthu.txt
 */
public class DanhSachThuThu extends DanhSachCoBan<Librarian> {

    public DanhSachThuThu() {
        super(new Librarian[SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(Librarian l) {
        return l.getId();
    }

    @Override
    protected boolean khopTuKhoa(Librarian l, String q) {
        return l.getId().toLowerCase().contains(q)
                || l.getName().toLowerCase().contains(q)
                || l.getDepartment().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-8s | %-22s | %-26s | %-11s | %-22s | %s",
                "MA TT", "HO TEN", "EMAIL", "SDT", "PHONG BAN", "NGAY VAO LAM");
    }

    @Override
    protected String dongBang(Librarian l) {
        return String.format("%-8s | %-22s | %-26s | %-11s | %-22s | %s",
                l.getId(), rutGon(l.getName(), 22), rutGon(l.getEmail(), 26), l.getPhone(),
                rutGon(l.getDepartment(), 22), DateUtils.formatDate(l.getHireDate()));
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_TT|MAT_KHAU|HO_TEN|EMAIL|SDT|DIA_CHI(duong;tp;tinh;zip;quoc gia)|SO_THE|MA_VACH_THE"
                + "|NGAY_CAP_THE|THE_HOAT_DONG|TRANG_THAI|PHONG_BAN|NGAY_VAO_LAM";
    }

    @Override
    protected String chuyenThanhDongText(Librarian l) {
        LibraryCard c = l.getCard();
        return text(l.getId()) + "|" + text(l.getPassword()) + "|" + text(l.getName()) + "|"
                + text(l.getEmail()) + "|" + text(l.getPhone()) + "|" + text(l.getAddress().toDongText()) + "|"
                + (c != null ? text(c.getCardNumber()) + "|" + text(c.getBarcode()) + "|"
                        + DateUtils.ngayRaText(c.getIssuedAt()) + "|" + c.isActive() : "|||false") + "|"
                + l.getStatus().name() + "|" + text(l.getDepartment()) + "|" + DateUtils.ngayRaText(l.getHireDate());
    }

    @Override
    protected Librarian taoTuDongText(String[] t) {
        Person p = new Person(t[2], Address.tuDongText(t[5]), t[3], t[4]);
        LibraryCard card = t[6].isEmpty() ? null
                : new LibraryCard(t[6], t[7], DateUtils.parseDate(t[8]), Boolean.parseBoolean(t[9]));
        return new Librarian(t[0], t[1], p, card, AccountStatus.valueOf(t[10]), t[11], DateUtils.parseDate(t[12]));
    }

    /** Dang nhap thu thu: dung ma va mat khau -> tra ve thu thu, sai -> null. */
    public Librarian dangNhap(String ma, String matKhau) {
        Librarian l = timKiemTheoMa(ma);
        if (l != null && l.isActive() && l.checkPassword(matKhau)) {
            return l;
        }
        return null;
    }
}
