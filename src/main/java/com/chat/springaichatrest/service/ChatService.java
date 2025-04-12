package com.chat.springaichatrest.service;

import com.chat.springaichatrest.model.Chat;
import com.chat.springaichatrest.model.ChatEntry;
import com.chat.springaichatrest.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Optional<Chat> getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public Chat createChat(String title) {
        Chat chat = Chat.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
        return chatRepository.save(chat);
    }

    public void deleteChat(Long chatId) {
        chatRepository.findById(chatId).ifPresent(chatRepository::delete);
    }

    public List<ChatEntry> getChatEntries(Long chatId) {
        return getChatById(chatId)
                .map(Chat::getChatEntries)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    public ChatEntry addChatEntry(Long chatId, String role, String content) {
        Chat chat = getChatById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        ChatEntry entry = ChatEntry.builder()
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        chat.addEntry(entry);
        chatRepository.save(chat);
        return entry;
    }
}
