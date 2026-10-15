package coderarmy.in.testing.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter

public class AgentResponse {
    private String answer;

    private List<String> retrievedDocuments;

    private List<String> toolsUsed;

    private Map<String, String> toolArguments;

    private Map<String, String> toolResults;

    private String context;
}
