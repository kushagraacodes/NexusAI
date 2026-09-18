package com.javapowered.nexusai.repository;

import com.javapowered.nexusai.database.DatabaseManager;
import com.javapowered.nexusai.model.Conversation;
import com.javapowered.nexusai.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    private final DatabaseManager db;
    public ChatRepository(DatabaseManager db) { this.db = db; }

    public long createConversation(String title) {
        String sql = "INSERT INTO conversations(title,created_at) VALUES(?,?)";
        try (Connection c = db.connect(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, title); p.setLong(2, System.currentTimeMillis()); p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { return r.next() ? r.getLong(1) : -1; }
        } catch (SQLException e) { throw new RuntimeException("Could not create conversation", e); }
    }

    public List<Conversation> listConversations() {
        List<Conversation> out = new ArrayList<>();
        try (Connection c = db.connect(); PreparedStatement p = c.prepareStatement(
                "SELECT id,title,created_at FROM conversations ORDER BY id DESC");
             ResultSet r = p.executeQuery()) {
            while (r.next()) out.add(new Conversation(r.getLong(1), r.getString(2), r.getLong(3)));
        } catch (SQLException e) { throw new RuntimeException("Could not load conversations", e); }
        return out;
    }

    public void saveMessage(long cid, String role, String content) {
        try (Connection c = db.connect(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO messages(conversation_id,role,content,created_at) VALUES(?,?,?,?)")) {
            p.setLong(1, cid); p.setString(2, role); p.setString(3, content);
            p.setLong(4, System.currentTimeMillis()); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Could not save message", e); }
    }

    public List<Message> getMessages(long cid) {
        List<Message> out = new ArrayList<>();
        try (Connection c = db.connect(); PreparedStatement p = c.prepareStatement(
                "SELECT id,conversation_id,role,content,created_at FROM messages WHERE conversation_id=? ORDER BY id")) {
            p.setLong(1, cid);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) out.add(new Message(r.getLong(1), r.getLong(2), r.getString(3), r.getString(4), r.getLong(5)));
            }
        } catch (SQLException e) { throw new RuntimeException("Could not load messages", e); }
        return out;
    }

    public void deleteConversation(long cid) {
        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement messages = c.prepareStatement("DELETE FROM messages WHERE conversation_id=?")) {
                messages.setLong(1, cid); messages.executeUpdate();
            }
            try (PreparedStatement conversation = c.prepareStatement("DELETE FROM conversations WHERE id=?")) {
                conversation.setLong(1, cid); conversation.executeUpdate();
            }
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete conversation", e);
        }
    }
}
