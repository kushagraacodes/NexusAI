# Testing Strategy

## Unit Tests
The project uses JUnit 5.

### Test 1 – Blank input validation
**Input:** whitespace-only prompt.
**Expected:** `Please enter a message.` and no AI request.

### Test 2 – Message persistence
**Input:** a valid question.
**Expected:** both user and assistant messages are stored in SQLite.

### Test 3 – Conversation context
**Input:** an existing user/assistant exchange followed by another question.
**Expected:** the AI service receives the previous messages plus the latest user prompt.

## Manual Integration Tests
1. Start without `GEMINI_API_KEY` → Demo Mode should appear.
2. Configure a valid key → real Gemini response should appear.
3. Select a model that temporarily returns 503/429 → application should attempt fallback models.
4. Disconnect network → application should show a connection error instead of crashing.
5. Enter blank input → no request should be sent.
6. Create a new chat → a new conversation should appear.
7. Delete a conversation → it should disappear from the history and its messages should be removed.

## Reliability Behaviour
Temporary HTTP statuses (`408`, `429`, `500`, `502`, `503`, `504`) are treated as retryable. The application tries configured fallback models before presenting a final failure message.
