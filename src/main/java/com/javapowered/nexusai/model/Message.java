package com.javapowered.nexusai.model;

public record Message(long id, long conversationId, String role, String content, long createdAt) {}
