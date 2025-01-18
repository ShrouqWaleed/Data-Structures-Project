package Backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class BillingManager {
    private Connection connection;

    public BillingManager(Connection connection) {
        this.connection = connection;
    }

    public String generateBill(int patientID, double billAmount) {
        try {
            // Ensure patientID is an integer
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO billing (patient_id, billing_amount) VALUES (?, ?)");
            stmt.setInt(1, patientID);
            stmt.setDouble(2, billAmount);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Bill generated successfully.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Error generating bill.";
    }
    public boolean insertBilling(int patientID, double amount, String description) throws SQLException {
        String insertBillingSQL = "INSERT INTO billing (patient_id, amount, description) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertBillingSQL)) {
            // Set the parameters for the SQL query
            stmt.setInt(1, patientID);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);

            // Execute the insert query
            int rowsAffected = stmt.executeUpdate();

            // Return true if the billing record was successfully inserted
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting billing details into database: " + e.getMessage());
            throw e;  // Rethrow the exception to be handled by the caller
        }
    }

    public String addPayment(int patientID, double paymentAmount) {
        try {
            // Ensure patientID is an integer
            PreparedStatement stmt = connection.prepareStatement("UPDATE billing SET payment_history = CONCAT(COALESCE(payment_history, ''), ?) WHERE patient_id = ?");
            stmt.setDouble(1, paymentAmount);
            stmt.setInt(2, patientID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Payment added successfully.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Payment failed.";
    }

    public String getPaymentStatus(String patientID) {
        BillingImpl billing = new BillingImpl(patientID, connection);
        return billing.getPaymentStatus();
    }

    public ArrayList<Double> getAllRevenue() {
        return BillingImpl.getAllRevenue(connection);
    }

    public double getTotalRevenue() {
        return BillingImpl.calculateTotalRevenue(connection);
    }
}
