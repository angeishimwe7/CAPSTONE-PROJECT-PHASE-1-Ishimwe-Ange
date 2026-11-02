package Lab2.DAO;

/** One row of the students-LEFT JOIN-enrollments-LEFT JOIN-courses query. Course fields are null for students with no courses. */
public record StudentCourseRow(int studentId, String courseCode, String courseTitle, Integer credits, Double grade) { }
