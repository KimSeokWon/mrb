package kim.seokwon.web.sample.meetingroombooking;

public class ConflictedTimeException extends RuntimeException {
    public ConflictedTimeException( String msg) {
        super(msg);
    }
}
