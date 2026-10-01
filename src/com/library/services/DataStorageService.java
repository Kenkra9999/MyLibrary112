package com.library.services;

import java.io.*;

/**
 * Dich vu luu tru va khoi phuc du lieu he thong thu vien vao file (Persistence Layer).
 * Dong thoi dong bo hoa du lieu ra cac tap tin Plain Text (.txt) de phuc vu kiem tra.
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
            // 1. Luu file Binary
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeObject(library);
            }

            // 2. Luu dong thoi ra cac tap tin Text File (.txt)
            TextFileStorageService.exportToTextFiles(library);

            return true;
        } catch (Exception e) {
            System.err.println("[LOI LUU DU LIEU] " + e.getMessage());
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
            System.err.println("[LOI DOC DU LIEU] " + e.getMessage() + ". Se khoi tao lai du lieu mau.");
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
