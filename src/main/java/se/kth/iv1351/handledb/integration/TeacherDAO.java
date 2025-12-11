package se.kth.iv1351.handledb.integration;

import se.kth.iv1351.handledb.model.ActivityDTO;
import se.kth.iv1351.handledb.model.AllocatedActivityDTO;
import se.kth.iv1351.handledb.model.TeacherAllocationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static se.kth.iv1351.handledb.integration.DatabaseConstants.*;

public class TeacherDAO {

    private final Connection connection;
    private PreparedStatement addEmployeePlanedActivityStmt;
    private PreparedStatement getEmployeeIDStmt;
    private PreparedStatement addAllocationStmt;
    private PreparedStatement getNumTeacherAllocationsStmt;
    private PreparedStatement lockNumTeacherAllocationsStmt;
    private PreparedStatement getMaxAllocationsStmt;
    private PreparedStatement hasAllocationInCourseInstanceStmt;
    private PreparedStatement getTeachingActivityIdStmt;
    private PreparedStatement removeEmployeePlannedActivityStmt;
    private PreparedStatement removeAllocationStmt;
    private PreparedStatement findAllocatedActivityStmt;
    private PreparedStatement addTeachingActivityStmt;
    private PreparedStatement addPlannedActivityStmt;
    private PreparedStatement getStudyPeriodAndYearStmt;


    protected TeacherDAO(Connection connection) throws SQLException {
        this.connection = connection;
        prepareStatements();
    }


    public void updateTeachingActivity(ActivityDTO activityDTO) throws UniversityDBException {
        String failureMsg = "could not add activity";
        int rows = 0;
        int id = 0;
        int employeeId = 0;

        try {
            addTeachingActivityStmt.setString(1, activityDTO.getActivityName());
            addTeachingActivityStmt.setFloat(2, activityDTO.getFactor());

            rows = addTeachingActivityStmt.executeUpdate();
            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            id = readActivityIdByName(activityDTO.getActivityName());
            employeeId = readEmployeeId(activityDTO.getEmployeeId());


            addPlannedActivityStmt.setInt(1, id);
            addPlannedActivityStmt.setString(2, activityDTO.getInstanceId());
            addPlannedActivityStmt.setInt(3, activityDTO.getHoursAllocated());
            rows = addPlannedActivityStmt.executeUpdate();

            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            addEmployeePlanedActivityStmt.setInt(1, employeeId);
            addEmployeePlanedActivityStmt.setInt(2, id);
            addEmployeePlanedActivityStmt.setString(3, activityDTO.getInstanceId());

            rows = addEmployeePlanedActivityStmt.executeUpdate();
            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            addAllocationStmt.setInt(1, employeeId);
            addAllocationStmt.setInt(2, id);
            addAllocationStmt.setString(3, activityDTO.getInstanceId());
            addAllocationStmt.setInt(4, activityDTO.getHoursAllocated());

            rows = addAllocationStmt.executeUpdate();
            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            connection.commit();

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        }
    }

    public void updateTeachingLoad(TeacherAllocationDTO teacherAllocations) throws UniversityDBException {
        String failureMsg = "could not allocate teaching load";
        int employeeId = 0;
        int activityId = 0;
        int rows = 0;

        try {
            employeeId = readEmployeeId(teacherAllocations.getEmploymentId());
            activityId = readActivityIdByName(teacherAllocations.getActivityName());

            addEmployeePlanedActivityStmt.setInt(1, employeeId);
            addEmployeePlanedActivityStmt.setInt(2, activityId);
            addEmployeePlanedActivityStmt.setString(3, teacherAllocations.getInstanceId());

            rows = addEmployeePlanedActivityStmt.executeUpdate();

            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            addAllocationStmt.setInt(1, employeeId);
            addAllocationStmt.setInt(2, activityId);
            addAllocationStmt.setString(3, teacherAllocations.getInstanceId());
            addAllocationStmt.setInt(4, teacherAllocations.getAllocatedHours());

            rows = addAllocationStmt.executeUpdate();

            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        }
    }


