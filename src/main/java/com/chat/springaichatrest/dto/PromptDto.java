package com.chat.springaichatrest.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromptDto {
    private Long id;
    private String type;
    private String title;
    private String content;
    private boolean selected;
}


