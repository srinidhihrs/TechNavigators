package com.supplychain.resilience.agents;

import com.supplychain.resilience.framework.Agent;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.Event;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.services.LogisticsMock;

public class LogisticsAgent implements Agent {
    private LogisticsMock logisticsService;
    private RootAgent rootAgent; // For callback/forwarding
    private com.supplychain.resilience.services.EventLogService logService;

    public LogisticsAgent(LogisticsMock logisticsService, RootAgent rootAgent,
            com.supplychain.resilience.services.EventLogService logService) {
        this.logisticsService = logisticsService;
        this.rootAgent = rootAgent;
        this.logService = logService;
    }

    @Override
    public String getName() {
        return "LogisticsAgent";
    }

    @Override
    public void receiveMessage(Message message) {
        logService.log("[" + getName() + "] Received: " + message.getContent());

        if (message.getData() instanceof Event) {
            Event event = (Event) message.getData();
            if ("Webhook".equals(event.getType()) && "ExternalLogistics".equals(event.getSource())) {
                String shipmentId = (String) event.getPayload().get("shipmentId");
                checkDelayAndReport(shipmentId);
            }
        }
    }

    private void checkDelayAndReport(String shipmentId) {
        Shipment shipment = logisticsService.getShipmentStatus(shipmentId);
        if (shipment != null && "Delayed".equals(shipment.getStatus())) {
            logService.log("[" + getName() + "] Confirmed delay for " + shipmentId + ": Expected Date "
                    + shipment.getExpectedDeliveryDate());
            // Report back to Root
            rootAgent.receiveMessage(new Message(getName(), "DelayConfirmed", shipment));
        }
    }
}
