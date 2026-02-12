package com.supplychain.resilience;

import com.supplychain.resilience.services.SimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdkSimulationTest {

    @Autowired
    private SimulationService simulationService;

    @Test
    void testAdkSimulation() {
        // This test triggers the ADK simulation.
        // It helps verify that all components are correctly wired and can be instantiated.
        // It won't full run without a valid API key but serves as a build-time integration check.
        simulationService.runSimulation();
    }
}
