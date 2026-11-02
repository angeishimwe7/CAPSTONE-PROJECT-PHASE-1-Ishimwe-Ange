package Lab2.DAO;

import Lab1.SheCanCode.Instructor;
import java.util.List;
import java.util.Optional;

public interface InstructorDAO {
    Optional<Instructor> findById(int id);
    List<Instructor> findAll();
}
