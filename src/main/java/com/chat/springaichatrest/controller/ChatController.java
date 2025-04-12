package com.chat.springaichatrest.controller;

import com.chat.springaichatrest.model.Chat;
import com.chat.springaichatrest.service.ChatService;
import com.chat.springaichatrest.model.ChatEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/")
    public String chat(Model model) {
        List<Chat> chats = chatService.getAllChats();
        model.addAttribute("chats", chats);
        return "chat"; // шаблон chat-list.html
    }

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable Long chatId, Model model) {
        Chat chat = chatService.getChatById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        List<ChatEntry> chatHistory = chat.getChatEntries();
        model.addAttribute("chat", chat);
        model.addAttribute("chats", chatService.getAllChats());
        model.addAttribute("chatHistory", chatHistory);
        return "chat"; // шаблон chat.html
    }

    @PostMapping("/chat/new")
    public String createChat(@RequestParam String title, RedirectAttributes redirectAttributes) {
        Chat chat = chatService.createChat(title);
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return "redirect:/";
    }

    @PostMapping("/chat/{chatId}/entry")
    public String addChatEntry(@PathVariable Long chatId,
                               @RequestParam String role,
                               @RequestParam String content) {
        chatService.addChatEntry(chatId, role, content);
        return "redirect:/chat/" + chatId;
    }
}
