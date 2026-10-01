package coderarmy.in.meetingschedule;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MeetingService {
    private ChatClient chatClient;
    public MeetingService(ChatClient chatClient){
        this.chatClient = chatClient;
    }
    public MeetingDetails schedule(String message){
        String systemPrompt = """
                You extract meeting information from the user's request.

                        Today's date is %s.

                        Rules:

                        - Convert relative dates like today and tomorrow
                          into yyyy-MM-dd format.

                        - Convert time into 24-hour HH:mm format.

                        - If title is missing, create a simple title.

                        - If duration is missing, use 30 minutes.

                        - Do not invent attendee, date or time.

                        - If information is missing, keep it blank.
                """.formatted(LocalDate.now());
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .entity(MeetingDetails.class);
    }
}
