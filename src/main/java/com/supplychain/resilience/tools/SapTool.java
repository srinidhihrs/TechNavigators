package com.supplychain.resilience.tools;

import com.google.adk.tools.Annotations.Schema;
import com.supplychain.resilience.model.PurchaseOrder;
import com.supplychain.resilience.model.Supplier;
import com.supplychain.resilience.services.SAPMock;
import java.util.List;

public class SapTool {
    private final SAPMock sapMock;

    public SapTool(SAPMock sapMock) {
        this.sapMock = sapMock;
    }

    @Schema(description = "Check the current stock level for a product in the SAP system.")
    public int getStockLevel(@Schema(description = "The ID of the product") String productId) {
        return sapMock.getStockLevel(productId);
    }

    @Schema(description = "Find alternative suppliers for a given product in the SAP system.")
    public List<Supplier> findAlternativeSuppliers(@Schema(description = "The ID of the product") String productId) {
        return sapMock.findAlternativeSuppliers(productId);
    }

    @Schema(description = "Create a new Purchase Order (PO) in the SAP system.")
    public void createPurchaseOrder(PurchaseOrder po) {
        sapMock.createPurchaseOrder(po);
    }
}
