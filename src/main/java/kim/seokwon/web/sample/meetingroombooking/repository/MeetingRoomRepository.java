package kim.seokwon.web.sample.meetingroombooking.repository;

import kim.seokwon.web.sample.meetingroombooking.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
}
