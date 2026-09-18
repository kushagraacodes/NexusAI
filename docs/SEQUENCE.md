# Sequence Diagram

```text
User        ChatWindow       ChatController      GeminiService      Gemini API      ChatRepository
 |              |                  |                  |                |                 |
 |--prompt----->|                  |                  |                |                 |
 |              |--ask------------>|                  |                |                 |
 |              |                  |--validate------->|                |                 |
 |              |                  |--generate------->|                |                 |
 |              |                  |                  |--HTTP request->|                 |
 |              |                  |                  |<--JSON response|                 |
 |              |                  |<--AIResult-------|                |                 |
 |              |                  |----------------------------------------------save-->
 |              |<--display--------|                  |                |                 |
 |<--response---|                  |                  |                |                 |
```

Retryable 408/429/500/502/503/504 responses are handled inside `GeminiService`; fallback models are attempted before a final error is returned.
