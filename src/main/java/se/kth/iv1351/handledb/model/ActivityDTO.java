package se.kth.iv1351.handledb.model;

public class ActivityDTO {
    private final String employeeId;
    private final String instanceId;
    private final String activityName;
    private final int hoursAllocated;
    private final float factor;

    public ActivityDTO(String employeeId, String instanceId, String activityName, int hoursAllocated, float factor) {
        this.employeeId = employeeId;
        this.instanceId = instanceId;
        this.activityName = activityName;
        this.hoursAllocated = hoursAllocated;
        this.factor = factor;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getHoursAllocated() {
        return hoursAllocated;
    }

    public float getFactor() {
        return factor;
    }
}