package kim.seokwon.web.sample.meetingroombooking.repository;

import kim.seokwon.web.sample.meetingroombooking.model.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {
}
