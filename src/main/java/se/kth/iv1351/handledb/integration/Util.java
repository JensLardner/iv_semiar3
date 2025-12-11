package se.kth.iv1351.handledb.integration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class Util {

    protected static void handleException(Connection connection, String failureMsg, Exception cause) throws UniversityDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException e) {
            completeFailureMsg += ". Also failed to roll back transaction" + e.getMessage();
        }

        if (cause != null) {
            throw new UniversityDBException(completeFailureMsg, cause);
        } else {
            throw new UniversityDBException(completeFailureMsg);
        }
    }

    protected static void closeResults(String failureMSG, ResultSet result) throws UniversityDBException {
        if (result == null)
            return;
        try {
            result.close();
        } catch (Exception e) {
            throw new UniversityDBException(failureMSG + "Could not close result set. ", e);
        }
    }


}
