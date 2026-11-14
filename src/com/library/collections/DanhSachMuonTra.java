package com.library.collections;

import java.util.Arrays;
import com.library.models.BookLending;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachMuonTra: quan ly mang BookLending[] (cac luot muon sach).
 * File text: data/muontra.txt
 */
public class DanhSachMuonTra extends DanhSachCoBan<BookLending> {

    public DanhSachMuonTra() {
        super(new BookLending[SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(BookLending l) {
        return l.getLendingId();
    }

    @Override
    protected boolean khopTuKhoa(BookLending l, String q) {
        return l.getLendingId().toLowerCase().contains(q)
                || l.getItemBarcode().toLowerCase().contains(q)
                || l.getMemberId().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-9s | %-10s | %-8s | %-10s | %-10s | %s",
                "MA MUON", "MA VACH", "MA TV", "NGAY MUON", "HAN TRA", "TRANG THAI");
    }

    @Override
    protected String dongBang(BookLending l) {
        String trangThai;
        if (l.isReturned()) {
            trangThai = "Da tra " + DateUtils.formatDate(l.getReturnDate())
                    + (l.isOverdue() ? " (tre " + l.getOverdueDays() + " ngay)" : "");
        } else {
            trangThai = l.isOverdue() ? "QUA HAN " + l.getOverdueDays() + " ngay" : "Dang muon";
        }
        return String.format("%-9s | %-10s | %-8s | %-10s | %-10s | %s",
                l.getLendingId(), l.getItemBarcode(), l.getMemberId(),
                DateUtils.formatDate(l.getCreationDate()), DateUtils.formatDate(l.getDueDate()), trangThai);
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_MUON|MA_VACH|MA_TV|NGAY_MUON|HAN_TRA|NGAY_TRA|DA_TRA";
    }

    @Override
    protected String chuyenThanhDongText(BookLending l) {
        return text(l.getLendingId()) + "|" + text(l.getItemBarcode()) + "|" + text(l.getMemberId()) + "|"
                + DateUtils.ngayRaText(l.getCreationDate()) + "|" + DateUtils.ngayRaText(l.getDueDate()) + "|"
                + DateUtils.ngayRaText(l.getReturnDate()) + "|" + l.isReturned();
    }

    @Override
    protected BookLending taoTuDongText(String[] t) {
        return new BookLending(t[0], t[1], t[2], DateUtils.parseDate(t[3]), DateUtils.parseDate(t[4]),
                DateUtils.parseDate(t[5]), Boolean.parseBoolean(t[6]));
    }

    /** Tim luot muon CHUA TRA cua 1 cuon sach boi 1 thanh vien. */
    public BookLending timDangMuon(String maVach, String maThanhVien) {
        for (int i = 0; i < soLuong; i++) {
            BookLending l = danhSach[i];
            if (!l.isReturned() && l.getItemBarcode().equalsIgnoreCase(maVach)
                    && l.getMemberId().equalsIgnoreCase(maThanhVien)) {
                return l;
            }
        }
        return null;
    }

    /** Lich su muon tra cua 1 thanh vien. */
    public BookLending[] timTheoThanhVien(String maThanhVien) {
        BookLending[] kq = new BookLending[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMemberId().equalsIgnoreCase(maThanhVien)) kq[dem++] = danhSach[i];
        }
        return Arrays.copyOf(kq, dem);
    }

    /** Cac luot muon dang qua han (chua tra va da qua han tra). */
    public BookLending[] timQuaHan() {
        BookLending[] kq = new BookLending[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isReturned() && danhSach[i].isOverdue()) kq[dem++] = danhSach[i];
        }
        return Arrays.copyOf(kq, dem);
    }

    public int demDangMuon() {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isReturned()) dem++;
        }
        return dem;
    }
}
