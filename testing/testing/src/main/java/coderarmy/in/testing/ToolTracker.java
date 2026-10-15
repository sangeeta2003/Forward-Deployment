package coderarmy.in.testing;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component

public class ToolTracker {
    private final List<String> toolsUsed = new ArrayList<String>();
    private final Map<String, String> toolArguments = new LinkedHashMap<String, String>();
    private final Map<String, String> toolResults = new LinkedHashMap<String, String>();

    public void record(String toolName, String argument, String result) {

        if (!toolsUsed.contains(toolName)) {
            toolsUsed.add(toolName);
        }
        toolArguments.put(toolName, argument);
        toolResults.put(toolName, result);
    }

    public void clear() {
        toolsUsed.clear();
        toolArguments.clear();
        toolResults.clear();
    }

    public List<String> getToolsUsed() {
        return new ArrayList<String>(toolsUsed);
    }

    public Map<String, String> getToolArguments() {
        return new LinkedHashMap<String, String>(toolArguments);
    }

    public Map<String, String> getToolResults() {
        return new LinkedHashMap<String, String>(toolResults);
    }
}
