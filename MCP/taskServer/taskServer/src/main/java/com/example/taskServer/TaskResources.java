package com.example.taskServer;

import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

@Component
public class TaskResources {
    @McpResource(
            uri = "task://guidelines",
            name = "task-guidelines",
            description = "Guidelines for managing and prioritizing tasks",
            mimeType = "text/plain"
    )
    public String getGuidelines(){
        return """
                 Task Management Guidelines:
                
                                1. Deep work tasks should preferably be done before 12 PM.
                                2. Meetings should preferably be scheduled after 2 PM.
                                3. Urgent tasks should be completed on the same day.
                                4. Keep a maximum of three high-priority tasks per day.
                                5. Complete high-priority tasks before low-priority tasks.
                """;

    }
}
