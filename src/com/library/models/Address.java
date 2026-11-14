package com.library.models;


/**
 * Custom Data Type bieu dien dia chi lien lac.
 */
public class Address {

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public Address() {
        this.country = "Viet Nam";
    }

    public Address(String streetAddress, String city, String state, String zipCode, String country) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address(String streetAddress, String city) {
        this(streetAddress, city, city, "700000", "Viet Nam");
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (streetAddress != null && !streetAddress.isEmpty()) sb.append(streetAddress);
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.length() > 0 ? sb.toString() : "Chua co dia chi";
    }

    /** Chuyen dia chi thanh 1 truong text de ghi file: "duong;thanh pho;tinh;ma buu chinh;quoc gia". */
    public String toDongText() {
        return giaTri(streetAddress) + ";" + giaTri(city) + ";" + giaTri(state) + ";" + giaTri(zipCode) + ";" + giaTri(country);
    }

    /** PHUONG THUC STATIC tao doi tuong Address tu chuoi da ghi bang toDongText(). */
    public static Address tuDongText(String text) {
        if (text == null || text.trim().isEmpty()) return new Address();
        String[] p = text.split(";", -1);
        return new Address(
                p.length > 0 ? p[0] : "",
                p.length > 1 ? p[1] : "",
                p.length > 2 ? p[2] : "",
                p.length > 3 ? p[3] : "",
                p.length > 4 ? p[4] : "");
    }

    private static String giaTri(String s) {
        return s == null ? "" : s.replace(";", ",").replace("|", "/");
    }
}
