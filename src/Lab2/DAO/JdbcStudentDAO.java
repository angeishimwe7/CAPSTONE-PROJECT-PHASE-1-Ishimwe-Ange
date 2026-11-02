package Lab2.DAO;

import Lab3.dbConnection.DatabaseConnection;
import Lab1.SheCanCode.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStudentDAO implements StudentDAO {

    private static final String INSERT =
        "INSERT INTO students (id, name, email, major, student_type, year_level, thesis_topic) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
        "SELECT * FROM students WHERE id = ?";
    private static final String SELECT_ALL =
        "SELECT * FROM students ORDER BY id";
    private static final String SELECT_BY_MAJOR =
        "SELECT * FROM students WHERE major = ? ORDER BY id";
    private static final String UPDATE =
        "UPDATE students SET name = ?, email = ?, major = ?, year_level = ?, thesis_topic = ? WHERE id = ?";
    private static final String DELETE =
        "DELETE FROM students WHERE id = ?";

    @Override
    public void insert(Student s) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getMajor());
            bindTypeSpecificFields(ps, s, 5);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Could not insert student " + s.getId(), e);
        }
    }

    @Override
    public Optional<Student> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Could not load student " + id, e);
        }
    }

    @Override
    public List<Student> findAll() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Student> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Could not list students", e);
        }
    }

    @Override
    public List<Student> findByMajor(String major) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_MAJOR)) {
            ps.setString(1, major);   // bound as DATA, never as SQL
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Could not search students by major", e);
        }
    }

    @Override
    public void update(Student s) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(UPDATE)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getMajor());
            if (s instanceof UndergraduateStudent u) {
                ps.setInt(4, u.getYearLevel());
                ps.setNull(5, Types.VARCHAR);
            } else if (s instanceof GraduateStudent g) {
                ps.setNull(4, Types.INTEGER);
                ps.setString(5, g.getThesisTopic());
            }
            ps.setInt(6, s.getId());
            if (ps.executeUpdate() == 0) throw new DaoException("No student with id " + s.getId(), null);
        } catch (SQLException e) {
            throw new DaoException("Could not update student " + s.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;   // enrollments go too (ON DELETE CASCADE)
        } catch (SQLException e) {
            throw new DaoException("Could not delete student " + id, e);
        }
    }

    // ---- helpers -------------------------------------------------------

    /** Fills parameters 'start' .. 'start+2': type, year_level, thesis_topic. */
    private void bindTypeSpecificFields(PreparedStatement ps, Student s, int start) throws SQLException {
        if (s instanceof UndergraduateStudent u) {
            ps.setString(start, "UNDERGRAD");
            ps.setInt(start + 1, u.getYearLevel());
            ps.setNull(start + 2, Types.VARCHAR);
        } else if (s instanceof GraduateStudent g) {
            ps.setString(start, "GRAD");
            ps.setNull(start + 1, Types.INTEGER);
            ps.setString(start + 2, g.getThesisTopic());
        } else {
            throw new IllegalArgumentException("Unknown student type: " + s.getClass());
        }
    }

    /** Row -> correct subclass. This is where the table meets polymorphism. */
    private Student map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String major = rs.getString("major");
        if ("GRAD".equals(rs.getString("student_type"))) {
            return new GraduateStudent(id, name, email, major, rs.getString("thesis_topic"));
        }
        return new UndergraduateStudent(id, name, email, major, rs.getInt("year_level"));
    }
}
