package se.kth.iv1351.handledb.integration;

import se.kth.iv1351.handledb.model.InstanceCostDTO;
import se.kth.iv1351.handledb.model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static se.kth.iv1351.handledb.integration.DatabaseConstants.*;

public class CourseDAO {

    private static final double TEACHER_SALARY_KSEK = 0.2;

    private final Connection connection;
    private PreparedStatement findCourseInstanceCostStmt;
    private PreparedStatement updateNumStudentsStmt;
    private PreparedStatement getInstanceIdStmt;
    private PreparedStatement getPersonStmt;


    /**
     * Constructs the course data access object.
     *
     * @param connection sets the connection.
     * @throws SQLException if it can not prepare the statements.
     */
    protected CourseDAO(Connection connection) throws SQLException {
        this.connection = connection;
        prepareStatements();
    }

    /**
     * Finds the cost of a course instance.
     *
     * @param courseCode  the course code of the course.
     * @param studyPeriod the period the course instance is in.
     * @return the cost of the course instance.
     * @throws UniversityDBException ?????????????????????????????????
     */
    public InstanceCostDTO readCourseInstanceCost(String courseCode, String studyPeriod) throws UniversityDBException {
        String failureMsg = "Could not find cost for " + courseCode;
        ResultSet result = null;
        InstanceCostDTO courseInstanceCost = null;
        try {
            findCourseInstanceCostStmt.setDouble(1, TEACHER_SALARY_KSEK);
            findCourseInstanceCostStmt.setDouble(2, TEACHER_SALARY_KSEK);
            findCourseInstanceCostStmt.setString(3, courseCode);
            findCourseInstanceCostStmt.setString(4, studyPeriod);
            result = findCourseInstanceCostStmt.executeQuery();
            if (result.next()) {
                courseInstanceCost = new InstanceCostDTO(result.getString(COURSE_CODE_COLUMN_NAME),
                        result.getString(INSTANCE_ID_COLUMN_NAME),
                        result.getString(STUDY_PERIOD_COLUMN_NAME),
                        result.getDouble(PLANNED_COST_COLUMN_NAME),
                        result.getDouble(ACTUAL_COST_COLUMN_NAME));
            }

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }

        return courseInstanceCost;
    }

    /**
     * Increases the number of students in a course instance.
     *
     * @param instanceId       the instance if of the course instance.
     * @param numberOfStudents the number of students to the increase with.
     * @throws UniversityDBException ?????????????
     */
    public void updateNumOfStudents(String instanceId, int numberOfStudents) throws UniversityDBException {
        String failureMsg = "Could not update number of students";
        int rows = 0;

        try {
            updateNumStudentsStmt.setInt(1, numberOfStudents);
            updateNumStudentsStmt.setString(2, instanceId);

            rows = updateNumStudentsStmt.executeUpdate();
            if (rows != 1) {
                Util.handleException(connection, failureMsg, null);
            }
            connection.commit();
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        }
    }

    /**
     * Gets the instance id of a course.
     *
     * @param courseCode
     * @param studyPeriod
     * @param year
     * @return instance id
     * @throws UniversityDBException
     */

    public String readInstanceId(String courseCode, String studyPeriod, int year) throws UniversityDBException {
        String failureMsg = "Could not get instance id";
        ResultSet result = null;
        String instanceId = null;
        try {
            getInstanceIdStmt.setString(1, courseCode);
            getInstanceIdStmt.setString(2, studyPeriod);
            getInstanceIdStmt.setInt(3, year);
            result = getInstanceIdStmt.executeQuery();
            if (result.next()) {
                instanceId = result.getString(INSTANCE_ID_COLUMN_NAME);
            }
        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }
        return instanceId;
    }


    public List<Person> readPersons(String firstName, String lastName) throws UniversityDBException {
        String failureMsg = "Could not fetch person(s) with given name";
        ResultSet result = null;
        List<Person> persons = new ArrayList<>();
        try {
            getPersonStmt.setString(1, firstName);
            getPersonStmt.setString(2, lastName);

            result = getPersonStmt.executeQuery();

            while (result.next()) {
                persons.add(new Person(
                        result.getString(EMPLOYMENT_ID_COLUMN_NAME),
                        result.getString(FIRST_NAME_COLUMN_NAME),
                        result.getString(LAST_NAME_COLUMN_NAME),
                        result.getString(PERSONAL_NUMBER_COLUMN_NAME)));

            }

        } catch (SQLException e) {
            Util.handleException(connection, failureMsg, e);
        } finally {
            Util.closeResults(failureMsg, result);
        }

        return persons;
    }


    private void prepareStatements() throws SQLException {
        findCourseInstanceCostStmt = connection.prepareStatement(
                "SELECT " + COURSE_CODE_COLUMN_NAME + ", " + INSTANCE_ID_COLUMN_NAME + ", " + STUDY_PERIOD_COLUMN_NAME +
                        ", " + TOTAL_PLANNED_HOURS_COLUMN_NAME + " * ? AS " + PLANNED_COST_COLUMN_NAME +
                        ", " + TOTAL_ALLOCATED_HOURS_COLUMN_NAME + " * ? AS " + ACTUAL_COST_COLUMN_NAME +
                        " FROM " + COURSE_INSTANCE_HOURS_VIEW +
                        " WHERE " + COURSE_CODE_COLUMN_NAME + " = ? AND " +
                        STUDY_PERIOD_COLUMN_NAME + " = ?"
        );

        getInstanceIdStmt = connection.prepareStatement(
                "SELECT " + COURSE_INSTANCE_TABLE_NAME + "." + INSTANCE_ID_COLUMN_NAME +
                        " FROM " + COURSE_INSTANCE_TABLE_NAME +
                        " INNER JOIN " + COURSE_LAYOUT_TABLE_NAME +
                        " ON " + COURSE_INSTANCE_TABLE_NAME + ".course_layout_id = " + COURSE_LAYOUT_TABLE_NAME + ".id" +
                        " WHERE " + COURSE_LAYOUT_TABLE_NAME + "." + COURSE_CODE_COLUMN_NAME + " = ? AND " +
                        COURSE_INSTANCE_TABLE_NAME + "." + STUDY_PERIOD_COLUMN_NAME + " = ? AND " +
                        "EXTRACT(YEAR FROM " + COURSE_INSTANCE_TABLE_NAME + "." + STUDY_YEAR_COLUMN_NAME + ") = ?"
        );

        updateNumStudentsStmt = connection.prepareStatement(
                "UPDATE " + COURSE_INSTANCE_TABLE_NAME +
                        " SET " + NUM_STUDENTS_COLUMN_NAME + " = " + NUM_STUDENTS_COLUMN_NAME + " + ?" +
                        " WHERE " + INSTANCE_ID_COLUMN_NAME + " = ?"
        );

        getPersonStmt = connection.prepareStatement(
                "SELECT " + EMPLOYMENT_ID_COLUMN_NAME + " , " + FIRST_NAME_COLUMN_NAME + ", " + LAST_NAME_COLUMN_NAME + ", " + PERSONAL_NUMBER_COLUMN_NAME +
                        " FROM " + PERSON_TABLE_NAME + " INNER JOIN " + EMPLOYEE_TABLE_NAME
                        + " ON " + PERSON_TABLE_NAME + "." + ID_COLUMN_NAME + " = " + EMPLOYEE_TABLE_NAME + "." + PERSON_ID_COLUMN_NAME +
                        " WHERE " + FIRST_NAME_COLUMN_NAME + " = ?" +
                        " AND " + LAST_NAME_COLUMN_NAME + " = ?"
        );

    }
}