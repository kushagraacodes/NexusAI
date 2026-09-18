package com.javapowered.nexusai.controller;

import com.javapowered.nexusai.model.AIResult;
import com.javapowered.nexusai.model.Conversation;
import com.javapowered.nexusai.model.Message;
import com.javapowered.nexusai.repository.ChatRepository;
import com.javapowered.nexusai.service.AIService;

import java.util.List;

public class ChatController {
    private final AIService ai;
    private final ChatRepository repository;
    private long currentConversation = -1;

    public ChatController(AIService ai, ChatRepository repository) {
        this.ai = ai;
        this.repository = repository;
    }

    public synchronized long newConversation(String title) {
        currentConversation = repository.createConversation(title);
        return currentConversation;
    }

    public synchronized AIResult ask(String prompt, String model) {
        if (prompt == null || prompt.isBlank()) {
            return new AIResult("Please enter a message.", model, false, false);
        }
        if (currentConversation < 0) {
            String title = prompt.length() > 30 ? prompt.substring(0, 30) + "…" : prompt;
            newConversation(title);
        }

        repository.saveMessage(currentConversation, "user", prompt);
        List<Message> context = repository.getMessages(currentConversation);
        AIResult result = ai.generateResponse(context, model);
        repository.saveMessage(currentConversation, "assistant", result.text());
        return result;
    }

    public synchronized void deleteConversation(long id) {
        repository.deleteConversation(id);
        if (currentConversation == id) currentConversation = -1;
    }

    public synchronized void clearCurrentConversation() {
        if (currentConversation >= 0) {
            repository.deleteConversation(currentConversation);
            currentConversation = -1;
        }
    }

    public synchronized void selectConversation(long id) {
        currentConversation = id;
    }

    public synchronized List<Conversation> conversations() { return repository.listConversations(); }
    public synchronized List<Message> messages(long id) { return repository.getMessages(id); }
}
