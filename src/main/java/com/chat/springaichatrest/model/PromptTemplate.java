package com.chat.springaichatrest.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prompt")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}
