package Lab2.DAO;

import Lab1.SheCanCode.*;

/** Runs every Lab 2 operation once. Needs schema.sql already executed. Safe to run many times. */
public class Lab2Demo {
    public static void main(String[] args) {
        StudentDAO students = new JdbcStudentDAO();
        CourseDAO courses = new JdbcCourseDAO();
        EnrollmentDAO enrollments = new JdbcEnrollmentDAO();

        // Clean up leftovers from a previous run so this can be run again
        students.delete(100);
        students.delete(200);

        // CREATE: insert students
        students.insert(new UndergraduateStudent(100, "Alice", "alice@uni.edu", "CS", 2));
        students.insert(new GraduateStudent(200, "Bob", "bob@uni.edu", "CS", "Graph algorithms"));
        System.out.println("Inserted 2 students.");

        // READ: list courses (joined with their instructor)
        System.out.println("\n=== Courses ===");
        courses.findAll().forEach(System.out::println);

        // Enroll, then UPDATE enrollments (grades)
        enrollments.enroll(100, "CS101");
        enrollments.enroll(100, "MA201");
        enrollments.enroll(200, "CS501");
        enrollments.updateGrade(100, "CS101", 4.0);
        enrollments.updateGrade(100, "MA201", 1.0);

        System.out.println("\n=== Alice's enrollments ===");
        enrollments.findByStudent(100).forEach(System.out::println);

        // Rebuild Alice from DB data and reuse the Lab 1 polymorphic GPA
        Student alice = students.findById(100).orElseThrow();
        for (EnrollmentRecord r : enrollments.findByStudent(100)) {
            Course c = courses.findByCode(r.courseCode()).orElseThrow();
            c.enroll(alice);
            if (r.grade() != null) alice.recordGrade(c, r.grade());
        }
        System.out.printf("%nAlice (%s) GPA from DB data: %.2f%n", alice.getLevel(), alice.calculateGPA());

        // SQL injection attempt: treated as plain text, finds nothing, drops nothing
        System.out.println("\nInjection attempt returns: "
                + students.findByMajor("CS'; DROP TABLE students; --").size() + " rows");
        System.out.println("Students still there: " + students.findAll().size());

        // DELETE a student (their enrollments cascade away)
        System.out.println("\nDeleted Bob? " + students.delete(200));
        System.out.println("Bob's enrollments left: " + enrollments.findByStudent(200).size());
    }
}