package Lab2.DAO;

/** One row of the enrollments table. grade is null until the student is graded. */
public record EnrollmentRecord(int studentId, String courseCode, Double grade) { }
