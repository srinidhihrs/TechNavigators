package com.supplychain.resilience.agents;

import com.supplychain.resilience.framework.Agent;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.services.SAPMock;

public class InventoryAgent implements Agent {
    private SAPMock sapService;
    private RootAgent rootAgent;
    private com.supplychain.resilience.services.EventLogService logService;

    public InventoryAgent(SAPMock sapService, RootAgent rootAgent,
            com.supplychain.resilience.services.EventLogService logService) {
        this.sapService = sapService;
        this.rootAgent = rootAgent;
        this.logService = logService;
    }

    @Override
    public String getName() {
        return "InventoryAgent";
    }

    @Override
    public void receiveMessage(Message message) {
        logService.log("[" + getName() + "] Received: " + message.getContent());

        if (message.getData() instanceof Shipment) {
            Shipment shipment = (Shipment) message.getData();
            checkInventoryImpact(shipment);
        }
    }

    private void checkInventoryImpact(Shipment shipment) {
        // Loop Agent Logic: Check stock multiple times (Simulating monitoring)
        int maxIterations = 5;
        boolean actionRequired = false;

        for (int i = 1; i <= maxIterations; i++) {
            int currentStock = sapService.getStockLevel(shipment.getProduct().getId());
            logService.log("[" + getName() + "] 🔄 Iteration " + i + "/" + maxIterations + ": Checking stock for "
                    + shipment.getProduct().getName() + "...");

            // Simulate a slight delay or changing condition if this were real
            // For now, we just log the check

            if (currentStock < 500) {
                logService.log("[" + getName() + "]    ⚠ Stock is Critical (" + currentStock + "). Monitoring...");
                actionRequired = true;
                // In a real loop, we might break if condition is met, or keep monitoring.
                // Here we break to take action.
                break;
            } else {
                logService.log("[" + getName() + "]    ✅ Stock Safe. Continuing monitoring...");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            } // Simulate time passing
        }

        if (actionRequired) {
            logService.log(
                    "[" + getName() + "] 📢 PROPOSAL: Stock fill required. Asking Production Manager for approval...");

            // Simulation of Human-in-the-loop decision
            boolean managerApproved = askProductionManager();

            if (managerApproved) {
                logService.log("[" + getName() + "] ✅ APPROVED: Production Manager authorized new PO.");
                rootAgent.receiveMessage(new Message(getName(), "InventoryCritical", shipment));
            } else {
                logService.log("[" + getName() + "] ❌ REJECTED: Production Manager denied new PO.");
            }
        } else {
            rootAgent.receiveMessage(new Message(getName(), "InventorySafe", shipment));
        }
    }

    private boolean askProductionManager() {
        // Mocking user input - always return true for the happy path simulation
        return true;
    }
}
