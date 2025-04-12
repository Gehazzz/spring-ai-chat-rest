package com.chat.springaichatrest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChatEntry> chatEntries = new ArrayList<>();

    public void addEntry(ChatEntry entry) {
        chatEntries.add(entry);
        entry.setChat(this);
    }

    public void removeEntry(ChatEntry entry) {
        chatEntries.remove(entry);
        entry.setChat(null);
    }
}

