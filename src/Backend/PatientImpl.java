package Backend;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PatientImpl implements Patient {

    private String patientID; // Patient ID as a string for flexibility
    private String name; // Patient's name
    private int age; // Patient's age
    private String contactInfo; // Patient's contact information
    private List<String> medicalHistory; // List of medical history entries
    private ArrayList<String> visitRecords; // List of visit records
    private Connection connection; // Database connection

    // Constructor for creating a patient object with ID and connection (used when ID is known)
    public PatientImpl(String patientID, Connection connection) {
        if (patientID == null || patientID.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty.");
        }
        this.patientID = patientID;
        this.connection = connection;
        this.medicalHistory = new ArrayList<>();
        this.visitRecords = new ArrayList<>();
    }

    // Constructor for creating a new patient object without ID (ID is auto-generated)
    public PatientImpl(String name, int age, String contactInfo, String medicalHistory, String visitRecord) {
        validatePatientDetails(name, age, contactInfo);

        this.name = name;
        this.age = age;
        this.contactInfo = contactInfo;
        this.medicalHistory = new ArrayList<>();
        this.visitRecords = new ArrayList<>();
        this.medicalHistory.add(medicalHistory);
        this.visitRecords.add(visitRecord);
    }

    // Overloaded constructor without a visit record
    public PatientImpl(String name, int age, String contactInfo, String medicalHistory) {
        validatePatientDetails(name, age, contactInfo);

        this.name = name;
        this.age = age;
        this.contactInfo = contactInfo;
        this.medicalHistory = new ArrayList<>();
        this.visitRecords = new ArrayList<>();
        this.medicalHistory.add(medicalHistory);
    }

    // Full constructor with all details (used when all information is retrieved from the database)
    public PatientImpl(String patientID, String name, int age, String contactInfo, String medicalHistory, String visitRecord) {
        if (patientID == null || patientID.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty.");
        }
        validatePatientDetails(name, age, contactInfo);

        this.patientID = patientID;
        this.name = name;
        this.age = age;
        this.contactInfo = contactInfo;
        this.medicalHistory = new ArrayList<>();
        this.visitRecords = new ArrayList<>();
        this.medicalHistory.add(medicalHistory);
        this.visitRecords.add(visitRecord);
    }

    // Validate patient details
    private void validatePatientDetails(String name, int age, String contactInfo) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be greater than zero.");
        }
        if (contactInfo == null || contactInfo.isEmpty()) {
            throw new IllegalArgumentException("Contact Info cannot be null or empty.");
        }
    }

    // Setters
    public void setPatientID(String patientID) {
        if (patientID == null || patientID.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty.");
        }
        this.patientID = patientID;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be greater than zero.");
        }
        this.age = age;
    }

    public void setContactInfo(String newContact) {
        if (newContact == null || newContact.isEmpty()) {
            throw new IllegalArgumentException("Contact Info cannot be null or empty.");
        }
        this.contactInfo = newContact;
    }

    // Add visit record
    public void addVisitRecord(String visitRecord) {
        if (visitRecord != null && !visitRecord.isEmpty()) {
            visitRecords.add(visitRecord);
        } else {
            System.err.println("Invalid visit record.");
        }
    }

    @Override
    public void updateContactInfo(String newContact) {

    }

    // Add medical history
    public void addMedicalHistory(String historyEntry) {
        if (historyEntry != null && !historyEntry.isEmpty()) {
            medicalHistory.add(historyEntry);
        } else {
            System.err.println("Invalid medical history entry.");
        }
    }

    // Getters
    public String getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getVisitRecords() {
        return String.join(", ", visitRecords);
    }

    public ArrayList<String> getVisitRecordsArr() {
        return visitRecords;
    }

    public String getMedicalHistory() {
        return String.join(", ", medicalHistory);
    }

    // Get patient info as a formatted string
    @Override
    public String getPatientInfo() {
        return "Patient ID: " + (patientID != null ? patientID : "N/A") + "\n" +
                "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "Contact Info: " + contactInfo + "\n" +
                "Medical History: " + String.join(", ", medicalHistory) + "\n" +
                "Visit Records: " + String.join(", ", visitRecords);
    }

    // Override toString for debugging and printing
    @Override
    public String toString() {
        return getPatientInfo();
    }
}
