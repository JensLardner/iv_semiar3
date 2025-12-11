INSERT INTO department (department_name, manager)
VALUES
    ('Computer Science', 'mgr1'),
    ('Mathematics', 'mgr2'),
    ('Physics', 'mgr3');

INSERT INTO job_title (job_title)
VALUES ('Professor'), ('Lecturer'), ('Teaching Assistant');

INSERT INTO person (personal_number, first_name, last_name, phone_number, zip, street, city)
VALUES
    ('199001019999', 'Alice', 'Johnson', '0701234567', '12345', 'First Street 1', 'Uppsala'),
    ('198506209888', 'Bob', 'Smith', '0709876543', '54321', 'Second Street 5', 'Stockholm'),
    ('197712159777', 'Carol', 'Andersson', '0701112223', '11122', 'Third Street 9', 'Gothenburg');

INSERT INTO employee (employment_id, salary, manager, department_id, person_id, job_title_id)
VALUES
    ('E001', 45000, 'mgr1', 1, 1, 1),
    ('E002', 38000, 'mgr1', 1, 2, 2),
    ('E003', 30000, 'mgr2', 2, 3, 3);

INSERT INTO skill (skill_name)
VALUES ('Python'), ('Java'), ('Machine Learning');

INSERT INTO employee_skill (employee_id, skill_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2);

INSERT INTO course_layout (
    course_code, course_name,
    min_students, max_students, hp,
    start_date, end_date
)
VALUES
    ('CS101', 'Intro to Programming', 10, 60, 7.5, '2024-01-10', '2024-03-10'),
    ('CS201', 'Algorithms',            5, 40, 7.5, '2024-02-10', '2024-04-10'),
    ('CS301', 'Databases',             5, 45, 7.5, '2024-03-10', '2024-05-10'),
    ('CS401', 'Software Engineering',  5, 35, 7.5, '2024-04-10', '2024-06-10'),
    ('CS102', 'Data Structures',       5, 50, 7.5, '2024-05-15', '2024-07-15');

INSERT INTO course_instance (instance_id, num_students, course_layout_id, study_period, study_year)
VALUES
    ('CS101-2025-P1', 45, 1, 'P1', '2025-01-01'),
    ('CS101-2025-P4', 50, 1, 'P4', '2025-01-01'),
    ('CS201-2025-P2', 30, 2, 'P2', '2025-01-01'),
    ('CS301-2025-P3', 35, 3, 'P3', '2025-01-01'),
    ('CS201-2024-P2', 30, 2, 'P2', '2024-01-01'),
    ('CS201-2025-P1', 28, 2, 'P1', '2025-01-01'),
    ('CS301-2025-P1', 32, 3, 'P1', '2025-01-01'),
    ('CS401-2025-P1', 30, 4, 'P1', '2025-01-01'),
    ('CS102-2025-P1', 38, 5, 'P1', '2025-01-01');

INSERT INTO teaching_activity (activity_name, factor)
VALUES
    ('Lecture', 1.0),
    ('Seminar', 2.0),
    ('Lab', 3.0),
    ('Exam', 1.0),
    ('Admin', 1.0),
    ('Tutorial', 1.5);

INSERT INTO planned_activity (
    teaching_activity_id, instance_id,
    planned_hours
)
VALUES
    (1, 'CS101-2025-P1', 20),
    (2, 'CS101-2025-P1', 10),
    (3, 'CS101-2025-P1', 15),
    (4, 'CS101-2025-P1', 64),
    (5, 'CS101-2025-P1', 52),
    (1, 'CS101-2025-P4', 20),
    (2, 'CS101-2025-P4', 10),
    (4, 'CS101-2025-P4', 68),
    (5, 'CS101-2025-P4', 53),
    (1, 'CS201-2025-P2', 24),
    (3, 'CS201-2025-P2', 18),
    (4, 'CS201-2025-P2', 53),
    (5, 'CS201-2025-P2', 49),
    (1, 'CS301-2025-P3', 20),
    (2, 'CS301-2025-P3', 12),
    (4, 'CS301-2025-P3', 57),
    (5, 'CS301-2025-P3', 50),
    (3, 'CS201-2024-P2', 12),
    (4, 'CS201-2024-P2', 53),
    (5, 'CS201-2024-P2', 49),
    (1, 'CS201-2025-P1', 22),
    (1, 'CS301-2025-P1', 20),
    (2, 'CS401-2025-P1', 15),
    (1, 'CS102-2025-P1', 20);

