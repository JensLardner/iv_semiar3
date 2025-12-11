package se.kth.iv1351.handledb.model;

public class InstanceCostDTO {
    private final String COURSE_CODE;
    private final String INSTANCE_ID;
    private final String STUDY_PERIOD;
    private final double PLANNED_COST;
    private final double ACTUAL_COST;

    public InstanceCostDTO(String courseCode, String instanceId, String studyPeriod, double plannedCost, double actualCost) {
        COURSE_CODE = courseCode;
        INSTANCE_ID = instanceId;
        STUDY_PERIOD = studyPeriod;
        PLANNED_COST = plannedCost;
        ACTUAL_COST = actualCost;
    }

    public String getCourseCode() {
        return COURSE_CODE;
    }

    public String getInstanceId() {
        return INSTANCE_ID;
    }

    public String getStudyPeriod() {
        return STUDY_PERIOD;
    }

    public double getPlannedCost() {
        return PLANNED_COST;
    }

    public double getActualCost() {
        return ACTUAL_COST;
    }

}
