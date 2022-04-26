package com.example.blooddonationapp.Model;

public class Queries {
    String to, from, text, id;
    long timestamp;
    public Queries(){

    }
    public Queries(String to, String from, String text, String id, long timestamp) {
        this.to = to;
        this.from = from;
        this.text = text;
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
