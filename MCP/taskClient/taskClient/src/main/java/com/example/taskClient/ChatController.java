package com.example.taskClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }
    @GetMapping("/ask")
    public String ask(@RequestParam String message){
        return chatService.chat(message);
    }
    @GetMapping("/plan")
    public String plan(@RequestParam("hours") String hours) {
        return chatService.planDay(hours);
    }
}
