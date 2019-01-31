package kim.seokwon.web.sample.meetingroombooking.service;

import kim.seokwon.web.sample.meetingroombooking.ConflictedTimeException;
import kim.seokwon.web.sample.meetingroombooking.model.BookingRequestParam;
import kim.seokwon.web.sample.meetingroombooking.model.DailyBookingStatus;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

//@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("회의실 예약 서비스 단위 테스트")
class MeetingRoomServiceTest {

    private final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMdd");

    @Autowired
    private MeetingRoomService      meetingRoomService;

    @BeforeEach
    public void init() {
        try {
            meetingRoomService.deleteBookinParam();
            meetingRoomService.deleteBookingStatus();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Test
    @DisplayName("단일 회의실 예약")
    void registerRoomParam() {
        try {
            final BookingRequestParam param = new BookingRequestParam(
                    "20190101",
                    "20190101",
                    7,
                    3,
                    null,
                    "테스트회의",
                    1L, "#FFF");
            meetingRoomService.registerRoomParam(param);
            List<DailyBookingStatus> map = meetingRoomService.getBookingStatusByDay("20190101");
            Assertions.assertEquals(1, map.size());
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Test
    @DisplayName("연결된 시간 회의실 예약 성공")
    void shouldSuccessToSaveConnectedTimeBooking() {
            Assertions.assertDoesNotThrow(() -> {
                        meetingRoomService.registerRoomParam(new BookingRequestParam(
                                "20190101",
                                "20190101",
                                7,
                                3,
                                null,
                                "테스트회의1",
                                1L, "#FFF"));
                    });
        Assertions.assertDoesNotThrow(() -> {
            List<DailyBookingStatus> map = meetingRoomService.getBookingStatusByDay("20190101");
            Assertions.assertEquals(1, map.size());
        });
        Assertions.assertDoesNotThrow(() -> {
                        meetingRoomService.registerRoomParam(new BookingRequestParam(
                                "20190101",
                                "20190101",
                                10,
                                3,
                                null,
                                "테스트회의2",
                                1L, "#FFF"));
                    });
        Assertions.assertDoesNotThrow(() -> {
            List<DailyBookingStatus> map = meetingRoomService.getBookingStatusByDay("20190101");
            Assertions.assertEquals(2, map.size());
        });
    }

    @Test
    @DisplayName("중돌시간 회의실 예약 실패")
    void shouldFailToSaveConflictedTimeBooking() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam(new BookingRequestParam(
                    "20190101",
                    "20190101",
                    7,
                    3,
                    null,
                    "테스트회의1",
                    1L, "#FFF"));
        });

        Assertions.assertThrows(ConflictedTimeException.class, () -> {
                    meetingRoomService.registerRoomParam(new BookingRequestParam(
                            "20190101",
                            "20190101",
                            8,
                            3,
                            null,
                            "테스트회의2",
                            1L, "#FFF"));
                }
            );
        Assertions.assertDoesNotThrow(() -> {
            List<DailyBookingStatus> map = meetingRoomService.getBookingStatusByDay("20190101");
            Assertions.assertEquals(1, map.size());
        });
    }
    @Test
    @DisplayName("1달동안 월요일 예약 추가")
    void shouldSuccessToSaveRepeatedBooking() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam( new BookingRequestParam(
                    "20190101",
                    "20190131",
                    24,
                    3,
                    "0 0 0 ? * MON *",
                    "월요일 회의",
            2L, "#FFF"));
        });

        Assertions.assertDoesNotThrow(() -> {
                    Map<ZonedDateTime, List<DailyBookingStatus>> map = meetingRoomService.getBookingStatusByDayRange("20190101", "20190131");
                    Assertions.assertEquals(4, map.size());
            Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190107").size());
            Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190114").size());
            Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190121").size());
            Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190128").size());
        });
    }
    @Test
    @DisplayName("반복 예약 추가 후 단건 예약 충돌 실패#1")
    void shouldFailToSaveRepeatedBookingAndAnotherOne() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam( new BookingRequestParam(
                    "20190101",
                    "20190131",
                    24,
                    3,
                    "0 0 0 ? * MON *",
                    "월요일 회의",
                    2L, "#FFF"));
        });
        Assertions.assertThrows(ConflictedTimeException.class, () -> {
                    meetingRoomService.registerRoomParam(new BookingRequestParam(
                            "20190107",
                            "20190107",
                            24,
                            3,
                            null,
                            "테스트회의2",
                            2L, "#FFF"));
                }
        );
    }
    @Test
    @DisplayName("반복 예약 추가 후 단건 예약 충돌 실패#2")
    void shouldFailToSaveRepeatedBookingAndAnotherOne2() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam(new BookingRequestParam(
                    "20190101",
                    "20190131",
                    24,
                    3,
                    "0 0 0 ? * MON *",
                    "월요일 회의",
                    2L,
                    "#FFF"));
        });
        Assertions.assertThrows(ConflictedTimeException.class, () -> {
                    meetingRoomService.registerRoomParam(new BookingRequestParam(
                            "20190107",
                            "20190107",
                            26,
                            3,
                            null,
                            "테스트회의2",
                            2L,
                            "#FFF"));
                }
        );
    }
    @Test
    @DisplayName("반복 예약 추가 후 단건 예약 추가 성공")
    void shouldSuccessToSaveRepeatedBookingAndAnotherOne() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam( new BookingRequestParam(
                    "20190101",
                    "20190131",
                    24,
                    3,
                    "0 0 0 ? * MON *",
                    "월요일 회의",
                    2L,
                    "#FFF"));
        });
        Assertions.assertDoesNotThrow(() -> {
                    meetingRoomService.registerRoomParam(new BookingRequestParam(
                            "20190107",
                            "20190107",
                            27,
                            3,
                            null,
                            "테스트회의2",
                            2L,
                            "#FFF"));
                }
        );
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(2, meetingRoomService.getBookingStatusByDay("20190107").size());
        });
    }
    @Test
    @DisplayName("반복 예약 생성후 예약 변경")
    public void shouldSuccessToCreateThenModify() {
        Assertions.assertDoesNotThrow(() -> {
            meetingRoomService.registerRoomParam(new BookingRequestParam(
                    "20190101",
                    "20190131",
                    24,
                    3,
                    "0 0 0 ? * MON *",
                    "월요일 회의",
                    2L,
                    "#FFF"
            ));
        });
        Assertions.assertDoesNotThrow(() -> {
           List<DailyBookingStatus> list = meetingRoomService.getBookingStatusByDay("20190107");
           Assertions.assertEquals(1, list.size());
           meetingRoomService.getBookingParam(list.get(0).getRequestId()).ifPresent( item -> {
                item.setCronExpression("0 0 0 ? * TUE *");
                item.setDescription("화요일 회의");
               Assertions.assertDoesNotThrow(() -> {
                   meetingRoomService.modifyRoomParam(item);
                   Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190101").size());
                   Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190108").size());
                   Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190115").size());
                   Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190122").size());
                   Assertions.assertEquals(1, meetingRoomService.getBookingStatusByDay("20190129").size());
                   Assertions.assertTrue(meetingRoomService.getBookingStatusByDay("20190107").isEmpty());
                   Assertions.assertTrue(meetingRoomService.getBookingStatusByDay("20190114").isEmpty());
                   Assertions.assertTrue(meetingRoomService.getBookingStatusByDay("20190121").isEmpty());
                   Assertions.assertTrue(meetingRoomService.getBookingStatusByDay("20190128").isEmpty());
               });
           });
        });
    }
}