package Lab2.DAO;

import Lab3.dbConnection.DatabaseConnection;
import Lab1.SheCanCode.Course;
import Lab1.SheCanCode.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCourseDAO implements CourseDAO {

    // LEFT JOIN so courses without an instructor still show up
    private static final String SELECT_BASE =
        "SELECT c.code, c.title, c.credits, " +
        "       i.id AS i_id, i.name AS i_name, i.email AS i_email, i.department AS i_dept " +
        "FROM courses c LEFT JOIN instructors i ON c.instructor_id = i.id ";

    private static final String INSERT =
        "INSERT INTO courses (code, title, credits, instructor_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
        "UPDATE courses SET title = ?, credits = ?, instructor_id = ? WHERE code = ?";
    private static final String DELETE =
        "DELETE FROM courses WHERE code = ?";

    @Override
    public void insert(Course course) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            setInstructorId(ps, 4, course);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Could not insert course " + course.getCode(), e);
        }
    }

    @Override
    public Optional<Course> findByCode(String code) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + "WHERE c.code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Could not load course " + code, e);
        }
    }

    @Override
    public List<Course> findAll() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + "ORDER BY c.code");
             ResultSet rs = ps.executeQuery()) {
            List<Course> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Could not list courses", e);
        }
    }

    @Override
    public void update(Course course) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE)) {
            ps.setString(1, course.getTitle());
            ps.setInt(2, course.getCredits());
            setInstructorId(ps, 3, course);
            ps.setString(4, course.getCode());
            if (ps.executeUpdate() == 0) throw new DaoException("No course " + course.getCode(), null);
        } catch (SQLException e) {
            throw new DaoException("Could not update course " + course.getCode(), e);
        }
    }

    @Override
    public boolean delete(String code) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Could not delete course " + code, e);
        }
    }

    private void setInstructorId(PreparedStatement ps, int index, Course course) throws SQLException {
        if (course.getInstructor() == null) ps.setNull(index, Types.INTEGER);
        else ps.setInt(index, course.getInstructor().getId());
    }

    private Course map(ResultSet rs) throws SQLException {
        Course course = new Course(rs.getString("code"), rs.getString("title"), rs.getInt("credits"));
        int instructorId = rs.getInt("i_id");
        if (!rs.wasNull()) {
            course.assignInstructor(new Instructor(instructorId, rs.getString("i_name"),
                    rs.getString("i_email"), rs.getString("i_dept")));
        }
        return course;
    }
}
