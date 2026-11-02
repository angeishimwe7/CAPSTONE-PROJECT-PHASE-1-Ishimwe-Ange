package Lab2.DAO;

import Lab3.dbConnection.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcEnrollmentDAO implements EnrollmentDAO {

    private static final String INSERT =
        "INSERT INTO enrollments (student_id, course_code) VALUES (?, ?)";
    private static final String UPDATE_GRADE =
        "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_code = ?";
    private static final String DELETE =
        "DELETE FROM enrollments WHERE student_id = ? AND course_code = ?";
    private static final String BY_STUDENT =
        "SELECT student_id, course_code, grade FROM enrollments WHERE student_id = ? ORDER BY course_code";
    private static final String BY_COURSE =
        "SELECT student_id, course_code, grade FROM enrollments WHERE course_code = ? ORDER BY student_id";

    @Override
    public void enroll(int studentId, String courseCode) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Primary key blocks duplicates, foreign keys block unknown students/courses
            throw new DaoException("Could not enroll " + studentId + " in " + courseCode, e);
        }
    }

    @Override
    public boolean updateGrade(int studentId, String courseCode, double grade) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE_GRADE)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(grade));
            ps.setInt(2, studentId);
            ps.setString(3, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Could not update grade", e);
        }
    }

    @Override
    public boolean drop(int studentId, String courseCode) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Could not drop enrollment", e);
        }
    }

    private static final String ALL_WITH_COURSES =
        "SELECT s.id AS student_id, c.code, c.title, c.credits, e.grade " +
        "FROM students s " +
        "LEFT JOIN enrollments e ON e.student_id = s.id " +
        "LEFT JOIN courses c ON c.code = e.course_code " +
        "ORDER BY s.id, c.code";

    @Override
    public List<StudentCourseRow> findAllWithCourses() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(ALL_WITH_COURSES);
             ResultSet rs = ps.executeQuery()) {
            List<StudentCourseRow> list = new ArrayList<>();
            while (rs.next()) {
                int credits = rs.getInt("credits");
                Integer creditsOrNull = rs.wasNull() ? null : credits;
                double g = rs.getDouble("grade");
                Double grade = rs.wasNull() ? null : g;
                list.add(new StudentCourseRow(rs.getInt("student_id"), rs.getString("code"),
                        rs.getString("title"), creditsOrNull, grade));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Could not load students with their courses", e);
        }
    }

    @Override
    public List<EnrollmentRecord> findByStudent(int studentId) {
        return query(BY_STUDENT, ps -> ps.setInt(1, studentId));
    }

    @Override
    public List<EnrollmentRecord> findByCourse(String courseCode) {
        return query(BY_COURSE, ps -> ps.setString(1, courseCode));
    }

    // ---- helpers -------------------------------------------------------

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<EnrollmentRecord> query(String sql, Binder binder) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<EnrollmentRecord> list = new ArrayList<>();
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    Double grade = rs.wasNull() ? null : g;
                    list.add(new EnrollmentRecord(rs.getInt("student_id"), rs.getString("course_code"), grade));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Enrollment query failed", e);
        }
    }
}
