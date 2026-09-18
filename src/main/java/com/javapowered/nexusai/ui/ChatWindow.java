package com.javapowered.nexusai.ui;

import com.javapowered.nexusai.controller.ChatController;
import com.javapowered.nexusai.model.AIResult;
import com.javapowered.nexusai.model.Conversation;
import com.javapowered.nexusai.model.Message;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Nexus AI desktop chat interface.
 * Branding is separated from the Gemini provider name so the product can
 * present itself as Nexus AI while Gemini remains the underlying provider.
 */
public class ChatWindow {
    private static final String BRAND = "Nexus AI";
    private static final String THINKING_TEXT = "JavaGPT is thinking…";

    private final Stage stage;
    private final ChatController controller;
    private final VBox messagesBox = new VBox(12);
    private final TextArea input = new TextArea();
    private final ComboBox<String> model = new ComboBox<>();
    private final ListView<Conversation> history = new ListView<>();
    private final Label status = new Label("Ready");
    private final ProgressIndicator progress = new ProgressIndicator();
    private Button send;
    private Button deleteChat;

    public ChatWindow(Stage stage, ChatController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        stage.setTitle("Nexus AI – Gemini Assistant");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        Button newChat = new Button("＋  New Chat");
        newChat.setMaxWidth(Double.MAX_VALUE);
        stylePrimary(newChat);
        newChat.setOnAction(e -> startNewChat());

        deleteChat = new Button("🗑  Delete Chat");
        deleteChat.setMaxWidth(Double.MAX_VALUE);
        styleDanger(deleteChat);
        deleteChat.setOnAction(e -> deleteSelectedChat());

        Button refresh = new Button("↻");
        refresh.setTooltip(new Tooltip("Refresh conversations"));
        refresh.setOnAction(e -> refreshHistory());
        refresh.setStyle("-fx-font-size: 17px; -fx-background-color: white; -fx-border-color: #d8e0ef; -fx-border-radius: 8; -fx-background-radius: 8;");

        model.getItems().addAll(
                "gemini-3.8-flash",
                "gemini-3.7-flash",
                "gemini-3.6-flash"
        );
        model.setValue("gemini-3.8-flash");
        model.setPrefWidth(220);
        model.setStyle("-fx-background-color: white; -fx-border-color: #d8e0ef; -fx-border-radius: 8; -fx-background-radius: 8;");

        send = new Button("➤  Send");
        send.setDefaultButton(true);
        stylePrimary(send);
        send.setOnAction(e -> sendMessage());

        progress.setPrefSize(18, 18);
        progress.setVisible(false);

        input.setPromptText("Ask Nexus AI anything...");
        input.setWrapText(true);
        input.setPrefRowCount(2);
        input.setStyle("-fx-background-color: white; -fx-border-color: #d8e0ef; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12;");
        input.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER && e.isControlDown()) {
                sendMessage();
            }
        });

        Label shortcut = new Label("Ctrl + Enter to send");
        shortcut.setStyle("-fx-text-fill: #718096; -fx-font-size: 11px;");
        HBox actions = new HBox(8, shortcut, new Region(), progress, send);
        HBox.setHgrow(actions.getChildren().get(1), Priority.ALWAYS);
        VBox composer = new VBox(7, input, actions);

        Label logo = new Label("N");
        logo.setAlignment(Pos.CENTER);
        logo.setMinSize(50, 50);
        logo.setStyle("-fx-background-color: linear-gradient(to bottom right, #121b54, #2d7ff9); -fx-text-fill: white; -fx-font-size: 27px; -fx-font-weight: bold; -fx-background-radius: 14;");

        Label title = new Label(BRAND);
        title.setStyle("-fx-font-size: 27px; -fx-font-weight: 800; -fx-text-fill: #111a4d;");
        Label sub = new Label("Powered by Google Gemini");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
        VBox branding = new VBox(1, title, sub);
        HBox brand = new HBox(12, logo, branding);
        brand.setAlignment(Pos.CENTER_LEFT);

        history.setPrefWidth(285);
        history.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #f8fbff; -fx-border-color: transparent;");
        history.getSelectionModel().selectedItemProperty().addListener((o, oldValue, selected) -> {
            if (selected != null) loadConversation(selected);
        });

        Label conversations = new Label("Conversations");
        conversations.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #667085;");
        VBox left = new VBox(12, newChat, conversations, history);
        left.setPadding(new Insets(18));
        left.setStyle("-fx-background-color: #f8fbff; -fx-border-color: #e3eaf5; -fx-border-width: 0 1 0 0;");
        VBox.setVgrow(history, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(messagesBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        messagesBox.setPadding(new Insets(16, 12, 16, 12));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button info = new Button("ⓘ  Model Info");
        info.setOnAction(e -> showModelInfo());
        Button settings = new Button("⚙  Settings");
        settings.setOnAction(e -> showSettings());
        styleSecondary(info);
        styleSecondary(settings);

        HBox topRight = new HBox(10, new Label("AI Model:"), model, info, settings);
        topRight.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(topRight.getChildren().get(0), Priority.ALWAYS);
        HBox top = new HBox(20, brand, topRight);
        top.setPadding(new Insets(18, 20, 15, 20));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: white; -fx-border-color: #e3eaf5; -fx-border-width: 0 0 1 0;");

        HBox bottom = new HBox(10, deleteChat, new Region(), refresh);
        HBox.setHgrow(bottom.getChildren().get(1), Priority.ALWAYS);
        bottom.setPadding(new Insets(0, 18, 18, 18));
        left.getChildren().add(bottom);

        HBox statusBar = new HBox(8, status, new Region());
        HBox.setHgrow(statusBar.getChildren().get(1), Priority.ALWAYS);
        statusBar.setPadding(new Insets(4, 5, 4, 5));
        status.setStyle("-fx-text-fill: #667085; -fx-font-size: 11px;");

        VBox center = new VBox(top, scroll, composer, statusBar);
        center.setPadding(new Insets(0, 18, 12, 0));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        BorderPane root = new BorderPane(center, null, null, null, left);
        root.setStyle("-fx-font-family: 'Arial'; -fx-background-color: white;");

        stage.setScene(new Scene(root));
        refreshHistory();
        stage.show();
    }

    private void startNewChat() {
        controller.newConversation("New Conversation");
        messagesBox.getChildren().clear();
        status.setText("New conversation ready");
        refreshHistory();
    }

    private void deleteSelectedChat() {
        Conversation selected = history.getSelectionModel().getSelectedItem();
        if (selected == null) {
            status.setText("Select a conversation first");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete '" + selected.title() + "' and all its messages?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setTitle("Delete conversation");
        confirm.setHeaderText("Delete conversation");
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                controller.deleteConversation(selected.id());
                messagesBox.getChildren().clear();
                refreshHistory();
                status.setText("Conversation deleted");
            }
        });
    }

    private void sendMessage() {
        String text = input.getText().trim();
        if (text.isEmpty() || send.isDisabled()) return;

        addBubble("You", text, true);
        input.clear();
        setBusy(true);
        status.setText(THINKING_TEXT);

        String selectedModel = model.getValue();
        Task<AIResult> task = new Task<>() {
            @Override protected AIResult call() {
                return controller.ask(text, selectedModel);
            }
        };

        task.setOnSucceeded(e -> {
            AIResult result = task.getValue();
            addBubble(BRAND, result.text(), false);
            String modelLabel = result.modelUsed();
            if (result.demoMode()) {
                status.setText("Demo mode — API key not detected");
            } else if (result.fallbackUsed()) {
                status.setText("Response received using fallback model: " + modelLabel);
            } else {
                status.setText("Response received • " + modelLabel);
            }
            refreshHistory();
            setBusy(false);
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            addBubble("System", "Unexpected error: " + (error == null ? "Unknown error" : error.getMessage()), false);
            status.setText("Request failed");
            setBusy(false);
        });

        Thread thread = new Thread(task, "nexus-ai-request");
        thread.setDaemon(true);
        thread.start();
    }

    private void setBusy(boolean busy) {
        send.setDisable(busy);
        model.setDisable(busy);
        progress.setVisible(busy);
        input.setDisable(busy);
        if (!busy) Platform.runLater(() -> input.requestFocus());
    }

    private void loadConversation(Conversation c) {
        controller.selectConversation(c.id());
        messagesBox.getChildren().clear();
        for (Message m : controller.messages(c.id())) {
            addBubble(m.role().equals("user") ? "You" : BRAND, m.content(), m.role().equals("user"));
        }
        status.setText("Loaded conversation");
    }

    private void addBubble(String who, String text, boolean user) {
        Label label = new Label(who + "\n" + text);
        label.setWrapText(true);
        label.setMaxWidth(780);
        label.setPadding(new Insets(13));
        label.setStyle(user
                ? "-fx-background-color: #e9f2ff; -fx-background-radius: 14; -fx-text-fill: #172554; -fx-font-size: 13px;"
                : "-fx-background-color: #f5f7fb; -fx-background-radius: 14; -fx-text-fill: #1f2937; -fx-font-size: 13px;");
        HBox row = new HBox(label);
        row.setAlignment(user ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messagesBox.getChildren().add(row);
    }

    private void refreshHistory() {
        history.getItems().setAll(controller.conversations());
    }

    private void showModelInfo() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Nexus AI – Model Information");
        info.setHeaderText("Gemini models used by Nexus AI");
        info.setContentText(
                "Primary: gemini-3.8-flash\n"
                        + "Fallback 1: gemini-3.7-flash\n"
                        + "Fallback 2: gemini-3.6-flash\n\n"
                        + "If a model temporarily returns a retryable 5xx/429 error, "
                        + "Nexus AI automatically tries the next configured model.\n\n"
                        + "Provider: Google Gemini API\n"
                        + "API key source: GEMINI_API_KEY environment variable.");
        info.showAndWait();
    }

    private void showSettings() {
        Alert settings = new Alert(Alert.AlertType.INFORMATION);
        settings.setTitle("Nexus AI – Settings");
        settings.setHeaderText("Application settings");
        settings.setContentText(
                "Brand: Nexus AI\n"
                        + "AI provider: Google Gemini\n"
                        + "Local history: SQLite\n"
                        + "API key: GEMINI_API_KEY environment variable\n\n"
                        + "For a distributed version, keep the API key on a secure backend instead of embedding it in the desktop application.");
        settings.showAndWait();
    }

    private void stylePrimary(Button button) {
        button.setStyle("-fx-background-color: #2878f0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 9; -fx-border-radius: 9; -fx-padding: 10 16 10 16;");
    }

    private void styleSecondary(Button button) {
        button.setStyle("-fx-background-color: white; -fx-text-fill: #26345f; -fx-font-weight: bold; -fx-border-color: #d8e0ef; -fx-background-radius: 9; -fx-border-radius: 9; -fx-padding: 9 12 9 12;");
    }

    private void styleDanger(Button button) {
        button.setStyle("-fx-background-color: white; -fx-text-fill: #e11d48; -fx-font-weight: bold; -fx-border-color: #fb7185; -fx-background-radius: 9; -fx-border-radius: 9; -fx-padding: 10 16 10 16;");
    }
}
