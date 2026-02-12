package com.supplychain.resilience.model;

public class Supplier {
    private String id;
    private String name;
    private int reliabilityScore; // 1-10

    public Supplier(String id, String name, int reliabilityScore) {
        this.id = id;
        this.name = name;
        this.reliabilityScore = reliabilityScore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getReliabilityScore() { return reliabilityScore; }

    @Override
    public String toString() {
        return "Supplier{name='" + name + "', reliability=" + reliabilityScore + "}";
    }
}
