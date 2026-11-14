package com.library.collections;

import java.time.LocalDate;
import java.util.Scanner;
import com.library.enums.BookFormat;
import com.library.enums.BookStatus;
import com.library.models.Book;
import com.library.models.BookItem;
import com.library.models.Rack;
import com.library.search.Search;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * LOP MANG DOI TUONG DanhSachSach: quan ly mang BookItem[] (cac ban sao sach).
 * - Ke thua DanhSachCoBan<BookItem>: them, sua, xoa, tim kiem, doc/ghi file da co san.
 * - Cai dat interface Search: tim theo tua de, tac gia, the loai, ngay xuat ban.
 * - Bo sung: nhap n sach, sap xep, thong ke.
 * File text: data/sach.txt
 */
public class DanhSachSach extends DanhSachCoBan<BookItem> implements Search {

    public DanhSachSach() {
        super(new BookItem[SUC_CHUA_MAC_DINH]);
    }

    public DanhSachSach(int sucChua) {
        super(new BookItem[sucChua > 0 ? sucChua : SUC_CHUA_MAC_DINH]);
    }

    // ===================== CAI DAT HAM TRUU TUONG =====================

    @Override
    protected String layMa(BookItem item) {
        return item.getBarcode();
    }

    @Override
    protected boolean khopTuKhoa(BookItem b, String q) {
        return b.getBarcode().toLowerCase().contains(q)
                || b.getIsbn().toLowerCase().contains(q)
                || b.getTitle().toLowerCase().contains(q)
                || b.getAuthorsAsString().toLowerCase().contains(q)
                || b.getSubject().toLowerCase().contains(q)
                || b.getPublisher().toLowerCase().contains(q);
    }

    @Override
    protected String tieuDeBang() {
        return String.format("%-10s | %-34s | %-22s | %-19s | %10s | %-18s | %s",
                "MA VACH", "TUA DE", "TAC GIA", "THE LOAI", "GIA (VND)", "TRANG THAI", "KE");
    }

    @Override
    protected String dongBang(BookItem b) {
        String trangThai = b.getStatus().getDisplayName() + (b.isReferenceOnly() ? "*" : "");
        return String.format("%-10s | %-34s | %-22s | %-19s | %,10.0f | %-18s | %s",
                b.getBarcode(), rutGon(b.getTitle(), 34), rutGon(b.getAuthorsAsString(), 22),
                rutGon(b.getSubject(), 19), b.getPrice(), rutGon(trangThai, 18), b.getPlacedAt());
    }

    @Override
    protected String tieuDeFile() {
        return "# MA_VACH|ISBN|TUA_DE|TAC_GIA(;)|THE_LOAI|NXB|NGON_NGU|SO_TRANG|GIA|DINH_DANG|TRANG_THAI"
                + "|CHI_DOC_TAI_CHO|NGAY_MUA|NGAY_XB|NGAY_MUON|HAN_TRA|SO_KE|VI_TRI_KE";
    }

    @Override
    protected String chuyenThanhDongText(BookItem b) {
        return text(b.getBarcode()) + "|" + text(b.getIsbn()) + "|" + text(b.getTitle()) + "|"
                + text(b.getAuthorsAsText()) + "|" + text(b.getSubject()) + "|" + text(b.getPublisher()) + "|"
                + text(b.getLanguage()) + "|" + b.getNumberOfPages() + "|" + soRaText(b.getPrice()) + "|"
                + b.getFormat().name() + "|" + b.getStatus().name() + "|" + b.isReferenceOnly() + "|"
                + DateUtils.ngayRaText(b.getDateOfPurchase()) + "|" + DateUtils.ngayRaText(b.getPublicationDate()) + "|"
                + DateUtils.ngayRaText(b.getBorrowedDate()) + "|" + DateUtils.ngayRaText(b.getDueDate()) + "|"
                + b.getPlacedAt().getNumber() + "|" + text(b.getPlacedAt().getLocationIdentifier());
    }

    @Override
    protected BookItem taoTuDongText(String[] t) {
        BookItem b = new BookItem(t[1], t[2], t[4], t[5], t[6], Integer.parseInt(t[7]),
                Book.taoMangTacGia(t[3]), t[0], Boolean.parseBoolean(t[11]), Double.parseDouble(t[8]),
                BookFormat.valueOf(t[9]), BookStatus.valueOf(t[10]), DateUtils.parseDate(t[12]),
                DateUtils.parseDate(t[13]), new Rack(Integer.parseInt(t[16]), t[17]));
        b.setBorrowedDate(DateUtils.parseDate(t[14]));
        b.setDueDate(DateUtils.parseDate(t[15]));
        return b;
    }

    // ===================== CAI DAT INTERFACE Search =====================

