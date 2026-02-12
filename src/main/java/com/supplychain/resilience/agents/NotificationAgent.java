package com.supplychain.resilience.agents;

import com.google.adk.agents.LlmAgent;

public class NotificationAgent {
    public static LlmAgent create(String modelName) {
        return LlmAgent.builder()
                .name("NotificationAgent")
                .instruction("You are a Notification Agent. Your role is to alert human stakeholders about " +
                        "critical events in the supply chain, such as confirmed delays or new purchase orders. " +
                        "Format your alerts clearly.")
                .model(modelName)
                .build();
    }
}
