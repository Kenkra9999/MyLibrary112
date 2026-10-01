package com.library.services;

import java.io.*;

/**
 * Dịch vụ lưu trữ và khôi phục dữ liệu hệ thống thư viện vào file (Persistence Layer).
 */
public class DataStorageService {
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + "/library_state.dat";

    public static synchronized boolean saveData(Library library) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeObject(library);
                return true;
            }
        } catch (Exception e) {
            System.err.println("[LỖI LƯU DỮ LIỆU] " + e.getMessage());
            return false;
        }
    }

    public static synchronized Library loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Library) ois.readObject();
        } catch (Exception e) {
            System.err.println("[LỖI ĐỌC DỮ LIỆU] " + e.getMessage() + ". Sẽ khởi tạo lại dữ liệu mẫu.");
            return null;
        }
    }

    public static synchronized boolean clearData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
