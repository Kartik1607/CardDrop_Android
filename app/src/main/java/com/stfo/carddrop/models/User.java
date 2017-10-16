package com.stfo.carddrop.models;

/**
 * Created by Kartik on 10/14/2017.
 */

public class User {
    private String id;
    private String name;
    private String detail;
    private long phone;
    private String cardImageId;

    public User(String id, String name, String detail, long phone, String cardImageId) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.phone = phone;
        this.cardImageId = cardImageId;
    }

    public User() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void copyFrom(User user) {
        this.name = user.name;
        this.detail = user.detail;
        this.phone = user.phone;
        this.cardImageId = user.cardImageId;
    }
}
