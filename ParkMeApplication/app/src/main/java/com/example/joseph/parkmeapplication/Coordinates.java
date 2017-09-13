package com.example.joseph.parkmeapplication;

/**
 * author: ELaine
 */

public class Coordinates {
    private int userId;
    private double latitude;
    private double longitude;
    private String name;
    private String license;
    private int selected;
    private double BuyerLatitude;
    private double BuyerLongitude;
    private String BuyerName;
    private int Payment;


    Coordinates() {
        // no-args constructor
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double latitude) { this.longitude = longitude; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getLicense() { return license; }

    public void setLicense(String license) { this.license = license; }

    public double getBuyerLatitude() { return BuyerLatitude; }

    public void setBuyerLatitude(double BuyerLatitude) { this.BuyerLatitude = BuyerLatitude; }

    public double getBuyerLongitude() { return BuyerLongitude; }

    public void setBuyerLongitude(double BuyerLongitude) { this.BuyerLongitude = BuyerLongitude; }

    public String getBuyerName() { return BuyerName; }

    public void setBuyerNsme(String BuyerName) { this.BuyerName = BuyerName; }

    public int getSelected() { return selected; }

    public void setSelected(int selected) { this.selected = selected; }

    public int getPayment() { return Payment; }

    public void setPayment(int Payment) { this.Payment = Payment; }
}
