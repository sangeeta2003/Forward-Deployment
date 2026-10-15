package coderarmy.in.testing.tools;

import coderarmy.in.testing.ToolTracker;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Component

public class RefundAmountTool {
    private final ToolTracker toolTracker;

    public RefundAmountTool(ToolTracker toolTracker) {
        this.toolTracker = toolTracker;
    }

    @Tool(description = """
            Get the approved refund amount for an order.
            Use this when the user asks how much money
            will be refunded.
           """
    )
    public String getRefundAmount(
            @ToolParam(description = "Order ID such as ORD-103") String orderId) {

        String normalizedOrderId = orderId.trim().toUpperCase();
        String result;

        if ("ORD-103".equalsIgnoreCase(normalizedOrderId)) {
            result = "The approved refund amount for ORD-103 is INR 4999.";
        }

        else if ("ORD-101".equalsIgnoreCase(normalizedOrderId)) {
            result = "No refund has been approved for ORD-101.";
        }

        else if ("ORD-102".equalsIgnoreCase(normalizedOrderId)) {
            result = "No refund has been approved for ORD-102.";
        }
        else {
            result = "Refund information not found.";
        }

        toolTracker.record("getRefundAmount", normalizedOrderId, result);
        return result;
    }
}
