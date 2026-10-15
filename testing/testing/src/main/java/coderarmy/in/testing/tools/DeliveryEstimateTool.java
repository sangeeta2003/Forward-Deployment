package coderarmy.in.testing.tools;

import coderarmy.in.testing.ToolTracker;
import org.springframework.stereotype.Component;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEstimateTool {
    private final ToolTracker toolTracker;

    public DeliveryEstimateTool(ToolTracker toolTracker) {
        this.toolTracker = toolTracker;
    }

    @Tool(description = """
            Get the expected delivery date for an order.
            Use this when the user asks when the order will arrive,
            when it will be delivered, or asks for a delivery estimate.
           """
    )
    public String getDeliveryEstimate(
            @ToolParam(description ="Order ID such as ORD-101") String orderId) {

        String normalizedOrderId = orderId.trim().toUpperCase();
        String result;

        if ("ORD-101".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-101 is expected to be delivered on 15 October 2026.";
        }

        else if ("ORD-102".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-102 has already been delivered.";
        }

        else if ("ORD-103".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-103 has already been delivered.";
        }
        else {
            result = "Delivery information not found.";
        }

        toolTracker.record("getDeliveryEstimate", normalizedOrderId, result);

        return result;
    }
}
