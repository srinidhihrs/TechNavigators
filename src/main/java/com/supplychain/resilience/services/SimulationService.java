package com.supplychain.resilience.services;

import com.google.adk.agents.LlmAgent;
import com.google.adk.agents.SequentialAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.supplychain.resilience.agents.*;
import com.supplychain.resilience.model.Product;
import com.supplychain.resilience.model.Shipment;
import com.supplychain.resilience.model.Supplier;
import com.supplychain.resilience.tools.LogisticsTool;
import com.supplychain.resilience.tools.SapTool;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SimulationService {
    private final EventLogService logService;

    public SimulationService(EventLogService logService) {
        this.logService = logService;
    }

    public void runSimulation() {
        logService.clear();
        logService.log("=== Starting Google ADK 0.5.0 Powered Simulation ===");

        try {
            // 1. Model Configuration
            String modelName = "gemini-1.5-flash";
            String apiKey = System.getenv("GOOGLE_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                logService.log("WARNING: GOOGLE_API_KEY is not set. ADK integration might fail at runtime.");
            }

            // 2. Initialize Mock Services & Data
            LogisticsMock logisticsService = new LogisticsMock();
            SAPMock sapService = new SAPMock();

            Product chips = new Product("PROD-001", "Semiconductor Chips", 150.0);
            Supplier taiwanSemi = new Supplier("SUP-001", "Taiwan Semi", 10);
            Shipment shipment = new Shipment("SHP-999", chips, taiwanSemi, LocalDate.now().plusDays(2));
            logisticsService.registerShipment(shipment);

            // 3. Initialize Tools
            LogisticsTool logisticsTool = new LogisticsTool(logisticsService);
            SapTool sapTool = new SapTool(sapService);

            // 4. Initialize ADK Agents
            LlmAgent logisticsAgent = LogisticsAgent.create(logisticsTool, modelName);
            LlmAgent inventoryAgent = InventoryAgent.create(sapTool, modelName);
            LlmAgent procurementAgent = ProcurementAgent.create(sapTool, modelName);
            LlmAgent notificationAgent = NotificationAgent.create(modelName);

            // 5. Initialize Root Sequential Manager
            SequentialAgent manager = RootAgent.create(List.of(
                    logisticsAgent, 
                    inventoryAgent, 
                    procurementAgent, 
                    notificationAgent
            ));

            // 6. Simulate External Event: 10-day delay
            logService.log("\n--- Event Encountered: 10-Day Delay for SHP-999 ---");
            logisticsService.updateDelay("SHP-999", 10);

            // 7. Run Orchestration via InMemoryRunner
            logService.log("ADK Sequential Manager starting workflow...");
            
            String promptText = "Shipment SHP-999 is reported as delayed. " +
                            "LogisticsAgent: Confirm the delay. " +
                            "InventoryAgent: Assess impact. " +
                            "ProcurementAgent: If critical, create recovery PO. " +
                            "NotificationAgent: Inform stakeholders.";
            
            InMemoryRunner runner = new InMemoryRunner(manager);
            Content content = Content.builder()
                    .parts(List.of(Part.fromText(promptText)))
                    .build();
            
            // runAsync(userId, sessionId, content)
            Flowable<Event> events = runner.runAsync("admin", "simulation-session-01", content);
            
            logService.log("\n--- Workflow Execution Log ---");
            
            // Blocking subscribe to capture all events and log them
            events.blockingSubscribe(event -> {
                String text = event.stringifyContent();
                if (text != null && !text.isEmpty()) {
                    logService.log("Agent Prompt/Response: " + text);
                }
            }, error -> {
                logService.log("ERROR during agent execution: " + error.getMessage());
            });

            logService.log("\n--- Simulation Complete ---");

        } catch (Exception e) {
            logService.log("CRITICAL ERROR during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
