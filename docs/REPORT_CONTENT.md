# Project Report Content – Nexus AI

## 1. Cover Page
**Nexus AI – Gemini Conversational Assistant**

Student: Kushagra Ojha  
Registration No.: 25BAI11324  
Programme: B.Tech CSE (AI & ML)  
University: VIT Bhopal

## 2. Introduction
Nexus AI is a JavaFX desktop application that demonstrates how a modular Java program can integrate a generative-AI service while maintaining local conversation data. The application combines GUI programming, REST API communication, JSON processing, database persistence, asynchronous execution, validation, automated testing, and resilient model fallback.

The visible product name is **Nexus AI**. The underlying provider is Google Gemini. The request status intentionally uses **“JavaGPT is thinking…”** in the interface.

## 3. Problem Statement
The project provides a simple student-oriented desktop interface for interacting with a cloud AI model while demonstrating the software-engineering components required to make such an application usable and reliable.

## 4. Objectives
- Build a functional desktop conversational assistant in Java.
- Integrate Gemini through a REST API.
- Preserve conversations using SQLite.
- Implement model selection and fallback.
- Keep the GUI responsive during network calls.
- Demonstrate testing and error handling.
- Provide a polished Nexus AI user interface.

## 5. Functional Requirements
- User enters a text prompt.
- System validates the prompt.
- System sends recent conversation context to Gemini.
- System displays the response.
- System stores messages.
- User can create, load, refresh and delete conversations.
- User can choose a Gemini model.
- System handles temporary API errors using fallback models.
- System displays a loading state while the request runs.

## 6. Non-Functional Requirements
- **Usability:** clear desktop interface and visible request status.
- **Performance:** network calls execute in a background JavaFX Task.
- **Security:** API key is externalized through `GEMINI_API_KEY`.
- **Reliability:** retryable API failures trigger fallback models.
- **Maintainability:** MVC/service/repository separation.
- **Resource efficiency:** only recent conversation messages are sent as context.

## 7. System Architecture
`Nexus AI JavaFX UI → ChatController → AIService/GeminiService`

`Nexus AI JavaFX UI → ChatController → ChatRepository → SQLite`

The service layer isolates external AI communication from the user interface. The repository layer isolates database operations.

## 8. Design Diagrams
The repository contains visual-ready design diagrams and their source documentation:
- Use Case Diagram.
- Workflow Diagram.
- Sequence Diagram.
- Class Diagram.
- System Architecture Diagram.
- ER Diagram and Schema Design.

## 9. Design Decisions
### REST API
REST was selected because it makes HTTP, JSON and authentication concepts explicit in Java.

### SQLite
SQLite provides persistent local storage without requiring a separate database server.

### Layered separation
Separating UI, controller, service and repository responsibilities improves maintainability and testability.

### Automatic fallback
Temporary 429/5xx errors should not immediately terminate the user interaction. The application therefore tries configured fallback models.

### Background execution
Network requests execute in a JavaFX `Task`, keeping the GUI responsive and allowing the status indicator to show **“JavaGPT is thinking…”**.

## 10. Implementation Details
Important classes:
- `Main` – application entry point.
- `ChatWindow` – Nexus AI JavaFX user interface.
- `ChatController` – application workflow and validation.
- `GeminiService` – REST integration, context construction, retry/fallback.
- `AIResult` – structured AI response metadata.
- `ChatRepository` – CRUD operations for conversations/messages.
- `DatabaseManager` – SQLite initialization.
- `Conversation` / `Message` – data models.

## 11. API Key Architecture
The submission uses environment-based API-key configuration for the direct desktop build. This avoids placing the secret in source code or GitHub.

For a public multi-user application, embedding the key in a desktop JAR is not a secure solution because recipients can extract it. The recommended production architecture is:

`Nexus AI Desktop → Secure Backend → Gemini API`

The backend keeps the API key as a server-side secret and makes Gemini requests on behalf of users.

## 12. Screenshots / Results
Recommended final screenshots:
1. Nexus AI interface with the model selector.
2. Nexus AI response after a successful request.
3. **JavaGPT is thinking…** loading state.
4. Conversation history.
5. Model Info dialog.
6. Settings dialog.
7. IntelliJ Run Configuration showing `GEMINI_API_KEY` with the value hidden.

## 13. Testing Approach
JUnit 5 tests cover blank input validation, persistence and multi-turn context. Manual integration testing covers Demo Mode, valid API connection, fallback, network errors and conversation management.

## 14. Challenges Faced
- JavaFX dependencies initially required Maven configuration.
- Environment variables set in Terminal were not automatically available to the IntelliJ Run Configuration.
- A temporary Gemini HTTP 503 response was observed for a busy model; fallback handling was added.
- Product branding was separated from the underlying Gemini provider so the interface can be presented as Nexus AI.

## 15. Learnings
- JavaFX event-driven GUI development.
- REST API integration and JSON serialization.
- Environment-based secret management.
- SQLite persistence.
- Layered architecture and dependency inversion.
- Background tasks for network operations.
- Unit testing and defensive error handling.
- Secure architecture considerations for multi-user AI applications.

## 16. Future Enhancements
- Secure hosted backend for multi-user access without individual API keys.
- Streaming responses.
- Markdown rendering.
- Image/document upload.
- Searchable conversation history.
- Export chats to PDF/Markdown.
- User preferences and theme switching.
- Optional migration to Google's newer GenAI SDK/Interactions API.

## 17. References
- Google AI for Developers – Gemini API documentation.
- Google AI for Developers – Gemini API key and security documentation.
- JavaFX documentation.
- SQLite JDBC documentation.
- JUnit 5 documentation.

## 18. Submission Artefacts
The final project package includes the visual design diagrams in `docs/diagrams/`, normalized JavaGPT and Nexus AI screenshots in `docs/screenshots/`, and the final college submission report in `report/`.

GitHub repository: https://github.com/kushagraacodes/NexusAI
