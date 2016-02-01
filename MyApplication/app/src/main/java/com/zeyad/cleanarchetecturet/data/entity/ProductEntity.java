package com.zeyad.cleanarchetecturet.data.entity;

public class ProductEntity {

    private String product_id, name, image, description;
    private int price;

    public ProductEntity(String product_id, String name, String image, int price) {
        this.product_id = product_id;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
