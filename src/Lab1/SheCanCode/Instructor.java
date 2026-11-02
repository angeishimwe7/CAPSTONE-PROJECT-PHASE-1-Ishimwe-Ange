package Lab1.SheCanCode;

import java.util.*;

public class Instructor extends Person {
    private String department;
    private final Set<Course> assignedCourses = new LinkedHashSet<>();

    public Instructor(int id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Set<Course> getAssignedCourses() { return Collections.unmodifiableSet(assignedCourses); }

    void addCourse(Course c) { assignedCourses.add(c); }
    void removeCourse(Course c) { assignedCourses.remove(c); }

    @Override public String toString() {
        return super.toString().replaceFirst("}$", "") + ", department='" + department
                + "', courses=" + assignedCourses.size() + "}";
    }
}