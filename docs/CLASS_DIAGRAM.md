# Class Diagram

```text
+-------------------+
|   <<interface>>   |
|     AIService     |
+---------^---------+
          |
+---------+---------+
|    GeminiService  |
+-------------------+

+-------------+       +----------------+
| ChatWindow  |------>| ChatController |
+-------------+       +-------+--------+
                              |
                  +-----------+-----------+
                  |                       |
                  v                       v
          +---------------+       +---------------+
          |  AIService    |       | ChatRepository|
          +---------------+       +-------+-------+
                                          |
                                          v
                                  +---------------+
                                  |DatabaseManager|
                                  +---------------+

+--------------+       +-------------+
| Conversation |       |   Message   |
+--------------+       +-------------+

ChatRepository --> Conversation
ChatRepository --> Message
AIService --> AIResult
ChatController --> AIResult
Main --> ChatWindow
```

The diagram reflects the actual final source structure and uses `GeminiService`, not the earlier OpenAI-compatible implementation.
