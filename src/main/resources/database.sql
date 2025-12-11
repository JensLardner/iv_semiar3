DROP DATABASE IF EXISTS uni;
CREATE DATABASE uni;


DROP VIEW IF EXISTS v_course_instance_hours CASCADE;
DROP VIEW IF EXISTS v_allocated_activity CASCADE;

DROP TABLE IF EXISTS employee_planned_activity CASCADE;
DROP TABLE IF EXISTS planned_activity CASCADE;
DROP TABLE IF EXISTS employee_skill CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS allocations CASCADE;
DROP TABLE IF EXISTS salary CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS job_title CASCADE;
DROP TABLE IF EXISTS department CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS course_instance CASCADE;
DROP TABLE IF EXISTS course_layout CASCADE;
DROP TABLE IF EXISTS teaching_activity CASCADE;
DROP TABLE IF EXISTS business_rule CASCADE;

CREATE TABLE person(
                       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       personal_number VARCHAR(12) UNIQUE NOT NULL,
                       first_name VARCHAR(500) NOT NULL,
                       last_name VARCHAR(500) NOT NULL,
                       phone_number VARCHAR(500) NOT NULL,
                       zip VARCHAR(500) NOT NULL,
                       street VARCHAR(500) NOT NULL,
                       city VARCHAR(500) NOT NULL
);

CREATE TABLE department(
                           id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           department_name VARCHAR(500) UNIQUE NOT NULL,
                           manager VARCHAR(500) NOT NULL
);

CREATE TABLE job_title(
                          id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          job_title VARCHAR(500) UNIQUE NOT NULL
);

CREATE TABLE employee(
                         id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         employment_id VARCHAR(500) UNIQUE NOT NULL,
                         salary INT NOT NULL,
                         manager VARCHAR(500),
                         department_id INT NOT NULL,
                         person_id INT NOT NULL,
                         job_title_id INT NOT NULL,
                         FOREIGN KEY (department_id) REFERENCES department(id),
                         FOREIGN KEY (person_id) REFERENCES person(id),
                         FOREIGN KEY (job_title_id) REFERENCES job_title(id)
);

CREATE TABLE skill(
                      id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      skill_name VARCHAR(500) NOT NULL
);

CREATE TABLE employee_skill(
                               employee_id INT NOT NULL,
                               skill_id INT NOT NULL,
                               PRIMARY KEY (employee_id, skill_id),
                               FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
                               FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

CREATE TABLE course_layout(
                              id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              course_code VARCHAR(10) NOT NULL,
                              course_name VARCHAR(500) NOT NULL,
                              min_students INT,
                              max_students INT,
                              hp FLOAT(5) NOT NULL,
                              start_date DATE UNIQUE NOT NULL,
                              end_date DATE UNIQUE
);

CREATE TABLE course_instance(
                                instance_id VARCHAR(200) UNIQUE PRIMARY KEY,
                                num_students INT NOT NULL,
                                course_layout_id INT NOT NULL,
                                FOREIGN KEY (course_layout_id) REFERENCES course_layout(id),
                                study_period VARCHAR(10) NOT NULL,
                                study_year TIMESTAMP(4) NOT NULL
);

CREATE TABLE teaching_activity(
                                  id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  activity_name VARCHAR(500) UNIQUE NOT NULL,
                                  factor FLOAT(5)
);

CREATE TABLE planned_activity(
                                 teaching_activity_id INT NOT NULL,
                                 instance_id VARCHAR(200) NOT NULL,
                                 planned_hours INT NOT NULL,
                                 PRIMARY KEY (teaching_activity_id, instance_id),
                                 FOREIGN KEY (teaching_activity_id) REFERENCES teaching_activity(id),
                                 FOREIGN KEY (instance_id) REFERENCES course_instance(instance_id)
);

CREATE TABLE employee_planned_activity(
                                          employee_id INT NOT NULL,
                                          teaching_activity_id INT NOT NULL,
                                          instance_id VARCHAR(200) NOT NULL,
                                          PRIMARY KEY (employee_id, teaching_activity_id, instance_id),
                                          FOREIGN KEY (employee_id) REFERENCES employee(id),
                                          FOREIGN KEY (teaching_activity_id) REFERENCES teaching_activity(id),
                                          FOREIGN KEY (instance_id) REFERENCES course_instance(instance_id)
);

CREATE TABLE allocations(
                            employee_id INT NOT NULL,
                            teaching_activity_id INT NOT NULL,
                            instance_id VARCHAR(200) NOT NULL,
                            allocated_hours INT NOT NULL,
                            PRIMARY KEY (employee_id, teaching_activity_id, instance_id),
                            FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
                            FOREIGN KEY (teaching_activity_id) REFERENCES teaching_activity(id),
                            FOREIGN KEY (instance_id) REFERENCES course_instance(instance_id)
);

CREATE TABLE salary(
                       employee_id INT NOT NULL,
                       salary INT NOT NULL,
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,
                       PRIMARY KEY(salary, employee_id),
                       FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE business_rule(
                              max_num_allocations INT NOT NULL,
                              PRIMARY KEY(max_num_allocations)
);

CREATE VIEW v_course_instance_hours AS
SELECT
    course_code,
    course_instance.study_period,
    course_instance.instance_id,
    hp,
    COALESCE(SUM(planned_hours * factor) FILTER (WHERE activity_name NOT IN ('Admin', 'Exam')), 0)
        + (32 + 0.725 * num_students)
        + (2 * hp + 28 + 0.2 * num_students) AS total_planned_hours,

    COALESCE(SUM(allocated_hours) FILTER (WHERE activity_name NOT IN ('Admin', 'Exam')), 0)
        + (32 + 0.725 * num_students)
        + (2 * hp + 28 + 0.2 * num_students) AS total_allocated_hours
FROM course_instance
         INNER JOIN course_layout ON course_instance.course_layout_id = course_layout.id
         INNER JOIN planned_activity ON planned_activity.instance_id = course_instance.instance_id
         INNER JOIN teaching_activity ON planned_activity.teaching_activity_id = teaching_activity.id
         INNER JOIN employee_planned_activity ON employee_planned_activity.instance_id = course_instance.instance_id
    AND employee_planned_activity.teaching_activity_id = planned_activity.teaching_activity_id
         INNER JOIN allocations ON allocations.employee_id = employee_planned_activity.employee_id
    AND allocations.teaching_activity_id = planned_activity.teaching_activity_id
    AND allocations.instance_id = course_instance.instance_id
WHERE EXTRACT(YEAR FROM course_instance.study_year) = EXTRACT(YEAR FROM CURRENT_DATE)
GROUP BY
    course_code,
    course_instance.instance_id,
    hp,
    num_students;

CREATE VIEW v_allocated_activity AS
SELECT
    course_layout.course_code,
    course_layout.course_name,
    course_instance.instance_id,
    person.first_name,
    person.last_name,
    teaching_activity.activity_name,
    planned_activity.planned_hours,
    teaching_activity.factor
FROM teaching_activity
         INNER JOIN planned_activity ON teaching_activity.id = planned_activity.teaching_activity_id
         INNER JOIN course_instance ON planned_activity.instance_id = course_instance.instance_id
         INNER JOIN course_layout ON course_instance.course_layout_id = course_layout.id
         INNER JOIN allocations ON allocations.teaching_activity_id = teaching_activity.id
    AND course_instance.instance_id = allocations.instance_id
         INNER JOIN employee ON employee.id = allocations.employee_id
         INNER JOIN person ON person.id = employee.person_id;

