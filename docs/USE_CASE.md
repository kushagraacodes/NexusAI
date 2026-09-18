# Use Case Diagram

```text
                         +-----------------------+
                         |         User          |
                         +-----------+-----------+
                                     |
        +----------------------------+-----------------------------+
        |             |              |              |               |
        v             v              v              v               v
   +---------+   +---------+   +-----------+   +---------+   +-------------+
   | New Chat|   | Send    |   | Select    |   | Manage  |   | View Model  |
   |         |   | Prompt  |   | Model     |   | History |   | / Settings  |
   +---------+   +----+----+   +-----------+   +---------+   +-------------+
                      |
                      v
                +-------------+
                | View AI     |
                | Response    |
                +-------------+
                      |
                      v
                +-------------+
                | Handle API  |
                | Errors /    |
                | Fallback    |
                +-------------+
```

The primary actor is the user. The use cases cover the application's major functional modules and the main interaction workflow.
