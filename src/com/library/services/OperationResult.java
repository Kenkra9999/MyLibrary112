package com.library.services;

/**
 * Ket qua cua mot nghiep vu (muon, tra, gia han...): thanh cong hay that bai + thong bao.
 * Dung PHUONG THUC STATIC (success / failure) de tao doi tuong cho gon.
 */
public class OperationResult {
    private final boolean success;
    private final String message;

    private OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    public static OperationResult failure(String message) {
        return new OperationResult(false, message);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return (success ? "[+] " : "[-] ") + message;
    }
}
