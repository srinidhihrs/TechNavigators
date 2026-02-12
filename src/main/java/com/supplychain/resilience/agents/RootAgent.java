package com.supplychain.resilience.agents;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.SequentialAgent;
import java.util.List;

public class RootAgent {
    public static SequentialAgent create(List<BaseAgent> agents) {
        return SequentialAgent.builder()
                .name("SupplyChainManager")
                .description("Orchestrates the supply chain resilience workflow sequentially.")
                .subAgents(agents)
                .build();
    }
}
