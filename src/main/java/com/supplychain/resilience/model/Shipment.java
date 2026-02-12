package com.supplychain.resilience.model;

import java.time.LocalDate;

public class Shipment {
    private String id;
    private Product product;
    private Supplier supplier;
    private LocalDate expectedDeliveryDate;
    private String status; // "In Transit", "Delayed", "Delivered"

    public Shipment(String id, Product product, Supplier supplier, LocalDate expectedDeliveryDate) {
        this.id = id;
        this.product = product;
        this.supplier = supplier;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = "In Transit";
    }

    public String getId() { return id; }
    public Product getProduct() { return product; }
    public Supplier getSupplier() { return supplier; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getStatus() { return status; }

    public void setExpectedDeliveryDate(LocalDate date) { this.expectedDeliveryDate = date; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Shipment{id='" + id + "', product=" + product.getName() + ", status='" + status + "'}";
    }
}
