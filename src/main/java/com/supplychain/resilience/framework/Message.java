package com.supplychain.resilience.framework;

public class Message {
    private String sender;
    private String content; // JSON-like string or command
    private Object data; // Generic payload

    public Message(String sender, String content, Object data) {
        this.sender = sender;
        this.content = content;
        this.data = data;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Msg(from=" + sender + "): " + content;
    }
}
