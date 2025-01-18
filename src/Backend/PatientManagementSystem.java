package Backend;

import java.sql.*;
//بسم الله
// Seif Elkerdany 23101399
// Ammar Yasser 23101991
// Shrouq waleed 23101402
// Hoda Mahmoud  23101498
// Mohamed Alaa 23101467

public class PatientManagementSystem implements PatientManagementSystemI {
    private DatabaseManager dbManager;
    private PatientBST patients;
    private BillingImpl billing;
    private ReportGenerator reportGenerator;
    PriorityQueue appointmentQueue= new PriorityQueue();
    PriorityQueue waitingListQueue = new PriorityQueue();

    // Constructor to initialize the system and connect to DB
    public PatientManagementSystem() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "Alamein");
        dbManager = new DatabaseManager(connection);
        patients = new PatientBST(dbManager);

        // Initialize billing with a specific patient ID
        // You would need a valid patientID to initialize BillingImpl
        String patientID = "0000"; // Replace with actual patient ID when available
        billing = new BillingImpl(patientID, connection); // Pass patientID and connection



        // Load patients from database
        loadPatientsFromDatabase(connection);
        loadAppointmentsFromDatabase(connection);
        loadWaitinglistFromDatabase(connection);

        reportGenerator = new ReportGenerator(this.appointmentQueue,connection);
    }

    // Load patients from the database into the BST
    private void loadPatientsFromDatabase(Connection connection) {
        try {
            System.out.println("Loading patients from database...");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM patients");
            while (resultSet.next()) {
                String patientID = String.valueOf(resultSet.getInt("patient_id"));
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String contactInfo = resultSet.getString("contact_info");
                String medicalHistory = resultSet.getString("medical_history");
                String visitRecords = resultSet.getString("visit_records");

                PatientImpl p = new PatientImpl(patientID, name, age, contactInfo, medicalHistory, visitRecords);
                patients.insert(p); // Insert into BST and also reflected in database
            }
            System.out.println("Patients loaded into BST.");
        } catch (SQLException e) {
            System.out.println("Failed to load patients from database!");
            e.printStackTrace();
        }
    }

    // Add a new patient, insert to both BST and database
    @Override
    public boolean addPatient(String name, int age, String contactInfo, String medicalHistory) {
        // Create a new patient and insert into BST and DB
        PatientImpl newPatient = new PatientImpl(name, age, contactInfo, medicalHistory);
        patients.insert(newPatient);  // Insert into BST

        return true;
    }

    // Find a patient by their unique ID
    @Override
    public Patient findPatient(String id) throws SQLException {
        PatientImpl p = patients.search(id);
        dbManager.searchPatientById(id);
        if (p != null) {
            return p;
        }
        return null;
    }

    // Update patient contact info and sync to DB
    @Override
    public boolean updatePatientContactInfo(String id, String newContactInfo) {
        PatientImpl p = patients.search(id);
        if (p != null) {
            p.setContactInfo(newContactInfo);
            try {
                dbManager.updatePatient(p);  // Sync to DB
            } catch (SQLException e) {
                System.err.println("Failed to update patient contact info: " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    // Add a visit record to a patient and sync to DB
    @Override
    public boolean addVisitRecord(String id, String visitDetails) {
        PatientImpl p = patients.search(id);
        if (p != null) {
            p.addVisitRecord(visitDetails);
            try {
                dbManager.updatePatient(p);  // Sync to DB
            } catch (SQLException e) {
                System.err.println("Failed to add visit record: " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    // Generate Bill for a patient and sync to DB
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "Alamein");
    public String generateBill(String patientId, double billingAmount, String paymentHistory) {
        billing = new BillingImpl(patientId,connection);
        String bill = billing.generateBill(billingAmount);

        try {
            // Sync billing to DB with patientId, billingAmount, and paymentHistory
            dbManager.insertBilling(Integer.parseInt(patientId), billingAmount, paymentHistory);
        } catch (SQLException e) {
            System.err.println("Failed to generate billing: " + e.getMessage());
        }
        return bill;
    }


    // Add Payment to patient and sync to DB


    // Display all patients
    @Override
    public void displayAllPatients() {
        patients.printInOrder();
    }

    @Override
    public String displayAllAppointments() {
        return "";
    }

    @Override

    public boolean scheduleAppointment(PatientImpl patient, String date, String time, int severity) {
        // Create the AppointmentNode object
        AppointmentNode node = new AppointmentNode(patient, date, time, severity);

        // Inside scheduleAppointment method, when you insert into the appointments table
        // Ensure patient ID is not null before proceeding
        String patientIDStr = patient.getPatientID();
        if (patientIDStr == null || patientIDStr.isEmpty()) {
            System.err.println("Patient ID is null or empty. Cannot schedule appointment.");
            return false;  // Return early if there's no valid patient ID
        }

        // Check if the time slot is already occupied in the priority queue (appointmentQueue)
        boolean isTimeSlotTaken = false;
        for (AppointmentNode existingNode : appointmentQueue.heap) {
            if (existingNode.getDate().equals(date) && existingNode.getTime().equals(time)) {
                isTimeSlotTaken = true;
                break;
            }
        }

        // If the time slot is taken, move the patient to the waiting list
        if (isTimeSlotTaken) {
            // Add the patient to the waiting list if the slot is already occupied
            System.out.println("Time slot already taken. Adding patient to waiting list.");

            // Insert the patient into the waiting list
            String patientID = patient.getPatientID();
            try {
                String waitingListSQL = "INSERT INTO waiting_list (patient_id, date, time, severity) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(waitingListSQL, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, Integer.parseInt(patientID));
                    stmt.setString(2, date);
                    stmt.setString(3, time);
                    stmt.setInt(4, severity);

                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int waitingListId = generatedKeys.getInt(1);  // Get the waiting list ID
                                System.out.println("Patient added to the waiting list with ID: " + waitingListId);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error adding patient to waiting list: " + e.getMessage());
                return false;
            }


            return false;  }

        // If the time slot is available, proceed with scheduling the appointment
        String appointmentInsertSQL = "INSERT INTO appointments (patient_id, appointment_date, appointment_time, severity, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(appointmentInsertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, Integer.parseInt(patientIDStr));  // Use the patient_id
            stmt.setString(2, date);
            stmt.setString(3, time);
            stmt.setInt(4, severity);  // Include severity in the database
            stmt.setString(5, "Scheduled");  // Default status

            int affectedRows = stmt.executeUpdate();

            // Retrieve the automatically generated appointment_id from the database
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int appointmentId = generatedKeys.getInt(1);  // Get generated appointment_id
                        node.setAppointmentId(String.valueOf(appointmentId));  // Set the appointment ID in the node
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling appointment: " + e.getMessage());
            return false;
        }

        // Add the new appointment node to the custom priority queue
        appointmentQueue.enqueue(node);
        System.out.println("Appointment scheduled for " + patient.getPatientID() + " on " + date + " at " + time);



        return true;  // Successfully scheduled appointment
    }

    public boolean isAvailable(String date, String time) {
        // Check if the time slot is taken in the in-memory appointment queue
        for (int i = 0; i < appointmentQueue.size(); i++) {
            AppointmentNode current = appointmentQueue.heap.get(i);
            if (current.getDate().equals(date) && current.getTime().equals(time)) {
                return false; // The time slot is already taken
            }
        }

        // Check if the time slot is already taken in the database
        String query = "SELECT * FROM appointments WHERE appointment_date = ? AND appointment_time = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, date);
            stmt.setString(2, time);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return false; // Slot is already taken in the database
            }
        } catch (SQLException e) {
            System.err.println("Error checking availability: " + e.getMessage());
        }

        return true; // The time slot is available
    }


    // Method to display appointments from the database
    public void displayAppointments() {
        String query = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("--- Appointment Queue ---");
            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                String date = rs.getString("appointment_date");
                String time = rs.getString("appointment_time");
                int severity = rs.getInt("severity");
                System.out.println("Appointment ID: " + appointmentId + ", Patient ID: " + patientId
                        + ", Severity: " + severity + ", Date: " + date + ", Time: " + time);
            }
        } catch (SQLException e) {
            System.err.println("Error displaying appointments: " + e.getMessage());
        }
    }

    // Method to display waiting list from the database
    public String displayWaitingList() {
        String query = "SELECT * FROM waiting_list ORDER BY added_at";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            String finalWaitinglist = "";
            while (rs.next()) {
                int waitingId = rs.getInt("waiting_id");
                int patientId = rs.getInt("patient_id");
                String date = rs.getString("date");
                String time = rs.getString("time");
                int severity = rs.getInt("severity");
                finalWaitinglist += "Waiting ID: " + waitingId + "\nPatient ID: " + patientId
                        + "\nSeverity: " + severity + "\nDate: " + date + "\nTime: " + time + "\n\n\n";
            }
            return finalWaitinglist;
        } catch (SQLException e) {
            return "Error displaying waiting list";
        }
    }
//
public boolean cancelAppointment(String appointmentId) {
    AppointmentNode appointmentToCancel = findAppointmentById(appointmentId);

    if (appointmentToCancel == null) {
        System.out.println("Appointment with ID " + appointmentId + " not found.");
        return false;
    }

    try {
        // Remove the canceled appointment from the priority queue
        appointmentQueue.dequeueByAppointmentID(appointmentId);

        // Delete the appointment from the database
        String deleteQuery = "DELETE FROM appointments WHERE appointment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, appointmentId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Appointment with ID " + appointmentId + " has been deleted from the database.");

                // Now check for the highest severity patient in the waiting list for the same date and time
                AppointmentNode nextPatient = findNextPatientWithHighestSeverity(appointmentToCancel.getDate(), appointmentToCancel.getTime());

                if (nextPatient != null) {
                    // Add the patient from the waiting list to the appointment table
                    scheduleAppointment(nextPatient.getPatient(), nextPatient.getDate(), nextPatient.getTime(), nextPatient.getSeverity());
                    System.out.println("Patient from the waiting list assigned to the canceled appointment slot.");

                    // Ensure only the highest severity patient was moved to the appointment table
                    // Leaving the others in the waiting list

                    // Remove the highest severity patient from the waiting list (modified logic)
                    removeFromWaitingList(nextPatient.getPatient().getPatientID(), nextPatient.getDate(), nextPatient.getTime());

                    return true;
                } else {
//                    System.out.println("No matching patient found in the waiting list for the canceled slot.");
                }

            } else {
                System.out.println("No appointment found with ID " + appointmentId + " in the database.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error deleting appointment: " + e.getMessage());
    }

    return false;
}

    // Helper method to remove the patient from the waiting list with specific date and time
    private void removeFromWaitingList(String patientId, String date, String time) {
        try {
            // Query to remove the patient from waiting list with highest severity
            String deleteQuery =
                    "DELETE w FROM waiting_list w " +
                            "JOIN (SELECT MAX(severity) AS max_severity FROM waiting_list WHERE patient_id = ? AND date = ? AND time = ?) sub " +
                            "ON w.patient_id = ? AND w.date = ? AND w.time = ? AND w.severity = sub.max_severity";

            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setString(1, patientId);
                stmt.setString(2, date);
                stmt.setString(3, time);
                stmt.setString(4, patientId);
                stmt.setString(5, date);
                stmt.setString(6, time);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Removed patient from waiting list: " + patientId);
                } else {
                    System.out.println("No matching patient found for deletion.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error removing patient from waiting list: " + e.getMessage());
        }
    }


    private AppointmentNode findNextPatientWithHighestSeverity(String date, String time) {
        // Query the waiting list to find all patients with the same date and time
        String query = "SELECT * FROM waiting_list WHERE date = ? AND time = ? ORDER BY severity DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, date);
            stmt.setString(2, time);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String patientId = resultSet.getString("patient_id");
                String addedAt = resultSet.getString("added_at");
                int severity = resultSet.getInt("severity");


                PatientImpl patient = patients.search(patientId);

                // Create an AppointmentNode for the new appointment with the highest severity
                AppointmentNode nextPatient = new AppointmentNode(
                        //String.valueOf(System.currentTimeMillis()), // Generate a unique appointment ID
                        patient, date, time, severity, "Scheduled"
                );

                return nextPatient;
            }

        } catch (SQLException e) {
            System.err.println("Error querying waiting list: " + e.getMessage());
        }

        return null; // No matching patient found
    }


    private AppointmentNode findAppointmentById(String appointmentId) {

        for (int i = 0; i < appointmentQueue.size(); i++) {
            if (appointmentQueue.heap.get(i).getAppointmentId().equalsIgnoreCase(appointmentId)) {
                return appointmentQueue.heap.get(i);
            }
        }
        return null;
    }


    private AppointmentNode assignNextFromWaitingList(String date, String time) {
        if (waitingListIsEmpty()) {
            System.out.println("Waiting list is empty.");
            return null;
        }

        // Dequeue the next patient from the waiting list
        AppointmentNode nextAppointment = waitingListQueue.dequeue();

        // Remove the patient from the waiting list table in the database
        try {
            dbManager.removeFromWaitingList(Integer.parseInt(nextAppointment.getPatient().getPatientID()));
        } catch (SQLException e) {
            System.err.println("Error removing patient from waiting list: " + e.getMessage());
            return null;
        }


        try {
            int patientId = Integer.parseInt(nextAppointment.getPatient().getPatientID());
            String appointmentDate = date;
            String appointmentTime = time;
            int severity = nextAppointment.getSeverity();
            int appointmentId = dbManager.insertAppointment(patientId, appointmentDate, appointmentTime, severity);

            if (appointmentId != -1) {
                System.out.println("Next waiting patient scheduled for appointment ID: " + appointmentId);
            } else {
                System.err.println("Failed to schedule next patient.");
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling next patient from waiting list: " + e.getMessage());
        }

        return nextAppointment;
    }


    private boolean waitingListIsEmpty() {
        return waitingListQueue.isEmpty();
    }

    @Override
    public boolean rescheduleAppointment(String appointmentID, String newDate, String newTime) {
        AppointmentNode current = null;

        for (AppointmentNode node : appointmentQueue.heap) {
            if (node.getAppointmentId().equals(appointmentID)) {
                current = node;
                break;
            }
        }

        if (current == null) {
            System.out.println("Appointment with ID " + appointmentID + " not found.");
            return false;
        }

        if (!isAvailable(newDate, newTime)) {
            System.out.println("Time slot unavailable for rescheduling.");
            return false;
        }

        // Set the status to "Rescheduled"
        current.setStatus("Rescheduled");

        appointmentQueue.dequeueByPatientID(current.getPatient().getPatientID());
        current.setDate(newDate);
        current.setTime(newTime);
        appointmentQueue.enqueue(current);

        try {
            String updateSQL = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, status = ? WHERE appointment_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
                stmt.setString(1, newDate);
                stmt.setString(2, newTime);
                stmt.setString(3, "Rescheduled");
                stmt.setString(4, appointmentID);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Appointment with ID " + appointmentID + " rescheduled to " + newDate + " at " + newTime);
                    return true;  // Success
                } else {
                    System.err.println("No appointment found with ID " + appointmentID + " in the database.");
                    return false;  // Database update failed
                }
            }
        } catch (SQLException e) {
            System.err.println("Error rescheduling appointment: " + e.getMessage());
            return false;  // SQL error
        }
    }



    @Override
    public boolean addToWaitingList(PatientImpl patient, String date, String time, int severity) {
        waitingListQueue.enqueue(new AppointmentNode(patient, date, time, severity));
        return true;
    }

    @Override
    public AppointmentNode removeFromWaitingList(String appointmentId) {
        return waitingListQueue.dequeue();
    }

    public String addPayment(double paymentAmount, String patientid) {
        billing.setPatientId(patientid); // Set the patient ID
        return billing.addPayment(paymentAmount); // Delegate to BillingImpl
    }


    public String getPaymentStatus(String patientID) {
        billing.setPatientId(patientID);
        return billing.getPaymentStatus();
    }

    public String generateBill(String id, double billingAmount) {
        billing.setPatientId(id);
        return billing.generateBill(billingAmount);
    }
    public String generatePatientReport(String patientID) {
        PatientImpl p = patients.search(patientID);
        this.reportGenerator.setPatient(p);

        if (p != null) {
            return this.reportGenerator.generatePatientReport();
        }
        return "There is no patient with ID: " + patientID;
    }


    public String generateAppointmentReport() {
        return this.reportGenerator.generateAppointmentReport();
    }

    @Override
    public String generateRevenueReport() {
        return reportGenerator.generateRevenueReport();
    }
    private void loadAppointmentsFromDatabase(Connection connection) {
        try {
            System.out.println("Loading Appointment from database...");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");
            while (resultSet.next()) {
                String appointmentID = String.valueOf(resultSet.getInt("appointment_id"));
                String patientid = resultSet.getString("patient_id");
                String date = resultSet.getString("appointment_date");
                String time = resultSet.getString("appointment_time");
                String status = resultSet.getString("status");

                int sev = 0;
                try {
                    sev = resultSet.getInt("severity");
                } catch (SQLException e) {
                    System.out.println("Error parsing severity: " + e.getMessage());
                }

                PatientImpl p = patients.search(patientid);
                if (p == null) {
                    System.out.println("Warning: Patient with ID " + patientid + " not found. Skipping appointment.");
                    continue; // Skip this appointment if patient is not found
                }

                AppointmentNode app = new AppointmentNode(appointmentID, p, date, time, sev, status);
                appointmentQueue.enqueue(app);
            }
            System.out.println("Appointment loaded into Queue.");
        } catch (SQLException e) {
            System.out.println("Failed to load Appointment from database!");
            e.printStackTrace();
        }
    }

    private void loadWaitinglistFromDatabase(Connection connection) {
        try {
            System.out.println("Loading WaitingList from database...");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM waiting_list");
            while (resultSet.next()) {
                String waitinglistID = String.valueOf(resultSet.getInt("waiting_id"));
                String patientid = resultSet.getString("patient_id");
                String added_at = resultSet.getString("added_at");
               // String appointment_id = resultSet.getString("appointment_id");
                String severity = resultSet.getString("severity");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");

                PatientImpl p = patients.search(patientid);
                if (p == null) {
                    System.out.println("Warning: Patient with ID " + patientid + " not found. Skipping waiting list entry.");
                    continue; // Skip this waiting list entry if patient is not found
                }

                int sev = 0;
                if (severity != null && !severity.isEmpty()) {
                    try {
                        sev = Integer.parseInt(severity);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid severity value: " + severity);
                        continue;
                    }
                }

                addToWaitingList( p, date, time, sev);
            }
            System.out.println("Waiting List loaded into Priority Queue.");
        } catch (SQLException e) {
            System.out.println("Failed to load Waiting List from database!");
            e.printStackTrace();
        }
    }

}
