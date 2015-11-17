package com.asinenko.carcalendar.items;

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
    private String timerDate;
    private String addedDate;

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

    public String getTimerDate() {
        return timerDate;
    }

    public void setTimerDate(String timerDate) {
        this.timerDate = timerDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }
}