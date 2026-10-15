package coderarmy.in.testing;

import coderarmy.in.testing.models.AgentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class SupportController {
    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/chat")
    public AgentResponse chat(@RequestParam String message) {
        return supportService.chat(message);
    }
}
