package com.chat.springaichatrest.controller;

import com.chat.springaichatrest.dto.PromptDto;
import com.chat.springaichatrest.model.PromptTemplate;
import com.chat.springaichatrest.service.PromptTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PromptConfigController {

    private final PromptTemplateService promptService;

    @GetMapping("/config")
    public String showConfigPage(@RequestParam(required = false) Long selectedRagId,
                                 @RequestParam(required = false) Long selectedQeId,
                                 Model model) {

        List<PromptDto> ragPrompts = promptService.getPromptsByType("RAG")
                .stream().map(this::toDto).collect(Collectors.toList());
        List<PromptDto> qePrompts = promptService.getPromptsByType("QUERY_EXPANSION")
                .stream().map(this::toDto).collect(Collectors.toList());

        model.addAttribute("ragPrompts", ragPrompts);
        model.addAttribute("qePrompts", qePrompts);

        if (selectedRagId != null) {
            promptService.getPromptById(selectedRagId)
                    .map(this::toDto)
                    .ifPresent(dto -> model.addAttribute("selectedRag", dto));
        }

        if (selectedQeId != null) {
            promptService.getPromptById(selectedQeId)
                    .map(this::toDto)
                    .ifPresent(dto -> model.addAttribute("selectedQe", dto));
        }

        return "config";
    }

    @PostMapping("/config/update/{id}")
    public String updatePrompt(@PathVariable Long id,
                               @ModelAttribute PromptDto dto,
                               @RequestParam(required = false) String action,
                               RedirectAttributes redirectAttributes) {
        Long redirectId;

        if ("saveAsNew".equals(action)) {
            PromptTemplate newPrompt = promptService.createPrompt(dto.getType(), dto.getTitle(), dto.getContent());
            redirectId = newPrompt.getId();
        } else {
            promptService.updatePrompt(id, dto.getTitle(), dto.getContent());
            redirectId = id;
        }

        if ("RAG".equals(dto.getType())) {
            redirectAttributes.addAttribute("selectedRagId", redirectId);
        } else {
            redirectAttributes.addAttribute("selectedQeId", redirectId);
        }

        return "redirect:/config";
    }

    @PostMapping("/config/delete/{id}")
    public String deletePrompt(@PathVariable Long id) {
        promptService.deletePrompt(id);
        return "redirect:/config";
    }

    @PostMapping("/config/add")
    public String addPrompt(@ModelAttribute PromptDto dto,
                            RedirectAttributes redirectAttributes) {
        PromptTemplate newPrompt = promptService.createPrompt(dto.getType(), dto.getTitle(), dto.getContent());

        if ("RAG".equals(dto.getType())) {
            redirectAttributes.addAttribute("selectedRagId", newPrompt.getId());
        } else {
            redirectAttributes.addAttribute("selectedQeId", newPrompt.getId());
        }

        return "redirect:/config";
    }

    private PromptDto toDto(PromptTemplate entity) {
        return PromptDto.builder()
                .id(entity.getId())
                .title(entity.getName())
                .content(entity.getContent())
                .type(entity.getType())
                .build();
    }
}
