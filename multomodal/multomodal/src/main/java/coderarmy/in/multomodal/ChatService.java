package coderarmy.in.multomodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private ChatClient chatClient;
    private final List<Message> history = new ArrayList<>();
    public ChatService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }
    public String chat(String message, MultipartFile image) throws Exception{
        UserMessage userMessage;
        if(image != null && !image.isEmpty()){
            byte[] imageBytes = image.getBytes();
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            Media media = new Media(
                    MimeTypeUtils.parseMimeType(image.getContentType()),
                    imageResource
            );
            userMessage = UserMessage.builder()
                    .text(message)
                    .media(media)
                    .build();
        }else{
            userMessage = new UserMessage(message);
        }
        history.add(userMessage);
        String systemPrompt =
                "You are a helpful AI assistant. Answer questions clearly and simply.";
        String response = chatClient.prompt()
                .system(systemPrompt)
                .messages(history)
                .call()
                .content();
        history.add(new AssistantMessage(response));
        return response;
    }
    public void clearHistory(){
        history.clear();
    }
}
