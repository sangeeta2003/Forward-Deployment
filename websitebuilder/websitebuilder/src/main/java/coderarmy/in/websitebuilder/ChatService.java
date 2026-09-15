package coderarmy.in.websitebuilder;

import coderarmy.in.websitebuilder.aitools.CalculatorTool;
import coderarmy.in.websitebuilder.aitools.CurrencyExchangeTool;
import coderarmy.in.websitebuilder.aitools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ChatService {
    private final ChatClient chatClient;
    private final CalculatorTool calculatorTool;
    private final WeatherTool weatherTool;
    private final CurrencyExchangeTool currencyExchangeTool;
    private final List<Message> history = new ArrayList<>();

    public ChatService(ChatClient.Builder builder,
                       CalculatorTool calculatorTool,
                       WeatherTool weatherTool,
                       CurrencyExchangeTool currencyExchangeTool) {
        this.chatClient = builder.build();
        this.calculatorTool = calculatorTool;
        this.weatherTool = weatherTool;
        this.currencyExchangeTool = currencyExchangeTool;

    }

    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant with access to external tools.
            
            Follow these rules:
            1. For arithmetic calculations, ALWAYS use the calculator tool.
            2. Always use calculator tool for even trivial calculation
            3. For current weather, ALWAYS use the currentWeather tool.
            4. For currency conversion or exchange rates, ALWAYS use the convertCurrency tool.
            5. You may call multiple tools when solving a multi-step request.
            6. After receiving tool results, explain the answer naturally.
            7. Never invent current weather or exchange-rate information.
            """;

    public String chat(String message) {
        history.add(new UserMessage(message));
        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(history)
                .call()
                .content();
        history.add(new AssistantMessage(response));
        return response;


    }

    public void clearHistory() {
        history.clear();
    }
}
