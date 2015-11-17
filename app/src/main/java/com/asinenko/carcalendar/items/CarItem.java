package com.asinenko.carcalendar.items;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alexander on 17.11.15.
 */
public class CarItem extends RealmObject {

    @PrimaryKey
    private String name;
    private String vin;
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}