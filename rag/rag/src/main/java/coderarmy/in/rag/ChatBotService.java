package coderarmy.in.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
@Service
public class ChatBotService {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    @Value("classpath*:knowledge/*.pdf")
    private Resource[] policyFiles;
    public ChatBotService(VectorStore vectorStore, ChatClient.Builder builder){
        this.vectorStore = vectorStore;
        this.chatClient= builder.build();
    }
    @PostConstruct
    public void loadKnowledgeBase(){
        System.out.println("========== RAG INGESTION STARTED ==========");
        // 1. read all pdf file
        // 2. for each file i will divide them into chunks
        // 3. i will store those chunks in vector db
        List<Document>allChunks = new ArrayList<>();
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(300).build();
        for(Resource resource:policyFiles){
            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
            List<Document>pages = reader.read();
            List<Document>chunks = splitter.apply(pages);
            allChunks.addAll(chunks);
        }
        vectorStore.add(allChunks);
        System.out.println("========== RAG INGESTION COMPLETE ==========");
    }
    public String answerUserQuery(String question){
        //1. question --> vector
        //2. similarity search in our vector db
        //3. top 4 results fetch
        List<Document> relevantChunks = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(4)
                        .build()
        );
        StringBuilder context = new StringBuilder();
        for(Document document : relevantChunks){
            context.append(document.getText()).append("\n\n");

        }
        String prompt= """
                    You are an AI customer support assistant for our e commerce company ShopEase.
                    Answer the customer using ONLY the company information provided below.
                    If the answer is not available in the provided information, say :
                    "I don't have that information"
                    
                    Company Information
                    %s
                """.formatted(context.toString());
        return chatClient.prompt()
                .system(prompt)
                .user(question)
                .call()
                .content();
    }


}
