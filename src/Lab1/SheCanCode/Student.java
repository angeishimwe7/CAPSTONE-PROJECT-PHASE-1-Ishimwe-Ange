package Lab1.SheCanCode;

import java.util.*;

public abstract class Student extends Person {
    private String major;
    private final Set<Course> enrolledCourses = new LinkedHashSet<>();
    private final Map<Course, Double> grades = new LinkedHashMap<>(); // grade points 0.0 - 4.0

    protected Student(int id, String name, String email, String major) {
        super(id, name, email);
        this.major = major;
    }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public Set<Course> getEnrolledCourses() { return Collections.unmodifiableSet(enrolledCourses); }
    public Map<Course, Double> getGrades() { return Collections.unmodifiableMap(grades); }

    // Package-private: only Course.enroll/drop should touch this, keeping both sides in sync.
    void addCourse(Course c) { enrolledCourses.add(c); }
    void removeCourse(Course c) { enrolledCourses.remove(c); grades.remove(c); }

    public void recordGrade(Course course, double gradePoints) {
        if (!enrolledCourses.contains(course))
            throw new IllegalStateException(getName() + " is not enrolled in " + course.getCode());
        if (gradePoints < 0.0 || gradePoints > 4.0)
            throw new IllegalArgumentException("Grade points must be between 0.0 and 4.0");
        grades.put(course, gradePoints);
    }

    /** Polymorphic hook: how a single grade counts toward GPA depends on student level. */
    protected abstract double effectiveGradePoints(double gradePoints);

    /** Polymorphic: each subclass defines its own GPA rules. */
    public abstract double calculateGPA();

    /** Shared credit-weighted average used by subclasses. */
    protected double weightedAverage() {
        double points = 0; int credits = 0;
        for (Map.Entry<Course, Double> e : grades.entrySet()) {
            int cr = e.getKey().getCredits();
            points += effectiveGradePoints(e.getValue()) * cr;
            credits += cr;
        }
        return credits == 0 ? 0.0 : points / credits;
    }

    public abstract String getLevel();

    @Override public String toString() {
        return super.toString().replaceFirst("}$", "") + ", major='" + major + "', courses=" + enrolledCourses.size() + "}";
    }
}
