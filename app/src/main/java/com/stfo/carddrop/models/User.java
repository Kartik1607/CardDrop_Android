package com.stfo.carddrop.models;

/**
 * Created by Kartik on 10/14/2017.
 */

public class User {
    private String name;
    private String detail;
    private long phone;
    private String cardImageId;

    public User(String name, String detail, long phone, String cardImageId) {
        this.name = name;
        this.detail = detail;
        this.phone = phone;
        this.cardImageId = cardImageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getCardImageId() {
        return cardImageId;
    }

    public void setCardImageId(String cardImageId) {
        this.cardImageId = cardImageId;
    }
}
