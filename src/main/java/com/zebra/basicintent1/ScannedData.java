package com.zebra.basicintent1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scanned_data")
public class ScannedData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String source;
    public String data;
    public String labelType;

    //getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }
}
