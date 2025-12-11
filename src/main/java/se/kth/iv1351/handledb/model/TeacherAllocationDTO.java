package se.kth.iv1351.handledb.model;

public class TeacherAllocationDTO {
    private final String employmentId;
    private final String instanceId;
    private final String activityName;
    private int allocatedHours;

    public TeacherAllocationDTO(String employmentId, String instanceId, String activityName, int allocatedHours) {
        this.employmentId = employmentId;
        this.instanceId = instanceId;
        this.activityName = activityName;
        this.allocatedHours = allocatedHours;
    }

    public TeacherAllocationDTO(String employmentId, String instanceId, String activityName) {
        this.employmentId = employmentId;
        this.instanceId = instanceId;
        this.activityName = activityName;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getAllocatedHours() {
        return allocatedHours;
    }
}
