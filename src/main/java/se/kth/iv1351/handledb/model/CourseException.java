package se.kth.iv1351.handledb.model;

public class CourseException extends Exception {
    public CourseException(String cause) {
        super(cause);
    }

    public CourseException(String message, Throwable cause) {
        super(message, cause);
    }
}
