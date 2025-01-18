package Backend;

import java.sql.*;

class PatientBST {
    private PatientNode root;
    private DatabaseManager dbManager;

    public PatientBST(DatabaseManager dbManager) {
        this.root = null;
        this.dbManager = dbManager;
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        try {
            ResultSet result = dbManager.getAllPatients();
            while (result.next()) {
                String patientID = result.getString("patient_id");
                String name = result.getString("name");
                int age = result.getInt("age");
                String contactInfo = result.getString("contact_info");
                String medicalHistory = result.getString("medical_history");
                String visitRecord = result.getString("visit_records");

                if (patientID == null || name == null || patientID.isEmpty() || name.isEmpty()) {
                    continue;
                }

                PatientImpl patient = new PatientImpl(patientID, name, age, contactInfo, medicalHistory, visitRecord);
                root = insertRec(root, patient);
            }
        } catch (SQLException e) {
            System.err.println("Error loading patients from database: " + e.getMessage());
        }
    }

    public void insert(PatientImpl patient) {
        if (patient == null || patient.getName() == null || patient.getName().isEmpty()) {
            return;
        }

        try {
            PatientImpl existingPatient = search(patient.getPatientID());
            if (existingPatient != null) {
                System.out.println("Patient already exists in the database.");
                return;
            }
            String generatedPatientID = dbManager.insertPatient(patient);
            if (generatedPatientID != null && !generatedPatientID.isEmpty()) {
                patient.setPatientID(generatedPatientID);
                root = insertRec(root, patient);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting patient into database: " + e.getMessage());
        }
    }

    private PatientNode insertRec(PatientNode root, PatientImpl patient) {
        if (patient.getPatientID() == null) {
            return root;
        }

        if (root == null) {
            return new PatientNode(patient);
        }

        if (patient.getPatientID().compareTo(root.patient.getPatientID()) < 0) {
            root.left = insertRec(root.left, patient);
        } else if (patient.getPatientID().compareTo(root.patient.getPatientID()) > 0) {
            root.right = insertRec(root.right, patient);
        }
        return root;
    }
    public void update(PatientImpl patient) {
        if (patient == null || patient.getPatientID() == null || patient.getPatientID().isEmpty()) {
            System.out.println("Patient ID is invalid.");
            return;
        }

        PatientImpl existingPatient = search(patient.getPatientID());
        if (existingPatient != null) {
            try {
                boolean isUpdated = dbManager.updatePatient(patient);

                if (isUpdated) {
                    // Delete the old patient from the BST
                    root = deleteRec(root, patient.getPatientID());

                    // Reinsert the updated patient into the BST
                    root = insertRec(root, patient);

                    System.out.println("Patient updated successfully.");
                } else {
                    System.out.println("Failed to update patient in the database.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating patient in database: " + e.getMessage());
            }
        } else {
            System.out.println("Patient not found in BST, unable to update.");
        }
    }


    public void delete(String patientID) {
        if (patientID == null || patientID.isEmpty()) {
            return;
        }
        root = deleteRec(root, patientID);
        try {
            dbManager.deletePatient(patientID);
        } catch (SQLException e) {
            System.err.println("Error deleting patient from database: " + e.getMessage());
        }
    }

    private PatientNode deleteRec(PatientNode root, String patientID) {
        if (root == null) {
            return null;
        }
        if (patientID.compareTo(root.patient.getPatientID()) < 0) {
            root.left = deleteRec(root.left, patientID);
        } else if (patientID.compareTo(root.patient.getPatientID()) > 0) {
            root.right = deleteRec(root.right, patientID);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.patient = findMin(root.right).patient;
            root.right = deleteRec(root.right, root.patient.getPatientID());
        }
        return root;
    }

    private PatientNode findMin(PatientNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    public PatientImpl search(String patientID) {
        if (patientID == null || patientID.isEmpty()) {
            return null;
        }
        return searchRec(root, patientID);
    }

    private PatientImpl searchRec(PatientNode root, String patientID) {
        if (root == null) {
            return null;
        }
        if (patientID.equals(root.patient.getPatientID())) {
            return root.patient;
        }
        if (patientID.compareTo(root.patient.getPatientID()) < 0) {
            return searchRec(root.left, patientID);
        }
        return searchRec(root.right, patientID);
    }

    public void printInOrder() {
        printInOrderRec(root);
    }

    private void printInOrderRec(PatientNode root) {
        if (root != null) {
            printInOrderRec(root.left);
            System.out.println(root.patient.getPatientInfo());
            printInOrderRec(root.right);
        }
    }

    public void displayAll() {
        if (root == null) {
            System.out.println("The BST is empty.");
        } else {
            displayAllRec(root);
        }
    }

    private void displayAllRec(PatientNode node) {
        if (node != null) {
            displayAllRec(node.left);
            System.out.println(node.patient.getPatientInfo());
            System.out.println("--------------------");
            displayAllRec(node.right);
        }
    }
}
