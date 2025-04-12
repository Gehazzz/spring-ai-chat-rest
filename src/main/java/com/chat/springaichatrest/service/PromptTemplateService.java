package com.chat.springaichatrest.service;

import com.chat.springaichatrest.model.PromptTemplate;
import com.chat.springaichatrest.repository.PromptTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromptTemplateService {

    private final PromptTemplateRepository promptRepository;

    public List<PromptTemplate> getPromptsByType(String type) {
        return promptRepository.findByType(type);
    }

    public Optional<PromptTemplate> getPromptById(Long id) {
        return promptRepository.findById(id);
    }

    public PromptTemplate createPrompt(String type, String name, String content) {
        PromptTemplate prompt = PromptTemplate.builder()
                .type(type)
                .name(name)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        return promptRepository.save(prompt);
    }

    public PromptTemplate updatePrompt(Long id, String name, String content) {
        return promptRepository.findById(id)
                .map(prompt -> {
                    prompt.setName(name);
                    prompt.setContent(content);
                    return promptRepository.save(prompt);
                })
                .orElseThrow(() -> new IllegalArgumentException("Prompt not found with ID: " + id));
    }

    public boolean deletePrompt(Long id) {
        if (promptRepository.existsById(id)) {
            promptRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
