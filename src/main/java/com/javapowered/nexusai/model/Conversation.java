package com.javapowered.nexusai.model;

public record Conversation(long id, String title, long createdAt) {
    @Override public String toString() { return title; }
}