INSERT INTO employee_planned_activity (employee_id, teaching_activity_id, instance_id)
VALUES
    (1, 1, 'CS101-2025-P1'),
    (2, 2, 'CS101-2025-P1'),
    (3, 3, 'CS101-2025-P1'),
    (1, 4, 'CS101-2025-P1'),
    (2, 5, 'CS101-2025-P1'),
    (1, 1, 'CS101-2025-P4'),
    (2, 2, 'CS101-2025-P4'),
    (1, 4, 'CS101-2025-P4'),
    (2, 5, 'CS101-2025-P4'),
    (1, 1, 'CS201-2025-P2'),
    (2, 3, 'CS201-2025-P2'),
    (1, 4, 'CS201-2025-P2'),
    (2, 5, 'CS201-2025-P2'),
    (2, 1, 'CS301-2025-P3'),
    (3, 2, 'CS301-2025-P3'),
    (2, 4, 'CS301-2025-P3'),
    (3, 5, 'CS301-2025-P3'),
    (3, 3, 'CS201-2024-P2'),
    (3, 4, 'CS201-2024-P2'),
    (3, 5, 'CS201-2024-P2'),
    (3, 1, 'CS201-2025-P1'),
    (3, 1, 'CS301-2025-P1');

INSERT INTO business_rule (max_num_allocations)
VALUES (4);

INSERT INTO allocations (employee_id, teaching_activity_id, instance_id, allocated_hours)
VALUES
    (1, 1, 'CS101-2025-P1', 18),
    (1, 4, 'CS101-2025-P1', 60),
    (2, 2, 'CS101-2025-P1', 12),
    (2, 5, 'CS101-2025-P1', 50),
    (3, 3, 'CS101-2025-P1', 40),
    (1, 1, 'CS101-2025-P4', 20),
    (1, 4, 'CS101-2025-P4', 65),
    (2, 2, 'CS101-2025-P4', 15),
    (2, 5, 'CS101-2025-P4', 52),
    (1, 1, 'CS201-2025-P2', 22),
    (1, 4, 'CS201-2025-P2', 52),
    (2, 3, 'CS201-2025-P2', 50),
    (2, 5, 'CS201-2025-P2', 48),
    (2, 1, 'CS301-2025-P3', 19),
    (2, 4, 'CS301-2025-P3', 55),
    (3, 2, 'CS301-2025-P3', 20),
    (3, 5, 'CS301-2025-P3', 49),
    (3, 3, 'CS201-2024-P2', 35),
    (3, 4, 'CS201-2024-P2', 52),
    (3, 5, 'CS201-2024-P2', 47),
    (3, 1, 'CS201-2025-P1', 20),
    (3, 1, 'CS301-2025-P1', 18);

INSERT INTO salary (employee_id, salary, start_date, end_date)
VALUES
    (1, 45000, '2022-01-01', '2023-01-01'),
    (1, 47000, '2023-01-02', '2024-01-01'),
    (1, 50000, '2024-01-02', '2025-01-01'),
    (2, 38000, '2022-01-01', '2023-01-01'),
    (2, 40000, '2023-01-02', '2024-01-01'),
    (2, 42000, '2024-01-02', '2025-01-01'),
    (3, 30000, '2022-01-01', '2023-01-01'),
    (3, 32000, '2023-01-02', '2024-01-01'),
    (3, 35000, '2024-01-02', '2025-01-01');