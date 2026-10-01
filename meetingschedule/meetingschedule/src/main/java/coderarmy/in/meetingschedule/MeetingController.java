package coderarmy.in.meetingschedule;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MeetingController {
    private final MeetingService meetingService;
    public MeetingController(MeetingService meetingService){
        this.meetingService = meetingService;
    }
    @PostMapping("/schedule")
    public MeetingDetails(String message){
return meetingService.schedule(message);
    }
}
