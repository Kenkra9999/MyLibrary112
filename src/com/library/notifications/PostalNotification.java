package com.library.notifications;

import java.time.LocalDateTime;
import com.library.models.Address;

/**
 * Class PostalNotification ke thua tu Notification (Gui thong bao qua Buu dien).
 */
public class PostalNotification extends Notification {

    private Address address;

    public PostalNotification() {
        super();
        this.address = new Address();
    }

    public PostalNotification(String content, String recipientMemberId, Address address) {
        super(content, recipientMemberId);
        this.address = address != null ? address : new Address();
    }

    /** Constructor day du - dung khi doc lai tu file text. */
    public PostalNotification(String notificationId, LocalDateTime createdOn, String content,
                              String recipientMemberId, boolean isSent, Address address) {
        super(notificationId, createdOn, content, recipientMemberId, isSent);
        this.address = address != null ? address : new Address();
    }

    @Override
    public String getNoiNhanText() {
        return address.toDongText();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean sendNotification() {
        System.out.println("[POSTAL OUTBOX] Dang in phieu thong bao gui toi " + address + ": " + getContent());
        setSent(true);
        return true;
    }

    @Override
    public String getNotificationType() {
        return "Buu dien";
    }

    @Override
    public String toString() {
        return super.toString() + " (Dia chi: " + address + ")";
    }
}
