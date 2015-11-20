package com.asinenko.carcalendar.items;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexander on 17.11.15.
 */

public class MeasurementItem extends RealmObject {

    public static int TIME_MEASUREMENT = 0;
    public static int DISTANCE_MEASUREMENT = 1;

    @PrimaryKey
    private String name;
    private String timeType;
    private long standartInterval; // time - days, distance - kilometers

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public long getStandartInterval() {
        return standartInterval;
    }

    public void setStandartInterval(long standartInterval) {
        this.standartInterval = standartInterval;
    }
}
