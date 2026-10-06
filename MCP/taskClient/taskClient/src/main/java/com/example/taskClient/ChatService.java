package com.example.taskClient;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final McpSyncClient mcpClient;

    public ChatService(
            ChatClient.Builder builder,
            ToolCallbackProvider mcpTools,
            List<McpSyncClient> mcpClients) {

        this.chatClient = builder
                .defaultTools(mcpTools)
                .build();

        this.mcpClient = mcpClients.get(0);
    }

    public String chat(String message) {

        String guidelines = readTaskGuidelines();

        String systemPrompt =
                """
                You are a task management assistant.

                Use the following task management guidelines
                whenever they are relevant to the user's question.

                TASK GUIDELINES:

                %s
                """.formatted(guidelines);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
    }

    private String readTaskGuidelines() {

        McpSchema.ReadResourceResult result =
                mcpClient.readResource(
                        McpSchema.ReadResourceRequest
                                .builder("task://guidelines")
                                .build()
                );

        McpSchema.TextResourceContents content =
                (McpSchema.TextResourceContents)
                        result.contents().getFirst();

        return content.text();
    }

    public String planDay(String hours) {

        McpSchema.GetPromptResult result =
                mcpClient.getPrompt(
                        McpSchema.GetPromptRequest
                                .builder("plan_day")
                                .arguments(Map.of("availableHours", hours))
                                .build()
                );

        McpSchema.TextContent content =
                (McpSchema.TextContent)
                        result.messages()
                                .getFirst()
                                .content();

        String generatedPrompt = content.text();

        return chatClient.prompt()
                .user(generatedPrompt)
                .call()
                .content();
    }
}