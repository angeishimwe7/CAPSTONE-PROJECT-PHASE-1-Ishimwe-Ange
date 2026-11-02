package Lab2.DAO;

import java.sql.SQLException;

/** Wraps SQLException so business code isn't forced to import java.sql. */
public class DaoException extends RuntimeException {
    public DaoException(String message, Throwable cause) { super(message, cause); }

    /** SQLState of the underlying database error, or null if there isn't one. */
    public String getSqlState() {
        return getCause() instanceof SQLException e ? e.getSQLState() : null;
    }
}
