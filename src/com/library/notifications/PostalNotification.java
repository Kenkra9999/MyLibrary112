package com.library.notifications;

import com.library.models.Address;

/**
 * Class PostalNotification ke thua tu Notification (Gui thong bao qua Buu dien).
 */
public class PostalNotification extends Notification {
    private static final long serialVersionUID = 1L;

    private Address address;

    public PostalNotification() {
        super();
        this.address = new Address();
    }

    public PostalNotification(String content, String recipientMemberId, Address address) {
        super(content, recipientMemberId);
        this.address = address != null ? address : new Address();
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
