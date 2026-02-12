package com.supplychain.resilience.agents;

import com.google.adk.agents.LlmAgent;
import com.supplychain.resilience.tools.LogisticsTool;
import java.util.List;

public class LogisticsAgent {
    public static LlmAgent create(LogisticsTool tool, String modelName) {
        return LlmAgent.builder()
                .name("LogisticsAgent")
                .instruction("You are a Logistics Agent responsible for monitoring shipments. " +
                        "Use the LogisticsTool to get shipment status when provided with a shipment ID. " +
                        "If a shipment is delayed, report the details.")
                .tools(List.of(tool))
                .model(modelName)
                .build();
    }
}
