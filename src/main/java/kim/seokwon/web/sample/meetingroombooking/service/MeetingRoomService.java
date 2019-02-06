package kim.seokwon.web.sample.meetingroombooking.service;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import kim.seokwon.web.sample.meetingroombooking.ConflictedTimeException;
import kim.seokwon.web.sample.meetingroombooking.MRBConst;
import kim.seokwon.web.sample.meetingroombooking.model.*;
import kim.seokwon.web.sample.meetingroombooking.repository.DailyBookingStatusRepository;
import kim.seokwon.web.sample.meetingroombooking.repository.MeetingRoomRepository;
import kim.seokwon.web.sample.meetingroombooking.repository.BookingParamRepository;
import kim.seokwon.web.sample.meetingroombooking.repository.TimeTableRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

@Service
@Slf4j
public class MeetingRoomService implements MRBConst {

    /**
     * 시스템의 초기 상태를 나타냄.
     * @see MRBConst
     */
    private static int SYSTEM_STATUS = 0;
    private final   DailyBookingStatusRepository        dailyBookingStatusRepository;
    private final   MeetingRoomRepository               meetingRoomRepository;
    private final   BookingParamRepository              bookingParamRepository;
    private final   TimeTableRepository                 timeTableRepository;

    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd" );


    @Autowired
    public MeetingRoomService(DailyBookingStatusRepository     dailyBookingStatusRepository,
                              MeetingRoomRepository            meetingRoomRepository,
                              BookingParamRepository            bookingParamRepository,
                              TimeTableRepository               timeTableRepository) {
        this.bookingParamRepository = bookingParamRepository;
        this.meetingRoomRepository          = meetingRoomRepository;
        this.dailyBookingStatusRepository   = dailyBookingStatusRepository;
        this.timeTableRepository            = timeTableRepository;
    }

    public int getStatus() {

        return SYSTEM_STATUS;
    }

    @PostConstruct
    protected void checkDataBase() {
        if ( meetingRoomRepository.count() == 0) {
            SYSTEM_STATUS = NOT_YET_INIT;
        } else if ( timeTableRepository.count() == 0 ) {
            SYSTEM_STATUS = NOT_YET_INIT;
        } else {
            SYSTEM_STATUS = FINISH_INIT;
        }
    }
    @Transactional
    void resetBaseCodes() {
        try {
            SYSTEM_STATUS = NOT_YET_INIT;
            meetingRoomRepository.deleteAllInBatch();
            assert meetingRoomRepository.count() == 0;
            timeTableRepository.deleteAllInBatch();
            assert timeTableRepository.count() == 0;
        }
        catch ( AssertionError ex ) {
            System.err.println("meeting room count: " + meetingRoomRepository.count());
            System.err.println("timetable count: " + timeTableRepository.count());
            log.debug("meeting room count: {}", meetingRoomRepository.count());
            log.debug("timetable count: {}", timeTableRepository.count());
            throw ex;
        }
    }

    public synchronized int initDatabase(InitParam param) {
        if ( SYSTEM_STATUS > NOT_YET_INIT) {
            return SYSTEM_STATUS;
        }
        if ( SYSTEM_STATUS == NOT_YET_INIT ) {
            SYSTEM_STATUS = 1;
        }
        try {
            //init databases
            resetBaseCodes();

            log.info("초기화: 회의실 목록 삽입");
            for ( int i = 0 ;i < param.getCount() ; i++ ) {
                registerMeetingRoom(new MeetingRoom("회의실 " + (char)('A' + i)));
            }
            log.info("초기화: 회의실 기본 사용시간 목록 삽입");
            for ( int i = 0; i < ( param.getEndHour() - param.getStartHour() + 1); i++ ) {
                timeTableRepository.save(
                        new TimeTable(i, getTimeFormat(i + param.getStartHour()), (i*30)+"분")
                );
            }

        } catch ( Exception ex ) {
            SYSTEM_STATUS = ERROR_TO_INIT;
            return SYSTEM_STATUS;
        }
        SYSTEM_STATUS = FINISH_INIT;
        return SYSTEM_STATUS;
    }

