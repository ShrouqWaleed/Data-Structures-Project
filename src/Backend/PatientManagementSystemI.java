package Backend;

import java.sql.SQLException;

public interface PatientManagementSystemI {
    boolean addPatient(String name, int age, String contactInfo, String medicalHistory);

    Patient findPatient(String id) throws SQLException;

    boolean updatePatientContactInfo(String id, String newContactInfo);

    boolean addVisitRecord(String id, String visitDetails);

    boolean scheduleAppointment(PatientImpl patient, String date, String time, int severity);

    boolean rescheduleAppointment(String appointmentID, String newDate, String newTime);

    boolean cancelAppointment(String appointmentID);

    boolean addToWaitingList(PatientImpl patient, String date, String time, int severity);

    AppointmentNode removeFromWaitingList(String appointmentId);

    String generateBill(String id, double billingAmount);

    String addPayment(double paymentAmount, String patientid);

    // Default implementation for getPaymentStatus
    default String getPaymentStatus(String patientID) {
        return "Payment status is not available."; // Default message if not implemented
    }

    String generatePatientReport(String id);

    String generateAppointmentReport();

    String generateRevenueReport();

    void displayAllPatients();

    String displayAllAppointments();
}
