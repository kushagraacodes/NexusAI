# Project Statement – Nexus AI

## Problem Statement
Students and general users often need a simple conversational AI interface for learning, coding assistance, explanations and everyday questions. Nexus AI addresses this by providing a modular JavaFX desktop assistant that communicates with Google's Gemini API, maintains local conversation history, supports model selection, and handles temporary API failures through automatic model fallback.

## Scope
The project covers:
- Text-based conversational interaction.
- Multi-turn conversation context.
- Gemini model selection.
- Local SQLite persistence.
- Conversation creation, loading and deletion.
- API key management through environment variables.
- Retry/fallback handling for temporary service failures.
- GUI validation and status feedback.
- Nexus AI branding with the requested “JavaGPT is thinking…” request status.

Out of scope for the current direct-desktop version:
- Image/audio input.
- User accounts and cloud synchronization.
- Training or fine-tuning a custom ML model.
- A hosted shared backend for multi-user API-key management.

## Target Users
- Students learning Java, AI and software engineering.
- Developers experimenting with Gemini API integration.
- Users who want a lightweight desktop conversational assistant.

## High-Level Features
1. Chat with Gemini through Nexus AI.
2. Select a Gemini model.
3. Automatic fallback when a selected model is temporarily unavailable.
4. Store and reload conversations locally.
5. Delete and refresh conversations.
6. Run in Demo Mode without an API key.
7. Display request status, including “JavaGPT is thinking…”.
8. Keep the API key outside the source code.
