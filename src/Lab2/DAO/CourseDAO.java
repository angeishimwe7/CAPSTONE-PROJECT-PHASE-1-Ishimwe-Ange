package Lab2.DAO;

import Lab1.SheCanCode.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDAO {
    void insert(Course course);
    Optional<Course> findByCode(String code);
    List<Course> findAll();
    void update(Course course);
    boolean delete(String code);
}
