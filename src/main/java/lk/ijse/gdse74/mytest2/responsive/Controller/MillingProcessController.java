//package lk.ijse.gdse74.mytest2.responsive.Controller;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.input.MouseEvent;
//import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
//import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
//import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto; // Using the existing DTO name
//
//import java.net.URL;
//import java.sql.SQLException;
//import java.sql.Time;
//import java.time.LocalTime;
//import java.util.List; // Use List instead of ArrayList
//import java.util.Optional;
//import java.util.ResourceBundle;
//
//public class MillingProcessController implements Initializable {
//
//    @FXML private Button btnClear;
//    @FXML private Button btnDelete;
//    @FXML private Button btnSave;
//    @FXML private Button btnUpdate;
//    @FXML private Button btnOverride;
//    @FXML private TableColumn<MillingProcessdto, Double> colBran_rice; // Renamed to reflect DTO
//    @FXML private TableColumn<MillingProcessdto, Double> colBroken_rice;
//    @FXML private TableColumn<MillingProcessdto, Time> colEnd_time;
//    @FXML private TableColumn<MillingProcessdto, Double> colHusk; // Renamed to reflect DTO
//    @FXML private TableColumn<MillingProcessdto, String> colMilling_id;
//    @FXML private TableColumn<MillingProcessdto, String> colPaddy_id;
//    @FXML private TableColumn<MillingProcessdto, Time> colStart_time;
//    @FXML private TableColumn<MillingProcessdto, Double> colmilled_Quantity;
//    @FXML private TableView<MillingProcessdto> table;
//    @FXML private TextField txtBran;
//    @FXML private TextField txtBrokenRice;
//    @FXML private TextField txtHusk;
//    @FXML private TextField txtMilledQuantity;
//    @FXML private TextField txtMilling_id;
//    @FXML private ComboBox<String> cmbPaddyId;
//    @FXML private Spinner<Integer> endHourSpinner;
//    @FXML private Spinner<Integer> endMinuteSpinner;
//    @FXML private Spinner<Integer> endSecondSpinner;
//    @FXML private Label lblDuration;
//    @FXML private Label lblStartTime;
//
//    private static final double BROKEN_RICE_RATIO = 0.05;
//    private static final double HUSK_RATIO = 0.20;
//    private static final double BRAN_RATIO = 0.10;
//    private boolean overrideEnabled = false;
//    private Time currentStartTime;
//    private ObservableList<String> paddyIdList = FXCollections.observableArrayList();
//
//    // Use BOFactory to get BO instance
//    private final MillingProcessBO millingProcessBO = BOFactory.getInstance().getBO(BOTypes.MILLING_PROCESS);
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        setCurrentTimeAsStartTime();
//        initTimeSpinners();
//        setupAutomaticCalculations();
//        disableActionButtons(true); // Disable Update, Delete, Save initially
//        loadPaddyIds();
//
//        try {
//            loadNextId();
//            loadTable();
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Database Error during initialization: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void loadPaddyIds() {
//        try {
//            List<String> ids = millingProcessBO.getAllPaddyIdsForMilling(); // Use BO
//            paddyIdList.setAll(ids); // Use setAll to clear and add
//            cmbPaddyId.setItems(paddyIdList);
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Failed to load paddy IDs: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void setCurrentTimeAsStartTime() {
//        LocalTime now = LocalTime.now();
//        currentStartTime = Time.valueOf(now);
//        lblStartTime.setText(String.format("%02d:%02d:%02d",
//                now.getHour(), now.getMinute(), now.getSecond()));
//    }
//
//    private void initTimeSpinners() {
//        LocalTime now = LocalTime.now();
//        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
//        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));
//        endSecondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getSecond()));
//
//        endHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
//        endMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
//        endSecondSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
//    }
//
//    private void setupAutomaticCalculations() {
//        txtMilledQuantity.textProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal.isEmpty() && !overrideEnabled) {
//                try {
//                    calculateByproducts(Double.parseDouble(newVal));
//                } catch (NumberFormatException e) {
//                    clearByproductFields();
//                }
//            } else if (newVal.isEmpty()) {
//                clearByproductFields();
//            }
//            updateSaveAndUpdateButtonState(); // Update button state based on fields
//        });
//
//        // Add listeners to other fields to enable/disable save/update
//        cmbPaddyId.valueProperty().addListener((obs, oldVal, newVal) -> updateSaveAndUpdateButtonState());
//        txtBrokenRice.textProperty().addListener((obs, oldVal, newVal) -> updateSaveAndUpdateButtonState());
//        txtHusk.textProperty().addListener((obs, oldVal, newVal) -> updateSaveAndUpdateButtonState());
//        txtBran.textProperty().addListener((obs, oldVal, newVal) -> updateSaveAndUpdateButtonState());
//
//        txtBrokenRice.setEditable(false);
//        txtHusk.setEditable(false);
//        txtBran.setEditable(false);
//    }
//
//    private void calculateByproducts(double milledQuantity) {
//        double brokenRice = milledQuantity * BROKEN_RICE_RATIO;
//        double husk = milledQuantity * HUSK_RATIO;
//        double bran = milledQuantity * BRAN_RATIO;
//
//        txtBrokenRice.setText(String.format("%.2f", brokenRice));
//        txtHusk.setText(String.format("%.2f", husk));
//        txtBran.setText(String.format("%.2f", bran));
//    }
//
//    private void calculateDuration() {
//        try {
//            Time endTime = getEndTimeFromSpinners();
//
//            if (currentStartTime != null && endTime != null) {
//                if (endTime.before(currentStartTime)) {
//                    showInvalidDuration("Invalid: End before Start");
//                } else {
//                    showValidDuration(currentStartTime, endTime);
//                }
//            }
//        } catch (Exception e) {
//            showInvalidDuration("Invalid Time");
//        }
//        updateSaveAndUpdateButtonState(); // Update button state after duration calculation
//    }
//
//    private Time getEndTimeFromSpinners() {
//        try {
//            int hour = endHourSpinner.getValue();
//            int minute = endMinuteSpinner.getValue();
//            int second = endSecondSpinner.getValue();
//            return Time.valueOf(String.format("%02d:%02d:%02d", hour, minute, second));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @FXML
//    void btnSaveOnAction(ActionEvent event) {
//        if (!validateInputs()) return;
//
//        try {
//            MillingProcessdto dto = createMillingProcessDto();
//            millingProcessBO.saveMillingProcess(dto); // Use BO
//
//            showAlert(Alert.AlertType.INFORMATION, "Milling Process Saved Successfully!");
//            clearFields();
//        } catch (DuplicateException e) { // Catch specific BO exception
//            showAlert(Alert.AlertType.ERROR, e.getMessage());
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Error saving milling process: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    void btnUpdateOnAction(ActionEvent event) {
//        if (!validateInputs()) return;
//
//        try {
//            MillingProcessdto dto = createMillingProcessDto();
//            millingProcessBO.updateMillingProcess(dto); // Use BO
//
//            showAlert(Alert.AlertType.INFORMATION, "Milling Process Updated Successfully!");
//            clearFields();
//        } catch (NotFoundException e) { // Catch specific BO exception
//            showAlert(Alert.AlertType.ERROR, e.getMessage());
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Error updating milling process: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    void btnDeleteOnAction(ActionEvent event) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation");
//        alert.setHeaderText("Delete Milling Process");
//        alert.setContentText("Are you sure you want to delete this process?");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                boolean isDeleted = millingProcessBO.deleteMillingProcess(txtMilling_id.getText()); // Use BO
//                if (isDeleted) {
//                    showAlert(Alert.AlertType.INFORMATION, "Milling Process Deleted Successfully!");
//                    clearFields();
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Milling Process Deletion Failed!");
//                }
//            } catch (NotFoundException | InUseException e) { // Catch specific BO exceptions
//                showAlert(Alert.AlertType.ERROR, e.getMessage());
//            } catch (Exception e) {
//                showAlert(Alert.AlertType.ERROR, "Error deleting milling process: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @FXML
//    void btnClearOnAction(ActionEvent event) {
//        try {
//            clearFields();
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Error clearing fields: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    void toggleOverride(ActionEvent event) {
//        overrideEnabled = !overrideEnabled;
//        txtBrokenRice.setEditable(overrideEnabled);
//        txtHusk.setEditable(overrideEnabled);
//        txtBran.setEditable(overrideEnabled);
//
//        if (overrideEnabled) {
//            btnOverride.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // Red for "Lock"
//            btnOverride.setText("Lock");
//        } else {
//            btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;"); // Purple for "Override"
//            btnOverride.setText("Override");
//            recalculateByproducts();
//        }
//        updateSaveAndUpdateButtonState(); // Update button state after override toggle
//    }
//
//    public void tableColumnOnClicked(MouseEvent mouseEvent) {
//        MillingProcessdto process = table.getSelectionModel().getSelectedItem();
//        if (process != null) {
//            txtMilling_id.setText(process.getMillingId());
//            cmbPaddyId.setValue(process.getPaddyId());
//            txtMilledQuantity.setText(String.valueOf(process.getMilledQuantity()));
//            txtBrokenRice.setText(String.valueOf(process.getBrokenRice()));
//            txtHusk.setText(String.valueOf(process.getHusk()));
//            txtBran.setText(String.valueOf(process.getBran()));
//
//            currentStartTime = process.getStartTime();
//            lblStartTime.setText(String.format("%02d:%02d:%02d",
//                    currentStartTime.getHours(),
//                    currentStartTime.getMinutes(),
//                    currentStartTime.getSeconds()));
//
//            endHourSpinner.getValueFactory().setValue(process.getEndTime().getHours());
//            endMinuteSpinner.getValueFactory().setValue(process.getEndTime().getMinutes());
//            endSecondSpinner.getValueFactory().setValue(process.getEndTime().getSeconds());
//
//            disableActionButtons(false); // Enable Update, Delete
//            btnSave.setDisable(true); // Disable Save
//            overrideEnabled = true; // Set override to true when loading data
//            toggleOverride(null); // Apply override styling and text
//
//            calculateDuration(); // Recalculate duration for selected item
//        }
//    }
//
//    private MillingProcessdto createMillingProcessDto() {
//        return new MillingProcessdto(
//                txtMilling_id.getText(),
//                cmbPaddyId.getValue(),
//                currentStartTime,
//                getEndTimeFromSpinners(),
//                Double.parseDouble(txtMilledQuantity.getText()),
//                Double.parseDouble(txtBrokenRice.getText()),
//                Double.parseDouble(txtHusk.getText()),
//                Double.parseDouble(txtBran.getText())
//        );
//    }
//
//    private boolean validateInputs() {
//        if (cmbPaddyId.getValue() == null || cmbPaddyId.getValue().isEmpty()) {
//            showAlert(Alert.AlertType.ERROR, "Please select a Paddy ID.");
//            return false;
//        }
//        if (txtMilledQuantity.getText().isEmpty()) {
//            showAlert(Alert.AlertType.ERROR, "Milled quantity cannot be empty.");
//            return false;
//        }
//        if (txtBrokenRice.getText().isEmpty() || txtHusk.getText().isEmpty() || txtBran.getText().isEmpty()) {
//            showAlert(Alert.AlertType.ERROR, "Byproduct fields cannot be empty. Enter values or uncheck override.");
//            return false;
//        }
//
//        try {
//            Time endTime = getEndTimeFromSpinners();
//            if (endTime == null || (currentStartTime != null && endTime.before(currentStartTime))) {
//                showAlert(Alert.AlertType.ERROR, "Invalid end time. It must be after start time.");
//                return false;
//            }
//
//            double milledQty = Double.parseDouble(txtMilledQuantity.getText());
//            if (milledQty <= 0) {
//                showAlert(Alert.AlertType.ERROR, "Milled quantity must be positive.");
//                return false;
//            }
//
//            // Validate byproduct fields if override is enabled
//            if (overrideEnabled) {
//                Double.parseDouble(txtBrokenRice.getText());
//                Double.parseDouble(txtHusk.getText());
//                Double.parseDouble(txtBran.getText());
//            }
//
//            return true;
//        } catch (NumberFormatException e) {
//            showAlert(Alert.AlertType.ERROR, "Invalid numeric values entered.");
//            return false;
//        }
//    }
//
//    private void clearFields() throws SQLException {
//        cmbPaddyId.setValue(null);
//        txtMilledQuantity.clear();
//        clearByproductFields();
//        setCurrentTimeAsStartTime();
//
//        LocalTime now = LocalTime.now();
//        endHourSpinner.getValueFactory().setValue(now.getHour());
//        endMinuteSpinner.getValueFactory().setValue(now.getMinute());
//        endSecondSpinner.getValueFactory().setValue(now.getSecond());
//
//        lblDuration.setText("Duration: 00:00:00");
//        lblDuration.setStyle("-fx-text-fill: black;");
//
//        overrideEnabled = false;
//        btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
//        btnOverride.setText("Override");
//        txtBrokenRice.setEditable(false); // Ensure fields are not editable after clear
//        txtHusk.setEditable(false);
//        txtBran.setEditable(false);
//
//        loadNextId();
//        loadTable();
//        disableActionButtons(true); // Disable Update, Delete after clear
//        btnSave.setDisable(false); // Enable Save after clear
//    }
//
//    private void clearByproductFields() {
//        txtBrokenRice.clear();
//        txtHusk.clear();
//        txtBran.clear();
//    }
//
//    private void loadNextId() throws SQLException {
//        txtMilling_id.setText(millingProcessBO.getNextMillingProcessId()); // Use BO
//        txtMilling_id.setEditable(false); // Keep ID field non-editable
//    }
//
//    private void loadTable() {
//        colMilling_id.setCellValueFactory(new PropertyValueFactory<>("millingId"));
//        colPaddy_id.setCellValueFactory(new PropertyValueFactory<>("paddyId"));
//        colStart_time.setCellValueFactory(new PropertyValueFactory<>("startTime"));
//        colEnd_time.setCellValueFactory(new PropertyValueFactory<>("endTime"));
//        colmilled_Quantity.setCellValueFactory(new PropertyValueFactory<>("milledQuantity"));
//        colBroken_rice.setCellValueFactory(new PropertyValueFactory<>("brokenRice"));
//        colHusk.setCellValueFactory(new PropertyValueFactory<>("husk")); // DTO uses 'husk'
//        colBran_rice.setCellValueFactory(new PropertyValueFactory<>("bran")); // DTO uses 'bran'
//
//        try {
//            List<MillingProcessdto> processes = millingProcessBO.getAllMillingProcesses(); // Use BO
//            table.setItems(FXCollections.observableArrayList(processes));
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Error loading data into table: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void disableActionButtons(boolean disable) {
//        btnUpdate.setDisable(disable);
//        btnDelete.setDisable(disable);
//        // btnSave state is managed separately by updateSaveAndUpdateButtonState and clearFields
//    }
//
//    private void updateSaveAndUpdateButtonState() {
//        boolean allFieldsFilled = !txtMilledQuantity.getText().isEmpty() &&
//                cmbPaddyId.getValue() != null && !cmbPaddyId.getValue().isEmpty() &&
//                !txtBrokenRice.getText().isEmpty() &&
//                !txtHusk.getText().isEmpty() &&
//                !txtBran.getText().isEmpty();
//
//        boolean isDurationValid = lblDuration.getStyle().contains("green");
//
//        if (table.getSelectionModel().getSelectedItem() == null) { // Not editing existing record
//            btnSave.setDisable(!(allFieldsFilled && isDurationValid));
//            btnUpdate.setDisable(true);
//            btnDelete.setDisable(true);
//        } else { // Editing existing record
//            btnSave.setDisable(true); // Always disable save when an item is selected
//            btnUpdate.setDisable(!(allFieldsFilled && isDurationValid));
//            btnDelete.setDisable(false);
//        }
//    }
//
//    private void showInvalidDuration(String message) {
//        lblDuration.setText(message);
//        lblDuration.setStyle("-fx-text-fill: red;");
//        btnSave.setDisable(true); // Cannot save if duration is invalid
//        btnUpdate.setDisable(true); // Cannot update if duration is invalid
//    }
//
//    private void showValidDuration(Time startTime, Time endTime) {
//        long diff = endTime.getTime() - startTime.getTime();
//        long diffHours = diff / (60 * 60 * 1000);
//        long diffMinutes = (diff / (60 * 1000)) % 60;
//        long diffSeconds = (diff / 1000) % 60;
//
//        lblDuration.setText(String.format("Duration: %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
//        lblDuration.setStyle("-fx-text-fill: green;");
//        updateSaveAndUpdateButtonState(); // Enable save/update if duration becomes valid
//    }
//
//    private void recalculateByproducts() {
//        if (!txtMilledQuantity.getText().isEmpty()) {
//            try {
//                calculateByproducts(Double.parseDouble(txtMilledQuantity.getText()));
//            } catch (NumberFormatException e) {
//                clearByproductFields();
//            }
//        } else {
//            clearByproductFields(); // Clear if milled quantity is empty
//        }
//    }
//
//    private void showAlert(Alert.AlertType type, String message) {
//        new Alert(type, message).show();
//    }
//}