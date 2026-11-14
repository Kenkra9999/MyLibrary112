package com.library.collections;

import java.util.Arrays;
import com.library.models.Fine;
import com.library.utils.DateUtils;

/**
 * LOP MANG DOI TUONG DanhSachPhat: quan ly mang Fine[] (cac bien lai tien phat).
 * File text: data/phat.txt
 */
public class DanhSachPhat extends DanhSachCoBan<Fine> {

    public DanhSachPhat() {
        super(new Fine[SUC_CHUA_MAC_DINH]);
    }

    @Override
    protected String layMa(Fine f) {
        return f.getFineId();
    }

    @Override
    protected boolean khopTuKhoa(Fine f, String q) {
        return f.getFineId().toLowerCase().contains(q)
                || f.getMemberId().toLowerCase().contains(q)
                || f.getItemBarcode().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-9s | %-8s | %-10s | %12s | %-10s | %s", "MA PHAT", "MA TV", "MA VACH", "SO TIEN", "NGAY TAO", "TRANG THAI");
    }

    @Override
    protected String dongBang(Fine f) {
        return String.format("%-9s | %-8s | %-10s | %,12.0f | %-10s | %s", f.getFineId(), f.getMemberId(),
                f.getItemBarcode(), f.getAmount(), DateUtils.formatDate(f.getCreationDate()),
                f.isPaid() ? "Da dong " + DateUtils.formatDate(f.getPaidDate()) : "CHUA DONG");
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_PHAT|SO_TIEN|NGAY_TAO|MA_VACH|MA_TV|DA_DONG|NGAY_DONG";
    }

    @Override
    protected String chuyenThanhDongText(Fine f) {
        return text(f.getFineId()) + "|" + soRaText(f.getAmount()) + "|" + DateUtils.ngayRaText(f.getCreationDate()) + "|"
                + text(f.getItemBarcode()) + "|" + text(f.getMemberId()) + "|" + f.isPaid() + "|"
                + DateUtils.ngayRaText(f.getPaidDate());
    }

    @Override
    protected Fine taoTuDongText(String[] t) {
        return new Fine(t[0], Double.parseDouble(t[1]), DateUtils.parseDate(t[2]), t[3], t[4],
                Boolean.parseBoolean(t[5]), DateUtils.parseDate(t[6]));
    }

    /** Thanh vien con khoan phat chua dong hay khong. */
    public boolean coPhatChuaDong(String maThanhVien) {
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isPaid() && danhSach[i].getMemberId().equalsIgnoreCase(maThanhVien)) return true;
        }
        return false;
    }

    /** Cac khoan phat chua dong cua 1 thanh vien. */
    public Fine[] timChuaDongCuaThanhVien(String maThanhVien) {
        Fine[] kq = new Fine[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isPaid() && danhSach[i].getMemberId().equalsIgnoreCase(maThanhVien)) kq[dem++] = danhSach[i];
        }
        return Arrays.copyOf(kq, dem);
    }

    public double tongTienPhat() {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) tong += danhSach[i].getAmount();
        return tong;
    }

    public double tongTienChuaThu() {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].isPaid()) tong += danhSach[i].getAmount();
        }
        return tong;
    }
}
