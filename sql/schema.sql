New-Item -ItemType Directory -Force sql | Out-Null
@'
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS instructors;

CREATE TABLE instructors (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE students (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    major VARCHAR(100),
    student_type VARCHAR(10) NOT NULL CHECK (student_type IN ('UNDERGRAD', 'GRAD')),
    year_level INTEGER,
    thesis_topic VARCHAR(200)
);

CREATE TABLE courses (
    code VARCHAR(10) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    credits INTEGER NOT NULL CHECK (credits > 0),
    instructor_id INTEGER REFERENCES instructors(id) ON DELETE SET NULL
);

CREATE TABLE enrollments (
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_code VARCHAR(10) NOT NULL REFERENCES courses(code) ON DELETE CASCADE,
    grade NUMERIC(2,1) CHECK (grade >= 0.0 AND grade <= 4.0),
    PRIMARY KEY (student_id, course_code)
);

INSERT INTO instructors VALUES (1, 'Dr. Uwase', 'uwase@uni.edu', 'Computer Science');
INSERT INTO instructors VALUES (2, 'Dr. Mugisha', 'mugisha@uni.edu', 'Mathematics');
INSERT INTO courses VALUES ('CS101', 'Intro to Programming', 3, 1);
INSERT INTO courses VALUES ('MA201', 'Linear Algebra', 4, 2);
INSERT INTO courses VALUES ('CS501', 'Advanced Algorithms', 3, 1);
'@ | Set-Content sql/schema.sql