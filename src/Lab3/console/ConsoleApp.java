package Lab3.console;

import Lab2.DAO.*;
import Lab3.dbConnection.DatabaseConnection;
import Lab1.SheCanCode.*;
import Lab3.service.RegistrarService;
import Lab3.service.StudentReport;
import Lab3.service.ValidationException;

import java.sql.Connection;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleApp {
    private final RegistrarService service;
    private final ConsoleIO io;

    public ConsoleApp(RegistrarService service, ConsoleIO io) {
        this.service = service;
        this.io = io;
    }

    public static void main(String[] args) {
        try (Connection c = DatabaseConnection.getConnection()) {
            System.out.println("Connected to database.");
        } catch (Exception e) {
            System.out.println("Cannot connect to the database: " + e.getMessage());
            System.out.println("Check that PostgreSQL is running and DB_URL / DB_USER / DB_PASSWORD are set.");
            return;
        }
        RegistrarService service = new RegistrarService(
                new JdbcStudentDAO(), new JdbcCourseDAO(), new JdbcInstructorDAO(), new JdbcEnrollmentDAO());
        try (Scanner scanner = new Scanner(System.in)) {
            new ConsoleApp(service, new ConsoleIO(scanner)).run();
        }
    }

    public void run() {
        System.out.println("\n=== University Management Console ===");
        try {
            while (true) {
                printMenu();
                int choice = io.readInt("Choose an option");
                if (choice == 0) break;
                runSafely(choice);
            }
        } catch (NoSuchElementException endOfInput) {
            // input stream closed (e.g. piped input ended) - just leave
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("""

                 1) Add student            6) List courses
                 2) Remove student         7) Enroll student in course
                 3) List students          8) Drop student from course
                 4) Add course             9) Record a grade
                 5) Remove course         10) View students with their courses
                 0) Exit""");
    }

    /** Every action runs inside here, so one bad input never crashes the app (Exercise 3.2). */
    private void runSafely(int choice) {
        try {
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> removeStudent();
                case 3 -> listStudents();
                case 4 -> addCourse();
                case 5 -> removeCourse();
                case 6 -> listCourses();
                case 7 -> enroll();
                case 8 -> drop();
                case 9 -> recordGrade();
                case 10 -> viewReport();
                default -> System.out.println("Unknown option. Please choose from the menu.");
            }
        } catch (ValidationException | IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (DaoException e) {
            System.out.println(friendlyMessage(e));
        }
    }

    // ---- actions ----
    private void addStudent() {
        int id = io.readInt("Student id");
        String name = io.readRequired("Name");
        String email = io.readRequired("Email");
        String major = io.readRequired("Major");
        String type = io.readRequired("Type: (U)ndergraduate or (G)raduate").toUpperCase();

        Student s;
        if (type.startsWith("U")) {
            int year = io.readInt("Year level (1-4)");
            if (year < 1 || year > 4) throw new ValidationException("Year level must be between 1 and 4.");
            s = new UndergraduateStudent(id, name, email, major, year);
        } else if (type.startsWith("G")) {
            s = new GraduateStudent(id, name, email, major, io.readRequired("Thesis topic"));
        } else {
            throw new ValidationException("Type must be U or G.");
        }
        service.addStudent(s);
        System.out.println("Added " + s.getName() + ".");
    }

    private void removeStudent() {
        int id = io.readInt("Student id to remove");
        if (io.confirm("This also deletes their enrollments. Continue?")) {
            service.removeStudent(id);
            System.out.println("Student removed.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void listStudents() {
        var list = service.listStudents();
        if (list.isEmpty()) { System.out.println("No students yet."); return; }
        System.out.printf("%-6s %-20s %-26s %-12s %s%n", "ID", "Name", "Email", "Major", "Level");
        list.forEach(s -> System.out.printf("%-6d %-20s %-26s %-12s %s%n",
                s.getId(), s.getName(), s.getEmail(), s.getMajor(), s.getLevel()));
    }

    private void addCourse() {
        String code = io.readRequired("Course code (e.g. CS101)");
        String title = io.readRequired("Title");
        int credits = io.readInt("Credits");
        System.out.println("Available instructors:");
        service.listInstructors().forEach(i ->
                System.out.printf("  %d - %s (%s)%n", i.getId(), i.getName(), i.getDepartment()));
        Integer instructorId = io.readOptionalInt("Instructor id (Enter to skip)");
        service.addCourse(code, title, credits, instructorId);
        System.out.println("Course added.");
    }

    private void removeCourse() {
        String code = io.readRequired("Course code to remove");
        if (io.confirm("This also deletes its enrollments. Continue?")) {
            service.removeCourse(code);
            System.out.println("Course removed.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void listCourses() {
        var list = service.listCourses();
        if (list.isEmpty()) { System.out.println("No courses yet."); return; }
        System.out.printf("%-8s %-28s %-8s %s%n", "Code", "Title", "Credits", "Instructor");
        list.forEach(c -> System.out.printf("%-8s %-28s %-8d %s%n", c.getCode(), c.getTitle(), c.getCredits(),
                c.getInstructor() == null ? "TBA" : c.getInstructor().getName()));
    }

    private void enroll() {
        service.enroll(io.readInt("Student id"), io.readRequired("Course code"));
        System.out.println("Enrolled.");
    }

    private void drop() {
        service.drop(io.readInt("Student id"), io.readRequired("Course code"));
        System.out.println("Dropped.");
    }

    private void recordGrade() {
        int id = io.readInt("Student id");
        String code = io.readRequired("Course code");
        double grade = io.readGrade("Grade points (0.0 - 4.0)");
        service.recordGrade(id, code, grade);
        System.out.println("Grade saved.");
    }

    private void viewReport() {
        var reports = service.buildReports();
        if (reports.isEmpty()) { System.out.println("No students yet."); return; }
        for (StudentReport r : reports) {
            Student s = r.student();
            System.out.printf("%n[%d] %s - %s - GPA %.2f%n", s.getId(), s.getName(), s.getLevel(), r.gpa());
            if (r.courses().isEmpty()) {
                System.out.println("      (not enrolled in any course)");
            }
            r.courses().forEach(c -> System.out.printf("      %-7s %-26s %d cr  grade: %s%n",
                    c.courseCode(), c.courseTitle(), c.credits(), c.grade() == null ? "-" : c.grade()));
        }
    }

    /** Turns raw SQL states into messages a normal person can act on. */
    static String friendlyMessage(DaoException e) {
        String state = e.getSqlState();
        if (state == null) return "Database error: " + e.getMessage();
        if (state.equals("23505")) return "That record already exists (duplicate id or email).";
        if (state.equals("23503")) return "That change conflicts with related data (something referenced doesn't exist, or is still in use).";
        if (state.equals("23514")) return "A value is outside the allowed range.";
        if (state.startsWith("08")) return "Lost connection to the database. Is PostgreSQL still running?";
        return "Database error (" + state + "): " + e.getMessage();
    }
}
