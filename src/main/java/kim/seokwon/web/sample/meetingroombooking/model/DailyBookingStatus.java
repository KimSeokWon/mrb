package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 일별 미팅룸 예약현황, 응답 파라미터로 사용된다.
 */
@Getter @Setter @NoArgsConstructor @ToString
@Entity
@Table(name="DAILY_BOOK", indexes = {
        @Index(name="IDX_DAILY_BOOK_REQ_ID", columnList = "REQ_ID", unique = false),
        @Index(name="IDX_DAILY_BOOK_BOOK_DT", columnList = "BOOK_DT", unique = false),
        @Index(name="IDX_DAILY_BOOK_START_TM", columnList = "START_TM", unique = false),
        @Index(name="IDX_DAILY_BOOK_END_TM", columnList = "END_TM", unique = false),
        @Index(name="IDX_DAILY_BOOK_ROOM_ID", columnList = "ROOM_ID", unique = false),
        @Index(name="IDX_DAILY_BOOK_START_TM_END_TM", columnList = "START_TM,END_TM", unique = false),
})
public class DailyBookingStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookingId;

    @Column(name="BOOK_DT")
    private     ZonedDateTime       date;
    /**
     * 시작시간
     */
    @Column(name="START_TM")
    private     int        startTime;
    /**
     * 종료시간
     */
    @Column(name="END_TM")
    private    int     endTime;
    /**
     * 예약명
     */
    @Column(name="DESC")
    private String      desc;
    /**
     * 요청 파라미터 ID
     */
    @Column(name="REQ_ID")
    private Long        requestId;
    /**
     * 회의실 ID
     */
    @Column(name="ROOM_ID")
    private Long        roomId;

    public DailyBookingStatus(ZonedDateTime date, int startTime, int endTime, String desc, Long requestId, Long roomId) {
        this.date           = date;
        this.startTime      = startTime;
        this.endTime        = endTime;
        this.desc           = desc;
        this.requestId      = requestId;
        this.roomId         = roomId;
    }
}
