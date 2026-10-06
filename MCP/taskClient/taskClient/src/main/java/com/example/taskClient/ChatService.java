package com.example.taskClient;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final McpAsyncClient mcpAsyncClient;

    public ChatService(
            ChatClient.Builder builder,
            ToolCallbackProvider mcpTools,
            List<McpAsyncClient> mcpAsyncClients) {

        this.chatClient = builder
                .defaultTools(mcpTools)
                .build();

        this.mcpAsyncClient = mcpAsyncClients.get(0);
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

    // Read Resource from MCP Server
    private String readTaskGuidelines() {

        McpSchema.ReadResourceResult result =
                mcpAsyncClient.readResource(
                        McpSchema.ReadResourceRequest
                                .builder("task://guidelines")
                                .build()
                ).block();

        if (result == null || result.contents().isEmpty()) {
            return "";
        }

        McpSchema.TextResourceContents content =
                (McpSchema.TextResourceContents)
                        result.contents().get(0);

        return content.text();
    }

    // Get Prompt from MCP Server
    public String planDay(String hours) {

        McpSchema.GetPromptResult result =
                mcpAsyncClient.getPrompt(
                        McpSchema.GetPromptRequest
                                .builder("plan_day")
                                .arguments(
                                        Map.of(
                                                "availableHours",
                                                hours
                                        )
                                )
                                .build()
                ).block();

        if (result == null || result.messages().isEmpty()) {
            return "";
        }

        McpSchema.TextContent content =
                (McpSchema.TextContent)
                        result.messages()
                                .get(0)
                                .content();

        String generatedPrompt = content.text();

        return chatClient.prompt()
                .user(generatedPrompt)
                .call()
                .content();
    }
}