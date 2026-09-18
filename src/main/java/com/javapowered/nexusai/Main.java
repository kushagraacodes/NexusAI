package com.javapowered.nexusai;

import com.javapowered.nexusai.controller.ChatController;
import com.javapowered.nexusai.database.DatabaseManager;
import com.javapowered.nexusai.repository.ChatRepository;
import com.javapowered.nexusai.service.AIService;
import com.javapowered.nexusai.service.GeminiService;
import com.javapowered.nexusai.ui.ChatWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseManager db = new DatabaseManager();
        db.initialize();

        ChatRepository repository = new ChatRepository(db);
        AIService ai = new GeminiService();
        ChatController controller = new ChatController(ai, repository);

        new ChatWindow(stage, controller).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
