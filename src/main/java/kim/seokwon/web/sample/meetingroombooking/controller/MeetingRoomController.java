package kim.seokwon.web.sample.meetingroombooking.controller;

import kim.seokwon.web.sample.meetingroombooking.model.*;
import kim.seokwon.web.sample.meetingroombooking.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/resources/meeting-room")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Autowired
    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 예약 정보를 등록한다.
     *
     * @param param 등록하고자 하는 정보 내용
     * @return 성공시에는 ID 가 부여된 등록 정보를 리턴한다.
     *
     * @see BookingRequestParam
     */
    @PostMapping(value="/create", consumes = "application/json")
    public @ResponseBody
    BookingRequestParam createRecord(@RequestBody BookingRequestParam param) throws Exception {
        checkParameter(param);
        return meetingRoomService.registerRoomParam(param);
    }

    void checkParameter(BookingRequestParam param) {
        if ( 48 >= param.getStartTime() + param.getDuration() ) {
            throw new InvalidParameterException("회의 종료시간은 하루를 넘어갈 수 없습니다.");
        }
        if ( param.getDuration() == 0 ) {
            throw new InvalidParameterException("종료시간을 다시 확인바랍니다.");
        }
        if ( param.getFromDate().before(param.getToDate()) ) {
            throw new InvalidParameterException("종료날짜를 다시 확인바랍니다.");
        }
        if ( StringUtils.isEmpty(param.getDescription()) ) {
            throw new InvalidParameterException("예약자명을 확인바랍니다.");
        }
    }
    /**
     * 등록된 정보를 변경한다.
     *
     * @param param 변경하고자 하는 정보 내용, 식별자는 기존 식별자이나 내용은 변경할 정보를 가지고 있다.
     * @return 성공여부
     *
     * @see BookingRequestParam
     */
    @PostMapping(value="/modify", consumes = "application/json")
    public @ResponseBody String modifyRecord(@RequestBody BookingRequestParam param) throws Exception {
        checkParameter(param);
        meetingRoomService.modifyRoomParam(param);
        return StringUtils.EMPTY;
    }

    /**
     * 지정 날짜의 예약 정보를 조회한다.
     *
     * @param date 시작 날짜의 문자열, YYYYMMDD 와 같은 형식으로 사용한다.
     * @return 예약 정보를 나타낸다.
     */
    @GetMapping(value="/select/day/{date}")
    public @ResponseBody List<DailyBookingStatus> getDailyBookingInfoByDay( @PathVariable(name = "date") String date)
            throws Exception {
        return meetingRoomService.getBookingStatusByDay(date);
    }

    /**
     * 시작 날짜를 포함하고 종료 날짜를 포함하지 않는 특정 예약 정보를 조회한다.
     *
     * @param fromDate 시작 날짜의 문자열, YYYYMMDD 와 같은 형식으로 사용한다.
     * @param toDate 종료 날짜의 문자열, YYYYMMDD 와 같은 형식으로 사용한다.
     * @param id 예약 등록 ID, 서버에서 생성된다.
     * @return 일별로 등록된 예약 정보를 나타낸다.
     */
    @GetMapping(value="/select/from/{fromDate}/to/{toDate}/id/{id}")
    public @ResponseBody Map<ZonedDateTime, List<DailyBookingStatus>> getDailyBookingInfoByRange(
            @PathVariable(name = "fromDate") String fromDate,
            @PathVariable(name = "toDate") String toDate,
            @PathVariable(name="id") String id)
            throws Exception {
        return meetingRoomService.getBookingStatusByDayRange(fromDate, toDate, id);
    }

    /**
     * 지정한 날짜의 예약 정보를 조회한다.
     * @param date 예약 정보를 가져올 날짜의 문자열, YYYYMMDD 와 같은 형식으로 사용한다.
     * @return 요청일에 등록된 예약 정보를 나타낸다. 없으면 null 을 리턴한다.
     */
    @GetMapping(value="/select/{date}")
    public @ResponseBody List<DailyBookingStatus> getBookingInfoByDay(@PathVariable(name="date") String date) throws Exception {
        return meetingRoomService.getBookingStatusByDay(date);

    }

    @DeleteMapping(value="/remove/{id}")
    public void removeBookingInfo(@PathVariable(name="id") String id) {

        meetingRoomService.removeRoomParam(Long.parseLong(id));
    }

    /**
     * 식별자에 해당하는 예약정보를 조회한다.
     * @param id 등록된 예약정보 식별자
     * @return 식별자에 해당하는 상세 예약 정보 BookingRequestParam 의 id
     */
    @GetMapping(value="/select/detail/{id}")
    public @ResponseBody Optional<? extends BookingRequestParam> getBookingParam(@PathVariable(name="id") String id) throws Exception {
        return meetingRoomService.getBookingParam(Long.parseLong(id));
    }

    /**
     * 회의실 목록 조회
     * @return
     */
    @GetMapping(value="select/meeting-room")
    public @ResponseBody List<MeetingRoom> getMeetingRoomList() {
        return meetingRoomService.getMeetingRoomList();
    }

    /**
     * 타임테이블 코드 값 조회
     * @return
     */
    @GetMapping(value="/select/time-table")
    public @ResponseBody List<TimeTable> getTimeTable() {
        return meetingRoomService.getTimeTable();
    }
}
