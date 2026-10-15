package coderarmy.in.testing.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class EvalCase {
    private String id;

    private String question;

    private String expectedAnswer;

    private String expectedBehavior;

    private String expectedTool;

    private String expectedToolArgument;

    private String expectedSource;
}
