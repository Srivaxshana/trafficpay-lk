package lk.gov.police.trafficfine.exception;

public class FineNotFoundException extends RuntimeException {
    public FineNotFoundException(String message) {
        super(message);
    }
}
