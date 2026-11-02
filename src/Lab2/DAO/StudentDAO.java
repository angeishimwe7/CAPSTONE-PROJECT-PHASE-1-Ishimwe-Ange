package Lab2.DAO;

import Lab1.SheCanCode.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDAO {
    void insert(Student student);
    Optional<Student> findById(int id);
    List<Student> findAll();
    List<Student> findByMajor(String major);
    void update(Student student);
    boolean delete(int id);
}
