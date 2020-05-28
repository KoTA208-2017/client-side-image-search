package com.example.image_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class Product {
    @SerializedName("id")
    @Expose
    private int id;

    @Expose
    private String name;

    @Expose
    private String siteName;

    @Expose
    private String url;

    @Expose
    private int price;

    @Expose
    private String image;

    public Product(int id, String name, String siteName, String url, int price, String image) {
        this.id = id;
        this.name = name;
        this.siteName = siteName;
        this.url = url;
        this.price = price;
        this.image = image;
    }

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

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
