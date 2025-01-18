package Backend;

public interface Appointment {
    // make schedule for a patient
    public boolean scheduleAppointment(String patient,String date,String time,String doctor);
    // make cancel appointment for a patient
    public boolean cancelAppointment(String appointmentId);
    // make reschedule for an appointment
    public boolean reschedule(String appointmentId,String date,String time,String doctor);
    // this method check if there is a conflict appointment for the same patient
    public boolean isConflict(String patient,String date,String time);
    // get patient
    public String getPatient();
    // get date
    public String getDate();
    // get time
    public String getTime();
    // get doctor
    public String getDoctor();
    // get appointmentId
    public String getAppointmentId();

    void setStatus(String canceled);
}
