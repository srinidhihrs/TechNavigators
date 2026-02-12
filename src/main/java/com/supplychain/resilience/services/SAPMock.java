package com.supplychain.resilience.services;

import com.supplychain.resilience.model.Product;
import com.supplychain.resilience.model.Supplier;
import com.supplychain.resilience.model.PurchaseOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAPMock {
    private Map<String, Integer> inventoryLevels = new HashMap<>();
    private List<Supplier> suppliers = new ArrayList<>();
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    public SAPMock() {
        // Seed Data
        Product chips = new Product("PROD-001", "Semiconductor Chips", 150.0);

        // Suppliers
        suppliers.add(new Supplier("SUP-001", "Taiwan Semi", 10));
        suppliers.add(new Supplier("SUP-002", "Korea Electronics", 9));
        suppliers.add(new Supplier("SUP-003", "Local Tech", 7));

        // Initial Stock
        inventoryLevels.put("PROD-001", 100);
    }

    public int getStockLevel(String productId) {
        return inventoryLevels.getOrDefault(productId, 0);
    }

    public List<Supplier> findAlternativeSuppliers(String productId) {
        // Simple mock: return all except the primary (assumed)
        // In real app, would filter by capability
        return new ArrayList<>(suppliers);
    }

    public void createPurchaseOrder(PurchaseOrder po) {
        purchaseOrders.add(po);
        System.out.println("[SAP] Created PO: " + po);
    }
}
