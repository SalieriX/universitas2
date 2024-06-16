CREATE DATABASE Universitas;

USE Universitas;

CREATE TABLE enrollments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             user_id INT,
                             course_name VARCHAR(255),
                             semester VARCHAR(255),
                             status VARCHAR(255)
);

INSERT INTO enrollments (user_id, course_name, semester, status) VALUES
                                                                     (1, 'Mathematics', 'Fall 2023', 'enrolled'),
                                                                     (2, 'Physics', 'Fall 2023', 'enrolled'),
                                                                     (3, 'Chemistry', 'Spring 2024', 'pending'),
                                                                     (4, 'Biology', 'Fall 2023', 'completed'),
                                                                     (5, 'Computer Science', 'Spring 2024', 'enrolled');
