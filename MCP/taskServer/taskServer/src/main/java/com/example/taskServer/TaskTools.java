package com.example.taskServer;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class TaskTools {
    private final List<String> tasks = new ArrayList<>();
    @McpTool(
            name = "create_task",
            description = "Create a new task"
    )
    public String createTask(@McpToolParam(
            description = "Title of the task",
            required = true
    ) String title){
        tasks.add(title);
        return "Task created : " + title;

    }
    @McpTool(
            name = "list_task",
            description = "List all pending task"
    )
    public List<String> listTask(){
        return List.copyOf(tasks);
    }
    @McpTool(
            name = "complete_task",
            description = "Complete a task using its exact title"
    )
    public String completeTask(String title){
        boolean removed = tasks.remove(title);
        if(removed){
            return "Task completed : "+ title;

        }
        return "Task not found: " + title;
    }
}