    private final static String getTimeFormat(int tm) {
        LocalTime time = LocalTime.of(tm/2, (tm%2)*30);
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }

    /**
     * 회의실 등록
     * @param room
     */
    public void registerMeetingRoom(MeetingRoom room) {
        meetingRoomRepository.save(room);
    }

    /**
     * 등록하고자 하는 일정이 기존에 등록되어 있는지 조회한다.
     * @param source 일일 예약 등록 정보
     * @return 등록된 정보가 있으면 true, 아니면 false 를 리턴한다.
     */
    private boolean containDuplicatedBooking(List<DailyBookingStatus> source) {

        for ( DailyBookingStatus s : source) {
            List<DailyBookingStatus> list = dailyBookingStatusRepository.getListByDayByRoomId(
                    s.getDate(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getRoomId());
            if ( !list.isEmpty() ) {
                return true;
            }
        }

        return false;
    }


    /**
     * 회의실 목록을 조회한다.
     * @return 회의실 목록
     */
    public List<MeetingRoom> getMeetingRoomList() {
        return meetingRoomRepository.findAll();
    }

    /**
     * 예약 요청 정보를 저장한다.
     * @param param 예약 요청 정보
     * @return 저장된 요청정보, 요청 ID 를 가지고 있다.
     */
    @Transactional
    public BookingRequestParam registerRoomParam(BookingRequestParam param) throws ConflictedTimeException, Exception {
        final BookingRequestParam insertedParam = bookingParamRepository.save(param);
        Assert.notNull(insertedParam, "Inserted Param must not null.");

        List<DailyBookingStatus> dailyBookingStatus = createDailyBookingFromParam(insertedParam);
        if ( containDuplicatedBooking(dailyBookingStatus) ) {
            bookingParamRepository.delete(insertedParam);
            throw new ConflictedTimeException("중복된 예약이 존재합니다. 다시 선택바랍니다.");
        }
        dailyBookingStatusRepository.saveAll(dailyBookingStatus);

        return insertedParam;
    }

    private final ExecutionTime getExecutionTimeFromCronExpression(String expression) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser cronParser = new CronParser(cronDefinition);
        return ExecutionTime.forCron(cronParser.parse(expression));
    }
    /**
     * 요청 파라미터로 일일 예약 정보를 생성한다.
     * @param param 예약 요청 파라미터
     * @return 일일 예약 정보
     */
    private List<DailyBookingStatus> createDailyBookingFromParam(BookingRequestParam param) throws Exception {
        try {

            final ZonedDateTime fromDate = ZonedDateTime.ofInstant(param.getFromDate().toInstant(), ZoneId.systemDefault()).minusSeconds(1);
            final ZonedDateTime toDate = ZonedDateTime.ofInstant(param.getToDate().toInstant(), ZoneId.systemDefault()).plusDays(1).minusSeconds(1);

            final String cronExpression = (StringUtils.isEmpty(param.getCronExpression() ) ) ? "0 0 0 * * ? *" : param.getCronExpression();


            ExecutionTime executionTime = getExecutionTimeFromCronExpression(cronExpression);
            ZonedDateTime targetDate = executionTime.nextExecution(fromDate).get();
            List<DailyBookingStatus> list = new ArrayList<DailyBookingStatus>();

            while ( toDate.isAfter(targetDate) ) {
                list.add(new DailyBookingStatus(
                        targetDate,
                        param.getStartTime(),
                        param.getStartTime() + param.getDuration(),
                        param.getDescription(),
                        param.getRequestId(),
                        param.getMeetingRoomId(),
                        param.getColor()));
                targetDate = executionTime.nextExecution(targetDate).get();
            }

            return list;
        } catch ( Exception ex) {
            log.error("Date Format is incorrect.", ex);
            throw ex;
        }
    }

    /**
     * 일별 예약 상태를 등록한다.
     * @param status
     */
    private void registerDailyBookingStatus(List<DailyBookingStatus> status) {
        dailyBookingStatusRepository.saveAll(status);
    }

    /**
     * 요청ID 에 따른 예약 요청 정보를 조회한다.
     * @param id 요청ID
     * @return 예약 요청 정보
     */
    public Optional<BookingRequestParam> getBookingParam(Long id) {
        return bookingParamRepository.findById(id);
    }

