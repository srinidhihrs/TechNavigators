package com.supplychain.resilience.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class EventLogService {
    private final List<String> logs = new CopyOnWriteArrayList<>();

    public void log(String message) {
        logs.add(message);
        System.out.println(message); // Keep console output too
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void clear() {
        logs.clear();
    }
}
