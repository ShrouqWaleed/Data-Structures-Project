package Backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppointmentImp {
    protected final PriorityQueue appointmentQueue; // Use the custom PriorityQueue
    private final Connection connection;

    // Constructor that accepts the database connection
    public AppointmentImp(Connection connection) {
        this.connection = connection;
        this.appointmentQueue = new PriorityQueue(); // Initialize the custom PriorityQueue
    }

    // Method for scheduling an appointment and storing it in the database
    public boolean scheduleAppointment(PatientImpl patient, String date, String time, int severity) {
        // Create the AppointmentNode object
        AppointmentNode node = new AppointmentNode(patient, date, time, severity);

        // Inside scheduleAppointment method, when you insert into the appointments table
        try {
            // Ensure patient ID is not null before proceeding
            String patientIDStr = patient.getPatientID();
            if (patientIDStr == null || patientIDStr.isEmpty()) {
                System.err.println("Patient ID is null or empty. Cannot schedule appointment.");
              //  return false;  // Return early if there's no valid patient ID
            }

            // Insert the appointment into the database
            String appointmentInsertSQL = "INSERT INTO appointments (patient_id, appointment_date, appointment_time, severity, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(appointmentInsertSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, Integer.parseInt(patientIDStr)); // Use the patient_id
                stmt.setString(2, date);
                stmt.setString(3, time);
                stmt.setInt(4, severity); // Include severity in the database
                stmt.setString(5, "Scheduled"); // Default status

                int affectedRows = stmt.executeUpdate();

                // Retrieve the automatically generated appointment_id from the database
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int appointmentId = generatedKeys.getInt(1); // Get generated appointment_id
                            node.setAppointmentId(String.valueOf(appointmentId)); // Set the appointment ID in the node
                        }
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
        return true;
    }

    // Method to cancel an appointment
    public AppointmentNode cancelAppointment(String appointmentID) {
        AppointmentNode removedNode = null;

        // Step 1: Remove the appointment from the PriorityQueue by patient ID
        for (AppointmentNode node : appointmentQueue.heap) {
            if (node.getAppointmentId().equalsIgnoreCase(appointmentID)) {
                removedNode = node;
                appointmentQueue.dequeueByPatientID(node.getPatient().getPatientID());
                break;
            }
        }

        // Step 2: Handle if appointment not found
        if (removedNode == null) {
            System.out.println("Appointment with ID " + appointmentID + " not found.");
            return null;
        }

        // Step 3: Update the appointment status in the database
        String updateSQL = "UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, appointmentID);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Appointment cancelled for ID " + appointmentID);
            } else {
                System.err.println("No appointment found with ID " + appointmentID + " in the database.");
            }
        } catch (SQLException e) {
            System.err.println("Error cancelling appointment: " + e.getMessage());
        }

        return removedNode;
    }

    // Method to reschedule an appointment
    public boolean rescheduleAppointment(String appointmentID, String newDate, String newTime) {
        AppointmentNode current = null;

        // Find the appointment with the given appointmentID
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

        // Remove the old appointment and re-add the updated appointment
        appointmentQueue.dequeueByPatientID(current.getPatient().getPatientID());
        current.setDate(newDate);
        current.setTime(newTime);
        appointmentQueue.enqueue(current);

        // Update the appointment date and time in the database
        try {
            String updateSQL = "UPDATE appointments SET appointment_date = ?, appointment_time = ? WHERE appointment_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
                stmt.setString(1, newDate);
                stmt.setString(2, newTime);
                stmt.setString(3, appointmentID);
                stmt.executeUpdate();
                System.out.println("Appointment rescheduled for " + current.getPatient().getPatientID() + " on " + newDate + " at " + newTime);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error rescheduling appointment: " + e.getMessage());
        }

        return false;
    }

    // Check if an appointment is available
    public boolean isAvailable(String date, String time) {
        for (AppointmentNode node : appointmentQueue.heap) {
            if (node.getDate().equals(date) && node.getTime().equals(time)) {
                return false; // Time slot is taken
            }
        }
        return true; // Time slot is available
    }

    // Display all appointments
    public void displayAppointments() {
        appointmentQueue.displayQueue();
    }
}
