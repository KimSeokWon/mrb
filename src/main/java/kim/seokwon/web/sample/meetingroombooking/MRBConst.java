package kim.seokwon.web.sample.meetingroombooking;

public interface MRBConst {
    /**
     * 시스템의 초기 상태를 나타냄.
     * 초기화 시작 전
     */
    public final static int NOT_YET_INIT = 0;
    /**
     * 초기화 진행 중
     */
    public final static int IN_PROGRESS_INIT = 1;
    /**
     * 초기화가 종료됨. 사용할 수 있음.
     */
    public final static int FINISH_INIT = 9;

    public final static int ERROR_TO_INIT = -1;
}
