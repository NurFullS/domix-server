package com.example.demo;

import java.util.List;

public class AdRequest {
    private String title;
    private String description;
    private double price;
    private String category;
    private String city;
    private String phone;
    private List<String> image_url;

    // --- Дополнительные поля ---

    // Недвижимость
    private Double landSize;      // площадь участка (сотки)
    private Double houseArea;     // площадь дома (м²)
    private Integer rooms;        // количество комнат
    private Integer floor;        // этаж
    private String buildingType;  // тип здания

    // Транспорт
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private String fuel;
    private String transmission;

    // Электроника
    private String condition;     // состояние
    private Boolean warranty;

    // Одежда
    private String size;
    private String gender;

    // --- Геттеры и сеттеры для всех полей ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getImage_url() { return image_url; }
    public void setImage_url(List<String> image_url) { this.image_url = image_url; }

    public Double getLandSize() { return landSize; }
    public void setLandSize(Double landSize) { this.landSize = landSize; }

    public Double getHouseArea() { return houseArea; }
    public void setHouseArea(Double houseArea) { this.houseArea = houseArea; }

    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public String getFuel() { return fuel; }
    public void setFuel(String fuel) { this.fuel = fuel; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public Boolean getWarranty() { return warranty; }
    public void setWarranty(Boolean warranty) { this.warranty = warranty; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
