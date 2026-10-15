
package coderarmy.in.testing;

import coderarmy.in.testing.models.AgentResponse;
import coderarmy.in.testing.tools.DeliveryEstimateTool;
import coderarmy.in.testing.tools.OrderStatusTool;
import coderarmy.in.testing.tools.RefundAmountTool;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupportService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final OrderStatusTool orderStatusTool;
    private final DeliveryEstimateTool deliveryEstimateTool;
    private final RefundAmountTool refundAmountTool;
    private final ToolTracker toolTracker;

    @Value("classpath*:knowledge/*.txt")
    private Resource[] policyFiles;

    public SupportService(
            ChatClient.Builder builder,
            VectorStore vectorStore,
            OrderStatusTool orderStatusTool,
            DeliveryEstimateTool deliveryEstimateTool,
            RefundAmountTool refundAmountTool,
            ToolTracker toolTracker) {

        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.orderStatusTool = orderStatusTool;
        this.deliveryEstimateTool = deliveryEstimateTool;
        this.refundAmountTool = refundAmountTool;
        this.toolTracker = toolTracker;
    }

    // Load policy files into the vector store when the application starts.
    @PostConstruct
    public void loadKnowledgeBase() throws IOException {
        List<Document> documents = new ArrayList<>();

        for (Resource resource : policyFiles) {
            String content = StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            String filename = resource.getFilename();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", filename);

            Document document = new Document(content, metadata);
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }

        System.out.println(
                "Knowledge base loaded: " + documents.size() + " documents"
        );
    }

    public AgentResponse chat(String question) {

        // Start a fresh tool trace for this request.
        toolTracker.clear();

        // Retrieve the most relevant policy.
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(1)
                        .build()
        );

        // Build policy context and track retrieved sources.
        StringBuilder contextBuilder = new StringBuilder();
        List<String> retrievedDocuments = new ArrayList<>();

        for (Document document : documents) {
            String source = String.valueOf(
                    document.getMetadata().get("source")
            );

            retrievedDocuments.add(source);

            contextBuilder.append("SOURCE: ")
                    .append(source)
                    .append("\n");

            contextBuilder.append(document.getText())
                    .append("\n\n");
        }

        String context = contextBuilder.toString();

        String systemPrompt =
                "You are an e-commerce customer support agent.\n\n" +

                        "Use the supplied POLICY CONTEXT for questions about company policies.\n\n" +

                        "Use tools only when the user asks for information about a " +
                        "specific order and provides an order ID such as ORD-101.\n\n" +

                        "Do not call an order tool for general policy questions.\n\n" +

                        "If the user already provides enough information in the question, " +
                        "use that information directly instead of calling a tool.\n\n" +

                        "For example, if the user says an item was received three weeks ago, " +
                        "treat that as known information and compare it with the return policy. " +
                        "Do not call getOrderStatus unless an order ID is provided and " +
                        "order-specific information is actually required.\n\n" +

                        "Tool selection rules:\n" +
                        "1. Current status of a specific order -> getOrderStatus\n" +
                        "2. Expected delivery date of a specific order -> getDeliveryEstimate\n" +
                        "3. Refund amount for a specific order -> getRefundAmount\n\n" +

                        "If return eligibility is asked for a specific order ID, " +
                        "and delivery information is not already provided by the user, " +
                        "use getOrderStatus and combine the result with the return policy.\n\n" +

                        "Never guess order-specific information.\n\n" +

                        "The retrieved policy context may sometimes be unrelated. " +
                        "Ignore irrelevant policy information.\n\n" +

                        "POLICY CONTEXT:\n" + context;

        // Spring AI handles the tool-call loop.
        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .tools(
                        orderStatusTool,
                        deliveryEstimateTool,
                        refundAmountTool
                )
                .call()
                .content();

        // Return the answer and evaluation trace.
        return new AgentResponse(
                answer,
                retrievedDocuments,
                toolTracker.getToolsUsed(),
                toolTracker.getToolArguments(),
                toolTracker.getToolResults(),
                context
        );
    }
}
