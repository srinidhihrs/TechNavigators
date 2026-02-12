package com.supplychain.resilience.tools;

import com.google.adk.tools.Annotations.Schema;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.services.LogisticsMock;

public class LogisticsTool {
    private final LogisticsMock logisticsMock;

    public LogisticsTool(LogisticsMock logisticsMock) {
        this.logisticsMock = logisticsMock;
    }

    @Schema(description = "Get the current status and delay information for a specific shipment.")
    public Shipment getShipmentStatus(@Schema(description = "The ID of the shipment") String shipmentId) {
        return logisticsMock.getShipmentStatus(shipmentId);
    }
}
