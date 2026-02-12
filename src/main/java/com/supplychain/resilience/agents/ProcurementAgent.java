package com.supplychain.resilience.agents;

import com.supplychain.resilience.framework.Agent;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.PurchaseOrder;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.model.Supplier;
import com.supplychain.resilience.services.SAPMock;
import java.util.List;

public class ProcurementAgent implements Agent {
    private SAPMock sapService;
    private RootAgent rootAgent;
    private com.supplychain.resilience.services.EventLogService logService;

    public ProcurementAgent(SAPMock sapService, RootAgent rootAgent,
            com.supplychain.resilience.services.EventLogService logService) {
        this.sapService = sapService;
        this.rootAgent = rootAgent;
        this.logService = logService;
    }

    @Override
    public String getName() {
        return "ProcurementAgent";
    }

    @Override
    public void receiveMessage(Message message) {
        logService.log("[" + getName() + "] Received: " + message.getContent());

        if ("InitiateRecovery".equals(message.getContent()) && message.getData() instanceof Shipment) {
            Shipment shipment = (Shipment) message.getData();
            executeRecovery(shipment);
        }
    }

    private void executeRecovery(Shipment shipment) {
        logService.log(
                "[" + getName() + "] Searching for alternative suppliers for " + shipment.getProduct().getName());
        List<Supplier> alternatives = sapService.findAlternativeSuppliers(shipment.getProduct().getId());

        if (!alternatives.isEmpty()) {
            Supplier bestOption = alternatives.get(0); // Simply pick the first one
            logService.log("[" + getName() + "] Found supplier: " + bestOption.getName());

            PurchaseOrder newPO = new PurchaseOrder("PO-" + System.currentTimeMillis(), shipment.getProduct(),
                    bestOption, 500);
            sapService.createPurchaseOrder(newPO);

            rootAgent.receiveMessage(new Message(getName(), "RecoveryComplete", newPO));
        } else {
            logService.log("[" + getName() + "] No alternative suppliers found!");
            rootAgent.receiveMessage(new Message(getName(), "RecoveryFailed", shipment));
        }
    }
}
