package coderarmy.in.testing.tools;
import coderarmy.in.testing.ToolTracker;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component

public class OrderStatusTool {
    private final ToolTracker toolTracker;

    public OrderStatusTool(ToolTracker toolTracker) {
        this.toolTracker = toolTracker;
    }

    @Tool(description = """
           Get the current status of an order.
           Use this only when the user asks whether the order
           is shipped, delivered, cancelled or its current status.
           Do not use this for expected delivery dates.
          """
    )
    public String getOrderStatus(
            @ToolParam(description = "Order ID such as ORD-101") String orderId) {

        String normalizedOrderId = orderId.trim().toUpperCase();
        String result;

        // hardcoded vales.
        if ("ORD-101".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-101 status is SHIPPED. It has not been delivered yet.";
        }

        else if ("ORD-102".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-102 status is DELIVERED. It was delivered 20 days ago.";
        }

        else if ("ORD-103".equalsIgnoreCase(normalizedOrderId)) {
            result = "Order ORD-103 status is DELIVERED. A refund has been approved.";
        }
        else {
            result = "Order not found.";
        }

        toolTracker.record("getOrderStatus", normalizedOrderId, result);
        return result;
    }
}
