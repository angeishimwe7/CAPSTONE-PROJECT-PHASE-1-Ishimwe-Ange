package Lab2.DAO;

import java.util.List;

public interface EnrollmentDAO {
    void enroll(int studentId, String courseCode);
    boolean updateGrade(int studentId, String courseCode, double grade);
    boolean drop(int studentId, String courseCode);
    List<EnrollmentRecord> findByStudent(int studentId);
    List<EnrollmentRecord> findByCourse(String courseCode);

    /** JOIN across students, enrollments and courses (Exercise 3.1). Students with no courses still appear. */
    List<StudentCourseRow> findAllWithCourses();
}
