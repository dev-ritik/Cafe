package io.github.dev_ritik.cafe;

import io.realm.RealmObject;

public class Client extends RealmObject {
    private String id;

    private String name;

    private String checkInTime;

    private String checkOutTime;

    // Standard getters & setters generated by your IDE…


    public Client() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}