    @Override
    public BookItem[] searchByTitle(String title) {
        String q = title == null ? "" : title.trim().toLowerCase();
        BookItem[] kq = new BookItem[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getTitle().toLowerCase().contains(q)) kq[dem++] = danhSach[i];
        }
        return catMang(kq, dem);
    }

    @Override
    public BookItem[] searchByAuthor(String authorName) {
        String q = authorName == null ? "" : authorName.trim().toLowerCase();
        BookItem[] kq = new BookItem[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getAuthorsAsString().toLowerCase().contains(q)) kq[dem++] = danhSach[i];
        }
        return catMang(kq, dem);
    }

    @Override
    public BookItem[] searchBySubject(String subject) {
        String q = subject == null ? "" : subject.trim().toLowerCase();
        BookItem[] kq = new BookItem[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getSubject().toLowerCase().contains(q)) kq[dem++] = danhSach[i];
        }
        return catMang(kq, dem);
    }

    @Override
    public BookItem[] searchByPubDate(LocalDate pubDate) {
        BookItem[] kq = new BookItem[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (pubDate != null && pubDate.equals(danhSach[i].getPublicationDate())) kq[dem++] = danhSach[i];
        }
        return catMang(kq, dem);
    }

    private BookItem[] catMang(BookItem[] kq, int dem) {
        BookItem[] gon = new BookItem[dem];
        for (int i = 0; i < dem; i++) gon[i] = kq[i];
        return gon;
    }

    // ===================== NHAP n SACH =====================

    /** Sinh ma vach moi chua ton tai trong mang (BC-0001, BC-0002, ...). */
    public String taoMaVachMoi() {
        String ma;
        do {
            ma = IdGenerator.taoMaVachSach();
        } while (timKiemTheoMa(ma) != null);
        return ma;
    }

    /**
     * Nhap n cuon sach moi tu ban phim va them vao mang.
     * Ma vach do he thong tu sinh (IdGenerator) nen khong bao gio trung.
     * @return so sach them thanh cong
     */
    public int nhapDanhSach(int n, Scanner sc) {
        int dem = 0;
        for (int i = 0; i < n; i++) {
            System.out.println("--- Nhap cuon sach thu " + (i + 1) + "/" + n + " ---");
            BookItem b = new BookItem();
            b.nhap(sc);  // goi BookItem.nhap() -> ben trong goi super.nhap() cua Book
            b.setBarcode(taoMaVachMoi());
            if (them(b)) {
                dem++;
                System.out.println("  [+] Da them: " + b.getTitle() + " - Ma vach: " + b.getBarcode());
            }
        }
        return dem;
    }

    // ===================== SAP XEP (tren chinh mang) =====================

    /** Sap xep tang dan theo tua de (thuat toan chon - selection sort). */
    public void sapXepTheoTuaDe() {
        for (int i = 0; i < soLuong - 1; i++) {
            int min = i;
            for (int j = i + 1; j < soLuong; j++) {
                if (danhSach[j].getTitle().compareToIgnoreCase(danhSach[min].getTitle()) < 0) min = j;
            }
            hoanDoi(i, min);
        }
    }

    /** Sap xep giam dan theo gia bia. */
    public void sapXepTheoGiaGiamDan() {
        for (int i = 0; i < soLuong - 1; i++) {
            int max = i;
            for (int j = i + 1; j < soLuong; j++) {
                if (danhSach[j].getPrice() > danhSach[max].getPrice()) max = j;
            }
            hoanDoi(i, max);
        }
    }

    private void hoanDoi(int i, int j) {
        BookItem tam = danhSach[i];
        danhSach[i] = danhSach[j];
        danhSach[j] = tam;
    }

    // ===================== THONG KE =====================

    /** Dem so DAU SACH khac nhau (cac ban sao cung ISBN tinh la 1 dau sach). */
    public int demSoDauSach() {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            boolean daGap = false;
            for (int j = 0; j < i; j++) {
                if (danhSach[j].getIsbn().equalsIgnoreCase(danhSach[i].getIsbn())) {
                    daGap = true;
                    break;
                }
            }
            if (!daGap) dem++;
        }
        return dem;
    }

    public double tongGiaTri() {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) tong += danhSach[i].getPrice();
        return tong;
    }

    public int demTheoTrangThai(BookStatus trangThai) {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getStatus() == trangThai) dem++;
        }
        return dem;
    }

    /** In thong ke so ban sao theo the loai (dung 2 mang song song, khong dung HashMap). */
    public void inThongKeTheoTheLoai() {
        String[] theLoai = new String[soLuong];
        int[] soLuongTheoLoai = new int[soLuong];
        int soLoai = 0;
        for (int i = 0; i < soLuong; i++) {
            String tl = danhSach[i].getSubject();
            int viTri = -1;
            for (int j = 0; j < soLoai; j++) {
                if (theLoai[j].equalsIgnoreCase(tl)) {
                    viTri = j;
                    break;
                }
            }
            if (viTri == -1) {
                theLoai[soLoai] = tl;
                soLuongTheoLoai[soLoai] = 1;
                soLoai++;
            } else {
                soLuongTheoLoai[viTri]++;
            }
        }
        System.out.println("* Theo the loai:");
        for (int j = 0; j < soLoai; j++) {
            System.out.printf("    - %-28s: %d ban sao%n", theLoai[j], soLuongTheoLoai[j]);
        }
    }

    public void inThongKe() {
        System.out.println("\n===== THONG KE KHO SACH =====");
        System.out.println("* Tong so ban sao: " + soLuong + " | So dau sach: " + demSoDauSach());
        System.out.printf("* Tong gia tri kho: %,.0f VND%n", tongGiaTri());
        System.out.println("* Theo trang thai:");
        for (BookStatus st : BookStatus.values()) {
            System.out.printf("    - %-28s: %d%n", st.getDisplayName(), demTheoTrangThai(st));
        }
        inThongKeTheoTheLoai();
    }
}
