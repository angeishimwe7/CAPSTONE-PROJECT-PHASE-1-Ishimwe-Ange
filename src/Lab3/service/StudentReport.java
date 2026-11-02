package Lab3.service;

import Lab2.DAO.StudentCourseRow;
import Lab1.SheCanCode.Student;
import java.util.List;

/** A student, their enrolled courses (from the JOIN) and their GPA (from Lab 1's polymorphic calculateGPA). */
public record StudentReport(Student student, List<StudentCourseRow> courses, double gpa) { }
