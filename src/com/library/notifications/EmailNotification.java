package com.library.notifications;

import java.time.LocalDateTime;

/**
 * Class EmailNotification ke thua tu Notification (Gui thong bao qua Email).
 */
public class EmailNotification extends Notification {

    private String emailAddress;

    public EmailNotification() {
        super();
    }

    public EmailNotification(String content, String recipientMemberId, String emailAddress) {
        super(content, recipientMemberId);
        this.emailAddress = emailAddress;
    }

    /** Constructor day du - dung khi doc lai tu file text. */
    public EmailNotification(String notificationId, LocalDateTime createdOn, String content,
                             String recipientMemberId, boolean isSent, String emailAddress) {
        super(notificationId, createdOn, content, recipientMemberId, isSent);
        this.emailAddress = emailAddress;
    }

    @Override
    public String getNoiNhanText() {
        return emailAddress == null ? "" : emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean sendNotification() {
        System.out.println("[EMAIL OUTBOX] Dang gui email toi " + emailAddress + ": " + getContent());
        setSent(true);
        return true;
    }

    @Override
    public String getNotificationType() {
        return "Email";
    }

    @Override
    public String toString() {
        return super.toString() + " (Email: " + emailAddress + ")";
    }
}
