package Backend;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/hospital_db";
    private final String username = "root";
    private final String password = "Alamein";

    // Constructor to initialize the database connection
    public DatabaseManager(String url, String username, String password) throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }

    // Constructor to use an existing connection
    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    // Method to search for a patient by their ID
    public boolean searchPatientById(String patientId) throws SQLException {
        String query = "SELECT * FROM patients WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, patientId);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Patient with ID " + patientId + " found.");
            return true;
        } else {
            System.out.println("Patient with ID " + patientId + " not found.");
            return false;
        }
    }

    // Method to fetch all patients from the database
    public ResultSet getAllPatients() throws SQLException {
        String query = "SELECT * FROM patients";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    // Method to search patients by name
    public ResultSet searchPatientsByName(String name) throws SQLException {
        String query = "SELECT * FROM patients WHERE name LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, "%" + name + "%");
        return stmt.executeQuery();
    }

    // Insert a new patient into the database and retrieve the generated ID
    public String insertPatient(PatientImpl patient) throws SQLException {
        String insertSQL = "INSERT INTO patients (name, age, contact_info, medical_history, visit_records) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getContactInfo());
            stmt.setString(4, patient.getMedicalHistory());
            stmt.setString(5, patient.getVisitRecords());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getString(1); // Return the generated patient ID
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting patient into database: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public boolean updatePatient(PatientImpl patient) throws SQLException {
        String query = "UPDATE patients SET name = ?, age = ?, contact_info = ?, medical_history = ?, visit_records = ? WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getContactInfo());
            stmt.setString(4, patient.getMedicalHistory());
            stmt.setString(5, patient.getVisitRecords());
            stmt.setString(6, patient.getPatientID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public void deletePatient(String patientID) throws SQLException {
        String query = "DELETE FROM patients WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientID);
            stmt.executeUpdate();
        }
    }

    public ResultSet searchAppointmentsByDate(String appointmentDate) throws SQLException {
        String query = "SELECT * FROM appointments WHERE appointment_date = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, appointmentDate);
        return stmt.executeQuery();
    }

    public ResultSet searchAppointmentsByPatientId(int patientId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, patientId);
        return stmt.executeQuery();
    }
    public int insertAppointment(int patientId, String appointmentDate, String appointmentTime, int severity) throws SQLException {
        // Insert appointment into the appointments table
        String insertAppointmentSQL = "INSERT INTO appointments (patient_id, appointment_date, appointment_time, status, severity) VALUES (?, ?, ?, 'Scheduled', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertAppointmentSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, patientId);
            stmt.setString(2, appointmentDate);
            stmt.setString(3, appointmentTime);
            stmt.setInt(4, severity);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int appointmentId = generatedKeys.getInt(1); // Get the generated appointment ID
                        System.out.println("Appointment added successfully! Appointment ID: " + appointmentId);
                        return appointmentId;
                    }
                }
            }
        }
        return -1; // Return -1 if the insertion fails
    }
    // Method to update the status of the appointment in the database
    public void updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);  // Set status to 'CANCELED' or 'Scheduled'
            stmt.setInt(2, appointmentId);  // Set the appointment ID
            stmt.executeUpdate();
            System.out.println("Updated appointment status for ID: " + appointmentId + " to " + status);
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            throw e;
        }
    }


    public void insertBilling(int patientId, double billingAmount, String paymentHistory) throws SQLException {
        String insertSQL = "INSERT INTO billing (patient_id, billing_amount, payment_history) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, patientId);
            stmt.setDouble(2, billingAmount);
            stmt.setString(3, paymentHistory);
            stmt.executeUpdate();
        }
    }

    public ResultSet searchBillingByPatientId(int patientId) throws SQLException {
        String query = "SELECT * FROM billing WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, patientId);
        return stmt.executeQuery();
    }

    public int insertIntoWaitingList(int patientId, String waitingListDate, String waitingListTime, int severity) throws SQLException {
        // Insert patient into the waiting list
        String insertWaitingListSQL = "INSERT INTO waiting_list (patient_id, date, time, severity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertWaitingListSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, patientId);
            stmt.setString(2, waitingListDate);
            stmt.setString(3, waitingListTime);
            stmt.setInt(4, severity);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int waitingListId = generatedKeys.getInt(1); // Get the generated waiting list ID
                        System.out.println("Patient added to waiting list successfully! Waiting List ID: " + waitingListId);
                        return waitingListId;
                    }
                }
            }
        }
        return -1; // Return -1 if the insertion fails
    }


    public void removeFromWaitingList(int patientId) throws SQLException {
        String query = "DELETE FROM waiting_list WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        }
    }

    public ResultSet searchWaitingListByPatientId(int patientId) throws SQLException {
        String query = "SELECT * FROM waiting_list WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, patientId);
        return stmt.executeQuery();
    }

    public ResultSet getWaitingList() throws SQLException {
        String query = "SELECT * FROM waiting_list";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    public void updateWaitingListSeverity(int patientId, int severity) throws SQLException {
        String query = "UPDATE waiting_list SET severity = ? WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, severity);
            stmt.setInt(2, patientId);
            stmt.executeUpdate();
        }
    }

    public ResultSet getPatientAppointments(int patientId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, patientId);
        return stmt.executeQuery();
    }

    public ResultSet getPatientBilling(int patientId) throws SQLException {
        String query = "SELECT * FROM billing WHERE patient_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, patientId);
        return stmt.executeQuery();
    }

    public String getGeneratedPatientID(String name, int age, String medicalHistory) throws SQLException {
        String query = "SELECT patient_id FROM patients WHERE name = ? AND age = ? AND medical_history = ? ORDER BY patient_id DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, medicalHistory);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("patient_id");
                }
            }
        }
        return null;
    }
}
