package com.library.notifications;

/**
 * Class EmailNotification ke thua tu Notification (Gui thong bao qua Email).
 */
public class EmailNotification extends Notification {
    private static final long serialVersionUID = 1L;

    private String emailAddress;

    public EmailNotification() {
        super();
    }

    public EmailNotification(String content, String recipientMemberId, String emailAddress) {
        super(content, recipientMemberId);
        this.emailAddress = emailAddress;
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
