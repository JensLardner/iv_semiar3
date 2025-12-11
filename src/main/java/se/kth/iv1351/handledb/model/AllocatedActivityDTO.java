package se.kth.iv1351.handledb.model;

public class AllocatedActivityDTO {
    private final String COURSE_CODE;
    private final String COURSE_NAME;
    private final String INSTANCE_ID;
    private final String FIRST_NAME;
    private final String LAST_NAME;
    private final String ACTIVITY_NAME;
    private final int PLANNED_HOURS;
    private final float FACTOR;

    public AllocatedActivityDTO(String courseCode, String courseName, String instanceId, String firstName, String lastName, String activityName, int plannedHours, float factor) {
        COURSE_CODE = courseCode;
        COURSE_NAME = courseName;
        INSTANCE_ID = instanceId;
        FIRST_NAME = firstName;
        LAST_NAME = lastName;
        ACTIVITY_NAME = activityName;
        PLANNED_HOURS = plannedHours;
        FACTOR = factor;
    }

    public String getCourseCode() {
        return COURSE_CODE;
    }

    public String getCourseName() {
        return COURSE_NAME;
    }

    public String getInstanceId() {
        return INSTANCE_ID;
    }

    public String getFirstName() {
        return FIRST_NAME;
    }

    public String getLastName() {
        return LAST_NAME;
    }

    public String getActivityName() {
        return ACTIVITY_NAME;
    }

    public int getPlannedHours() {
        return PLANNED_HOURS;
    }

    public float getFactor() {
        return FACTOR;
    }
}




