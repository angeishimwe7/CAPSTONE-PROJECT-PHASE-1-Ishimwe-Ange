package Lab1.SheCanCode;

import java.util.*;
import java.util.stream.Collectors;

/** Holds the collections that tie the system together (Exercise 1.3). */
public class UniversityService {
    private final Map<Integer, Student> students = new LinkedHashMap<>();   // lookup by id
    private final Map<String, Course> courses = new LinkedHashMap<>();      // lookup by code
    private final Map<Integer, Instructor> instructors = new LinkedHashMap<>();

    public void addStudent(Student s) {
        if (students.putIfAbsent(s.getId(), s) != null) throw new IllegalArgumentException("Duplicate student id " + s.getId());
    }
    public void addInstructor(Instructor i) {
        if (instructors.putIfAbsent(i.getId(), i) != null) throw new IllegalArgumentException("Duplicate instructor id " + i.getId());
    }
    public void addCourse(Course c) {
        if (courses.putIfAbsent(c.getCode(), c) != null) throw new IllegalArgumentException("Duplicate course " + c.getCode());
    }

    public Optional<Student> findStudent(int id) { return Optional.ofNullable(students.get(id)); }
    public Optional<Course> findCourse(String code) { return Optional.ofNullable(courses.get(code)); }

    public void enroll(int studentId, String courseCode) {
        Student s = findStudent(studentId).orElseThrow(() -> new NoSuchElementException("No student " + studentId));
        Course c = findCourse(courseCode).orElseThrow(() -> new NoSuchElementException("No course " + courseCode));
        if (!c.enroll(s)) System.out.println(s.getName() + " is already in " + courseCode);
    }

    public void assign(int instructorId, String courseCode) {
        Instructor i = instructors.get(instructorId);
        Course c = courses.get(courseCode);
        if (i == null || c == null) throw new NoSuchElementException("Unknown instructor or course");
        c.assignInstructor(i);
    }

    /** Instructor -> courses they teach. */
    public Map<Instructor, Set<Course>> instructorAssignments() {
        Map<Instructor, Set<Course>> map = new LinkedHashMap<>();
        instructors.values().forEach(i -> map.put(i, i.getAssignedCourses()));
        return map;
    }

    /** Course -> students (roster view). */
    public Map<Course, Set<Student>> courseRosters() {
        Map<Course, Set<Student>> map = new LinkedHashMap<>();
        courses.values().forEach(c -> map.put(c, c.getRoster()));
        return map;
    }

    public List<Student> studentsSortedByGpa() {
        return students.values().stream()
                .sorted(Comparator.comparingDouble(Student::calculateGPA).reversed())
                .collect(Collectors.toList());
    }

    public Collection<Student> getStudents() { return students.values(); }
}
