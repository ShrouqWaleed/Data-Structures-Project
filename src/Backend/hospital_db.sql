
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;


CREATE TABLE patients (
                          patient_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(30) NOT NULL,
                          age INT NOT NULL CHECK (age > 0),
                          contact_info VARCHAR(100) NULL,
                          medical_history TEXT NULL,
                          visit_records TEXT NULL
);


CREATE TABLE appointments (
                              appointment_id INT AUTO_INCREMENT PRIMARY KEY,
                              patient_id INT,
                              appointment_date DATE NOT NULL,
                              appointment_time TIME NOT NULL,
                              status ENUM('Scheduled', 'Rescheduled') DEFAULT 'Scheduled',
                              FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
                                  ON DELETE CASCADE ON UPDATE CASCADE,
                              INDEX idx_patient_id (patient_id), -- Index for better performance
                              INDEX idx_appointment_date (appointment_date) -- Index for frequent date queries
);


CREATE TABLE waiting_list (
                              waiting_id INT AUTO_INCREMENT PRIMARY KEY,
                              patient_id INT,
                              added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
                                  ON DELETE CASCADE ON UPDATE CASCADE,
                              INDEX idx_waiting_patient_id (patient_id)
);

CREATE TABLE billing (
                         billing_id INT AUTO_INCREMENT PRIMARY KEY,
                         patient_id INT,
                         billing_amount DECIMAL(10, 2) NOT NULL CHECK (billing_amount >= 0),
                         payment_history TEXT NULL,
                         payment_status ENUM('Pending', 'Paid', 'Failed') DEFAULT 'Pending',  -- Add this line
                         FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
                             ON DELETE CASCADE ON UPDATE CASCADE,
                         INDEX idx_billing_patient_id (patient_id)
);

CREATE TABLE reports (
                         report_id INT AUTO_INCREMENT PRIMARY KEY,
                         report_type ENUM('Patient', 'Appointment', 'Billing') NOT NULL,
                         report_data TEXT NOT NULL
);



SELECT * FROM patients; -- done
SELECT * FROM appointments;
SELECT * FROM waiting_list;
ALTER TABLE appointments AUTO_INCREMENT = 1;
ALTER TABLE waiting_list AUTO_INCREMENT = 1;
-- done
SELECT * FROM billing; -- done
SELECT * FROM reports;
SHOW TABLES;


-- describe waiting_list;

ALTER TABLE billing
    ADD COLUMN payment_status ENUM('Pending', 'Paid', 'Failed') DEFAULT 'Pending';
ALTER TABLE billing
    ADD COLUMN remaining_balance DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (remaining_balance >= 0),
    ADD COLUMN last_payment_date DATETIME NULL,
    ADD COLUMN last_payment_amount DECIMAL(10, 2) NULL CHECK (last_payment_amount >= 0);
ALTER TABLE waiting_list
    -- ADD COLUMN appointment_id VARCHAR(255),
   --  ADD COLUMN severity INT,
    ADD COLUMN date DATE,
    ADD COLUMN time TIME;
-- Change the column type to VARCHAR(10) for flexibility
ALTER TABLE appointments
    MODIFY COLUMN appointment_time VARCHAR(10);
ALTER TABLE waiting_list
    ADD COLUMN severity INT;


ALTER TABLE appointments MODIFY COLUMN appointment_time VARCHAR(10);
ALTER TABLE appointments MODIFY COLUMN appointment_time TIME;
SELECT DISTINCT status FROM appointments WHERE status NOT IN ('Scheduled', 'Rescheduled');
DESCRIBE appointments;
ALTER TABLE appointments MODIFY COLUMN status ENUM('Scheduled', 'Rescheduled') DEFAULT 'Scheduled';
