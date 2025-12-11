package se.kth.iv1351.handledb.controller;

import se.kth.iv1351.handledb.integration.CourseDAO;
import se.kth.iv1351.handledb.integration.DAOCreator;
import se.kth.iv1351.handledb.integration.TeacherDAO;
import se.kth.iv1351.handledb.integration.UniversityDBException;
import se.kth.iv1351.handledb.model.*;

import java.util.List;

/**
 * The controller for the application. All calls to the model pass through here.
 * The controller also calls the DAO.
 */
public class Controller {
    private final DAOCreator daoCreator;
    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;

    /**
     * Creates a new instance and initializes DAO objects that are used to
     * access the database.
     *
     * @throws UniversityDBException if the database connection fails or
     *                               if DAO creation fails.
     */
    public Controller() throws UniversityDBException {
        daoCreator = new DAOCreator();
        courseDAO = daoCreator.getCourseDAO();
        teacherDAO = daoCreator.getTeacherDAO();
    }


    /**
     * Finds the planned and actual cost of a course instance.
     *
     * @param course_code is the course code for the course instance we want to find the cost of.
     * @param studyPeriod is the period of the course instance we want to check.
     * @return the course instance cost.
     * @throws CourseException if the course code or study period is null, or if a call to the DB fails.
     */
    public InstanceCostDTO readCourseInstanceCost(String course_code, String studyPeriod) throws CourseException {
        String failureMessage = "Could not find course instance for " + course_code;
        if (course_code == null || studyPeriod == null) {
            throw new CourseException(failureMessage);
        }
        try {
            return courseDAO.readCourseInstanceCost(course_code, studyPeriod);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    /**
     * Increases the number of students for a course instnace.
     *
     * @param instanceId  is the instance id of a course instance.
     * @param numStudents the number of students to increase the course instance with.
     * @throws CourseException if the instance id does not exist or if the number of students to
     *                         increase with is non-positive, or if a call to the DB fails..
     */
    public void updateNumberOfStudents(String instanceId, int numStudents) throws CourseException {
        String failureMessage = "could not increase number of students with: " + numStudents;
        if (instanceId == null || numStudents < 1) {
            throw new CourseException("Could not increase number of students for: " + instanceId);
        }
        try {
            courseDAO.updateNumOfStudents(instanceId, numStudents);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    /**
     * Allocates the teaching load. If the employee has allocations in more than 4 course instances in the same period,
     * and the new load does not belong to an already existing allocation the request is rejected.
     *
     * @param allocationsDTO is the data transfer object that holds data about the allocations.
     * @throws CourseException if the number of allocations in the DTO is greater than the maximum
     *                         amount of allowed allocations in a particular period or if a call to the DB fails.
     */
    public void updateTeachingLoad(TeacherAllocationDTO allocationsDTO) throws CourseException {
        String failureMessage = "could not allocate teaching load";
        if (allocationsDTO == null) {
            throw new CourseException(failureMessage);
        }
        try {

            boolean hasAllocation = teacherDAO.readAllocationInCourseInstance(allocationsDTO.getEmploymentId(), allocationsDTO.getInstanceId());

            if (!hasAllocation) {
                int maxAllocations = teacherDAO.readMaxAllocations();
                int numAllocations = teacherDAO.readNumTeacherAllocations(allocationsDTO.getEmploymentId(), allocationsDTO.getInstanceId());
                if (numAllocations >= maxAllocations) {
                    throw new CourseException("Maximum number of teacher allocations exceeded: " + maxAllocations);
                }
            }
            teacherDAO.updateTeachingLoad(allocationsDTO);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    /**
     * Deallocates the teaching load.
     *
     * @param allocationsDTO is the data transfer object that holds data about the allocations.
     * @throws CourseException if the data transfer object does not exist or if a call to the DB fails.
     */
    public void deleteTeachingLoad(TeacherAllocationDTO allocationsDTO) throws CourseException {
        String failureMessage = "could not deallocate teaching load";
        if (allocationsDTO == null) {
            throw new CourseException(failureMessage);
        }
        try {
            teacherDAO.deleteTeachingLoad(allocationsDTO);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    /**
     * Adds a teaching activity.
     *
     * @param activityDTO is the data transfer object that stores data about the activity.
     * @throws CourseException if the DTO does not exist or if a call to the DB fails.
     */
    public void updateTeachingActivity(ActivityDTO activityDTO) throws CourseException {
        String failureMessage = "could not add teaching activity";
        if (activityDTO == null) {
            throw new CourseException(failureMessage);
        }
        try {
            teacherDAO.updateTeachingActivity(activityDTO);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    public List<AllocatedActivityDTO> getAllocatedActivities(String activityName) throws CourseException {
        String failureMessage = "could not get allocated activities";

        if (activityName == null) {
            throw new CourseException(failureMessage);
        }
        try {
            return teacherDAO.readAllocatedActivities(activityName);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

    public String readInstanceID(String courseCode, String studyPeriod, int year) throws CourseException {
        String failureMessage = "could not get instance ID";

        if (courseCode == null || studyPeriod == null) {
            throw new CourseException(failureMessage);
        }

        try {
            return courseDAO.readInstanceId(courseCode, studyPeriod, year);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }

    }

    public List<Person> readPerson(String firstName, String lastName) throws CourseException {
        String failureMessage = "could not get persons by name";
        if (firstName == null || lastName == null) {
            throw new CourseException(failureMessage);
        }
        try {
            return courseDAO.readPersons(firstName, lastName);
        } catch (UniversityDBException e) {
            throw new CourseException(failureMessage, e);
        }
    }

}


