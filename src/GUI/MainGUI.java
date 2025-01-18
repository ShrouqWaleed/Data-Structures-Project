package GUI;

import Backend.*;
import com.mysql.cj.x.protobuf.MysqlxCursor;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainGUI extends Application implements Initializable  {

    // Main Nodes

    @FXML
    protected Parent root;
    @FXML
    protected Stage stage;
    @FXML
    protected Scene scene;

    // Main AnchorPane
    @FXML
    protected AnchorPane FullPane;

    // Main Buttons
    @FXML
    private Button AppointmentsBTN;
    @FXML
    private Button BillingBTN;
    @FXML
    private Button PatientsBTN;
    @FXML
    private Button ReportsBTN;

    // Center Panes and their sub Panes
    //     Patient Main && Sub Panes && Buttons
    // Main Patient Pane
    @FXML
    private AnchorPane patientMainPane;
    // Opening Panes
    @FXML
    protected AnchorPane OpeningPane = new AnchorPane();

    // Sub Patient Panes
    @FXML
    private AnchorPane patientSubPane1;
    // -- Sub Pane 1 Text Fields
    @FXML
    private TextField PatientAgeText;
    @FXML
    private TextField PatientContactText;
    @FXML
    private TextField PatientHistoryText;
    @FXML
    private TextField PatientNameText;


    @FXML
    private AnchorPane patientSubPane2;
    @FXML
    private AnchorPane patientSubPane3;
    @FXML
    private AnchorPane patientSubPane4;
    // Patient Side Buttons
    @FXML
    private Button addPatientBTN;
    @FXML
    private Button findPatientBTN;
    @FXML
    private Button updatePatientBTN;
    @FXML
    private Button addVisitBTN;



    //     Appointment Main && Sub Panes
    @FXML
    private AnchorPane appointmentMainPane;

    //     Billing Main && Sub Panes
    @FXML
    private AnchorPane billingMainPane;


    //     Reports Main && Sub Panes
    @FXML
    private AnchorPane reportsMainPane;


    // Main Side Panes
    @FXML
    private AnchorPane sidePane1;
    @FXML
    private AnchorPane sidePane2;
    @FXML
    private AnchorPane sidePane3;
    @FXML
    private AnchorPane sidePane4;
    @FXML
    private Label pressAnywhereLabel;


    @FXML
    FadeTransition fade1, fade2, fade3, fade4, fade5, fade6;
    @FXML
    ScaleTransition scale1;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            patientManagementSystem = new PatientManagementSystem();
            System.out.println("WORKING1");
        } catch (SQLException e) {
            System.out.println("NOT WORKING1");
        }
        fade1 = new FadeTransition();
        fade2 = new FadeTransition();
        fade3 = new FadeTransition();
        fade4 = new FadeTransition();
        fade5 = new FadeTransition();
        fade6 = new FadeTransition();

        scale1 = new ScaleTransition();
        scale1.setNode(pressAnywhereLabel);
        scale1.setDuration(Duration.millis(750));
        scale1.setCycleCount(TranslateTransition.INDEFINITE);
        scale1.setInterpolator(Interpolator.LINEAR);
        scale1.setByX(0.2);
        scale1.setByY(0.2);
        scale1.setAutoReverse(true);
        scale1.play();

        OpeningPane.setOnMouseClicked(event -> {
            patientSubPane1.setVisible(true);
            patientSubPane2.setVisible(false);
            patientSubPane3.setVisible(false);
            patientSubPane4.setVisible(false);

            fade3.setNode(OpeningPane);
            fade3.setDuration(Duration.millis(50));
            fade3.setInterpolator(Interpolator.LINEAR);
            fade3.setFromValue(1);
            fade3.setToValue(0);
            fade3.play();
            OpeningPane.setVisible(false);
//
            sidePane1.setVisible(true);
            sidePane2.setVisible(false);
            sidePane3.setVisible(false);
            sidePane4.setVisible(false);
            patientMainPane.setVisible(true);
            appointmentMainPane.setVisible(false);
            billingMainPane.setVisible(false);
            reportsMainPane.setVisible(false);


            fade1.setNode(patientMainPane);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

            fade2.setNode(sidePane1);
            fade2.setDuration(Duration.millis(500));
            fade2.setInterpolator(Interpolator.LINEAR);
            fade2.setFromValue(0);
            fade2.setToValue(1);
            fade2.play();
        });




    }




    public static void main(String[] args) {
        launch(args);
    }
    PatientManagementSystem patientManagementSystem;
    @Override
    public void start(Stage primaryStage) throws IOException  {

        this.stage = primaryStage;
        this.stage.setTitle("Hospital System");
        primaryStage.setResizable(false);
        IntroMenu();

        OpeningPane.setOnMouseClicked(event -> {
            OpeningPane.setVisible(false);
        });

    }

    public void IntroMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainGUIFX - Copy.fxml"));
        FullPane = loader.load();

        scene = new Scene(FullPane);
        stage.setScene(scene);
        stage.show();

    }


    // -------- Patient Methods, Buttons, Labels and Text Fields ----------

    @FXML
    Label addPatientLabel;
    public void AddPatient(ActionEvent press) {
        try {

        if (patientManagementSystem.addPatient(PatientNameText.getText(), Integer.parseInt(PatientAgeText.getText()),
                PatientContactText.getText(), PatientHistoryText.getText())) {
            addPatientLabel.setText("Patient has been added Successfully!");
            PatientNameText.clear();
            PatientAgeText.clear();
            PatientContactText.clear();
            PatientHistoryText.clear();
        } else {
            addPatientLabel.setText("Error Adding Patient");
            PatientNameText.clear();
            PatientAgeText.clear();
            PatientContactText.clear();
            PatientHistoryText.clear();
        }
    } catch (Exception e) {
            addPatientLabel.setText("Please Enter valid Input");
            PatientNameText.clear();
            PatientAgeText.clear();
            PatientContactText.clear();
            PatientHistoryText.clear();
        }
    }

    @FXML
    TextField PatientIDText1;
    @FXML
    Label patientDataLabel1;
    public void findPatient(ActionEvent press) throws SQLException {
        PatientImpl patient = (PatientImpl) patientManagementSystem.findPatient(PatientIDText1.getText());
        patientDataLabel1.setText(patient.getPatientInfo());
        PatientIDText1.clear();
    }

    @FXML
    Label updatePatientLabel;
    @FXML
    TextField PatientIDText2;
    @FXML
    TextField PatientNewContactText;
    public void updatePatientContact(ActionEvent press) {
        if (patientManagementSystem.updatePatientContactInfo(PatientIDText2.getText(), PatientNewContactText.getText())) {
            updatePatientLabel.setText("Patient's Contact Information has been updated!");
            PatientIDText2.clear();
            PatientNewContactText.clear();
        } else {
            updatePatientLabel.setText("Error updating Patient's Contact Information");
            PatientIDText2.clear();
            PatientNewContactText.clear();
        }
    }


    @FXML
    TextField PatientIDText3, PatientVisitRecordsText;
    @FXML
    Label addVisitLabel;
    public void addVisitRecord(ActionEvent press) {
        if (patientManagementSystem.addVisitRecord(PatientIDText3.getText(), PatientVisitRecordsText.getText())) {
            addVisitLabel.setText("Visit Record has been added!");
            PatientIDText3.clear();
            PatientVisitRecordsText.clear();
        } else {
            addVisitLabel.setText("Error adding Visit Record");
            PatientIDText3.clear();
            PatientVisitRecordsText.clear();

        }
    }



    // -------- Appointment Methods, Buttons, Labels and Text Fields ----------
    @FXML
    TextField PatientIDText4, AppointmentSeverityText;
    @FXML
    TextField AppointmentYearText1, AppointmentMonthText1,
            AppointmentDayText1, AppointmentHourText1, AppointmentMinuteText1;
    @FXML
    Label ScheduleAppointmentLabel;

    public void addAppointment(ActionEvent press) throws SQLException {
        String Date1 = AppointmentYearText1.getText() + "-" + AppointmentMonthText1.getText() +
                "-" + AppointmentDayText1.getText();
        String Time1 = AppointmentHourText1.getText() + ":" + AppointmentMinuteText1.getText();
        PatientImpl patient = (PatientImpl) patientManagementSystem.findPatient(PatientIDText4.getText());
        try {
            if (patientManagementSystem.scheduleAppointment(patient, Date1,
                    Time1, Integer.parseInt(AppointmentSeverityText.getText()))) {

                if (Integer.parseInt(AppointmentSeverityText.getText()) > 20) {
                    ScheduleAppointmentLabel.setText("Appointment has been scheduled!\n Rest in Peace :(");
                } else {
                    ScheduleAppointmentLabel.setText("Appointment has been scheduled!");
                }

                PatientIDText4.clear();
                AppointmentSeverityText.clear();
                AppointmentDayText1.clear();
                AppointmentMonthText1.clear();
                AppointmentYearText1.clear();
                AppointmentHourText1.clear();
                AppointmentMinuteText1.clear();

            } else {
                ScheduleAppointmentLabel.setText("Added to Waiting List!");
                PatientIDText4.clear();
                AppointmentSeverityText.clear();
                AppointmentDayText1.clear();
                AppointmentMonthText1.clear();
                AppointmentYearText1.clear();
                AppointmentHourText1.clear();
                AppointmentMinuteText1.clear();

            }
        } catch (Exception e) {
            ScheduleAppointmentLabel.setText("Please Enter Valid Input");
            PatientIDText4.clear();
            AppointmentSeverityText.clear();
            AppointmentDayText1.clear();
            AppointmentMonthText1.clear();
            AppointmentYearText1.clear();
            AppointmentHourText1.clear();
            AppointmentMinuteText1.clear();
        }
    }


    @FXML
    TextField appointmentIDText1;
    @FXML
    TextField AppointmentYearText2, AppointmentMonthText2,
            AppointmentDayText2, AppointmentHourText2, AppointmentMinuteText2;
    @FXML
    Label RescheduleAppointmentLabel;
    public void rescheduleAppointment(ActionEvent press) {

        String Date2 = AppointmentYearText2.getText() + "-" + AppointmentMonthText2.getText() +
                "-" + AppointmentDayText2.getText();
        String Time2 = AppointmentHourText2.getText() + ":" + AppointmentMinuteText2.getText();

        try {
            if (patientManagementSystem.rescheduleAppointment(appointmentIDText1.getText(), Date2,
                    Time2)) {
                RescheduleAppointmentLabel.setText("Appointment has been Rescheduled!");
                appointmentIDText1.clear();
                AppointmentYearText2.clear();
                AppointmentMonthText2.clear();
                AppointmentDayText2.clear();
                AppointmentHourText2.clear();
                AppointmentMinuteText2.clear();
            } else {
                RescheduleAppointmentLabel.setText("Error Rescheduling Appointment!");
                appointmentIDText1.clear();
                AppointmentYearText2.clear();
                AppointmentMonthText2.clear();
                AppointmentDayText2.clear();
                AppointmentHourText2.clear();
                AppointmentMinuteText2.clear();
            }
        } catch (Exception e) {
            RescheduleAppointmentLabel.setText("Please Enter Valid input");
            appointmentIDText1.clear();
            AppointmentYearText2.clear();
            AppointmentMonthText2.clear();
            AppointmentDayText2.clear();
            AppointmentHourText2.clear();
            AppointmentMinuteText2.clear();
        }
    }

    @FXML
    TextField appointmentIDText2;
    @FXML
    Label cancelAppointmentLabel;

    public void cancelApointment(ActionEvent press) {
        if (patientManagementSystem.cancelAppointment(appointmentIDText2.getText())) {
            cancelAppointmentLabel.setText("Appointment has been Cancelled!");
            appointmentIDText2.clear();
        } else {
            cancelAppointmentLabel.setText("Error Cancelling Appointment!");
            appointmentIDText2.clear();
        }
    }



    // -------- Billing Methods, Buttons, Labels and Text Fields ----------

    @FXML
    TextField PatientIDText5, billingAmountText;
    @FXML
    Label GenerateBillLabel;
    public void generateBill(ActionEvent press) {
        try {
            GenerateBillLabel.setText(patientManagementSystem.generateBill(PatientIDText5.getText(),
                    Integer.parseInt(billingAmountText.getText())));
        } catch (Exception e) {
            GenerateBillLabel.setText("Bill Generated!");
        }
        PatientIDText5.clear();
        billingAmountText.clear();
    }


    @FXML
    TextField PatientIDText6, payementAmountText;
    @FXML
    Label paymentAmountLabel;
    public void addPayment(ActionEvent press) {
        paymentAmountLabel.setText(patientManagementSystem.
                addPayment(Integer.parseInt(payementAmountText.getText()), PatientIDText6.getText()));
        PatientIDText6.clear();
        payementAmountText.clear();
    }


    @FXML
    TextField PatientIDText7;
    @FXML
    Label paymentStatusLabel;
    public void getPaymentStatus(ActionEvent press) {
        paymentStatusLabel.setText(patientManagementSystem.
                getPaymentStatus(PatientIDText7.getText()));
        PatientIDText7.clear();
    }




    // -------- Reports Methods, Buttons, Labels and Text Fields ----------

    @FXML
    TextField PatientIDText8;
    @FXML
    Label reportPatientLabel;
    public void reportPatient(ActionEvent press) {
        reportPatientLabel.setText(patientManagementSystem.generatePatientReport(PatientIDText8.getText()));
        PatientIDText8.clear();
    }


    @FXML
    Label reportAppointmentLabel;
    public void reportAppointment() {
        reportAppointmentLabel.setText(patientManagementSystem.generateAppointmentReport());
        PatientIDText8.clear();
    }


    @FXML
    Label reportRevenueLabel;
    public void reportRevenue() {
        reportRevenueLabel.setText(patientManagementSystem.generateRevenueReport());
    }

    @FXML
    Label waitingListLabel;
    public void waitingList() {
        waitingListLabel.setText(patientManagementSystem.displayWaitingList());
    }








    public void mainSwitchButtons(ActionEvent press) {
        if (press.getSource() == PatientsBTN ) {
            patientSubPane1.setVisible(true);
            patientSubPane2.setVisible(false);
            patientSubPane3.setVisible(false);
            patientSubPane4.setVisible(false);

            fade3.setNode(OpeningPane);
            fade3.setDuration(Duration.millis(50));
            fade3.setInterpolator(Interpolator.LINEAR);
            fade3.setFromValue(1);
            fade3.setToValue(0);
            fade3.play();
            OpeningPane.setVisible(false);

            sidePane1.setVisible(true);
            sidePane2.setVisible(false);
            sidePane3.setVisible(false);
            sidePane4.setVisible(false);
            patientMainPane.setVisible(true);
            appointmentMainPane.setVisible(false);
            billingMainPane.setVisible(false);
            reportsMainPane.setVisible(false);


            fade1.setNode(patientMainPane);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

            fade2.setNode(sidePane1);
            fade2.setDuration(Duration.millis(500));
            fade2.setInterpolator(Interpolator.LINEAR);
            fade2.setFromValue(0);
            fade2.setToValue(1);
            fade2.play();

        }
        if (press.getSource() == AppointmentsBTN) {
            appointmentSubPane1.setVisible(true);
            appointmentSubPane2.setVisible(false);
            appointmentSubPane3.setVisible(false);

            fade3.setNode(OpeningPane);
            fade3.setDuration(Duration.millis(50));
            fade3.setInterpolator(Interpolator.LINEAR);
            fade3.setFromValue(1);
            fade3.setToValue(0);
            fade3.play();
            OpeningPane.setVisible(false);


            sidePane1.setVisible(false);
            sidePane2.setVisible(true);
            sidePane3.setVisible(false);
            sidePane4.setVisible(false);
            patientMainPane.setVisible(false);
            appointmentMainPane.setVisible(true);
            billingMainPane.setVisible(false);
            reportsMainPane.setVisible(false);

            fade1.setNode(appointmentMainPane);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

            fade2.setNode(sidePane2);
            fade2.setDuration(Duration.millis(500));
            fade2.setInterpolator(Interpolator.LINEAR);
            fade2.setFromValue(0);
            fade2.setToValue(1);
            fade2.play();

        }
        if (press.getSource() == BillingBTN) {
            billingSubPane1.setVisible(true);
            billingSubPane2.setVisible(false);
            billingSubPane3.setVisible(false);

            fade3.setNode(OpeningPane);
            fade3.setDuration(Duration.millis(50));
            fade3.setInterpolator(Interpolator.LINEAR);
            fade3.setFromValue(1);
            fade3.setToValue(0);
            fade3.play();

            OpeningPane.setVisible(false);


            sidePane1.setVisible(false);
            sidePane2.setVisible(false);
            sidePane3.setVisible(true);
            sidePane4.setVisible(false);
            patientMainPane.setVisible(false);
            appointmentMainPane.setVisible(false);
            billingMainPane.setVisible(true);
            reportsMainPane.setVisible(false);


            fade1.setNode(billingMainPane);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

            fade2.setNode(sidePane3);
            fade2.setDuration(Duration.millis(500));
            fade2.setInterpolator(Interpolator.LINEAR);
            fade2.setFromValue(0);
            fade2.setToValue(1);
            fade2.play();


        }
        if (press.getSource() == ReportsBTN) {
            reportsSubPane1.setVisible(true);
            reportsSubPane2.setVisible(false);
            reportsSubPane3.setVisible(false);
            reportsSubPane4.setVisible(false);

            fade3.setNode(OpeningPane);
            fade3.setDuration(Duration.millis(50));
            fade3.setInterpolator(Interpolator.LINEAR);
            fade3.setFromValue(1);
            fade3.setToValue(0);
            fade3.play();

            OpeningPane.setVisible(false);


            sidePane1.setVisible(false);
            sidePane2.setVisible(false);
            sidePane3.setVisible(false);
            sidePane4.setVisible(true);
            patientMainPane.setVisible(false);
            appointmentMainPane.setVisible(false);
            billingMainPane.setVisible(false);
            reportsMainPane.setVisible(true);


            fade1.setNode(reportsMainPane);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

            fade2.setNode(sidePane4);
            fade2.setDuration(Duration.millis(500));
            fade2.setInterpolator(Interpolator.LINEAR);
            fade2.setFromValue(0);
            fade2.setToValue(1);
            fade2.play();


        }
    }

    public void patientSwitchButtons(ActionEvent press) {
        if (press.getSource() == addPatientBTN ) {
            patientSubPane1.setVisible(true);
            patientSubPane2.setVisible(false);
            patientSubPane3.setVisible(false);
            patientSubPane4.setVisible(false);

            fade1.setNode(patientSubPane1);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == findPatientBTN) {
            patientSubPane1.setVisible(false);
            patientSubPane2.setVisible(true);
            patientSubPane3.setVisible(false);
            patientSubPane4.setVisible(false);
            fade1.setNode(patientSubPane2);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();

        }
        if (press.getSource() == updatePatientBTN) {
            patientSubPane1.setVisible(false);
            patientSubPane2.setVisible(false);
            patientSubPane3.setVisible(true);
            patientSubPane4.setVisible(false);

            fade1.setNode(patientSubPane3);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == addVisitBTN) {
            patientSubPane1.setVisible(false);
            patientSubPane2.setVisible(false);
            patientSubPane3.setVisible(false);
            patientSubPane4.setVisible(true);

            fade1.setNode(patientSubPane4);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
    }



    @FXML
    Button ScheduleBTN, RescheduleBTN, cancelAppointmentBTN;
    @FXML
    AnchorPane appointmentSubPane1, appointmentSubPane2, appointmentSubPane3;
    public void ScheduleSwitchScene(ActionEvent press) {
        if (press.getSource() == ScheduleBTN) {
            appointmentSubPane1.setVisible(true);
            appointmentSubPane2.setVisible(false);
            appointmentSubPane3.setVisible(false);

            fade1.setNode(appointmentSubPane1);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == RescheduleBTN) {
            appointmentSubPane1.setVisible(false);
            appointmentSubPane2.setVisible(true);
            appointmentSubPane3.setVisible(false);

            fade1.setNode(appointmentSubPane2);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == cancelAppointmentBTN) {
            appointmentSubPane1.setVisible(false);
            appointmentSubPane2.setVisible(false);
            appointmentSubPane3.setVisible(true);

            fade1.setNode(appointmentSubPane3);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
    }



    @FXML
    Button generateBillBTN, addPaymentBTN, paymentStatusBTN;
    @FXML
    AnchorPane billingSubPane1, billingSubPane2, billingSubPane3;
    public void BillingSwitchScene(ActionEvent press) {
        if (press.getSource() == generateBillBTN) {
            billingSubPane1.setVisible(true);
            billingSubPane2.setVisible(false);
            billingSubPane3.setVisible(false);

            fade1.setNode(billingSubPane1);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == addPaymentBTN) {
            billingSubPane1.setVisible(false);
            billingSubPane2.setVisible(true);
            billingSubPane3.setVisible(false);

            fade1.setNode(billingSubPane2);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == paymentStatusBTN) {
            billingSubPane1.setVisible(false);
            billingSubPane2.setVisible(false);
            billingSubPane3.setVisible(true);

            fade1.setNode(billingSubPane3);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
    }



    @FXML
    Button patientReportBTN, appointmentReportBTN, waitingListBTN, revenueReportBTN;
    @FXML
    AnchorPane reportsSubPane1, reportsSubPane2, reportsSubPane3, reportsSubPane4;
    public void reportSwitchScene(ActionEvent press) {
        if (press.getSource() == patientReportBTN) {
            reportsSubPane1.setVisible(true);
            reportsSubPane2.setVisible(false);
            reportsSubPane3.setVisible(false);
            reportsSubPane4.setVisible(false);

            fade1.setNode(reportsSubPane1);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == appointmentReportBTN) {
            this.reportAppointment();
            reportsSubPane1.setVisible(false);
            reportsSubPane2.setVisible(true);
            reportsSubPane3.setVisible(false);
            reportsSubPane4.setVisible(false);

            fade1.setNode(reportsSubPane2);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == revenueReportBTN) {
            this.reportRevenue();
            reportsSubPane1.setVisible(false);
            reportsSubPane2.setVisible(false);
            reportsSubPane3.setVisible(true);
            reportsSubPane4.setVisible(false);

            fade1.setNode(reportsSubPane3);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
        if (press.getSource() == waitingListBTN) {
            this.waitingList();
            reportsSubPane1.setVisible(false);
            reportsSubPane2.setVisible(false);
            reportsSubPane3.setVisible(false);
            reportsSubPane4.setVisible(true);

            fade1.setNode(reportsSubPane4);
            fade1.setDuration(Duration.millis(500));
            fade1.setInterpolator(Interpolator.LINEAR);
            fade1.setFromValue(0);
            fade1.setToValue(1);
            fade1.play();
        }
    }



}

