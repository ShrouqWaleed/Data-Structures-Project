package Backend;

import java.sql.*;
import java.util.ArrayList;

public class ReportGenerator {

    private PatientImpl patient; // Patient details
    private PriorityQueue appointment; // Appointments queue
    private Connection connection; // Database connection

    // Constructor with DB connection
    public ReportGenerator(PriorityQueue appointment, Connection connection) {
        this.appointment = appointment;
        this.connection = connection;
    }

    // Generate patient report
    public String generatePatientReport() {
        try {
            // Fetch visit records from the database
            String query = "SELECT name, visit_records FROM patients WHERE patient_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(patient.getPatientID()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String patientName = rs.getString("name");
                String visitRecords = rs.getString("visit_records");

                String report = "Patient Name: " + patientName + "\nVisit Records:\n" + visitRecords;

                saveReport("Patient", report); // Save report in DB
                return report;
            } else {
                return "Patient not found.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error generating patient report.";
        }
    }

    // Generate appointment report
    public String generateAppointmentReport() {
        try {
            StringBuilder report = new StringBuilder("Appointments:\n");

            // Fetch appointments from the database
            String query = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                report.append("Appointment ID: ").append(appointmentId)
                        .append("\nPatient ID: ").append(patientId)
                        .append("\nDate: ").append(appointmentDate)
                        .append("\nTime: ").append(appointmentTime)
                        .append("\nStatus: ").append(status)
                        .append("\n\n");
            }

            saveReport("Appointment", report.toString()); // Save report in DB
            return report.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error generating appointment report.";
        }
    }

    // Generate revenue report
    public String generateRevenueReport() {
        ArrayList<Double> revenue = BillingImpl.getAllRevenue(connection); // Fetch revenue
        mergeSort(revenue); // Sort revenue
        double totalRevenue = BillingImpl.calculateTotalRevenue(connection); // Calculate total revenue

        StringBuilder report = new StringBuilder("Revenue Report:\nTotal Revenue: ").append(totalRevenue).append("\nDetails:\n");
        for (double value : revenue) {
            report.append(value).append("\n");
        }

        saveReport("Billing", report.toString()); // Save report in DB
        return report.toString();
    }

    // Save report in the reports table
    private void saveReport(String reportType, String reportData) {
        try {
            String query = "INSERT INTO reports (report_type, report_data) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, reportType);
            stmt.setString(2, reportData);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving report to the database.");
        }
    }

    // MergeSort implementation for ArrayList<Double>
    private void mergeSort(ArrayList<Double> list) {
        if (list.size() <= 1) return;

        int middle = list.size() / 2;
        ArrayList<Double> left = new ArrayList<>(list.subList(0, middle));
        ArrayList<Double> right = new ArrayList<>(list.subList(middle, list.size()));

        mergeSort(left);
        mergeSort(right);

        merge(list, left, right);
    }

    private void merge(ArrayList<Double> list, ArrayList<Double> left, ArrayList<Double> right) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i) <= right.get(j)) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) {
            list.set(k++, left.get(i++));
        }

        while (j < right.size()) {
            list.set(k++, right.get(j++));
        }
    }

    // Set the patient
    public void setPatient(PatientImpl patient) {
        this.patient = patient;
    }
}
