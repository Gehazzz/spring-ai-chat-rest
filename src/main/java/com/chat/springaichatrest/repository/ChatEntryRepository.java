package com.chat.springaichatrest.repository;

import com.chat.springaichatrest.model.ChatEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatEntryRepository extends JpaRepository<ChatEntry, Long> {
    List<ChatEntry> findByChatIdOrderByTimestampAsc(Long chatId);
}
