package Lab1.SheCanCode;

import Lab1.SheCanCode.*;

public class Main {
    public static void main(String[] args) {
        UniversityService uni = new UniversityService();

        uni.addInstructor(new Instructor(1, "Dr. Uwase", "uwase@uni.edu", "Computer Science"));
        uni.addInstructor(new Instructor(2, "Dr. Mugisha", "mugisha@uni.edu", "Mathematics"));

        uni.addCourse(new Course("CS101", "Intro to Programming", 3));
        uni.addCourse(new Course("MA201", "Linear Algebra", 4));
        uni.addCourse(new Course("CS501", "Advanced Algorithms", 3));

        uni.assign(1, "CS101");
        uni.assign(1, "CS501");
        uni.assign(2, "MA201");

        Student alice = new UndergraduateStudent(100, "Alice", "alice@uni.edu", "CS", 2);
        Student bob = new GraduateStudent(200, "Bob", "bob@uni.edu", "CS", "Graph algorithms");
        uni.addStudent(alice);
        uni.addStudent(bob);

        uni.enroll(100, "CS101");
        uni.enroll(100, "MA201");
        uni.enroll(200, "CS501");
        uni.enroll(200, "MA201");
        uni.enroll(200, "MA201"); // duplicate: Set ignores it

        // Same low grade in MA201 -> different GPA impact by polymorphism
        alice.recordGrade(uni.findCourse("CS101").get(), 4.0);
        alice.recordGrade(uni.findCourse("MA201").get(), 1.0); // D
        bob.recordGrade(uni.findCourse("CS501").get(), 4.0);
        bob.recordGrade(uni.findCourse("MA201").get(), 1.0);   // below C -> counts as 0

        System.out.println("=== Students ===");
        uni.getStudents().forEach(s -> System.out.printf("%s | %s | GPA %.2f%n", s, s.getLevel(), s.calculateGPA()));

        System.out.println("\n=== Course rosters ===");
        uni.courseRosters().forEach((c, r) -> {
            System.out.println(c);
            r.forEach(s -> System.out.println("   - " + s.getName()));
        });

        System.out.println("\n=== Instructor assignments ===");
        uni.instructorAssignments().forEach((i, cs) -> {
            System.out.print(i.getName() + ": ");
            cs.forEach(c -> System.out.print(c.getCode() + " "));
            System.out.println();
        });

        System.out.println("\n=== Ranked by GPA ===");
        uni.studentsSortedByGpa().forEach(s -> System.out.printf("%s %.2f%n", s.getName(), s.calculateGPA()));
    }
}
