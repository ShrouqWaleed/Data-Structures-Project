package Backend;

public interface Patient {


        // add a visit record
        void addVisitRecord(String visitRecord);

        // update the contact info of the patient
        void updateContactInfo(String newContact);

        // get patient's detailed info
        String getPatientInfo();

        // get patient ID ((for the BST))
        String getPatientID();

        // get patient's name
        String getName();

        // get patient's age
        int getAge();

        // get patient's visit records
        String getVisitRecords();

        // get patient's medical history
        String getMedicalHistory();




}
