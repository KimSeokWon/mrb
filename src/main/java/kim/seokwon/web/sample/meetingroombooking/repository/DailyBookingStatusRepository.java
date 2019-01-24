package kim.seokwon.web.sample.meetingroombooking.repository;

import kim.seokwon.web.sample.meetingroombooking.model.DailyBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface DailyBookingStatusRepository extends JpaRepository<DailyBookingStatus, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM DAILY_BOOK WHERE REQ_ID = :id")
    public void deleteByRequestId(@Param("id") Long id);

    @Query(nativeQuery = true, value = "SELECT * FROM DAILY_BOOK WHERE REQ_ID = :id")
    public List<DailyBookingStatus> getListByReqId(@Param("id") Long id);

    @Query(nativeQuery = true, value= "SELECT * FROM DAILY_BOOK WHERE BOOK_DT = :date AND START_TM >= :from AND END_TM < :to")
    public List<DailyBookingStatus> getListByTime(@Param("date") ZonedDateTime date, @Param("from") int from, @Param("to") int to);

    @Query(nativeQuery = true, value= "SELECT * FROM DAILY_BOOK WHERE BOOK_DT = :date AND START_TM >= :from AND END_TM < :to AND REQ_ID = :req_id")
    public List<DailyBookingStatus> getListByTimeByReqId(@Param("date") ZonedDateTime date, @Param("from") int from, @Param("to") int to, @Param("req_id") Long requestId);

    @Query(nativeQuery = true, value= "SELECT * FROM DAILY_BOOK WHERE BOOK_DT >= :fromDate AND BOOK_DT <= :toDate AND REQ_ID = :req_id")
    public List<DailyBookingStatus> getListInDayRangeByReqId(@Param("fromDate") ZonedDateTime fromDate, @Param("toDate") ZonedDateTime toDate, @Param("req_id") Long requestId);

    @Query(nativeQuery = true, value= "SELECT * FROM DAILY_BOOK WHERE BOOK_DT >= :fromDate AND BOOK_DT <= :toDate")
    public List<DailyBookingStatus> getListInDayRange(@Param("fromDate") ZonedDateTime fromDate, @Param("toDate") ZonedDateTime toDate);

    @Query(nativeQuery = true, value=
            "SELECT A.BOOKING_ID, A.BOOK_DT, A.DESC, A.END_TM, A.REQ_ID, A.ROOM_ID, A.START_TM, B.CNT FROM DAILY_BOOK A " +
            "INNER JOIN ( " +
            "SELECT COUNT(C.BOOKING_ID) CNT, C.REQ_ID FROM DAILY_BOOK C GROUP BY C.REQ_ID ) B ON A.REQ_ID=B.REQ_ID " +
            "WHERE BOOK_DT = :date")
    public List<DailyBookingStatus> getListByDay(@Param("date") ZonedDateTime date);

    @Query(nativeQuery = true, value= "SELECT * FROM DAILY_BOOK WHERE ROOM_ID = :roomId AND BOOK_DT = :date AND" +
            "( (START_TM <= :from AND END_TM >=:to ) OR " +
            "( START_TM >= :from AND END_TM >= :to AND START_TM < :to ) OR " +
            "( START_TM <= :from  AND END_TM > :from AND END_TM <= :to ) OR " +
            "( START_TM >= :from AND END_TM <= :to) )")
    public List<DailyBookingStatus> getListByDayByRoomId(@Param("date") ZonedDateTime date, @Param("from") int from, @Param("to") int to, @Param("roomId") Long roomId);

    @Query(nativeQuery = true, value="SELECT COUNT(BOOKING_ID) FROM DAILY_BOOK WHERE REQ_ID = :req_id")
    public int getCountByReqId(@Param("req_id") Long requestId);
}
