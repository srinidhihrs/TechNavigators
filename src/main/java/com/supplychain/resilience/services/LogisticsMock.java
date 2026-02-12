package com.supplychain.resilience.services;

import com.supplychain.resilience.model.Shipment;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LogisticsMock {
    private Map<String, Shipment> externalShipmentData = new HashMap<>();

    public LogisticsMock() {
        // Mock data will be added here
    }

    public void registerShipment(Shipment shipment) {
        externalShipmentData.put(shipment.getId(), shipment);
    }

    public void updateDelay(String shipmentId, int daysDelay) {
        if (externalShipmentData.containsKey(shipmentId)) {
            Shipment s = externalShipmentData.get(shipmentId);
            s.setExpectedDeliveryDate(s.getExpectedDeliveryDate().plusDays(daysDelay));
            s.setStatus("Delayed");
        }
    }

    public Shipment getShipmentStatus(String id) {
        return externalShipmentData.get(id);
    }
}
