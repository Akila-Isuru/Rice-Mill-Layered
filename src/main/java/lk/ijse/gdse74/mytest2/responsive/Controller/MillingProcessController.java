package lk.ijse.gdse74.mytest2.responsive.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.RawPaddyBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MillingProcessController implements Initializable {

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnOverride;
    @FXML private TableColumn<MillingProcessdto, BigDecimal> colBran_kg;
    @FXML private TableColumn<MillingProcessdto, BigDecimal> colBroken_rice;
    @FXML private TableColumn<MillingProcessdto, LocalTime> colEnd_time;
    @FXML private TableColumn<MillingProcessdto, BigDecimal> colHusk_kg;
    @FXML private TableColumn<MillingProcessdto, String> colMilling_id;
    @FXML private TableColumn<MillingProcessdto, String> colPaddy_id;
    @FXML private TableColumn<MillingProcessdto, LocalTime> colStart_time;
    @FXML private TableColumn<MillingProcessdto, BigDecimal> colMilled_Quantity;
    @FXML private TableView<MillingProcessdto> table;
    @FXML private TextField txtBran;
    @FXML private TextField txtBrokenRice;
    @FXML private TextField txtHusk;
    @FXML private TextField txtMilledQuantity;
    @FXML private TextField txtMilling_id;
    @FXML private ComboBox<String> cmbPaddyId;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinuteSpinner;
    @FXML private Spinner<Integer> endSecondSpinner;
    @FXML private Label lblDuration;
    @FXML private Label lblStartTime;
    @FXML private TextField txtSearch;
    @FXML private Label lblMillingProcessCount;

    private static final BigDecimal BROKEN_RICE_RATIO = new BigDecimal("0.05");
    private static final BigDecimal HUSK_RATIO = new BigDecimal("0.20");
    private static final BigDecimal BRAN_RATIO = new BigDecimal("0.10");

    private boolean overrideEnabled = false;
    private LocalTime currentStartTime;
    private ObservableList<MillingProcessdto> millingProcessMasterData = FXCollections.observableArrayList();

    // Initialize BO instances using BOFactory
    private final MillingProcessBO millingProcessBO = BOFactory.getInstance().getBO(BOTypes.MILLING_PROCESS);
    private final RawPaddyBO rawPaddyBO = BOFactory.getInstance().getBO(BOTypes.RAW_PADDY);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        setCurrentTimeAsStartTime();
        initTimeSpinners();
        setupAutomaticCalculations();
        setupFieldListeners();
        setupSearchFilter();

        try {
            loadNextId();
            loadTable();
            loadPaddyIds();
            updateButtonStatesAndStyles();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize MillingProcessController", e);
        }
    }

    private void setCellValueFactories() {
        colMilling_id.setCellValueFactory(new PropertyValueFactory<>("millingId"));
        colPaddy_id.setCellValueFactory(new PropertyValueFactory<>("paddyId"));
        colStart_time.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEnd_time.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colMilled_Quantity.setCellValueFactory(new PropertyValueFactory<>("milledQuantity"));
        colBroken_rice.setCellValueFactory(new PropertyValueFactory<>("brokenRice"));
        colHusk_kg.setCellValueFactory(new PropertyValueFactory<>("husk"));
        colBran_kg.setCellValueFactory(new PropertyValueFactory<>("bran"));
    }

    private void loadPaddyIds() {
        try {
            ObservableList<String> paddyIds = FXCollections.observableArrayList(rawPaddyBO.getAllRawPaddyIds());
            cmbPaddyId.setItems(paddyIds);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load paddy IDs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setCurrentTimeAsStartTime() {
        currentStartTime = LocalTime.now();
        lblStartTime.setText(String.format("%02d:%02d:%02d",
                currentStartTime.getHour(), currentStartTime.getMinute(), currentStartTime.getSecond()));
    }

    private void initTimeSpinners() {
        LocalTime now = LocalTime.now();
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));
        endSecondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getSecond()));

        endHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
        endMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
        endSecondSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateDuration());
    }

    private void setupAutomaticCalculations() {
        txtMilledQuantity.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && newVal.matches("\\d*\\.?\\d*")) {
                if (!overrideEnabled) {
                    try {
                        calculateByproducts(new BigDecimal(newVal));
                    } catch (NumberFormatException e) {
                        clearByproductFields();
                    }
                }
            } else if (newVal.isEmpty()) {
                clearByproductFields();
            }
            updateButtonStatesAndStyles();
        });

        txtBrokenRice.setEditable(false);
        txtHusk.setEditable(false);
        txtBran.setEditable(false);
    }

    private void calculateByproducts(BigDecimal milledQuantity) {
        BigDecimal brokenRice = milledQuantity.multiply(BROKEN_RICE_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal husk = milledQuantity.multiply(HUSK_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal bran = milledQuantity.multiply(BRAN_RATIO).setScale(2, BigDecimal.ROUND_HALF_UP);

        txtBrokenRice.setText(brokenRice.toPlainString());
        txtHusk.setText(husk.toPlainString());
        txtBran.setText(bran.toPlainString());
    }

    private void calculateDuration() {
        try {
            LocalTime endTime = getEndTimeFromSpinners();

            if (currentStartTime != null && endTime != null) {
                if (endTime.isBefore(currentStartTime)) {
                    showInvalidDuration("Invalid: End before Start");
                } else {
                    Duration duration = Duration.between(currentStartTime, endTime);
                    long totalSeconds = duration.getSeconds();
                    long hours = totalSeconds / 3600;
                    long minutes = (totalSeconds % 3600) / 60;
                    long seconds = totalSeconds % 60;
                    showValidDuration(String.format("Duration: %02d:%02d:%02d", hours, minutes, seconds));
                }
            } else {
                showInvalidDuration("Invalid Time Input");
            }
        } catch (Exception e) {
            showInvalidDuration("Error Calculating Duration");
            e.printStackTrace();
        }
        updateButtonStatesAndStyles();
    }

    private LocalTime getEndTimeFromSpinners() {
        try {
            int hour = endHourSpinner.getValue();
            int minute = endMinuteSpinner.getValue();
            int second = endSecondSpinner.getValue();
            return LocalTime.of(hour, minute, second);
        } catch (Exception e) {
            return null;
        }
    }

    private void setupFieldListeners() {
        cmbPaddyId.valueProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtMilledQuantity.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtBrokenRice.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtHusk.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtBran.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonStatesAndStyles();
        });
    }

    private void updateButtonStatesAndStyles() {
        boolean isValidInput = validateInputFields(false);
        MillingProcessdto selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            btnSave.setDisable(!isValidInput);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(true);
            btnUpdate.setDisable(!isValidInput);
            btnDelete.setDisable(false);
        }
        applyValidationStyles();
    }

    private boolean validateInputFields(boolean showDialog) {
        String paddyId = cmbPaddyId.getValue();
        String milledQtyStr = txtMilledQuantity.getText().trim();
        String brokenRiceStr = txtBrokenRice.getText().trim();
        String huskStr = txtHusk.getText().trim();
        String branStr = txtBran.getText().trim();
        LocalTime endTime = getEndTimeFromSpinners();

        if (paddyId == null || paddyId.isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Paddy ID.");
            return false;
        }
        if (milledQtyStr.isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Milled quantity cannot be empty.");
            return false;
        }
        if (brokenRiceStr.isEmpty() || huskStr.isEmpty() || branStr.isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Byproduct fields cannot be empty. Enter values or uncheck override.");
            return false;
        }

        try {
            BigDecimal milledQty = new BigDecimal(milledQtyStr);
            BigDecimal brokenRice = new BigDecimal(brokenRiceStr);
            BigDecimal husk = new BigDecimal(huskStr);
            BigDecimal bran = new BigDecimal(branStr);

            if (milledQty.compareTo(BigDecimal.ZERO) <= 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Milled quantity must be a positive value.");
                return false;
            }
            if (brokenRice.compareTo(BigDecimal.ZERO) < 0 ||
                    husk.compareTo(BigDecimal.ZERO) < 0 ||
                    bran.compareTo(BigDecimal.ZERO) < 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Byproduct quantities cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Numeric fields must contain valid numbers.");
            return false;
        }

        if (endTime == null || (currentStartTime != null && endTime.isBefore(currentStartTime))) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "End time must be after start time.");
            return false;
        }

        return true;
    }

    private void applyValidationStyles() {
        String validStyle = "-fx-border-color: #2980b9; -fx-border-radius: 3;";
        String invalidStyle = "-fx-border-color: #e74c3c; -fx-border-radius: 3;";
        String defaultStyle = "-fx-background-radius: 3; -fx-border-color: #bdc3c7; -fx-border-radius: 3;";

        txtMilledQuantity.setStyle(txtMilledQuantity.getText().trim().matches("\\d*\\.?\\d*") && !txtMilledQuantity.getText().isEmpty() ? validStyle : invalidStyle);
        txtBrokenRice.setStyle(txtBrokenRice.getText().trim().matches("\\d*\\.?\\d*") && !txtBrokenRice.getText().isEmpty() ? validStyle : invalidStyle);
        txtHusk.setStyle(txtHusk.getText().trim().matches("\\d*\\.?\\d*") && !txtHusk.getText().isEmpty() ? validStyle : invalidStyle);
        txtBran.setStyle(txtBran.getText().trim().matches("\\d*\\.?\\d*") && !txtBran.getText().isEmpty() ? validStyle : invalidStyle);

        if (txtMilledQuantity.getText().isEmpty()) txtMilledQuantity.setStyle(defaultStyle);
        if (txtBrokenRice.getText().isEmpty()) txtBrokenRice.setStyle(defaultStyle);
        if (txtHusk.getText().isEmpty()) txtHusk.setStyle(defaultStyle);
        if (txtBran.getText().isEmpty()) txtBran.setStyle(defaultStyle);

        cmbPaddyId.setStyle(cmbPaddyId.getValue() != null && !cmbPaddyId.getValue().isEmpty() ? validStyle : invalidStyle);
        if (cmbPaddyId.getValue() == null || cmbPaddyId.getValue().isEmpty()) cmbPaddyId.setStyle(defaultStyle);

        if (lblDuration.getText().contains("Invalid")) {
            lblDuration.setStyle("-fx-text-fill: red;");
        } else if (lblDuration.getText().contains("Duration:") && !lblDuration.getText().equals("Duration: 00:00:00")) {
            lblDuration.setStyle("-fx-text-fill: green;");
        } else {
            lblDuration.setStyle("-fx-text-fill: black;");
        }
    }


    private void setupSearchFilter() {
        FilteredList<MillingProcessdto> filteredData = new FilteredList<>(millingProcessMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(millingProcess -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return millingProcess.getMillingId().toLowerCase().contains(lowerCaseFilter) ||
                        millingProcess.getPaddyId().toLowerCase().contains(lowerCaseFilter) ||
                        (millingProcess.getMilledQuantity() != null &&
                                millingProcess.getMilledQuantity().toPlainString().toLowerCase().contains(lowerCaseFilter));
            });
            SortedList<MillingProcessdto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);
            updateMillingProcessCount();
        });
    }

    private void updateMillingProcessCount() {
        lblMillingProcessCount.setText("Milling Processes: " + table.getItems().size());
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateInputFields(true)) return;

        try {
            if (millingProcessBO.checkPaddyIdExistsInProcess(cmbPaddyId.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Paddy ID: " + cmbPaddyId.getValue() + " is already associated with a milling process.");
                return;
            }

            MillingProcessdto dto = createMillingProcessDto();
            millingProcessBO.saveMillingProcess(dto);

            showAlert(Alert.AlertType.INFORMATION, "Milling Process Saved Successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving milling process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputFields(true)) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Update Milling Process");
        alert.setContentText("Are you sure you want to update this process?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                MillingProcessdto dto = createMillingProcessDto();
                millingProcessBO.updateMillingProcess(dto);

                showAlert(Alert.AlertType.INFORMATION, "Milling Process Updated Successfully!");
                clearFields();
            } catch (NotFoundException | DuplicateException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error updating milling process: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Milling Process");
        alert.setContentText("Are you sure you want to delete this process?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = millingProcessBO.deleteMillingProcess(txtMilling_id.getText());
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Milling Process Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Milling Process Deletion Failed! (No record found or other issue)");
                }
            } catch (NotFoundException | InUseException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error deleting milling process: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void toggleOverride(ActionEvent event) {
        overrideEnabled = !overrideEnabled;
        txtBrokenRice.setEditable(overrideEnabled);
        txtHusk.setEditable(overrideEnabled);
        txtBran.setEditable(overrideEnabled);

        if (overrideEnabled) {
            btnOverride.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            btnOverride.setText("Lock Auto-Calc");
        } else {
            btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
            btnOverride.setText("Override Byproducts");
            recalculateByproducts();
        }
        updateButtonStatesAndStyles();
    }

    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        MillingProcessdto process = table.getSelectionModel().getSelectedItem();
        if (process != null) {
            txtMilling_id.setText(process.getMillingId());
            cmbPaddyId.setValue(process.getPaddyId());
            txtMilledQuantity.setText(process.getMilledQuantity().toPlainString());
            txtBrokenRice.setText(process.getBrokenRice().toPlainString());
            txtHusk.setText(process.getHusk().toPlainString());
            txtBran.setText(process.getBran().toPlainString());

            currentStartTime = process.getStartTime();
            lblStartTime.setText(String.format("%02d:%02d:%02d",
                    currentStartTime.getHour(),
                    currentStartTime.getMinute(),
                    currentStartTime.getSecond()));

            endHourSpinner.getValueFactory().setValue(process.getEndTime().getHour());
            endMinuteSpinner.getValueFactory().setValue(process.getEndTime().getMinute());
            endSecondSpinner.getValueFactory().setValue(process.getEndTime().getSecond());

            // Set overrideEnabled to true and update the button/editable state
            // This ensures that when an item is selected, byproduct fields are editable
            overrideEnabled = true; // Assume loaded values might be custom or need editing
            txtBrokenRice.setEditable(true);
            txtHusk.setEditable(true);
            txtBran.setEditable(true);
            btnOverride.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            btnOverride.setText("Lock Auto-Calc");

            calculateDuration();
            updateButtonStatesAndStyles();
        }
    }

    private MillingProcessdto createMillingProcessDto() {
        return new MillingProcessdto(
                txtMilling_id.getText(),
                cmbPaddyId.getValue(),
                currentStartTime,
                getEndTimeFromSpinners(),
                new BigDecimal(txtMilledQuantity.getText()),
                new BigDecimal(txtBrokenRice.getText()),
                new BigDecimal(txtHusk.getText()),
                new BigDecimal(txtBran.getText())
        );
    }

    private void clearFields() throws SQLException {
        cmbPaddyId.setValue(null);
        txtMilledQuantity.clear();
        clearByproductFields();
        setCurrentTimeAsStartTime();

        LocalTime now = LocalTime.now();
        endHourSpinner.getValueFactory().setValue(now.getHour());
        endMinuteSpinner.getValueFactory().setValue(now.getMinute());
        endSecondSpinner.getValueFactory().setValue(now.getSecond());

        lblDuration.setText("Duration: 00:00:00");
        lblDuration.setStyle("-fx-text-fill: black;");

        overrideEnabled = false;
        btnOverride.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnOverride.setText("Override Byproducts");
        txtBrokenRice.setEditable(false);
        txtHusk.setEditable(false);
        txtBran.setEditable(false);

        loadNextId();
        loadTable();
        table.getSelectionModel().clearSelection();
        updateButtonStatesAndStyles();
        txtSearch.clear();
        applyValidationStyles();
    }

    private void clearByproductFields() {
        txtBrokenRice.clear();
        txtHusk.clear();
        txtBran.clear();
    }

    private void loadNextId() throws SQLException {
        txtMilling_id.setText(millingProcessBO.getNextMillingProcessId());
        txtMilling_id.setEditable(false);
    }

    private void loadTable() {
        try {
            List<MillingProcessdto> processes = millingProcessBO.getAllMillingProcesses();
            millingProcessMasterData.setAll(processes);
            table.setItems(millingProcessMasterData);
            updateMillingProcessCount();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading data into table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showInvalidDuration(String message) {
        lblDuration.setText(message);
        lblDuration.setStyle("-fx-text-fill: red;");
    }

    private void showValidDuration(String durationText) {
        lblDuration.setText(durationText);
        lblDuration.setStyle("-fx-text-fill: green;");
    }

    private void recalculateByproducts() {
        if (!txtMilledQuantity.getText().isEmpty()) {
            try {
                calculateByproducts(new BigDecimal(txtMilledQuantity.getText()));
            } catch (NumberFormatException e) {
                clearByproductFields();
            }
        } else {
            clearByproductFields();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
    }

    @FXML
    void searchRawPaddy(KeyEvent event) {
        // The actual filtering logic is handled by the listener in setupSearchFilter()
    }
}