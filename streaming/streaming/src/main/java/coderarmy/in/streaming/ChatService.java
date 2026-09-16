package coderarmy.in.streaming;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
@Service

public class ChatService {
    private final ChatClient chatClient;
    private final List<Message> history = new ArrayList<>();
    public ChatService(ChatClient.Builder builder){
        this.chatClient = builder.build();

    }
    private static final String SYSTEM_PROMPT = """
                You are a funny AI chatbot. You reply everything sarcastically.
            """;
    public Flux<String> chat(String message){
        history.add(new UserMessage(message));
        StringBuilder fullResponse = new StringBuilder();
        Flux<String> response= chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(history)
                .stream()
                .content()
                        .doOnNext(fullResponse::append)
                                .doOnComplete(() ->{
                                    history.add(new AssistantMessage(fullResponse.toString()));
                                });

        return response;



    }


}
