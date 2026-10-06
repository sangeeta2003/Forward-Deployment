package com.example.taskServer;

import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskPrompt {
    @McpPrompt(
            name = "plan_day",
            description = "Plan pending tasks for the available time"
    )
    public McpSchema.GetPromptResult planDay(

            @McpArg(
                    name = "availableHours",
                    description = "Number of hours available today",
                    required = true
            )
            String availableHours
    ) {

        String message = """
                Help me plan my pending tasks for today.

                First check my current pending tasks
                using the available task tools.

                I have %s hours available today.

                Give me a concise and ordered plan.
                """.formatted(availableHours);

        var promptMessage =
                McpSchema.PromptMessage.builder(
                        McpSchema.Role.USER,
                        McpSchema.TextContent.builder(message).build()
                ).build();

        return McpSchema.GetPromptResult
                .builder(List.of(promptMessage))
                .description("Plan pending tasks for the day")
                .build();
    }
}
