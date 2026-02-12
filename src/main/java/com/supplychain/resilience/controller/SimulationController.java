package com.supplychain.resilience.controller;

import com.supplychain.resilience.services.EventLogService;
import com.supplychain.resilience.services.SimulationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulationController {
    private final SimulationService simulationService;
    private final EventLogService logService;

    public SimulationController(SimulationService simulationService, EventLogService logService) {
        this.simulationService = simulationService;
        this.logService = logService;
    }

    @PostMapping("/trigger")
    public String triggerSimulation() {
        simulationService.runSimulation();
        return "Simulation Started";
    }

    @GetMapping("/logs")
    public List<String> getLogs() {
        return logService.getLogs();
    }
}
