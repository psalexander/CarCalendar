package com.asinenko.carcalendar.items;

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
    private String replaceDate;
    private String updateDate;
}
