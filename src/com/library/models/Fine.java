package com.library.models;

import java.io.Serializable;
import java.time.LocalDate;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * Class Fine quan ly tien phat qua han muon sach (Yeu cau R15 / Pay Fine).
 */
public class Fine implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fineId;
    private double amount;
    private LocalDate creationDate;
    private String itemBarcode;
    private String memberId;
    private boolean isPaid;
    private LocalDate paidDate;

    public Fine() {
        this.fineId = IdGenerator.generateFineId();
        this.creationDate = LocalDate.now();
        this.isPaid = false;
    }

    public Fine(double amount, String itemBarcode, String memberId) {
        this.fineId = IdGenerator.generateFineId();
        this.amount = amount;
        this.creationDate = LocalDate.now();
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.isPaid = false;
    }

    public Fine(String fineId, double amount, LocalDate creationDate, String itemBarcode, String memberId, boolean isPaid, LocalDate paidDate) {
        this.fineId = fineId;
        this.amount = amount;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.isPaid = isPaid;
        this.paidDate = paidDate;
    }

    public String getFineId() {
        return fineId;
    }

    public void setFineId(String fineId) {
        this.fineId = fineId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public boolean payFine() {
        if (!isPaid) {
            this.isPaid = true;
            this.paidDate = LocalDate.now();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Phat #%s: %,.0f VND [Ma TV: %s, Sach: %s, Ngay tao: %s, Trang thai: %s]",
                fineId, amount, memberId, itemBarcode, DateUtils.formatDate(creationDate),
                isPaid ? "Da dong (" + DateUtils.formatDate(paidDate) + ")" : "CHUA DONG");
    }
}
