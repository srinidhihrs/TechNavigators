package com.supplychain.resilience.model;

import java.util.Map;

public class Event {
    private String type; // "Webhook", "Alert", "Task"
    private String source;
    private Map<String, Object> payload;

    public Event(String type, String source, Map<String, Object> payload) {
        this.type = type;
        this.source = source;
        this.payload = payload;
    }

    public String getType() { return type; }
    public String getSource() { return source; }
    public Map<String, Object> getPayload() { return payload; }

    @Override
    public String toString() {
        return "Event{type='" + type + "', source='" + source + "'}";
    }
}