    /**
     * 지정된 날짜 범위내의 예약 정보를 조회한다.
     * fromDate <= 예약일 < toDate
     * @param fromDate 시작 날짜 YYYYMMDD
     * @param toDate 종료 날짜 YYYYMMDD
     * @param id 예약 요청 ID
     * @return 일별 예약 정보
     */
    public Map<ZonedDateTime, List<DailyBookingStatus>> getBookingStatusByDayRange(String fromDate, String toDate, String id) throws Exception {
        //ZonedDateTime.

        List<DailyBookingStatus> list = dailyBookingStatusRepository.getListInDayRangeByReqId(ZonedDateTime.parse(fromDate, dateFormatter), ZonedDateTime.parse(toDate, dateFormatter), Long.parseLong(id));
        return convertToMap(list);
    }

    /**
     *
     * @param fromDate 시작 날짜 YYYYMMDD
     * @param toDate 종료 날짜 YYYYMMDD
     * @return 일별 예약 정보
     */
    public Map<ZonedDateTime, List<DailyBookingStatus>> getBookingStatusByDayRange(String fromDate, String toDate) throws Exception{

        List<DailyBookingStatus> list = dailyBookingStatusRepository.getListInDayRange(
                LocalDate.parse(fromDate, dateFormatter).atStartOfDay(ZoneId.systemDefault()),
                LocalDate.parse(toDate, dateFormatter).atStartOfDay(ZoneId.systemDefault()));
        return convertToMap(list);
    }

    private Map<ZonedDateTime, List<DailyBookingStatus>> convertToMap(List<DailyBookingStatus> fromList) throws Exception {
        Map<ZonedDateTime, List<DailyBookingStatus>> map = new HashMap<>();
        fromList.forEach( item -> {
            ZonedDateTime dateForm = item.getDate();
            if ( !map.containsKey(dateForm) ) {
                map.put(dateForm, new ArrayList<DailyBookingStatus>());
            }
            map.get(dateForm).add(item);
        });
        return map;
    }

    private final static ZonedDateTime parseDateString(String str) {
        return LocalDate.parse(str, dateFormatter).atStartOfDay(ZoneId.systemDefault());
    }

    /**
     * 일별 예약 정보를 조회한다.
     * @param date 요청일, 예약 파라미터 YYYYMMDD 형식으로 나타낸다.
     * @return 요청일의 예약 정보를 리턴한다.
     */
    public List<DailyBookingStatus> getBookingStatusByDay(String date) throws Exception {
        ZonedDateTime fromDate = parseDateString(date);
        List<DailyBookingStatus> list = dailyBookingStatusRepository.getListByDay(fromDate);

        return list;
    }

    /**
     * 요청 ID 에 따른 요청 파라미터 및 예약 정보를 모두 삭제한다.
     * @param requestId 요청 파라미터 ID
     */
    private void internalRemoveRoomParam(Long requestId) {
        dailyBookingStatusRepository.deleteByRequestId(requestId);
        bookingParamRepository.deleteByReqId(requestId);
    }
    @Transactional
    public void removeRoomParam(Long requestId) {
        dailyBookingStatusRepository.deleteByRequestId(requestId);
        bookingParamRepository.deleteById(requestId);
    }

    /**
     * 등록 요청한 정보를 변경한다. 변경하면 등록된 예약 정보가 모두 변경된다.(삭제 -> 추가)
     * @param param 등록 요청 파라미터
     */
    @Transactional
    public void modifyRoomParam(BookingRequestParam param) throws Exception {
        internalRemoveRoomParam(param.getRequestId());
        registerRoomParam(param);
    }

    /**
     * 타임 테이블 코드를 조회한다.
     * @return
     */
    public List<TimeTable> getTimeTable() {
        return timeTableRepository.findAll();
    }
    void deleteBookingStatus() throws Exception{
        dailyBookingStatusRepository.deleteAll();
        dailyBookingStatusRepository.flush();
    }
    void deleteBookinParam() throws Exception {
        bookingParamRepository.deleteAll();
        bookingParamRepository.flush();
    }

}
