package coderarmy.in.testing;


import coderarmy.in.testing.models.AgentResponse;
import coderarmy.in.testing.models.EvalCase;
import coderarmy.in.testing.models.JudgeResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.io.ClassPathResource;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;


import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class SupportAgentEvalTest {
    @Autowired
    private SupportService supportService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    public void runGoldenDataset() throws Exception {

        // Load Golden DataSet
        InputStream inputStream = new ClassPathResource("golden-dataset.json").getInputStream();
        List<EvalCase> evalCases = jsonMapper.readValue(inputStream, new TypeReference<List<EvalCase>>() {});
        inputStream.close();

        // Separate ChatClient for our judge.
        ChatClient judgeClient = chatClientBuilder.build();

        // Store failed cases.
        List<String> failures = new ArrayList<String>();

        int relevanceTotal = 0;
        int correctnessTotal = 0;
        int faithfulnessTotal = 0;
        int helpfulnessTotal = 0;
        int hallucinationFreeTotal = 0;

        // STEP 2: Run every golden test.
        for (EvalCase evalCase : evalCases) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("TEST: " + evalCase.getId());
            System.out.println("QUESTION: " + evalCase.getQuestion());

            // STEP 3: Run actual AI application
            AgentResponse response = supportService.chat(evalCase.getQuestion());

            // STEP 4: Deterministic evaluations.
            boolean responsePass = response.getAnswer() != null
                    &&
                    !response.getAnswer().trim().isEmpty();

            boolean toolPass;

            if (evalCase.getExpectedTool() == null) {
                toolPass = response.getToolsUsed().isEmpty();
            } else {
                toolPass = response.getToolsUsed().contains(evalCase.getExpectedTool());
            }

            boolean argumentPass = true;

            if (evalCase.getExpectedTool() != null) {
                String actualArgument = response.getToolArguments().get(evalCase.getExpectedTool());

                argumentPass = evalCase.getExpectedToolArgument().equals(actualArgument);
            }

            boolean sourcePass = true;

            if (evalCase.getExpectedSource() != null) {
                sourcePass = response.getRetrievedDocuments().contains(evalCase.getExpectedSource());
            }

            boolean deterministicPass = responsePass && toolPass && argumentPass && sourcePass;

            // STEP 5: Quality evaluation.
            JudgeResult judgeResult = judge(judgeClient, evalCase, response);

            // We choose 4/5 as our demo quality threshold.
            boolean qualityPass = judgeResult.getRelevance() >= 4 &&
                    judgeResult.getCorrectness() >= 4 &&
                    judgeResult.getFaithfulness() >= 4 &&
                    judgeResult.getHelpfulness() >= 4 &&
                    judgeResult.getHallucinationFree() >= 4;


            boolean finalPass = deterministicPass && qualityPass;

            // STEP 6: Print report.
            System.out.println();
            System.out.println("DETERMINISTIC EVALS");
            System.out.println("Response exists: " + passFail(responsePass));
            System.out.println("Correct tool: " + passFail(toolPass));
            System.out.println("Correct tool argument: " + passFail(argumentPass));
            System.out.println("Expected source retrieved: " + passFail(sourcePass));
            System.out.println();
            System.out.println("QUALITY EVALS");
            System.out.println("Relevance: " + judgeResult.getRelevance() + "/5");
            System.out.println("Correctness: " + judgeResult.getCorrectness() + "/5");
            System.out.println("Faithfulness: " + judgeResult.getFaithfulness() + "/5");
            System.out.println("Helpfulness: " + judgeResult.getHelpfulness() + "/5");
            System.out.println("Hallucination Free: " + judgeResult.getHallucinationFree() + "/5");
            System.out.println("Judge Reason: " + judgeResult.getReason());
            System.out.println();
            System.out.println("FINAL: " + passFail(finalPass));

            // STEP 7: Aggregate scores.
            relevanceTotal += judgeResult.getRelevance();
            correctnessTotal += judgeResult.getCorrectness();
            faithfulnessTotal += judgeResult.getFaithfulness();
            helpfulnessTotal += judgeResult.getHelpfulness();
            hallucinationFreeTotal += judgeResult.getHallucinationFree();

            if (!finalPass) {
                failures.add(evalCase.getId());
            }
        }

        // STEP 8: Overall report.
        int totalTests = evalCases.size();

        System.out.println();
        System.out.println();
        System.out.println("========================================");
        System.out.println("FINAL EVALUATION REPORT");
        System.out.println("========================================");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + (totalTests - failures.size()));
        System.out.println("Failed: " + failures.size());
        System.out.println();
        System.out.println("Average Relevance: " + average(relevanceTotal, totalTests));
        System.out.println("Average Correctness: " + average(correctnessTotal, totalTests));
        System.out.println("Average Faithfulness: " + average(faithfulnessTotal, totalTests));
        System.out.println("Average Helpfulness: " + average(helpfulnessTotal, totalTests));
        System.out.println("Average Hallucination Free: " + average(hallucinationFreeTotal, totalTests));


        // STEP 9: Finally fail the JUnit test
        Assertions.assertTrue(failures.isEmpty(), "Failed Eval Cases: " + failures);
    }

    private JudgeResult judge(ChatClient judgeClient, EvalCase evalCase, AgentResponse response) {
        String prompt = "Evaluate the AI answer below.\n\n" +

                "USER QUESTION:\n" +
                evalCase.getQuestion() +
                "\n\n" +

                "EXPECTED ANSWER:\n" +
                evalCase.getExpectedAnswer() +
                "\n\n" +

                "EXPECTED BEHAVIOR:\n" +
                evalCase.getExpectedBehavior() +
                "\n\n" +

                "RETRIEVED POLICY CONTEXT:\n" +
                response.getContext() +
                "\n\n" +

                "TOOL RESULTS:\n" +
                response.getToolResults().toString() +
                "\n\n" +

                "ACTUAL ANSWER:\n" +
                response.getAnswer() +
                "\n\n" +

                "Score the answer from 1 to 5 " +
                "for each metric.\n\n" +

                "RELEVANCE:\n" +
                "5 = directly answers the question.\n" +
                "4 = mostly answers it.\n" +
                "3 = partially answers it.\n" +
                "2 = barely related.\n" +
                "1 = irrelevant.\n\n" +

                "CORRECTNESS:\n" +
                "5 = fully consistent with expected answer and behavior.\n" +
                "4 = mostly correct with a minor issue.\n" +
                "3 = partially correct.\n" +
                "2 = major errors.\n" +
                "1 = substantially incorrect.\n\n" +

                "FAITHFULNESS:\n" +
                "5 = every factual claim is supported by policy context " +
                "or tool results.\n" +
                "4 = almost fully supported.\n" +
                "3 = meaningful unsupported claims exist.\n" +
                "2 = large parts are unsupported.\n" +
                "1 = contradicts or ignores the evidence.\n\n" +

                "HELPFULNESS:\n" +
                "5 = clear, complete and useful.\n" +
                "4 = useful with a minor limitation.\n" +
                "3 = somewhat useful.\n" +
                "2 = minimally useful.\n" +
                "1 = not useful.\n\n" +

                "HALLUCINATION FREE:\n" +
                "5 = no fabricated or unsupported claims.\n" +
                "4 = insignificant unsupported detail.\n" +
                "3 = some unsupported information.\n" +
                "2 = substantial unsupported information.\n" +
                "1 = heavily fabricated.\n\n" +

                "Use ONLY the expected answer, expected behavior, " +
                "policy context and tool results as evidence. " +
                "Do not use outside knowledge.";


        return judgeClient.prompt()
                .system("You are a strict evaluator of AI application responses.")
                .user(prompt)
                .call()
                .entity(JudgeResult.class);
    }

    private String passFail(boolean value) {
        return value ? "PASS" : "FAIL";
    }

    private double average(int total, int count) {
        return Math.round(((double) total / count) * 100.0) / 100.0;
    }
}
