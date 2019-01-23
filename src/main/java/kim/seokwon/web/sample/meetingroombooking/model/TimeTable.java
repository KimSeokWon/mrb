package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TM_TBL")
@Getter @ToString @NoArgsConstructor
public class TimeTable {
    @Id
    private Integer     time;
    @Column(name="TM_VAL")
    private String      timeValue;
    @Column(name="DUR_VAL")
    private String      durationValue;

    public TimeTable ( int time, String timeValue, String durationValue) {
        this.time = time;
        this.timeValue = timeValue;
        this.durationValue = durationValue;
    }
}
