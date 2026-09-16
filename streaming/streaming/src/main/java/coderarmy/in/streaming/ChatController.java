package coderarmy.in.streaming;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")

public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }
    @PostMapping("/chat")
    public Flux<String> chat(@RequestBody String message){
        return chatService.chat(message);

    }
//    @DeleteMapping
//    public void clearChat(){
//        chatService.clearHistory();
//    }
}
