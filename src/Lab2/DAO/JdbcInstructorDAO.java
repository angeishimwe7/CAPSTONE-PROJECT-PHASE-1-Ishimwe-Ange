package Lab2.DAO;

import Lab3.dbConnection.DatabaseConnection;
import Lab1.SheCanCode.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcInstructorDAO implements InstructorDAO {
    private static final String SELECT = "SELECT id, name, email, department FROM instructors ";

    @Override
    public Optional<Instructor> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Could not load instructor " + id, e);
        }
    }

    @Override
    public List<Instructor> findAll() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            List<Instructor> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Could not list instructors", e);
        }
    }

    private Instructor map(ResultSet rs) throws SQLException {
        return new Instructor(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("department"));
    }
}
