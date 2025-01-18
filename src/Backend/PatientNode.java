package Backend;

public class PatientNode {
    PatientImpl patient;
    PatientNode left, right;


    public PatientNode(PatientImpl patient) {
        this.patient = patient;
        this.left=null;
        this.right=null;
    }
}
