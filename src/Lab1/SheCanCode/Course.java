package Lab1.SheCanCode;

import java.util.*;

public class Course {
    private final String code;
    private String title;
    private int credits;
    private Instructor instructor;
    private final Set<Student> roster = new LinkedHashSet<>();

    public Course(String code, String title, int credits) {
        if (credits <= 0) throw new IllegalArgumentException("Credits must be positive");
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public Instructor getInstructor() { return instructor; }
    public Set<Student> getRoster() { return Collections.unmodifiableSet(roster); }

    public void assignInstructor(Instructor newInstructor) {
        if (instructor != null) instructor.removeCourse(this);
        instructor = newInstructor;
        if (newInstructor != null) newInstructor.addCourse(this);
    }

    /** Keeps both sides of the relationship consistent. Returns false if already enrolled. */
    public boolean enroll(Student s) {
        if (!roster.add(s)) return false;
        s.addCourse(this);
        return true;
    }

    public boolean drop(Student s) {
        if (!roster.remove(s)) return false;
        s.removeCourse(this);
        return true;
    }

    @Override public boolean equals(Object o) { return o instanceof Course c && code.equals(c.code); }
    @Override public int hashCode() { return code.hashCode(); }

    @Override public String toString() {
        return "Course{code='" + code + "', title='" + title + "', credits=" + credits
                + ", instructor=" + (instructor == null ? "TBA" : instructor.getName())
                + ", enrolled=" + roster.size() + "}";
    }
}