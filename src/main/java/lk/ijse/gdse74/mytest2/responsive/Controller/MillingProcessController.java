//package lk.ijse.gdse74.mytest2.responsive.Controller;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.collections.transformation.FilteredList;
//import javafx.collections.transformation.SortedList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
//import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO;
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.RawPaddyBO; // To get available Raw Paddy IDs
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
//import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto;
//
//import java.math.BigDecimal; // Import BigDecimal for precise calculations
//import java.net.URL;
//import java.sql.SQLException;
//import java.time.Duration;
//import java.time.LocalTime; // Use LocalTime for handling time in UI
//import java.util.List;
//import java.util.Optional;
//import java.util.ResourceBundle;
//
//public class MillingProcessController implements Initializable {
//
//    @FXML private Button btnClear;
//    @FXML private Button btnDelete;
//    @FXML private Button btnSave;
//    @FXML private Button btnUpdate;
//    @FXML private Button btnOverride; // Button to toggle manual byproduct entry
//    @FXML private TableColumn<MillingProcessdto, BigDecimal> colBran_kg; // Using DTO property 'bran'
//    @FXML private TableColumn<MillingProcessdto, BigDecimal> colBroken_rice;
//    @FXML private TableColumn<MillingProcessdto, LocalTime> colEnd_time;
//    @FXML private TableColumn<MillingProcessdto, BigDecimal> colHusk_kg; // Using DTO property 'husk'
//    @FXML private TableColumn<MillingProcessdto, String> colMilling_id;
//    @FXML private TableColumn<MillingProcessdto, String> colPaddy_id;
//    @FXML private TableColumn<MillingProcessdto, LocalTime> colStart_time;
//    @FXML private TableColumn<MillingProcessdto, BigDecimal> colMilled_Quantity;
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
//    @FXML private TextField txtSearch;
//    @FXML private Label lblMillingProcessCount;
//
//    // Constants for byproduct ratios using BigDecimal for precision
//    private static final BigDecimal BROKEN_RICE_RATIO = new BigDecimal("0.05");
//    private static final BigDecimal HUSK_RATIO = new BigDecimal("0.20");
//    private static final BigDecimal BRAN_RATIO = new BigDecimal("0.10");
//
//    private boolean overrideEnabled = false; // State for manual byproduct entry
//    private LocalTime currentStartTime; // Stores the start time (automatically set or loaded)
//    private ObservableList<MillingProcessdto> millingProcessMasterData = FXCollections.observableArrayList();
//
//    // BO instances using BOFactory
//    //private final MillingProcessBO millingProcessBO = BOFactory.getInstance().getBO(BOTypes.MILLING_PROCESS);
//    private final RawPaddyBO rawPaddyBO = BOFactory.getInstance().getBO(BOTypes.RAW_PADDY); // To get available Raw Paddy IDs
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        setCellValueFactories();
//        setCurrentTimeAsStartTime();
//        initTimeSpinners();
//        setupAutomaticCalculations();
//        setupFieldListeners(); // Centralized listener setup for validation and button states
//        setupSearchFilter(); // Configure table search functionality
//
//        try {
//            loadNextId();
//            loadTable(); // Load all data initially
//            loadPaddyIds(); // Load available paddy IDs for the ComboBox
//            updateButtonStatesAndStyles(); // Set initial button states and validation styles
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
//            // It's good practice to rethrow or log a more critical error if initialization fails
//            throw new RuntimeException("Failed to initialize MillingProcessController", e);
//        }
//    }
//
//    // Assigns DTO properties to TableColumn
//    private void setCellValueFactories() {
//        colMilling_id.setCellValueFactory(new PropertyValueFactory<>("millingId"));
//        colPaddy_id.setCellValueFactory(new PropertyValueFactory<>("paddyId"));
//        colStart_time.setCellValueFactory(new PropertyValueFactory<>("startTime"));
//        colEnd_time.setCellValueFactory(new PropertyValueFactory<>("endTime"));
//        colMilled_Quantity.setCellValueFactory(new PropertyValueFactory<>("milledQuantity"));
//        colBroken_rice.setCellValueFactory(new PropertyValueFactory<>("brokenRice"));
//        colHusk_kg.setCellValueFactory(new PropertyValueFactory<>("husk")); // DTO field is 'husk'
//        colBran_kg.setCellValueFactory(new PropertyValueFactory<>("bran")); // DTO field is 'bran'
//    }
//
//    // Loads available paddy IDs into the ComboBox
//    private void loadPaddyIds() {
//        try {
//            // Fetch IDs from RawPaddyBO, assuming it retrieves IDs from the raw_paddy table
//            ObservableList<String> paddyIds = FXCollections.observableArrayList(rawPaddyBO.getAllRawPaddyIds());
//            cmbPaddyId.setItems(paddyIds);
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Failed to load paddy IDs: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // Sets the current system time as the start time for a new milling process
//    private void setCurrentTimeAsStartTime() {
//        currentStartTime = LocalTime.now();
//        lblStartTime.setText(String.format("%02d:%02d:%02d",
//                currentStartTime.getHour(), currentStartTime.getMinute(), currentStartTime.getSecond()));
//    }
//
//    // Initializes the end time spinners with current time and adds listeners for duration calculation
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
//    // Sets up listener for milled quantity to automatically calculate byproducts
//    private void setupAutomaticCalculations() {
//        txtMilledQuantity.textProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal.isEmpty() && newVal.matches("\\d*\\.?\\d*")) { // Basic numeric check
//                if (!overrideEnabled) { // Only auto-calculate if override is NOT enabled
//                    try {
//                        calculateByproducts(new BigDecimal(newVal));
//                    } catch (NumberFormatException e) {
//                        clearByproductFields(); // Clear if input is invalid
//                    }
//                }
//            } else if (newVal.isEmpty()) {
//                clearByproductFields(); // Clear byproducts if milled quantity is empty
//            }
//            updateButtonStatesAndStyles(); // Update button state after calculation/clearing
//        });
//
//        // Initially set byproduct fields as non-editable
//        txtBrokenRice.setEditable(false);
//        txtHusk.setEditable(false);
//        txtBran.setEditable(false);
//    }
//
//    // Calculates broken rice, husk, and bran based on milled quantity and predefined ratios
//    private void calculateByproducts(BigDecimal milledQuantity) {
//        BigDecimal brokenRice = milledQuantity.multiply(BROKEN_RICE_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);
//        BigDecimal husk = milledQuantity.multiply(HUSK_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);
//        BigDecimal bran = milledQuantity.multiply(BRAN_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);
//
//        txtBrokenRice.setText(brokenRice.toPlainString());
//        txtHusk.setText(husk.toPlainString());
//        txtBran.setText(bran.toPlainString());
//    }
//
//    // Calculates and displays the duration between start and end times
//    private void calculateDuration() {
//        try {
//            LocalTime endTime = getEndTimeFromSpinners();
//
//            if (currentStartTime != null && endTime != null) {
//                if (endTime.isBefore(currentStartTime)) {
//                    showInvalidDuration("Invalid: End before Start");
//                } else {
//                    Duration duration = Duration.between(currentStartTime, endTime);
//                    long totalSeconds = duration.getSeconds();
//                    long hours = totalSeconds / 3600;
//                    long minutes = (totalSeconds % 3600) / 60;
//                    long seconds = totalSeconds % 60;
//                    showValidDuration(String.format("Duration: %02d:%02d:%02d", hours, minutes, seconds));
//                }
//            } else {
//                showInvalidDuration("Invalid Time Input");
//            }
//        } catch (Exception e) {
//            showInvalidDuration("Error Calculating Duration");
//            e.printStackTrace();
//        }
//        updateButtonStatesAndStyles(); // Update button state after duration calculation
//    }
//
//    // Retrieves end time from spinners as a LocalTime object
//    private LocalTime getEndTimeFromSpinners() {
//        try {
//            int hour = endHourSpinner.getValue();
//            int minute = endMinuteSpinner.getValue();
//            int second = endSecondSpinner.getValue();
//            return LocalTime.of(hour, minute, second);
//        } catch (Exception e) {
//            return null; // Return null if spinner values are invalid
//        }
//    }
//
//    // Centralized listener setup for all input fields and table selection
//    private void setupFieldListeners() {
//        cmbPaddyId.valueProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
//        txtMilledQuantity.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
//        txtBrokenRice.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
//        txtHusk.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
//        txtBran.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
//        // Listener for table selection to update button states when an item is selected
//        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            updateButtonStatesAndStyles();
//        });
//    }
//
//    // Updates the enabled/disabled state of Save, Update, and Delete buttons
//    // and applies real-time validation styling
//    private void updateButtonStatesAndStyles() {
//        boolean isValidInput = validateInputFields(false); // Validate without showing dialogs
//        MillingProcessdto selectedItem = table.getSelectionModel().getSelectedItem();
//
//        if (selectedItem == null) { // No item selected (new record mode)
//            btnSave.setDisable(!isValidInput); // Enable Save only if inputs are valid
//            btnUpdate.setDisable(true);
//            btnDelete.setDisable(true);
//        } else { // Item selected (edit/delete mode)
//            btnSave.setDisable(true); // Always disable save
//            btnUpdate.setDisable(!isValidInput); // Enable Update only if inputs are valid
//            btnDelete.setDisable(false); // Always enable Delete for a selected item
//        }
//        applyValidationStyles(); // Apply real-time visual feedback
//    }
//
//    // Validates all input fields based on their content and format
//    // `showDialog` parameter controls whether Alert dialogs are shown for invalid inputs
//    private boolean validateInputFields(boolean showDialog) {
//        String paddyId = cmbPaddyId.getValue();
//        String milledQtyStr = txtMilledQuantity.getText().trim();
//        String brokenRiceStr = txtBrokenRice.getText().trim();
//        String huskStr = txtHusk.getText().trim();
//        String branStr = txtBran.getText().trim();
//        LocalTime endTime = getEndTimeFromSpinners();
//
//        // 1. Check for empty required fields
//        if (paddyId == null || paddyId.isEmpty()) {
//            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Paddy ID.");
//            return false;
//        }
//        if (milledQtyStr.isEmpty()) {
//            if (showDialog) showAlert(Alert.AlertType.ERROR, "Milled quantity cannot be empty.");
//            return false;
//        }
//        if (brokenRiceStr.isEmpty() || huskStr.isEmpty() || branStr.isEmpty()) {
//            if (showDialog) showAlert(Alert.AlertType.ERROR, "Byproduct fields cannot be empty. Enter values or uncheck override.");
//            return false;
//        }
//
//
//        // 2. Validate numeric formats and values
//        try {
//            BigDecimal milledQty = new BigDecimal(milledQtyStr);
//            BigDecimal brokenRice = new BigDecimal(brokenRiceStr);
//            BigDecimal husk = new BigDecimal(huskStr);
//            BigDecimal bran = new BigDecimal(branStr);
//
//            if (milledQty.compareTo(BigDecimal.ZERO) <= 0) {
//                if (showDialog) showAlert(Alert.AlertType.ERROR, "Milled quantity must be a positive value.");
//                return false;
//            }
//            if (brokenRice.compareTo(BigDecimal.ZERO) < 0 ||
//                    husk.compareTo(BigDecimal.ZERO) < 0 ||
//                    bran.compareTo(BigDecimal.ZERO) < 0) {
//                if (showDialog) showAlert(Alert.AlertType.ERROR, "Byproduct quantities cannot be negative.");
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            if (showDialog) showAlert(Alert.AlertType.ERROR, "Numeric fields must contain valid numbers.");
//            return false;
//        }
//
//        // 3. Validate time
//        if (endTime == null || (currentStartTime != null && endTime.isBefore(currentStartTime))) {
//            if (showDialog) showAlert(Alert.AlertType.ERROR, "End time must be after start time.");
//            return false;
//        }
//
//        return true;
//    }
//
//    // Applies red/blue border styles to text fields and combo box for visual validation feedback
//    private void applyValidationStyles() {
//        String validStyle = "-fx-border-color: #2980b9; -fx-border-radius: 3;"; // Blue border for valid
//        String invalidStyle = "-fx-border-color: #e74c3c; -fx-border-radius: 3;"; // Red border for invalid
//        String defaultStyle = "-fx-background-radius: 3; -fx-border-color: #bdc3c7; -fx-border-radius: 3;"; // Default grey
//
//        // Validate numeric fields (allowing empty for dynamic clear)
//        txtMilledQuantity.setStyle(txtMilledQuantity.getText().trim().matches("\\d*\\.?\\d*") && !txtMilledQuantity.getText().isEmpty() ? validStyle : invalidStyle);
//        txtBrokenRice.setStyle(txtBrokenRice.getText().trim().matches("\\d*\\.?\\d*") && !txtBrokenRice.getText().isEmpty() ? validStyle : invalidStyle);
//        txtHusk.setStyle(txtHusk.getText().trim().matches("\\d*\\.?\\d*") && !txtHusk.getText().isEmpty() ? validStyle : invalidStyle);
//        txtBran.setStyle(txtBran.getText().trim().matches("\\d*\\.?\\d*") && !txtBran.getText().isEmpty() ? validStyle : invalidStyle);
//
//        // Apply default style if field is empty
//        if (txtMilledQuantity.getText().isEmpty()) txtMilledQuantity.setStyle(defaultStyle);
//        if (txtBrokenRice.getText().isEmpty()) txtBrokenRice.setStyle(defaultStyle);
//        if (txtHusk.getText().isEmpty()) txtHusk.setStyle(defaultStyle);
//        if (txtBran.getText().isEmpty()) txtBran.setStyle(defaultStyle);
//
//
//        // ComboBox validation
//        cmbPaddyId.setStyle(cmbPaddyId.getValue() != null && !cmbPaddyId.getValue().isEmpty() ? validStyle : invalidStyle);
//        if (cmbPaddyId.getValue() == null || cmbPaddyId.getValue().isEmpty()) cmbPaddyId.setStyle(defaultStyle);
//
//        // Duration label style
//        if (lblDuration.getText().contains("Invalid")) {
//            lblDuration.setStyle("-fx-text-fill: red;");
//        } else if (lblDuration.getText().contains("Duration:") && !lblDuration.getText().equals("Duration: 00:00:00")) {
//            lblDuration.setStyle("-fx-text-fill: green;");
//        } else {
//            lblDuration.setStyle("-fx-text-fill: black;");
//        }
//    }
//
//
//    // Sets up the search filter for the TableView
//    private void setupSearchFilter() {
//        FilteredList<MillingProcessdto> filteredData = new FilteredList<>(millingProcessMasterData, p -> true);
//
//        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
//            filteredData.setPredicate(millingProcess -> {
//                if (newValue == null || newValue.isEmpty()) {
//                    return true; // Display all records if search text is empty
//                }
//                String lowerCaseFilter = newValue.toLowerCase();
//                // Check if search text matches milling ID, paddy ID, or milled quantity
//                return millingProcess.getMillingId().toLowerCase().contains(lowerCaseFilter) ||
//                        millingProcess.getPaddyId().toLowerCase().contains(lowerCaseFilter) ||
//                        (millingProcess.getMilledQuantity() != null &&
//                                millingProcess.getMilledQuantity().toPlainString().toLowerCase().contains(lowerCaseFilter));
//            });
//            // Wrap the FilteredList in a SortedList to maintain sorting
//            SortedList<MillingProcessdto> sortedData = new SortedList<>(filteredData);
//            sortedData.comparatorProperty().bind(table.comparatorProperty()); // Bind to table's sort order
//            table.setItems(sortedData);
//            updateMillingProcessCount(); // Update the record count label
//        });
//    }
//
//    // Updates the label displaying the number of milling process records
//    private void updateMillingProcessCount() {
//        lblMillingProcessCount.setText("Milling Processes: " + table.getItems().size());
//    }
//
//    @FXML
//    void btnSaveOnAction(ActionEvent event) {
//        if (!validateInputFields(true)) return; // Validate with dialogs before saving
//
//        try {
//            // Business rule: Prevent saving if paddy ID is already linked to a milling process
//            if (millingProcessBO.checkPaddyIdExistsInProcess(cmbPaddyId.getValue())) {
//                showAlert(Alert.AlertType.ERROR, "Paddy ID: " + cmbPaddyId.getValue() + " is already associated with a milling process.");
//                return;
//            }
//
//            MillingProcessdto dto = createMillingProcessDto();
//            millingProcessBO.saveMillingProcess(dto); // Call BO to save
//
//            showAlert(Alert.AlertType.INFORMATION, "Milling Process Saved Successfully!");
//            clearFields(); // Clear fields and reload table after successful save
//        } catch (DuplicateException e) { // Catch specific BO exception for duplicates
//            showAlert(Alert.AlertType.ERROR, e.getMessage());
//        } catch (Exception e) {
//            showAlert(Alert.AlertType.ERROR, "Error saving milling process: " + e.getMessage());
//            e.printStackTrace(); // Log the full stack trace for debugging
//        }
//    }
//
//    @FXML
//    void btnUpdateOnAction(ActionEvent event) {
//        if (!validateInputFields(true)) return; // Validate with dialogs before updating
//
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation");
//        alert.setHeaderText("Update Milling Process");
//        alert.setContentText("Are you sure you want to update this process?");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                MillingProcessdto dto = createMillingProcessDto();
//                millingProcessBO.updateMillingProcess(dto); // Call BO to update
//
//                showAlert(Alert.AlertType.INFORMATION, "Milling Process Updated Successfully!");
//                clearFields(); // Clear fields and reload table after successful update
//            } catch (NotFoundException e) { // Catch specific BO exception for not found
//                showAlert(Alert.AlertType.ERROR, e.getMessage());
//            } catch (Exception e) {
//                showAlert(Alert.AlertType.ERROR, "Error updating milling process: " + e.getMessage());
//                e.printStackTrace();
//            }
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
//                boolean isDeleted = millingProcessBO.deleteMillingProcess(txtMilling_id.getText()); // Call BO to delete
//                if (isDeleted) {
//                    showAlert(Alert.AlertType.INFORMATION, "Milling Process Deleted Successfully!");
//                    clearFields(); // Clear fields and reload table after successful deletion
//                } else {
//                    showAlert(Alert.AlertType.ERROR, "Milling Process Deletion Failed! (No record found or other issue)");
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
//            clearFields(); // Clears all input fields and resets UI state
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Error preparing for new entry: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    void toggleOverride(ActionEvent event) {
//        overrideEnabled = !overrideEnabled; // Toggle the override state
//        txtBrokenRice.setEditable(overrideEnabled);
//        txtHusk.setEditable(overrideEnabled);
//        txtBran.setEditable(overrideEnabled);
//
//        if (overrideEnabled) {
//            btnOverride.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); // Red for "Lock"
//            btnOverride.setText("Lock Auto-Calc");
//        } else {
//            btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;"); // Purple for "Override"
//            btnOverride.setText("Override Byproducts");
//            recalculateByproducts(); // Recalculate if reverting from override
//        }
//        updateButtonStatesAndStyles(); // Update button state after override toggle
//    }
//
//    // Handles row clicks in the TableView to populate input fields
//    public void tableColumnOnClicked(MouseEvent mouseEvent) {
//        MillingProcessdto process = table.getSelectionModel().getSelectedItem();
//        if (process != null) {
//            txtMilling_id.setText(process.getMillingId());
//            cmbPaddyId.setValue(process.getPaddyId());
//            txtMilledQuantity.setText(process.getMilledQuantity().toPlainString());
//            txtBrokenRice.setText(process.getBrokenRice().toPlainString());
//            txtHusk.setText(process.getHusk().toPlainString());
//            txtBran.setText(process.getBran().toPlainString());
//
//            currentStartTime = process.getStartTime(); // Load start time
//            lblStartTime.setText(String.format("%02d:%02d:%02d",
//                    currentStartTime.getHour(),
//                    currentStartTime.getMinute(),
//                    currentStartTime.getSecond()));
//
//            // Set end time spinners
//            endHourSpinner.getValueFactory().setValue(process.getEndTime().getHour());
//            endMinuteSpinner.getValueFactory().setValue(process.getEndTime().getMinute());
//            endSecondSpinner.getValueFactory().setValue(process.getEndTime().getSecond());
//
//            overrideEnabled = true; // When loading, assume values were overridden or are fixed
//            // The next call to toggleOverride will switch it back to false (auto-calc)
//            // or if it was already true, it stays true, and the fields remain editable.
//            // If you want fields always editable on select: remove toggleOverride(null) and set editable=true manually.
//            toggleOverride(null); // Apply override styling and make fields editable
//
//            calculateDuration(); // Recalculate duration for the selected item
//            updateButtonStatesAndStyles(); // Update button states (Update/Delete enabled)
//        }
//    }
//
//    // Creates a MillingProcessdto object from the current input field values
//    private MillingProcessdto createMillingProcessDto() {
//        return new MillingProcessdto(
//                txtMilling_id.getText(),
//                cmbPaddyId.getValue(),
//                currentStartTime,
//                getEndTimeFromSpinners(),
//                new BigDecimal(txtMilledQuantity.getText()),
//                new BigDecimal(txtBrokenRice.getText()),
//                new BigDecimal(txtHusk.getText()),
//                new BigDecimal(txtBran.getText())
//        );
//    }
//
//    // Clears all input fields, resets UI elements, and loads next ID/table
//    private void clearFields() throws SQLException {
//        cmbPaddyId.setValue(null);
//        txtMilledQuantity.clear();
//        clearByproductFields();
//        setCurrentTimeAsStartTime(); // Reset start time to current time
//
//        // Reset end time spinners to current time
//        LocalTime now = LocalTime.now();
//        endHourSpinner.getValueFactory().setValue(now.getHour());
//        endMinuteSpinner.getValueFactory().setValue(now.getMinute());
//        endSecondSpinner.getValueFactory().setValue(now.getSecond());
//
//        lblDuration.setText("Duration: 00:00:00");
//        lblDuration.setStyle("-fx-text-fill: black;"); // Reset duration label style
//
//        overrideEnabled = false; // Reset override state
//        btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
//        btnOverride.setText("Override Byproducts");
//        txtBrokenRice.setEditable(false); // Ensure fields are not editable after clear
//        txtHusk.setEditable(false);
//        txtBran.setEditable(false);
//
//        loadNextId(); // Load the next available ID
//        loadTable(); // Reload table data
//        table.getSelectionModel().clearSelection(); // Clear table selection
//        updateButtonStatesAndStyles(); // Re-evaluate button states (Save enabled, Update/Delete disabled)
//        txtSearch.clear(); // Clear search field
//        applyValidationStyles(); // Reset styles to default/empty state
//    }
//
//    // Clears only the byproduct text fields
//    private void clearByproductFields() {
//        txtBrokenRice.clear();
//        txtHusk.clear();
//        txtBran.clear();
//    }
//
//    // Loads the next available Milling Process ID from the BO
//    private void loadNextId() throws SQLException {
//        txtMilling_id.setText(millingProcessBO.getNextMillingProcessId());
//        txtMilling_id.setEditable(false); // Keep ID field non-editable
//    }
//
//    // Loads all milling process data into the TableView
//    private void loadTable() {
//        try {
//            List<MillingProcessdto> processes = millingProcessBO.getAllMillingProcesses(); // Fetch from BO
//            millingProcessMasterData.setAll(processes); // Update master data list
//            // Directly set items to table, filtered list will pick it up
//            table.setItems(millingProcessMasterData);
//            updateMillingProcessCount(); // Update count
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Error loading data into table: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // Displays an error message for invalid duration
//    private void showInvalidDuration(String message) {
//        lblDuration.setText(message);
//        lblDuration.setStyle("-fx-text-fill: red;");
//    }
//
//    // Displays the calculated valid duration
//    private void showValidDuration(String durationText) {
//        lblDuration.setText(durationText);
//        lblDuration.setStyle("-fx-text-fill: green;");
//    }
//
//    // Recalculates byproducts if milled quantity is present and override is off
//    private void recalculateByproducts() {
//        if (!txtMilledQuantity.getText().isEmpty()) {
//            try {
//                calculateByproducts(new BigDecimal(txtMilledQuantity.getText()));
//            } catch (NumberFormatException e) {
//                clearByproductFields();
//            }
//        } else {
//            clearByproductFields(); // Clear if milled quantity is empty
//        }
//    }
//
//    // Helper method to show an alert dialog
//    private void showAlert(Alert.AlertType type, String message) {
//        new Alert(type, message).show();
//    }
//
//    @FXML
//    void clearSearch(ActionEvent event) {
//        txtSearch.clear();
//    }
//
//    @FXML
//    void searchRawPaddy(KeyEvent event) {
//        // This method is linked to onKeyReleased for txtSearch in FXML
//        // The actual filtering logic is handled by the listener in setupSearchFilter()
//    }
//}