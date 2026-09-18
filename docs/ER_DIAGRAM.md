# ER Diagram and Schema

```text
+-----------------------------+
|        CONVERSATIONS        |
+-----------------------------+
| PK id                       |
|    title                   |
|    created_at              |
+--------------+--------------+
               | 1
               |
               | N
               v
+-----------------------------+
|           MESSAGES          |
+-----------------------------+
| PK id                       |
| FK conversation_id          |
|    role                     |
|    content                  |
|    created_at               |
+-----------------------------+
```

## Schema

### `conversations`
- `id` – primary key
- `title` – conversation title
- `created_at` – creation timestamp

### `messages`
- `id` – primary key
- `conversation_id` – foreign key to `conversations.id`
- `role` – `user` or `assistant`
- `content` – message text
- `created_at` – message timestamp

Relationship: one conversation contains many messages.
