package lk.gov.police.trafficfine.exception;

public class AlreadyPaidException extends RuntimeException {
    public AlreadyPaidException(String message) {
        super(message);
    }
}
