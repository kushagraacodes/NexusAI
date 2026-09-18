package com.javapowered.nexusai;

import com.javapowered.nexusai.controller.ChatController;
import com.javapowered.nexusai.database.DatabaseManager;
import com.javapowered.nexusai.model.AIResult;
import com.javapowered.nexusai.model.Message;
import com.javapowered.nexusai.repository.ChatRepository;
import com.javapowered.nexusai.service.AIService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatControllerTest {
    @Test
    void blankPromptIsRejected() {
        AIService ai = (messages, model) -> new AIResult("ok", model, false, false);
        ChatController controller = new ChatController(ai, new ChatRepository(new DatabaseManager()));
        assertEquals("Please enter a message.", controller.ask("   ", "gemini-3.6-flash").text());
    }

    @Test
    void askSavesUserAndAssistantMessages() {
        AIService ai = (messages, model) -> {
            assertEquals(1, messages.size());
            assertEquals("user", messages.get(0).role());
            return new AIResult("Java is object-oriented.", model, false, false);
        };

        ChatRepository repository = new ChatRepository(new DatabaseManager());
        ChatController controller = new ChatController(ai, repository);
        long id = controller.newConversation("Test conversation");

        AIResult result = controller.ask("What is Java?", "gemini-3.6-flash");

        assertEquals("Java is object-oriented.", result.text());
        List<Message> messages = repository.getMessages(id);
        assertEquals(2, messages.size());
        assertEquals("user", messages.get(0).role());
        assertEquals("assistant", messages.get(1).role());
        assertEquals("Java is object-oriented.", messages.get(1).content());
        repository.deleteConversation(id);
    }

    @Test
    void conversationContextIsPassedToAi() {
        AIService ai = (messages, model) -> {
            assertEquals(3, messages.size());
            assertEquals("Hello", messages.get(0).content());
            assertEquals("Hi!", messages.get(1).content());
            assertEquals("Explain Java", messages.get(2).content());
            return new AIResult("Java is a programming language.", model, false, false);
        };

        ChatRepository repository = new ChatRepository(new DatabaseManager());
        ChatController controller = new ChatController(ai, repository);
        long id = controller.newConversation("Context test");
        repository.saveMessage(id, "user", "Hello");
        repository.saveMessage(id, "assistant", "Hi!");

        AIResult result = controller.ask("Explain Java", "gemini-3.6-flash");
        assertFalse(result.demoMode());
        repository.deleteConversation(id);
    }
}
