package com.library.services;

import java.io.File;
import com.library.collections.*;

/**
 * Dich vu doc va ghi du lieu he thong thu vien ra cac tap tin van ban thuan (Plain Text File).
 * Thoa man tieu chi: (1d) Doc va ghi du lieu len file (text).
 */
public class TextFileStorageService {
    private static final String DATA_DIR = "data";
    public static final String FILE_SACH = DATA_DIR + "/sach.txt";
    public static final String FILE_THANH_VIEN = DATA_DIR + "/thanhvien.txt";
    public static final String FILE_MUON_TRA = DATA_DIR + "/muontra.txt";
    public static final String FILE_PHAT = DATA_DIR + "/phat.txt";

    public static synchronized void exportToTextFiles(Library library) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        // 1. Ghi sach
        DanhSachSach dsSach = new DanhSachSach(library.getCatalog().getAllBookItems().toArray(new com.library.models.BookItem[0]));
        dsSach.ghiFileText(FILE_SACH);

        // 2. Ghi thanh vien
        DanhSachThanhVien dsTV = new DanhSachThanhVien(library.getAllMembers().size());
        dsTV.themK(library.getAllMembers().toArray(new com.library.users.Member[0]));
        dsTV.ghiFileText(FILE_THANH_VIEN);

        // 3. Ghi muon tra
        DanhSachMuonTra dsMT = new DanhSachMuonTra(library.getLendings().size());
        dsMT.themK(library.getLendings().toArray(new com.library.models.BookLending[0]));
        dsMT.ghiFileText(FILE_MUON_TRA);

        // 4. Ghi tien phat
        DanhSachPhat dsPhat = new DanhSachPhat(library.getFines().size());
        dsPhat.themK(library.getFines().toArray(new com.library.models.Fine[0]));
        dsPhat.ghiFileText(FILE_PHAT);

        System.out.println("[+] Da xuat toan bo du lieu ra cac tap tin Text trong thu muc /data/");
    }

    public static synchronized void importFromTextFiles(Library library) {
        File fSach = new File(FILE_SACH);
        if (fSach.exists()) {
            DanhSachSach dsSach = new DanhSachSach();
            dsSach.docFileText(FILE_SACH);
            for (com.library.models.BookItem item : dsSach.getDanhSach()) {
                library.getCatalog().addBookItem(item);
            }
        }

        File fTV = new File(FILE_THANH_VIEN);
        if (fTV.exists()) {
            DanhSachThanhVien dsTV = new DanhSachThanhVien();
            dsTV.docFileText(FILE_THANH_VIEN);
            for (com.library.users.Member m : dsTV.getDanhSach()) {
                library.registerMember(m);
            }
        }

        File fMT = new File(FILE_MUON_TRA);
        if (fMT.exists()) {
            DanhSachMuonTra dsMT = new DanhSachMuonTra();
            dsMT.docFileText(FILE_MUON_TRA);
            for (com.library.models.BookLending l : dsMT.getDanhSach()) {
                library.getLendings().add(l);
            }
        }

        File fPhat = new File(FILE_PHAT);
        if (fPhat.exists()) {
            DanhSachPhat dsPhat = new DanhSachPhat();
            dsPhat.docFileText(FILE_PHAT);
            for (com.library.models.Fine f : dsPhat.getDanhSach()) {
                library.getFines().add(f);
            }
        }
    }
}
