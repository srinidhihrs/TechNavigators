package com.supplychain.resilience.services;

import com.supplychain.resilience.agents.*;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.Event;
import com.supplychain.resilience.model.Product;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.model.Supplier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SimulationService {
    private final EventLogService logService;

    public SimulationService(EventLogService logService) {
        this.logService = logService;
    }

    public void runSimulation() {
        logService.clear();
        logService.log("=== Starting Simulation ===");

        // 1. Initialize Mock Services
        LogisticsMock logisticsService = new LogisticsMock();
        SAPMock sapService = new SAPMock();

        // 2. Setup Seed Data
        // Reset/init data to ensure consistent runs
        Product chips = new Product("PROD-001", "Semiconductor Chips", 150.0);
        Supplier taiwanSemi = new Supplier("SUP-001", "Taiwan Semi", 10);
        Shipment shipment = new Shipment("SHP-999", chips, taiwanSemi, LocalDate.now().plusDays(2));
        logisticsService.registerShipment(shipment);

        // 3. Initialize Agents with LogService
        RootAgent rootAgent = new RootAgent(logService);
        LogisticsAgent logisticsAgent = new LogisticsAgent(logisticsService, rootAgent, logService);
        InventoryAgent inventoryAgent = new InventoryAgent(sapService, rootAgent, logService);
        ProcurementAgent procurementAgent = new ProcurementAgent(sapService, rootAgent, logService);
        NotificationAgent notificationAgent = new NotificationAgent(logService);

        // 4. Register Agents with Root
        rootAgent.setAgents(logisticsAgent, inventoryAgent, procurementAgent, notificationAgent);

        // 5. Simulate External Event: 10-day delay
        logService.log("\n--- Event: 10-Day Delay Reported by Webhook ---");
        logisticsService.updateDelay("SHP-999", 10);

        // 6. Trigger Webhook
        Map<String, Object> payload = new HashMap<>();
        payload.put("shipmentId", "SHP-999");
        Event webhookEvent = new Event("Webhook", "ExternalLogistics", payload);

        rootAgent.receiveMessage(new Message("System", "NewEvent", webhookEvent));
    }
}
