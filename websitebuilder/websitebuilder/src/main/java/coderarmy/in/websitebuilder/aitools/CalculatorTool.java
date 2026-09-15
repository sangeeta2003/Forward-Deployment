package coderarmy.in.websitebuilder.aitools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {
    @Tool(description = """
            Performs arithmetic calculations.
                        Supported operations: add, subtract, multiply, divide, mod, power.""")
    public double calculate(@ToolParam(description = "Operation: add, subtract, multiply, divide, mod, power")
                            String operation,
                            @ToolParam(description = "First number")
                            int a ,
                            @ToolParam(description = "Second number")
                            int b){
        System.out.println("Calculator tool called");
        if(operation.equals("add")){
            return a+b;

        }
        else if(operation.equals("subtract")){
            return a-b;
        }
        else if(operation.equals("divide")){
            if(b== 0){
                throw new IllegalArgumentException(("Cannot divide by zero"));
            }
            return a / b;
        }
        else if(operation.equals("multiply")){
            return a * b;
        }
        else if(operation.equals("mod")){
            if(b==0){
                throw new IllegalArgumentException("Cannot calculate mod by 0");
            }
        }
        else if(operation.equals("power")){
            return Math.pow(a,b);
        }else{
            throw new IllegalArgumentException("Unsupported operation " + operation);
        }
        return 0;

    }
}
