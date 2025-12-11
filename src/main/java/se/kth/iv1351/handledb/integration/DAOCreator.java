package se.kth.iv1351.handledb.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOCreator {
    private final Connection connection;
    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;

    public DAOCreator() throws UniversityDBException {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/uni");
            connection.setAutoCommit(false);
            courseDAO = new CourseDAO(connection);
            teacherDAO = new TeacherDAO(connection);
        } catch (SQLException e) {
            throw new UniversityDBException("Could not connect to data source", e);
        }
    }

    public CourseDAO getCourseDAO() {
        return courseDAO;
    }

    public TeacherDAO getTeacherDAO() {
        return teacherDAO;
    }
}
