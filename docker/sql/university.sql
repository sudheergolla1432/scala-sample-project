-- Database
CREATE DATABASE university;
\c university;

-- Student
CREATE TABLE student (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL
);

-- Course
CREATE TABLE courses (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL
);

-- Link between student and course
CREATE TABLE student_course (
  student_id integer NOT NULL,
  course_id integer NOT NULL
);

ALTER TABLE student_course
ADD CONSTRAINT student_course_id_student_id_course PRIMARY KEY (student_id, course_id);
ALTER TABLE student_course
ADD FOREIGN KEY (student_id) REFERENCES student (id);
ALTER TABLE student_course
ADD FOREIGN KEY (course_id) REFERENCES courses (id);


-- student
INSERT INTO student (name) VALUES ('Sudheer');
INSERT INTO student (name) VALUES ('Karuna');
INSERT INTO student (name) VALUES ('Nish');
COMMIT;

-- Course
INSERT INTO courses (name) VALUES ('maths');
INSERT INTO courses (name) VALUES ('programming');
COMMIT;

-- Actor-Movie link
INSERT INTO student_course (student_id, course_id)
VALUES (1, 1);
INSERT INTO student_course (student_id, course_id)
VALUES (1, 2);
INSERT INTO student_course (student_id, course_id)
VALUES (2, 1);
INSERT INTO student_course (student_id, course_id)
VALUES (2, 2);
INSERT INTO student_course (student_id, course_id)
VALUES (3, 1);
INSERT INTO student_course (student_id, course_id)
VALUES (3, 2);
COMMIT;