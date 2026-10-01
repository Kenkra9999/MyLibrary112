package com.library.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.library.utils.Constants;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * Class BookLending quan ly thong tin va trang thai mot luot muon sach (Yeu cau R1, R7, R8, R10).
 */
public class BookLending implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lendingId;
    private String itemBarcode;
    private String memberId;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public BookLending() {
        this.lendingId = "LEN-" + IdGenerator.generateShortUuid();
        this.creationDate = LocalDate.now();
        this.dueDate = this.creationDate.plusDays(Constants.MAX_LENDING_DAYS);
        this.isReturned = false;
    }

    public BookLending(String itemBarcode, String memberId) {
        this.lendingId = "LEN-" + IdGenerator.generateShortUuid();
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.creationDate = LocalDate.now();
        this.dueDate = this.creationDate.plusDays(Constants.MAX_LENDING_DAYS);
        this.isReturned = false;
    }

    public BookLending(String lendingId, String itemBarcode, String memberId, LocalDate creationDate, LocalDate dueDate, LocalDate returnDate, boolean isReturned) {
        this.lendingId = lendingId;
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.dueDate = dueDate != null ? dueDate : this.creationDate.plusDays(Constants.MAX_LENDING_DAYS);
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }

    public String getLendingId() {
        return lendingId;
    }

    public void setLendingId(String lendingId) {
        this.lendingId = lendingId;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public boolean isOverdue() {
        if (isReturned) {
            return returnDate != null && returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        LocalDate effectiveEndDate = isReturned ? (returnDate != null ? returnDate : LocalDate.now()) : LocalDate.now();
        return ChronoUnit.DAYS.between(dueDate, effectiveEndDate);
    }

    public String fetchLendingDetails() {
        return String.format("Ma muon: %s | Sach: %s | TV: %s | Ngay muon: %s | Han tra: %s | Trang thai: %s",
                lendingId, itemBarcode, memberId,
                DateUtils.formatDate(creationDate),
                DateUtils.formatDate(dueDate),
                isReturned ? ("Da tra ngay " + DateUtils.formatDate(returnDate)) : (isOverdue() ? "QUA HAN (" + getOverdueDays() + " ngay)" : "Dang muon"));
    }

    @Override
    public String toString() {
        return fetchLendingDetails();
    }
}
