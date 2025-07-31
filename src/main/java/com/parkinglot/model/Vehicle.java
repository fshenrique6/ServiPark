package com.parkinglot.model;

public class Vehicle {
    private String plate;
    private String model;
    private int year;
    private String ownerName;

    public Vehicle(){
    }

    public Vehicle(String plate, String model, int year, String ownerName) {
        this.plate = plate;
        this.model = model;
        this.year = year;
        this.ownerName = ownerName;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
