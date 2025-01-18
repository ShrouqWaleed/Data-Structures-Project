package Backend;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.UUID;

public class WaitingListImp {
    // PriorityQueue to store AppointmentNode objects
    protected PriorityQueue<AppointmentNode> waitingList;

    public WaitingListImp() {
        this.waitingList = new PriorityQueue<>(Comparator.comparingInt(AppointmentNode::getSeverity).reversed());
    }

    public boolean createWaitingList() {
        System.out.println("Waiting list has been created.");
        return true;
    }

    // Method to add patient to the waiting list
    public boolean addToWaitingList(String appointmentId, PatientImpl patient, String date, String time, int severity) {
        if (patient == null || appointmentId == null || date == null || time == null) {
            System.out.println("Invalid input, cannot add to the waiting list.");
            return false;
        }

        if (patient.getPatientID() == null) {
            patient.setPatientID(UUID.randomUUID().toString());
        }

        // Create an AppointmentNode and add it to the queue
        AppointmentNode node = new AppointmentNode(patient, date, time, severity);
        waitingList.offer(node);

        System.out.println("Added to waiting list: Appointment ID " + appointmentId + ", Patient ID: " + patient.getPatientID() + ", Severity: " + severity);
        return true;
    }

    // Method to remove a patient from the waiting list by their patient ID
    public boolean removeFromWaitingList(String patientID) {
        if (waitingList.isEmpty()) {
            System.out.println("Waiting list is empty, cannot remove.");
            return false;
        }

        // Iterate over the list and remove the patient by ID
        for (AppointmentNode node : waitingList) {
            if (node.getPatient().getPatientID().equals(patientID)) {
                waitingList.remove(node);
                System.out.println("Removed from waiting list: Appointment ID " + node.getAppointmentId() + ", Patient ID: " + patientID);
                return true;
            }
        }

        System.out.println("Patient not found in the waiting list.");
        return false;
    }

    // Method to find the next patient based on appointment date and time
    public AppointmentNode findNextPatient(String date, String time) {
        for (AppointmentNode node : waitingList) {
            if (node.getDate().equals(date) && node.getTime().equals(time)) {
                return node;  // Return the patient with the highest severity if matching date/time
            }
        }
        return null;  // No matching patient found
    }

    // Display the current waiting list, sorted by severity
    public void displayWaitingList() {
        if (waitingList.isEmpty()) {
            System.out.println("The waiting list is currently empty.");
            return;
        }

        System.out.println("Current Waiting List (Sorted by Severity):");
        for (AppointmentNode node : waitingList) {
            System.out.println("Appointment ID: " + node.getAppointmentId() + ", Patient ID: " + node.getPatient().getPatientID() + ", Severity: " + node.getSeverity());
        }
    }
}
