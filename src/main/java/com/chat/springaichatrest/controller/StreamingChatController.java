package com.chat.springaichatrest.controller;

import com.chat.springaichatrest.model.Chat;
import com.chat.springaichatrest.model.ChatEntry;
import com.chat.springaichatrest.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat-stream")
public class StreamingChatController {

    private final ChatClient chatClient;
    private final ChatService chatService;

    public StreamingChatController(ChatClient.Builder chatClientBuilder, ChatService chatService) {
        this.chatClient = chatClientBuilder.build();
        this.chatService = chatService;
    }

    @GetMapping(value = "/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@PathVariable Long chatId,
                           @RequestParam String prompt,
                           @RequestParam(defaultValue = "gemma3:4b-it-q4_K_M") String model) {

        Chat chat = chatService.getChatById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        List<ChatEntry> history = chat.getChatEntries().stream()
                .sorted(Comparator.comparing(ChatEntry::getTimestamp))
                .toList();

        chatService.addChatEntry(chatId, "user", prompt);

        List<Message> fullHistory = history.stream()
                .map(entry -> "user".equals(entry.getRole())
                        ? new UserMessage(entry.getContent())
                        : new AssistantMessage(entry.getContent()))
                .collect(Collectors.toList());

        fullHistory.add(new UserMessage(prompt));

        SseEmitter emitter = new SseEmitter(0L);
        StringBuilder fullResponse = new StringBuilder();

        chatClient.prompt()
                .messages(fullHistory)
                .stream()
                .chatResponse()
                .subscribe(
                        value -> {
                            try {
                                AssistantMessage text = value.getResult().getOutput();
                                System.out.println("Received token: " + text);
                                fullResponse.append(text.getText());
                                emitter.send(SseEmitter.event().data(text));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        emitter::completeWithError,
                        () -> {
                            // Сохраняем весь ответ ассистента после завершения
                            chatService.addChatEntry(chatId, "assistant", fullResponse.toString()); // or entryRepo.save(...)
                            emitter.complete();
                        }
                );

        return emitter;
    }

}
