package com.asinenko.carcalendar.items;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexander on 17.11.15.
 */
public class TimerItem extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private int interval;
    private Date timerDate;
    private Date addedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Date getTimerDate() {
        return timerDate;
    }

    public void setTimerDate(Date timerDate) {
        this.timerDate = timerDate;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}