    public void deleteTeachingLoad(TeacherAllocationDTO teacherAllocations) throws UniversityDBException {
        String failureMsg = "could not deallocate teaching load";
        int employeeId = 0;
        int activityId = 0;
        int rows = 0;

        try {
            activityId = readActivityIdByName(teacherAllocations.getActivityName());
            employeeId = readEmployeeId(teacherAllocations.getEmploymentId());
            removeEmployeePlannedActivityStmt.setInt(1, employeeId);
            removeEmployeePlannedActivityStmt.setInt(2, activityId);
            removeEmployeePlannedActivityStmt.setString(3, teacherAllocations.getInstanceId());

            rows = removeEmployeePlannedActivityStmt.executeUpdate();
            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            removeAllocationStmt.setInt(1, employeeId);
            removeAllocationStmt.setInt(2, activityId);
            removeAllocationStmt.setString(3, teacherAllocations.getInstanceId());

            rows = removeAllocationStmt.executeUpdate();

            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }

            connection.commit();
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        }
    }

    public int readNumTeacherAllocations(String employmentId, String instanceID) throws UniversityDBException {
        String failureMsg = "could not find number of teacher allocations";
        ResultSet result = null;
        int numAllocations = 0;
        CoursePeriodAndYear periodAndYear = null;

        try {

            periodAndYear = readStudyPeriodAndYear(instanceID);

            //unhappy with this solution to lock with aggregate problem, maybe change later
            lockNumTeacherAllocationsStmt.setString(1, employmentId);
            lockNumTeacherAllocationsStmt.setString(2, periodAndYear.studyPeriod());
            lockNumTeacherAllocationsStmt.setInt(3, periodAndYear.studyYear());

            getNumTeacherAllocationsStmt.setString(1, employmentId);
            getNumTeacherAllocationsStmt.setString(2, periodAndYear.studyPeriod());
            getNumTeacherAllocationsStmt.setInt(3, periodAndYear.studyYear());

            result = getNumTeacherAllocationsStmt.executeQuery();
            if (result.next()) {
                numAllocations = result.getInt(1);
            }

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return numAllocations;
    }

    public int readMaxAllocations() throws UniversityDBException {
        String failureMsg = "could not find max number of allocations";
        ResultSet result = null;
        int maxAllocations = 0;
        try {
            result = getMaxAllocationsStmt.executeQuery();
            if (result.next()) {
                maxAllocations = result.getInt(MAX_NUM_ALLOCATIONS_COLUMN_NAME);
            }
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return maxAllocations;
    }

    public boolean readAllocationInCourseInstance(String employmentId, String instanceID) throws UniversityDBException {
        String failureMsg = "could not check if employee has allocation in instance";
        ResultSet result = null;
        boolean isAllocated = false;

        try {
            hasAllocationInCourseInstanceStmt.setString(1, employmentId);
            hasAllocationInCourseInstanceStmt.setString(2, instanceID);
            result = hasAllocationInCourseInstanceStmt.executeQuery();

            isAllocated = result.next();
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        }
        return isAllocated;
    }

    public List<AllocatedActivityDTO> readAllocatedActivities(String activityName) throws UniversityDBException {
        String failureMsg = "could not get allocated activity";
        ResultSet result = null;
        List<AllocatedActivityDTO> allocatedActivityList = new ArrayList<AllocatedActivityDTO>();
        try {
            findAllocatedActivityStmt.setString(1, activityName);
            result = findAllocatedActivityStmt.executeQuery();
            while (result.next()) {
                allocatedActivityList.add(new AllocatedActivityDTO(result.getString(COURSE_CODE_COLUMN_NAME)
                        , result.getString(COURSE_NAME_COLUMN_NAME)
                        , result.getString(INSTANCE_ID_COLUMN_NAME)
                        , result.getString(FIRST_NAME_COLUMN_NAME)
                        , result.getString(LAST_NAME_COLUMN_NAME)
                        , result.getString(ACTIVITY_NAME_COLUMN_NAME)
                        , result.getInt(PLANNED_HOURS_COLUMN_NAME)
                        , result.getFloat(ACTIVITY_FACTOR_COLUMN_NAME)));
            }
            connection.commit();
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }

        return allocatedActivityList;
    }

    private int readEmployeeId(String employmentId) throws UniversityDBException {
        String failureMsg = "Could not find employee id: ";
        ResultSet result = null;
        int employeeId = 0;
        try {
            getEmployeeIDStmt.setString(1, employmentId);
            result = getEmployeeIDStmt.executeQuery();
            if (result.next()) {
                employeeId = result.getInt(ID_COLUMN_NAME);
            } else {
                throw new UniversityDBException(failureMsg + employmentId);
            }
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return employeeId;
    }


    private CoursePeriodAndYear readStudyPeriodAndYear(String instanceID) throws UniversityDBException {
        String failureMsg = "Could not find study period and year";
        ResultSet result = null;
        CoursePeriodAndYear coursePeriodAndYear = null;
        try {
            getStudyPeriodAndYearStmt.setString(1, instanceID);
            result = getStudyPeriodAndYearStmt.executeQuery();
            if (result.next()) {
                coursePeriodAndYear = new CoursePeriodAndYear(result.getString(STUDY_PERIOD_COLUMN_NAME),
                        result.getInt("year"));
            } else {
                throw new UniversityDBException(failureMsg);
            }

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return coursePeriodAndYear;
    }


    private int readActivityIdByName(String activityName) throws UniversityDBException {
        String failureMsg = "Could not find activity with name " + activityName;
        ResultSet result = null;
        int activityId = 0;
        try {
            getTeachingActivityIdStmt.setString(1, activityName);
            result = getTeachingActivityIdStmt.executeQuery();
            if (result.next()) {
                activityId = result.getInt(1);
            } else {
                throw new UniversityDBException(failureMsg);
            }
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return activityId;
    }

    private void prepareStatements() throws SQLException {
        addEmployeePlanedActivityStmt = connection.prepareStatement(
                "INSERT INTO " + EMPLOYEE_PLANNED_ACTIVITY_TABLE_NAME + " (" + EMPLOYEE_ID_COLUMN_NAME + ", " +
                        TEACHING_ACTIVITY_ID_COLUMN_NAME + ", " + INSTANCE_ID_COLUMN_NAME + ") VALUES (?, ?, ?)"
        );

        addAllocationStmt = connection.prepareStatement(
                "INSERT INTO " + ALLOCATIONS_TABLE_NAME + " (" + EMPLOYEE_ID_COLUMN_NAME + ", " +
                        TEACHING_ACTIVITY_ID_COLUMN_NAME + ", " + INSTANCE_ID_COLUMN_NAME + ", " + ALLOCATED_HOURS_COLUMN_NAME +
                        ") VALUES (?, ?, ?, ?)"
        );

        getEmployeeIDStmt = connection.prepareStatement(
                "SELECT " + ID_COLUMN_NAME +
                        " FROM " + EMPLOYEE_TABLE_NAME +
                        " WHERE " + EMPLOYMENT_ID_COLUMN_NAME + " = ?"
        );


        getStudyPeriodAndYearStmt = connection.prepareStatement(
                "SELECT " + STUDY_PERIOD_COLUMN_NAME + ", "
                        + "EXTRACT(YEAR FROM " + STUDY_YEAR_COLUMN_NAME + ") AS year "
                        + "FROM " + COURSE_INSTANCE_TABLE_NAME
                        + " WHERE " + INSTANCE_ID_COLUMN_NAME + " = ?"
        );


        lockNumTeacherAllocationsStmt = connection.prepareStatement(
                "SELECT " + COURSE_INSTANCE_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME
                        + " FROM " + ALLOCATIONS_TABLE_NAME + " INNER JOIN " + COURSE_INSTANCE_TABLE_NAME + " ON "
                        + ALLOCATIONS_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME + " = " + COURSE_INSTANCE_TABLE_NAME + "."
                        + INSTANCE_ID_COLUMN_NAME + " INNER JOIN " + EMPLOYEE_TABLE_NAME + " ON " + EMPLOYEE_TABLE_NAME + "."
                        + ID_COLUMN_NAME + " = " + ALLOCATIONS_TABLE_NAME + "." + EMPLOYEE_ID_COLUMN_NAME
                        + " WHERE " + EMPLOYEE_TABLE_NAME + "." + EMPLOYMENT_ID_COLUMN_NAME + " = ?" + " AND "
                        + COURSE_INSTANCE_TABLE_NAME + "." + STUDY_PERIOD_COLUMN_NAME + " = ?" + " AND "
                        + "EXTRACT(YEAR FROM " + COURSE_INSTANCE_TABLE_NAME + "." + STUDY_YEAR_COLUMN_NAME + ") = ?"
                        + " FOR NO KEY UPDATE"
        );


        getNumTeacherAllocationsStmt = connection.prepareStatement(
                "SELECT COUNT( DISTINCT " + COURSE_INSTANCE_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME + " ) "
                        + "FROM " + ALLOCATIONS_TABLE_NAME + " INNER JOIN " + COURSE_INSTANCE_TABLE_NAME + " ON "
                        + ALLOCATIONS_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME + " = " + COURSE_INSTANCE_TABLE_NAME + "."
                        + INSTANCE_ID_COLUMN_NAME + " INNER JOIN " + EMPLOYEE_TABLE_NAME + " ON " + EMPLOYEE_TABLE_NAME + "."
                        + ID_COLUMN_NAME + " = " + ALLOCATIONS_TABLE_NAME + "." + EMPLOYEE_ID_COLUMN_NAME
                        + " WHERE " + EMPLOYEE_TABLE_NAME + "." + EMPLOYMENT_ID_COLUMN_NAME + " = ?" + " AND "
                        + COURSE_INSTANCE_TABLE_NAME + "." + STUDY_PERIOD_COLUMN_NAME + " = ?" + " AND "
                        + "EXTRACT(YEAR FROM " + COURSE_INSTANCE_TABLE_NAME + "." + STUDY_YEAR_COLUMN_NAME + ") = ?"
        );


        getMaxAllocationsStmt = connection.prepareStatement(
                "SELECT " + MAX_NUM_ALLOCATIONS_COLUMN_NAME + " FROM " + BUSINESS_RULE_TABLE_NAME
        );

        hasAllocationInCourseInstanceStmt = connection.prepareStatement(
                "SELECT " + ALLOCATIONS_TABLE_NAME + "." + TEACHING_ACTIVITY_ID_COLUMN_NAME +
                        " FROM " + ALLOCATIONS_TABLE_NAME +
                        " INNER JOIN " + EMPLOYEE_TABLE_NAME + " ON " +
                        EMPLOYEE_TABLE_NAME + "." + ID_COLUMN_NAME + " = " +
                        ALLOCATIONS_TABLE_NAME + "." + EMPLOYEE_ID_COLUMN_NAME +
                        " WHERE " + EMPLOYEE_TABLE_NAME + "." + EMPLOYMENT_ID_COLUMN_NAME + " = ?" +
                        " AND " + ALLOCATIONS_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME + " = ?" +
                        " FOR NO KEY UPDATE"
        );


        addTeachingActivityStmt = connection.prepareStatement("INSERT INTO " +
                TEACHING_ACTIVITY_TABLE_NAME + " ( " + ACTIVITY_NAME_COLUMN_NAME + ", " +
                ACTIVITY_FACTOR_COLUMN_NAME + ") VALUES (?, ?)"
        );

        addPlannedActivityStmt = connection.prepareStatement(
                "INSERT INTO " + PLANNED_ACTIVITY_TABLE_NAME + " (" + TEACHING_ACTIVITY_ID_COLUMN_NAME +
                        ", " + INSTANCE_ID_COLUMN_NAME + ", " + PLANNED_HOURS_COLUMN_NAME + ") VALUES (?, ?, ?)"
        );

        getTeachingActivityIdStmt = connection.prepareStatement(
                "SELECT " + ID_COLUMN_NAME + " FROM " + TEACHING_ACTIVITY_TABLE_NAME +
                        " WHERE " + ACTIVITY_NAME_COLUMN_NAME + " = ?"
        );

        removeEmployeePlannedActivityStmt = connection.prepareStatement(
                "DELETE FROM " + EMPLOYEE_PLANNED_ACTIVITY_TABLE_NAME +
                        " WHERE " + EMPLOYEE_ID_COLUMN_NAME + " = ? AND " +
                        TEACHING_ACTIVITY_ID_COLUMN_NAME + " = ? AND " +
                        INSTANCE_ID_COLUMN_NAME + " = ?"
        );

        removeAllocationStmt = connection.prepareStatement(
                "DELETE FROM " + ALLOCATIONS_TABLE_NAME +
                        " WHERE " + EMPLOYEE_ID_COLUMN_NAME + " = ? AND " +
                        TEACHING_ACTIVITY_ID_COLUMN_NAME + " = ? AND " +
                        INSTANCE_ID_COLUMN_NAME + " = ?"
        );

        findAllocatedActivityStmt = connection.prepareStatement(
                "SELECT * FROM " + ALLOCATED_ACTIVITY_VIEW + " WHERE " + ACTIVITY_NAME_COLUMN_NAME + " = ?"
        );
    }

    private record CoursePeriodAndYear(String studyPeriod, int studyYear) {
    }
}