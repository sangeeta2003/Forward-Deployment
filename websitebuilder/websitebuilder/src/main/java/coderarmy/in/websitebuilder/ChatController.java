package coderarmy.in.websitebuilder;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")

public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }
    @PostMapping("/chat")
    public String chat(@RequestBody String message){
        return chatService.chat(message);

    }
    @DeleteMapping
    public void clearChat(){
        chatService.clearHistory();
    }
}
