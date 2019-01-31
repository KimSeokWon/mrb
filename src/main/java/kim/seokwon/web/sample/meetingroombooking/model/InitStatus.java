package kim.seokwon.web.sample.meetingroombooking.model;

import lombok.Getter;

@Getter
public class InitStatus {
    private int status;
    public InitStatus(int status) {
        this.status = status;
    }
}
