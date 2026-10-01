package com.library.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class Rack bieu dien ke sach vat ly trong thu vien (Yeu cau R2).
 */
public class Rack implements Serializable {
    private static final long serialVersionUID = 1L;

    private int number;
    private String locationIdentifier;

    public Rack() {}

    public Rack(int number, String locationIdentifier) {
        this.number = number;
        this.locationIdentifier = locationIdentifier;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    public void setLocationIdentifier(String locationIdentifier) {
        this.locationIdentifier = locationIdentifier;
    }

    @Override
    public String toString() {
        return "Ke #" + number + " (" + locationIdentifier + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rack rack = (Rack) o;
        return number == rack.number && Objects.equals(locationIdentifier, rack.locationIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, locationIdentifier);
    }
}
