package com.supplychain.resilience.model;

public class Product {
    private String id;
    private String name;
    private double unitPrice;

    public Product(String id, String name, double unitPrice) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "'}";
    }
}
