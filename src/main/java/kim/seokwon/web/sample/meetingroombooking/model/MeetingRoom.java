package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@Table(name="MEETING_ROOM")
public class MeetingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingRoomId;

    /**
     * 회의실명
     */
    @Column(name="MEETING_ROOM_NM")
    private String name;

    public MeetingRoom(String name) {
        this.name = name;
    }
}
