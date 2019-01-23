package kim.seokwon.web.sample.meetingroombooking.repository;

import kim.seokwon.web.sample.meetingroombooking.model.BookingRequestParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingParamRepository extends JpaRepository<BookingRequestParam, Long> {
    @Query(nativeQuery = true,
            value = "SELECT * FROM ROOM_PARAM WHERE FROM_DT >= :startDate AND TO_DT < :endDate AND " +
                    "(START_TM <= :startTime AND (START_TM + DURATION) > (:startTime + :duration) ) OR " +
                    "(START_TM >= :startTime AND (START_TM + DURATION) > (:startTime + :duration) ) OR " +
                    "(START_TM <= :startTime AND (START_TM + DURATION) < (:startTime + :duration) ) OR " +
                    "(START_TM >= :startTime AND (START_TM + DURATION) > (:startTime + :duration) ) ")
    public List<BookingRequestParam> findByDateByTime(@Param("startDate")Date startDate, @Param("endDate") Date endDate, @Param("startTime") int startTime, @Param("duration") int duration);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM ROOM_PARAM WHERE request_id = :req_id")
    public void deleteByReqId(@Param("req_id") Long id);
}
