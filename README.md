# Nexus AI – Gemini Conversational Assistant

Nexus AI is a JavaFX desktop conversational assistant powered by Google's Gemini API. The application provides a modern chat interface, local SQLite conversation history, model selection, automatic fallback, validation, background API execution, and error handling.

> **Branding:** The product is presented to users as **Nexus AI**. The underlying AI provider is Google Gemini. During network requests the status indicator intentionally displays **“JavaGPT is thinking…”** as requested for the project UI.

## Academic Context
This project is designed for the VITyarthi **Build Your Own Project** requirements. It demonstrates modular Java development, GUI design, REST API integration, persistence, validation, testing, error handling, and documentation.

## Major Functional Modules
1. **Conversational AI Module** – sends multi-turn prompts to Gemini and displays generated answers.
2. **Conversation Management Module** – creates, loads, refreshes, and deletes chat sessions.
3. **Model Management Module** – lets the user select a Gemini model and automatically falls back after retryable API failures.
4. **Persistence Module** – stores conversations and messages locally in SQLite.
5. **Validation & Error Handling Module** – validates input and presents user-friendly API/network errors.

## Key Features
- Nexus AI branded JavaFX interface.
- Real Gemini API integration through REST.
- Multi-turn conversation context.
- Gemini model selector.
- Automatic fallback across `gemini-3.8-flash`, `gemini-3.7-flash`, and `gemini-3.6-flash` for temporary 429/5xx failures.
- Background API requests so the GUI remains responsive.
- **“JavaGPT is thinking…”** loading/status indicator.
- New Chat, Delete Chat, Refresh, Model Info, and Settings controls.
- SQLite conversation history.
- Demo Mode when `GEMINI_API_KEY` is not configured.
- Unit tests for validation, persistence, and conversation context.

## Technology Stack
- Java 17+
- JavaFX 21
- Maven
- SQLite
- Jackson Databind
- JUnit 5
- Google Gemini API

## Project Structure
```text
NexusAI/
├── pom.xml
├── README.md
├── statement.md
├── .gitignore
├── docs/
│   ├── ARCHITECTURE.md
│   ├── WORKFLOW.md
│   ├── USE_CASE.md
│   ├── CLASS_DIAGRAM.md
│   ├── SEQUENCE.md
│   ├── ER_DIAGRAM.md
│   ├── REPORT_CONTENT.md
│   ├── TESTING.md
│   └── screenshots/
└── src/
    ├── main/java/com/javapowered/nexusai/
    │   ├── Main.java
    │   ├── controller/ChatController.java
    │   ├── database/DatabaseManager.java
    │   ├── model/AIResult.java
    │   ├── model/Conversation.java
    │   ├── model/Message.java
    │   ├── repository/ChatRepository.java
    │   ├── service/AIService.java
    │   ├── service/GeminiService.java
    │   └── ui/ChatWindow.java
    └── test/java/com/javapowered/nexusai/
        └── ChatControllerTest.java
```

## Setup

### Requirements
- JDK 17 or newer
- Maven 3.9+
- IntelliJ IDEA or another Maven-compatible IDE
- Gemini API key for direct local API mode

### Configure the API key
The current desktop build calls Gemini directly, so the Java process needs access to `GEMINI_API_KEY`.

#### IntelliJ IDEA
`Run → Edit Configurations… → Main → Environment variables`

Add:
```text
GEMINI_API_KEY=YOUR_API_KEY
```

#### macOS Terminal
```bash
export GEMINI_API_KEY="YOUR_API_KEY"
```

Do **not** commit the key to GitHub.

### Run
```bash
mvn clean javafx:run
```

Or run `com.javapowered.nexusai.Main` from IntelliJ.

### Test
```bash
mvn test
```

## API Key and Multi-User Deployment

There are two different architectures:

### 1. Current desktop version – direct API access
Each person running the standalone desktop application needs an API key available to that Java process. This is the architecture included in this submission.

### 2. Shared chatbot for many users – recommended production architecture
A single private API key can be kept on a **backend/server**. The Nexus AI desktop client sends prompts to that backend, and the backend calls Gemini. Users then do not need their own Gemini API keys.

**Do not simply hardcode your Gemini key inside a distributed desktop JAR.** Anyone who receives the application can potentially extract it and use your quota. Google explicitly recommends keeping API keys confidential and using a server-side proxy/backend for client applications. See Google's official API-key security guidance.

For a public/shared deployment, the architecture should therefore be:
```text
Nexus AI Desktop → Your Secure Backend → Gemini API
                         ↑
                    Secret API key
```

For a classroom demonstration on your own machine, keeping the key in the IntelliJ environment variable is sufficient and keeps it out of the source code.

## Security
- API key is read from `GEMINI_API_KEY`.
- No API key is stored in Java source code.
- `.gitignore` excludes `.env`, database files, IDE files and common secret extensions.
- If a key is exposed, revoke/regenerate it and update the local environment.

## Error Handling
Retryable `429`, `500`, `502`, `503`, and `504` responses trigger model fallback. The selected model is attempted first, followed by the configured fallback models.

## Screenshots / Demonstration
The repository contains both development-stage **JavaGPT** screenshots and final **Nexus AI** screenshots. They document the progression from initial UI, Demo Mode and API-error testing to the final branded interface and successful Gemini response.

Screenshot groups:
- `docs/screenshots/javagpt-initial-ui.png`
- `docs/screenshots/javagpt-demo-mode.png`
- `docs/screenshots/javagpt-503-error.png`
- `docs/screenshots/javagpt-successful-response.png`
- `docs/screenshots/nexus-ai-main-ui.png`
- `docs/screenshots/nexus-ai-conversation-management.png`
- `docs/screenshots/nexus-ai-demo-mode.png`
- `docs/screenshots/nexus-ai-successful-response.png`

The final UI intentionally displays **“JavaGPT is thinking…”** while the product is branded **Nexus AI**.

## Design Diagrams
Visual design artefacts are available in `docs/diagrams/`:
- System Architecture
- Use Case Diagram
- Workflow Diagram
- Sequence Diagram
- Class Diagram
- ER Diagram / Schema

## Documentation
See `docs/REPORT_CONTENT.md` for the report structure, `docs/TESTING.md` for testing details, and `docs/DIAGRAMS.md` for the visual design artefacts.

## API Reference
The project uses Google's Gemini REST `generateContent` endpoint with the `x-goog-api-key` request header.

## Project Author
**Created by:** Kushagra Ojha  
**Registration No.:** 25BAI11324  
**GitHub repository:** https://github.com/kushagraacodes/NexusAI  
**GitHub profile:** https://github.com/kushagraacodes
