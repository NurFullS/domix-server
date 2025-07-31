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

    // Геттеры и сеттеры

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getImage_url() {
        return image_url;
    }
    public void setImage_url(List<String> image_url) {
        this.image_url = image_url;
    }
}
