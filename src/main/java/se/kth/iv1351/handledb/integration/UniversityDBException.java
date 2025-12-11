package se.kth.iv1351.handledb.integration;

public class UniversityDBException extends Exception {


    public UniversityDBException(String cause) {
        super(cause);
    }

    public UniversityDBException(String cause, Throwable rootCause) {
        super(cause, rootCause);
    }

}
