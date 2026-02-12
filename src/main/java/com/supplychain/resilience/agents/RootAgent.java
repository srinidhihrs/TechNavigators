package com.supplychain.resilience.agents;

import com.supplychain.resilience.framework.Agent;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.Shipment;

public class RootAgent implements Agent {
    private LogisticsAgent logisticsAgent;
    private InventoryAgent inventoryAgent;
    private ProcurementAgent procurementAgent;
    private NotificationAgent notificationAgent;
    private com.supplychain.resilience.services.EventLogService logService;

    public RootAgent(com.supplychain.resilience.services.EventLogService logService) {
        this.logService = logService;
    }

    public void setAgents(LogisticsAgent logistics, InventoryAgent inventory, ProcurementAgent procurement,
            NotificationAgent notification) {
        this.logisticsAgent = logistics;
        this.inventoryAgent = inventory;
        this.procurementAgent = procurement;
        this.notificationAgent = notification;
    }

    @Override
    public String getName() {
        return "RootManager";
    }

    @Override
    public void receiveMessage(Message message) {
        logService.log(
                "[" + getName() + "] Handling message from " + message.getSender() + ": " + message.getContent());

        String sender = message.getSender();
        String content = message.getContent();

        // Orchestration Logic
        if (message.getData() instanceof com.supplychain.resilience.model.Event) {
            // Initial Trigger
            logService.log("[" + getName() + "] Triggering Logistics Agent...");
            logisticsAgent.receiveMessage(new Message(getName(), "CheckDelay", message.getData()));

        } else if ("DelayConfirmed".equals(content)) {
            logService.log("[" + getName() + "] Delay confirmed. Triggering Inventory Check AND Notification...");

            // Parallel Execution Simulation
            // 1. Trigger Inventory Loop
            inventoryAgent.receiveMessage(new Message(getName(), "CheckImpact", message.getData()));

            // 2. Trigger Notification (Parallel)
            notificationAgent.receiveMessage(new Message(getName(), "DelayConfirmed", message.getData()));

        } else if ("InventoryCritical".equals(content)) {
            logService.log("[" + getName() + "] Inventory critical & Approved. Triggering Procurement...");
            procurementAgent.receiveMessage(new Message(getName(), "InitiateRecovery", message.getData()));

        } else if ("RecoveryComplete".equals(content)) {
            logService.log(
                    "[" + getName() + "] WORKFLOW COMPLETE: Supply Chain Recovered successfully. New PO Created.");

            // Notify about success
            notificationAgent.receiveMessage(new Message(getName(), "RecoveryComplete", message.getData()));
        }
    }
}
