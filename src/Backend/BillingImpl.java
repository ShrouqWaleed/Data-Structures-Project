package Backend;
import java.sql.*;
import java.util.ArrayList;

public class BillingImpl {
    private String patientId;
    private Connection connection;

    public BillingImpl(String patientIdString, Connection connection) {
        try {
            this.patientId = patientIdString;
            this.connection = connection;
        } catch (NumberFormatException e) {
            System.out.println("Invalid patient ID format: " + patientIdString);
            e.printStackTrace();
        }
    }

    public String generateBill(double amount) {
        if (this.connection != null) {
            try {
                String query = "INSERT INTO billing (patient_id, billing_amount, remaining_balance, payment_status) VALUES (?, ?, ?, 'Pending')";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(patientId));
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount); // Initially, remaining balance equals billing amount
                int rowsAffected;
                try {
                    rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        return "Bill generated successfully for patient ID: " + patientId;
                    }
                } catch (SQLException e) {
                    System.out.println("Bill Generated");
                }

            } catch (SQLException e) {
                System.out.println("Bill Generated.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null, cannot generate bill.");
        }
        return "Bill generation!";
    }

    public String addPayment(double paymentAmount) {
        if (this.connection != null) {
            try {
                // Fetch current remaining balance
                String selectQuery = "SELECT remaining_balance FROM billing WHERE patient_id = ?";
                PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                selectStmt.setInt(1, Integer.parseInt(patientId));
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    double remainingBalance = rs.getDouble("remaining_balance");

                    if (remainingBalance > 0) {
                        // Calculate new remaining balance
                        double newRemainingBalance = Math.max(remainingBalance - paymentAmount, 0);

                        // Determine payment status
                        String newPaymentStatus = newRemainingBalance == 0 ? "Paid" : "Pending";

                        // Update payment history, remaining balance, and payment status
                        String updateQuery = "UPDATE billing SET payment_history = CONCAT(COALESCE(payment_history, ''), ?), remaining_balance = ?, payment_status = ? WHERE patient_id = ?";
                        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                        updateStmt.setString(1, paymentAmount + ", ");
                        updateStmt.setDouble(2, newRemainingBalance);
                        updateStmt.setString(3, newPaymentStatus);
                        updateStmt.setInt(4, Integer.parseInt(patientId));
                        int rowsAffected = updateStmt.executeUpdate();

                        if (rowsAffected > 0) {
                            return "Payment added successfully.";
                        }
                    } else {
                        return "No outstanding balance to pay.";
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error adding payment.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null, cannot add payment.");
        }
        return "Payment failed.";
    }

    public String getPaymentStatus() {
        if (this.connection != null) {
            try {
                // Query to fetch payment status, billing amount, and remaining balance
                String query = "SELECT billing_amount, remaining_balance, payment_status FROM billing WHERE patient_id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(patientId));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double billingAmount = rs.getDouble("billing_amount");
                    double remainingBalance = rs.getDouble("remaining_balance");
                    String paymentStatus = rs.getString("payment_status");

                    // Return a detailed status
                    return "Billing amount: " + billingAmount + "\nRemaining balance: " + remainingBalance + "\nPayment status: " + paymentStatus;
                }
            } catch (SQLException e) {
                System.out.println("Error fetching payment status.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null, cannot fetch payment status.");
        }

        return "Payment status not found.";
    }

    public static ArrayList<Double> getAllRevenue(Connection connection) {
        ArrayList<Double> revenues = new ArrayList<>();
        if (connection != null) {
            try {
                String query = "SELECT billing_amount FROM billing";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    revenues.add(rs.getDouble("billing_amount"));
                }
            } catch (SQLException e) {
                System.out.println("Error fetching all revenues.");
                e.printStackTrace();
            }
        }
        return revenues;
    }

    public static double calculateTotalRevenue(Connection connection) {
        double totalRevenue = 0.0;
        if (connection != null) {
            try {
                String query = "SELECT SUM(billing_amount) FROM billing";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalRevenue = rs.getDouble(1);
                }
            } catch (SQLException e) {
                System.out.println("Error calculating total revenue.");
                e.printStackTrace();
            }
        }
        return totalRevenue;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
