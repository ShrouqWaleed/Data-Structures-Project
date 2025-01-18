package Backend;

public class AppointmentNode {
    private String appointmentId;
    private PatientImpl patient;
    private String date;
    private String time;
    private String status;
    private int severity;
    AppointmentNode next;
    public AppointmentNode front;


    // Constructor with appointmentId
    public AppointmentNode(String appointmentId, PatientImpl patient, String date, String time, int severity) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.status = "Scheduled"; // Default status
        this.severity = severity;
        this.next = null;
    }
    // Constructor with appointmentId
    public AppointmentNode(String appointmentId, PatientImpl patient, String date, String time, int severity, String status) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.status = status; // Default status
        this.severity = severity;
        this.next = null;
    }

    // Constructor without appointmentId, for when ID is generated in the database
    public AppointmentNode(PatientImpl patient, String date, String time, int severity) {
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.status = "Scheduled"; // Default status
        this.severity = severity;
        this.next = null;
    }

    public AppointmentNode(PatientImpl patient, String date, String time, int severity, String status) {
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.status = "Scheduled"; // Default status
        this.severity = severity;
        this.status = status;
        this.next = null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public PatientImpl getPatient() {
        return patient;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AppointmentID: " + this.appointmentId + "\n" +
                "Patient: " + this.patient.getName() + "\n" +
                "Date: " + this.date + "\n" +
                "Time: " + this.time + "\n" +
                "Status: " + this.status + "\n" +
                "Severity: " + this.severity;
    }
}
