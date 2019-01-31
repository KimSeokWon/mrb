package kim.seokwon.web.sample.meetingroombooking;

import lombok.Setter;

@Setter
public class InitializeException extends Exception {

    private int status;

    public InitializeException(int status) {
        super("Initialization is needed. status code: " + status);
        this.status = status;
    }
}
