package com.example.asm_api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Car {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("description")
    @Expose
    private String description;

    public Car(String id, String name, String manufacturer, Integer year, Integer price, String description) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.year = year;
        this.price = price;
        this.description = description;
    }

    public Car(String name, String manufacturer, Integer year, Integer price, String description) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.year = year;
        this.price = price;
        this.description = description;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
