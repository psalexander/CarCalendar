package com.asinenko.carcalendar.items;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexander on 17.11.15.
 */
public class ReplacementItem extends RealmObject {

    @PrimaryKey
    private int id;
    private String carName;
    private String measurementName;
    private Date replaceDate;
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public Date getReplaceDate() {
        return replaceDate;
    }

    public void setReplaceDate(Date replaceDate) {
        this.replaceDate = replaceDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
