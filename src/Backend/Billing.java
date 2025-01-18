package Backend;

public interface Billing {
    // Method returns true if bill is generated or false if biiling ammount is invalid
    public boolean generateBill(String patientID , double billAmount );

    // Method  returns true is payment was added correct
    public boolean addPayment (String patientID , double paymentAmount );

    // Method returns description of the payment
    public String getPaymentStatus (String patientID);


}
