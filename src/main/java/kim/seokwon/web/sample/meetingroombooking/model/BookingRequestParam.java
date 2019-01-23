package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * 데이터베이스에 저장되는 예약 정보
 * @author KimSeokWon
 */
@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@Table(name="ROOM_PARAM")
public class BookingRequestParam implements Serializable {
    private final static FastDateFormat dateFormat      = FastDateFormat.getInstance("yyyyMMdd");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private     Long        requestId;

    @Column(name="FROM_DT")
    private     Date        fromDate;
    /**
     * 종료일 YYYYMMDD<br/>
     * 시작일과 종료일이 다르고 반복 패턴이 없으면 매일 반복이다.
     */
    @Column(name="TO_DT")
    private     Date        toDate;

    /**
     * 0시 0분 부터 30분 단위로 계산된 정수값을 가짐.
     */
    @Column(name="START_TM")
    private     int      startTime;
    /**
     * 사용 기간(분)
     */
    @Column(name="DURATION")
    private     int         duration;
    /**
     * 시작일 YYYYMMDD
     */
    @Transient
    private     String        from;
    /**
     * 종료일 YYYYMMDD<br/>
     * 시작일과 종료일이 다르고 반복 패턴이 없으면 매일 반복이다.
     */
    @Transient
    private     String        to;

    /**
     * 반복 패턴은 Cron expression 으로 저장한다.<br/>
     * 반복이 없을때는 null 을 갖는다.
     * <p>
     *     cron expression 설명 :<br/>
     *     <table>
     *         <thead>
     *             <tr><th>초</th><th>분</th><th>시간</th><th>일</th><th>월</th><th>요일</th><th>년</th></tr>
     *         </thead>
     *         <tbody>
     *             <tr><td>0</td><td>0</td><td>0</td><td>?</td><td>*</td><td>SUN,MON,TUE,WED,THU,FRI,SAT</td><td>*</td></tr>
     *         </tbody>
     *         <caption>매년, 매월 일요일부터 토요일까지 반복한다.</caption>
     *     </table>
     * </p>
     * @see org.quartz.CronExpression
     */
    @Column(name="CRON_EXPR")
    protected String      cronExpression;
    /**
     * 예약명
     */
    @Column(name="DESC")
    protected     String      description;
    /**
     * 회의실 ID
     */
    @Column(name="MEETING_ROOM_ID")
    protected     Long        meetingRoomId;

    /**
     *
     * @param fromDate
     * @param toDate
     * @param time
     * @param duration
     * @param expr
     * @param desc
     * @param meetingId
     */
    public BookingRequestParam(Date fromDate, Date toDate, int time, int duration, String expr, String desc, Long meetingId) {
        this.startTime = time;
        this.duration = duration;
        this.cronExpression = expr;
        this.description = desc;
        this.meetingRoomId = meetingId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     *
     * @param fromDate YYYYMMDD 문자열
     * @param toDate YYYYMMDD 문자열
     * @param time 0~47
     * @param duration 0~47
     * @param expr cron expression
     * @param desc 설명
     * @param meetingId 회의실ID
     * @throws ParseException
     */
    public BookingRequestParam(String fromDate, String toDate, int time, int duration, String expr, String desc, Long meetingId) throws ParseException {
        this.startTime = time;
        this.duration = duration;
        this.cronExpression = expr;
        this.description = desc;
        this.meetingRoomId = meetingId;
        this.fromDate = dateFormat.parse(fromDate);
        this.toDate = dateFormat.parse(toDate);
    }
}
