package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 초기 파라미터를 세팅한다.
 * <p>
 *   시작시간, 종료시간, 회의실 수
 * </p>
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class InitParam {
    /**
     * 0 부터 47 까지 가질 수 있다.
     */
    private int startHour;
    /**
     * 0 부터 47 까지 가질 수 있다.
     */
    private int endHour;
    /**
     * 회의실 숫자. 최대 20개를 가질 수 있다.
     */
    private int     count;
}
