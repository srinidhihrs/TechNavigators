package com.supplychain.resilience.agents;

import com.google.adk.agents.LlmAgent;
import com.supplychain.resilience.tools.SapTool;
import java.util.List;

public class ProcurementAgent {
    public static LlmAgent create(SapTool tool, String modelName) {
        return LlmAgent.builder()
                .name("ProcurementAgent")
                .instruction("You are a Procurement Agent. Your role is to handle order recovery. " +
                        "When alerted of a delay or low stock, use SapTool to find alternative suppliers " +
                        "and create a new Purchase Order to mitigate risks. Report the new PO details.")
                .tools(List.of(tool))
                .model(modelName)
                .build();
    }
}
