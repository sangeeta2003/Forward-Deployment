package coderarmy.in.multomodal;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }
    @PostMapping("/chat")
    public String chat(@RequestParam String message, @RequestParam(required = false)MultipartFile image) throws Exception{
return chatService.chat(message,image);
    }
    @DeleteMapping("/chat")
    public void clearChat(){
        chatService.clearHistory();
    }

}
