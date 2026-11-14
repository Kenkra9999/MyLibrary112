package com.library.collections;

import com.library.enums.ReservationStatus;
import com.library.models.BookReservation;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachDatCho: quan ly mang BookReservation[] (cac phieu dat giu sach).
 * File text: data/datcho.txt
 */
public class DanhSachDatCho extends DanhSachCoBan<BookReservation> {

    public DanhSachDatCho() {
        super(new BookReservation[SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(BookReservation r) {
        return r.getReservationId();
    }

    @Override
    protected boolean khopTuKhoa(BookReservation r, String q) {
        return r.getReservationId().toLowerCase().contains(q)
                || r.getItemBarcode().toLowerCase().contains(q)
                || r.getMemberId().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-9s | %-10s | %-8s | %-19s | %s", "MA DAT", "MA VACH", "MA TV", "NGAY TAO", "TRANG THAI");
    }

    @Override
    protected String dongBang(BookReservation r) {
        return String.format("%-9s | %-10s | %-8s | %-19s | %s", r.getReservationId(), r.getItemBarcode(),
                r.getMemberId(), DateUtils.formatDateTime(r.getCreationDate()), r.getStatus().getDisplayName());
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_DAT|MA_VACH|MA_TV|NGAY_TAO(dd/MM/yyyy HH:mm:ss)|TRANG_THAI";
    }

    @Override
    protected String chuyenThanhDongText(BookReservation r) {
        return text(r.getReservationId()) + "|" + text(r.getItemBarcode()) + "|" + text(r.getMemberId()) + "|"
                + DateUtils.ngayGioRaText(r.getCreationDate()) + "|" + r.getStatus().name();
    }

    @Override
    protected BookReservation taoTuDongText(String[] t) {
        return new BookReservation(t[0], t[1], t[2], DateUtils.parseDateTime(t[3]), ReservationStatus.valueOf(t[4]));
    }

    /** Phieu dat giu CON HIEU LUC (dang cho hoac san sang nhan) cua 1 cuon sach. */
    public BookReservation timConHieuLuc(String maVach) {
        for (int i = 0; i < soLuong; i++) {
            BookReservation r = danhSach[i];
            if (r.getItemBarcode().equalsIgnoreCase(maVach)
                    && (r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING)) {
                return r;
            }
        }
        return null;
    }

    /** Cac phieu dat giu CON HIEU LUC cua 1 thanh vien. */
    public int demConHieuLucCuaThanhVien(String maThanhVien) {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            BookReservation r = danhSach[i];
            if (r.getMemberId().equalsIgnoreCase(maThanhVien)
                    && (r.getStatus() == ReservationStatus.WAITING || r.getStatus() == ReservationStatus.PENDING)) {
                dem++;
            }
        }
        return dem;
    }
}
