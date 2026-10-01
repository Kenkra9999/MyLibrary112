package com.library.notifications;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.library.utils.DateUtils;
import com.library.utils.IdGenerator;

/**
 * Abstract Class Notification dai dien cho thong bao he thong gui toi thanh vien (Yeu cau R12).
 */
public abstract class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationId;
    private LocalDateTime createdOn;
    private String content;
    private String recipientMemberId;
    private boolean isSent;

    public Notification() {
        this.notificationId = IdGenerator.generateNotificationId();
        this.createdOn = LocalDateTime.now();
        this.isSent = false;
    }

    public Notification(String content, String recipientMemberId) {
        this.notificationId = IdGenerator.generateNotificationId();
        this.createdOn = LocalDateTime.now();
        this.content = content;
        this.recipientMemberId = recipientMemberId;
        this.isSent = false;
    }

    public Notification(String notificationId, LocalDateTime createdOn, String content, String recipientMemberId, boolean isSent) {
        this.notificationId = notificationId;
        this.createdOn = createdOn != null ? createdOn : LocalDateTime.now();
        this.content = content;
        this.recipientMemberId = recipientMemberId;
        this.isSent = isSent;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipientMemberId() {
        return recipientMemberId;
    }

    public void setRecipientMemberId(String recipientMemberId) {
        this.recipientMemberId = recipientMemberId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public abstract boolean sendNotification();

    public abstract String getNotificationType();

    @Override
    public String toString() {
        return "[" + getNotificationType() + "] ID: " + notificationId + " | Gui TV: " + recipientMemberId + " | " + DateUtils.formatDateTime(createdOn) + " | Noi dung: " + content;
    }
}
