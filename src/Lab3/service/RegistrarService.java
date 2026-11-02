package Lab3.service;

import Lab2.DAO.*;
import Lab1.SheCanCode.*;

import java.util.*;

/**
 * Business rules live here. The console talks to this class;
 * this class talks to the DAOs. Neither side touches SQL.
 */
public class RegistrarService {
    private final StudentDAO students;
    private final CourseDAO courses;
    private final InstructorDAO instructors;
    private final EnrollmentDAO enrollments;

    public RegistrarService(StudentDAO students, CourseDAO courses,
                            InstructorDAO instructors, EnrollmentDAO enrollments) {
        this.students = students;
        this.courses = courses;
        this.instructors = instructors;
        this.enrollments = enrollments;
    }

    // ---- students ----
    public void addStudent(Student s) {
        if (students.findById(s.getId()).isPresent())
            throw new ValidationException("A student with id " + s.getId() + " already exists.");
        students.insert(s);
    }

    public void removeStudent(int id) {
        if (!students.delete(id)) throw new ValidationException("No student with id " + id + ".");
    }

    public List<Student> listStudents() { return students.findAll(); }

    // ---- courses ----
    public void addCourse(String code, String title, int credits, Integer instructorId) {
        code = normalizeCode(code);
        if (title == null || title.isBlank()) throw new ValidationException("Course title is required.");
        if (credits <= 0 || credits > 12) throw new ValidationException("Credits must be between 1 and 12.");
        if (courses.findByCode(code).isPresent())
            throw new ValidationException("Course " + code + " already exists.");

        Course course = new Course(code, title.trim(), credits);
        if (instructorId != null) {
            Instructor i = instructors.findById(instructorId)
                    .orElseThrow(() -> new ValidationException("No instructor with id " + instructorId + "."));
            course.assignInstructor(i);
        }
        courses.insert(course);
    }

    public void removeCourse(String code) {
        code = normalizeCode(code);
        if (!courses.delete(code)) throw new ValidationException("No course " + code + ".");
    }

    public List<Course> listCourses() { return courses.findAll(); }
    public List<Instructor> listInstructors() { return instructors.findAll(); }

    // ---- enrollments ----
    public void enroll(int studentId, String courseCode) {
        courseCode = normalizeCode(courseCode);
        requireStudent(studentId);
        requireCourse(courseCode);
        final String code = courseCode;
        boolean already = enrollments.findByStudent(studentId).stream().anyMatch(r -> r.courseCode().equals(code));
        if (already) throw new ValidationException("Student " + studentId + " is already enrolled in " + code + ".");
        enrollments.enroll(studentId, code);
    }

    public void drop(int studentId, String courseCode) {
        courseCode = normalizeCode(courseCode);
        if (!enrollments.drop(studentId, courseCode))
            throw new ValidationException("Student " + studentId + " is not enrolled in " + courseCode + ".");
    }

    public void recordGrade(int studentId, String courseCode, double gradePoints) {
        courseCode = normalizeCode(courseCode);
        if (gradePoints < 0.0 || gradePoints > 4.0)
            throw new ValidationException("Grade must be between 0.0 and 4.0.");
        if (!enrollments.updateGrade(studentId, courseCode, gradePoints))
            throw new ValidationException("Student " + studentId + " is not enrolled in " + courseCode + ".");
    }

    // ---- report: JOIN data + Lab 1 GPA logic ----
    public List<StudentReport> buildReports() {
        Map<Integer, List<StudentCourseRow>> rowsByStudent = new LinkedHashMap<>();
        for (StudentCourseRow row : enrollments.findAllWithCourses()) {
            rowsByStudent.computeIfAbsent(row.studentId(), k -> new ArrayList<>());
            if (row.courseCode() != null) rowsByStudent.get(row.studentId()).add(row);
        }

        List<StudentReport> reports = new ArrayList<>();
        for (Student s : students.findAll()) {
            List<StudentCourseRow> rows = rowsByStudent.getOrDefault(s.getId(), List.of());
            for (StudentCourseRow r : rows) {
                Course c = new Course(r.courseCode(), r.courseTitle(), r.credits());
                c.enroll(s);
                if (r.grade() != null) s.recordGrade(c, r.grade());
            }
            reports.add(new StudentReport(s, rows, s.calculateGPA()));
        }
        return reports;
    }

    // ---- helpers ----
    private void requireStudent(int id) {
        if (students.findById(id).isEmpty()) throw new ValidationException("No student with id " + id + ".");
    }

    private void requireCourse(String code) {
        if (courses.findByCode(code).isEmpty()) throw new ValidationException("No course " + code + ".");
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) throw new ValidationException("Course code is required.");
        return code.trim().toUpperCase();
    }
}
