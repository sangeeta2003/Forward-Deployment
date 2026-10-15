package coderarmy.in.testing.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class JudgeResult {
    private int relevance;

    private int correctness;

    private int faithfulness;

    private int helpfulness;

    private int hallucinationFree;

    private String reason;
}
