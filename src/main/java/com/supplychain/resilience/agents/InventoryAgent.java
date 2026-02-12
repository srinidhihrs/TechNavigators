package com.supplychain.resilience.agents;

import com.google.adk.agents.LlmAgent;
import com.supplychain.resilience.tools.SapTool;
import java.util.List;

public class InventoryAgent {
    public static LlmAgent create(SapTool tool, String modelName) {
        return LlmAgent.builder()
                .name("InventoryAgent")
                .instruction("You are an Inventory Agent. Your job is to check current stock levels. " +
                        "Use SapTool.getStockLevel to verify if a product is in stock as requested. " +
                        "Report findings back.")
                .tools(List.of(tool))
                .model(modelName)
                .build();
    }
}
