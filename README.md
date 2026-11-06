# University Management Console

A console-based student management system built with **Java, JDBC and PostgreSQL**.
Phase 1 Capstone, Java Backend (Labs 1 to 3).

## Features
- Add and remove students (undergraduate or graduate) and courses
- Enroll and drop students, and record grades
- View every student with their enrolled courses (JOIN query) and GPA
- Input validation and friendly error messages for invalid input and database errors

## Technologies
- Java 17+
- PostgreSQL and JDBC (`org.postgresql:postgresql:42.7.4`)
- IntelliJ IDEA
- Git and GitHub

## Project structure
```
src/
  Lab1/SheCanCode/   Domain model (OOP): Person, Student, UndergraduateStudent,
                     GraduateStudent, Instructor, Course, UniversityService, Main
  Lab2/DAO/          Data access (DAO pattern): DAO interfaces, Jdbc* implementations,
                     DaoException, Lab2Demo
  Lab3/service/      Business rules: RegistrarService, ValidationException, StudentReport
  Lab3/console/      User interface: ConsoleApp, ConsoleIO
  Lab3/dbConnection/ DatabaseConnection
sql/schema.sql       Database tables and sample data
```

## How it is designed
- **Layers:** `ConsoleApp` -> `RegistrarService` -> DAO classes -> PostgreSQL. Each layer only talks to the one below it.
- **OOP:** inheritance (`Person` -> `Student` -> `UndergraduateStudent` / `GraduateStudent`), encapsulation (private fields, validated setters) and polymorphism (`calculateGPA()` works differently for undergraduates and graduates).
- **Collections:** `Set` for rosters and enrollments, `Map` for grades and lookups, `List` for ordered results.
- **JDBC:** every query uses `PreparedStatement`, which prevents SQL injection.
- **DAO pattern:** each DAO is an interface plus a JDBC implementation.

## How to run
1. Install PostgreSQL and create a database named `university`.
2. Run `sql/schema.sql` in that database (for example in pgAdmin's Query Tool).
3. Add the JDBC driver: *File -> Project Structure -> Libraries -> + -> From Maven*, then enter `org.postgresql:postgresql:42.7.4`.
4. Set the environment variable `DB_PASSWORD` to your PostgreSQL password (IntelliJ: *Run -> Edit Configurations -> Environment variables*).
5. Run `Lab3.console.ConsoleApp`.

The database address is set in `src/Lab3/dbConnection/DatabaseConnection.java`.

## Menu
```
 1) Add student            6) List courses
 2) Remove student         7) Enroll student in course
 3) List students          8) Drop student from course
 4) Add course             9) Record a grade
 5) Remove course         10) View students with their courses
 0) Exit
```

## Runnable classes
| Class | What it does | Needs database? |
|---|---|---|
| `Lab3.console.ConsoleApp` | **Main application**: the full menu-driven console app | Yes |
| `Lab2.DAO.Lab2Demo` | Runs each DAO operation once (insert, list, update, delete, SQL injection test) | Yes |
| `Lab1.SheCanCode.Main` | In-memory OOP demo (inheritance, polymorphism, collections) | No |
