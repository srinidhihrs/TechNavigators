package com.supplychain.resilience.agents;

import com.supplychain.resilience.framework.Agent;
import com.supplychain.resilience.framework.Message;
import com.supplychain.resilience.model.PurchaseOrder;
import com.supplychain.resilience.model.Shipment;

public class NotificationAgent implements Agent {
    private com.supplychain.resilience.services.EventLogService logService;

    public NotificationAgent(com.supplychain.resilience.services.EventLogService logService) {
        this.logService = logService;
    }

    @Override
    public String getName() {
        return "NotificationAgent";
    }

    @Override
    public void receiveMessage(Message message) {
        logService.log("[" + getName() + "] Received: " + message.getContent());

        if ("DelayConfirmed".equals(message.getContent()) && message.getData() instanceof Shipment) {
            Shipment shipment = (Shipment) message.getData();
            notifyProductionManagerCancelPO(shipment);
        } else if ("RecoveryComplete".equals(message.getContent()) && message.getData() instanceof PurchaseOrder) {
            PurchaseOrder po = (PurchaseOrder) message.getData();
            notifyProductManagerNewPO(po);
        }
    }

    private void notifyProductionManagerCancelPO(Shipment shipment) {
        logService.log("[" + getName() + "] 📧 NOTIFICATION: To Production Manager & Supplier ("
                + shipment.getSupplier().getName() + ")");
        logService.log("[" + getName() + "]    Subject: ACTION REQUIRED - Cancel PO for Shipment " + shipment.getId());
        logService.log("[" + getName() + "]    Reason: Severe Delay of 10 days.");
    }

    private void notifyProductManagerNewPO(PurchaseOrder po) {
        logService.log("[" + getName() + "] 📧 NOTIFICATION: To Product Manager @ SAP");
        logService.log("[" + getName() + "]    Subject: New PO Created " + po.getId());
        logService.log("[" + getName() + "]    Details: " + po.toString());
    }
}
