package com.supplychain.resilience.model;

import java.time.LocalDate;

public class PurchaseOrder {
    private String id;
    private Product product;
    private Supplier supplier;
    private int quantity;
    private LocalDate orderDate;
    private String status; // "Draft", "Submitted", "Fulfilled"

    public PurchaseOrder(String id, Product product, Supplier supplier, int quantity) {
        this.id = id;
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.orderDate = LocalDate.now();
        this.status = "Draft";
    }

    public void setStatus(String status) { this.status = status; }
    public String getId() { return id; }

    @Override
    public String toString() {
        return "PO{id='" + id + "', product=" + product.getName() + ", supplier=" + supplier.getName() + ", qty=" + quantity + "}";
    }
}
