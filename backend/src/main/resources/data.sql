-- Default user for testing (password: password)
-- BCrypt hash of 'password'
INSERT INTO users (id, email, password, first_name, last_name, enabled) VALUES
(1, 'user@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'John', 'Doe', true);

INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER');

-- Sample customers
INSERT INTO customers (id, first_name, last_name, email, phone) VALUES
(1, 'Alice', 'Smith', 'alice@example.com', '555-0101'),
(2, 'Bob', 'Johnson', 'bob@example.com', '555-0102'),
(3, 'Charlie', 'Williams', 'charlie@example.com', '555-0103');

-- Sample notifications
INSERT INTO notifications (id, user_id, message, created_at, read) VALUES
(1, 1, 'Welcome to the application!', CURRENT_TIMESTAMP, false),
(2, 1, 'Your profile has been updated', CURRENT_TIMESTAMP, false),
(3, 1, 'New customer added successfully', CURRENT_TIMESTAMP, false);
