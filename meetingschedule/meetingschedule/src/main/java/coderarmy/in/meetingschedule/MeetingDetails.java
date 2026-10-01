package coderarmy.in.meetingschedule;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class MeetingDetails {
    private String title;
    private String attendee;
    private String date;
    private String time;
    private Integer durationMinutes;


}
