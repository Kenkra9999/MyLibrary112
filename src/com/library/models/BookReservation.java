package com.library.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.library.enums.ReservationStatus;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * Class BookReservation quan ly phieu dat giu sach (Yeu cau R9, R10, R13).
 */
public class BookReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String itemBarcode;
    private String memberId;
    private LocalDateTime creationDate;
    private ReservationStatus status;

    public BookReservation() {
        this.reservationId = "RES-" + IdGenerator.generateShortUuid();
        this.creationDate = LocalDateTime.now();
        this.status = ReservationStatus.WAITING;
    }

    public BookReservation(String itemBarcode, String memberId) {
        this.reservationId = "RES-" + IdGenerator.generateShortUuid();
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.creationDate = LocalDateTime.now();
        this.status = ReservationStatus.WAITING;
    }

    public BookReservation(String reservationId, String itemBarcode, String memberId, LocalDateTime creationDate, ReservationStatus status) {
        this.reservationId = reservationId;
        this.itemBarcode = itemBarcode;
        this.memberId = memberId;
        this.creationDate = creationDate != null ? creationDate : LocalDateTime.now();
        this.status = status != null ? status : ReservationStatus.WAITING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String fetchReservationDetails() {
        return String.format("Ma dat giu: %s | Ma sach: %s | Ma TV: %s | Ngay tao: %s | Trang thai: %s",
                reservationId, itemBarcode, memberId, DateUtils.formatDateTime(creationDate), status);
    }

    @Override
    public String toString() {
        return fetchReservationDetails();
    }
}
