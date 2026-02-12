package com.supplychain.resilience.framework;

public interface Agent {
    String getName();

    void receiveMessage(Message message);
}
