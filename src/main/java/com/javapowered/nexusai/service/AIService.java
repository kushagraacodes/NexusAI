package com.javapowered.nexusai.service;

import com.javapowered.nexusai.model.AIResult;
import com.javapowered.nexusai.model.Message;

import java.util.List;

/** Abstraction for any conversational AI provider. */
public interface AIService {
    AIResult generateResponse(List<Message> conversation, String requestedModel);
}
