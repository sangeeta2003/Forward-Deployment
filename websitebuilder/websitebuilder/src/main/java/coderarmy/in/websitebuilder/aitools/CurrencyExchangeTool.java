package coderarmy.in.websitebuilder.aitools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
@Component
public class CurrencyExchangeTool {
    private final RestClient restClient;
    public CurrencyExchangeTool(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }
    @Tool(description = "Gets the latest exchange rate " +
            "between two currencies.")
    public  String getExchangeRate(
            @ToolParam(description = "Source currency code, " +
                    "for example USD")
            String from,
            @ToolParam(description = "Target currency code," +
                    " for example INR")
            String to
    ){
        System.out.println("Currency Exchange tool called");
        return restClient.get()
                .uri("/v2/rate/{from}/{to}", from, to)
                .retrieve()
                .body(String.class);
    }
}
