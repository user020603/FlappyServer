-- Create User table
CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL
);

-- Create Score table
CREATE TABLE score (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT,
                       score INT,
                       created_at DATE,
                       FOREIGN KEY (user_id) REFERENCES user(id)
);