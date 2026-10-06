package com.example.taskClient;

import io.modelcontextprotocol.client.McpAsyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private final McpAsyncClient mcpAsyncClient;
    public ChatService(ChatClient.Builder builder, ToolCallbackProvider mcpTools, List<McpAsyncClient>mcpAsyncClients){
        this.chatClient = builder.defaultTools(mcpTools).build();
        this.mcpAsyncClient = mcpAsyncClients.get(0);
    }
    public String chat(String message){
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
