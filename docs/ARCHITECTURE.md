# System Architecture

Nexus AI follows a layered architecture that separates the JavaFX presentation layer from application workflow, AI integration, and persistence.

```text
+---------------------------+
|       JavaFX UI           |
|       ChatWindow          |
+-------------+-------------+
              |
              v
+---------------------------+
|      ChatController       |
| validation + workflow     |
+------+------+-------------+
       |      |
       |      v
       |   +----------------+
       |   | ChatRepository |
       |   +-------+--------+
       |           |
       |           v
       |       +--------+
       |       | SQLite |
       |       +--------+
       v
+---------------------------+
| AIService / GeminiService |
+-------------+-------------+
              |
              v
+---------------------------+
|      Google Gemini API    |
+---------------------------+
```

## Layers
- **Presentation:** `ChatWindow` provides the JavaFX interface.
- **Controller:** `ChatController` validates input and coordinates the workflow.
- **Service:** `AIService` defines the provider abstraction; `GeminiService` implements REST communication, context construction and fallback.
- **Repository:** `ChatRepository` isolates SQLite CRUD operations.
- **Database:** `DatabaseManager` initializes the local SQLite database.
- **Models:** `Conversation`, `Message`, and `AIResult` represent application data.

## Non-functional requirements
- Performance: network calls run in a JavaFX background `Task`.
- Security: API keys are read from environment variables and excluded from source control.
- Usability: clear chat interface, model selector and history.
- Reliability: retryable API errors trigger configured fallback models.
- Maintainability: controller/service/repository separation.
- Resource efficiency: recent conversation messages are used as AI context.
