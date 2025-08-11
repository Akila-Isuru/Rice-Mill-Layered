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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.FarmerBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.RawPaddyBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.SupplierBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.RawPaddydto;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;

public class RawPaddyController implements Initializable {

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<RawPaddydto,String> colFarmer_id;
    @FXML private TableColumn<RawPaddydto,BigDecimal> colMoisture_level;
    @FXML private TableColumn<RawPaddydto,String> colPaddy_id;
    @FXML private TableColumn<RawPaddydto,BigDecimal> colPrice_per_kg;
    @FXML private TableColumn<RawPaddydto, Date> colPurchase_date;
    @FXML private TableColumn<RawPaddydto,BigDecimal> colQuantity_Kg;
    @FXML private TableColumn<RawPaddydto,String> colSupplier_id;
    @FXML private TableView<RawPaddydto> table;
    @FXML private ComboBox<String> cmbFarmer_id;
    @FXML private TextField txtMoisture_level;
    @FXML private TextField txtPaddy_id;
    @FXML private DatePicker dpPurchase_date;
    @FXML private TextField txtPurchase_price_per_kg;
    @FXML private TextField txtQuantity_kg;
    @FXML private ComboBox<String> cmbSupplier_id;
    @FXML private TextField txtSearch;
    @FXML private Label lblRawPaddyCount;

    private final RawPaddyBO rawPaddyBO = BOFactory.getInstance().getBO(BOTypes.RAW_PADDY);
    private final SupplierBO supplierBO = BOFactory.getInstance().getBO(BOTypes.SUPPLIER);
    private final FarmerBO farmerBO = BOFactory.getInstance().getBO(BOTypes.FARMER);

    private ObservableList<RawPaddydto> rawPaddyMasterData = FXCollections.observableArrayList();

    private final String numericPattern = "^\\d*\\.?\\d+$";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        try {
            loadNextId();
            loadTable();
            loadSupplierIds();
            loadFarmerIds();
            setupFieldListeners();
            setupSearchFilter();
            fillCurrentDate();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage()).show();
            throw new RuntimeException("Failed to initialize RawPaddyController", e);
        }
    }

    private void setCellValueFactories() {
        colPaddy_id.setCellValueFactory(new PropertyValueFactory<>("paddyId"));
        colSupplier_id.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colFarmer_id.setCellValueFactory(new PropertyValueFactory<>("farmerId"));
        colQuantity_Kg.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMoisture_level.setCellValueFactory(new PropertyValueFactory<>("moisture"));
        colPrice_per_kg.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        colPurchase_date.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
    }

    private void loadTable() throws SQLException {
        try {
            rawPaddyMasterData.clear();
            rawPaddyMasterData.addAll(rawPaddyBO.getAllRawPaddy());
            table.setItems(rawPaddyMasterData);
            updateRawPaddyCount();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load raw paddy data: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void updateRawPaddyCount() {
        lblRawPaddyCount.setText("Raw Paddy Records: " + table.getItems().size());
    }

    private void loadSupplierIds() {
        try {
            ObservableList<String> supplierIds = FXCollections.observableArrayList(supplierBO.getAllSupplierIds());
            cmbSupplier_id.setItems(supplierIds);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load supplier IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void loadFarmerIds() throws SQLException {
        ObservableList<String> farmerIds = FXCollections.observableArrayList(farmerBO.getAllFarmerIds());
        cmbFarmer_id.setItems(farmerIds);
    }

    private void loadNextId() {
        try {
            String nextId = rawPaddyBO.getNextId();
            txtPaddy_id.setText(nextId);
            txtPaddy_id.setEditable(false);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate next ID: " + e.getMessage()).show();
            txtPaddy_id.setText("P001");
            txtPaddy_id.setEditable(false);
        }
    }

    private void fillCurrentDate() {
        dpPurchase_date.setValue(LocalDate.now());
    }

    private void setupFieldListeners() {
        txtQuantity_kg.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateMoistureLevel(newValue);
            updateButtonStatesAndStyles();
        });
        txtMoisture_level.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
        txtPurchase_price_per_kg.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
        dpPurchase_date.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
        cmbSupplier_id.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
        cmbFarmer_id.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtonStatesAndStyles());
    }

    private void calculateMoistureLevel(String quantityStr) {
        try {
            if (quantityStr.isEmpty() || !quantityStr.matches(numericPattern)) {
                txtMoisture_level.clear();
                return;
            }
            BigDecimal quantity = new BigDecimal(quantityStr);
            BigDecimal moistureLevel;

            if (quantity.compareTo(new BigDecimal(50)) < 0) {
                moistureLevel = new BigDecimal("12.5");
            } else if (quantity.compareTo(new BigDecimal(100)) < 0) {
                moistureLevel = new BigDecimal("12.0");
            } else if (quantity.compareTo(new BigDecimal(200)) < 0) {
                moistureLevel = new BigDecimal("11.5");
            } else {
                moistureLevel = new BigDecimal("11.0");
            }
            txtMoisture_level.setText(String.format("%.1f", moistureLevel));
        } catch (NumberFormatException e) {
            txtMoisture_level.clear();
        }
    }

    private void updateButtonStatesAndStyles() {
        boolean isValidInput = validateInputFields(false);
        RawPaddydto selectedItem = table.getSelectionModel().getSelectedItem();

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
        String quantityStr = txtQuantity_kg.getText().trim();
        String priceStr = txtPurchase_price_per_kg.getText().trim();
        LocalDate purchaseDate = dpPurchase_date.getValue();
        String selectedSupplierId = cmbSupplier_id.getValue();
        String selectedFarmerId = cmbFarmer_id.getValue();

        if (quantityStr.isEmpty() || priceStr.isEmpty() || purchaseDate == null) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Quantity, Purchase Price, and Purchase Date are required.").show();
            return false;
        }

        boolean isSupplierSelected = selectedSupplierId != null && !selectedSupplierId.trim().isEmpty();
        boolean isFarmerSelected = selectedFarmerId != null && !selectedFarmerId.trim().isEmpty();
        if (!isSupplierSelected && !isFarmerSelected) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Either a Supplier ID or a Farmer ID must be selected.").show();
            return false;
        }

        try {
            new BigDecimal(quantityStr);
            new BigDecimal(priceStr);
            if (!txtMoisture_level.getText().trim().isEmpty()) {
                new BigDecimal(txtMoisture_level.getText().trim());
            }
        } catch (NumberFormatException e) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Numeric fields (Quantity, Moisture, Price) must contain valid numbers.").show();
            return false;
        }

        return true;
    }

    private void applyValidationStyles() {
        boolean isQuantityValid = txtQuantity_kg.getText().trim().matches(numericPattern) && !txtQuantity_kg.getText().trim().isEmpty();
        boolean isPriceValid = txtPurchase_price_per_kg.getText().trim().matches(numericPattern) && !txtPurchase_price_per_kg.getText().trim().isEmpty();

        txtQuantity_kg.setStyle(isQuantityValid ? "-fx-border-color: blue" : "-fx-border-color: red");
        txtPurchase_price_per_kg.setStyle(isPriceValid ? "-fx-border-color: blue" : "-fx-border-color: red");

        String moistureText = txtMoisture_level.getText().trim();
        if (!moistureText.isEmpty()) {
            boolean isMoistureValid = moistureText.matches(numericPattern);
            txtMoisture_level.setStyle(isMoistureValid ? "-fx-border-color: blue" : "-fx-border-color: red");
        } else {
            txtMoisture_level.setStyle("");
        }

        if (txtQuantity_kg.getText().trim().isEmpty()) txtQuantity_kg.setStyle("");
        if (txtPurchase_price_per_kg.getText().trim().isEmpty()) txtPurchase_price_per_kg.setStyle("");

        dpPurchase_date.setStyle(dpPurchase_date.getValue() != null ? "-fx-border-color: blue" : "-fx-border-color: red");
        if (dpPurchase_date.getValue() == null) dpPurchase_date.setStyle("");
    }


    private void setupSearchFilter() {
        FilteredList<RawPaddydto> filteredData = new FilteredList<>(rawPaddyMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(rawPaddy -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return rawPaddy.getPaddyId().toLowerCase().contains(lowerCaseFilter) ||
                        (rawPaddy.getSupplierId() != null && rawPaddy.getSupplierId().toLowerCase().contains(lowerCaseFilter)) ||
                        (rawPaddy.getFarmerId() != null && rawPaddy.getFarmerId().toLowerCase().contains(lowerCaseFilter)) ||
                        String.valueOf(rawPaddy.getQuantity()).toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(rawPaddy.getPurchasePrice()).toLowerCase().contains(lowerCaseFilter);
            });

            SortedList<RawPaddydto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);
            updateRawPaddyCount();
        });
    }


    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Raw Paddy Record");
        alert.setContentText("Are you sure you want to delete this record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String paddyId = txtPaddy_id.getText();
                boolean isDeleted = rawPaddyBO.deleteRawPaddy(paddyId);

                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully!").show();
                    clearFields();
                    loadTable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Delete Failed!").show();
                }
            } catch (NotFoundException | InUseException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateInputFields(true)) {
            return;
        }

        try {
            BigDecimal quantity = new BigDecimal(txtQuantity_kg.getText().trim());
            BigDecimal moisture = txtMoisture_level.getText().trim().isEmpty() ? null : new BigDecimal(txtMoisture_level.getText().trim());
            BigDecimal price = new BigDecimal(txtPurchase_price_per_kg.getText().trim());
            java.sql.Date purchasedDate = java.sql.Date.valueOf(dpPurchase_date.getValue());

            String finalSupplierId = cmbSupplier_id.getValue();
            String finalFarmerId = cmbFarmer_id.getValue();

            RawPaddydto dto = new RawPaddydto(
                    txtPaddy_id.getText(),
                    finalSupplierId,
                    finalFarmerId,
                    quantity,
                    moisture,
                    price,
                    purchasedDate
            );

            rawPaddyBO.saveRawPaddy(dto);

            new Alert(Alert.AlertType.INFORMATION, "Saved Successfully!").show();
            clearFields();
            loadTable();
        } catch (DuplicateException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid numeric value for quantity, moisture, or price.").show();
        } catch (DateTimeParseException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid date format. Please select a valid date.").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputFields(true)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Raw Paddy Record");
        alert.setContentText("Are you sure you want to update this record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                BigDecimal quantity = new BigDecimal(txtQuantity_kg.getText().trim());
                BigDecimal moisture = txtMoisture_level.getText().trim().isEmpty() ? null : new BigDecimal(txtMoisture_level.getText().trim());
                BigDecimal price = new BigDecimal(txtPurchase_price_per_kg.getText().trim());
                java.sql.Date purchasedDate = java.sql.Date.valueOf(dpPurchase_date.getValue());

                String finalSupplierId = cmbSupplier_id.getValue();
                String finalFarmerId = cmbFarmer_id.getValue();

                RawPaddydto dto = new RawPaddydto(
                        txtPaddy_id.getText(),
                        finalSupplierId,
                        finalFarmerId,
                        quantity,
                        moisture,
                        price,
                        purchasedDate
                );

                rawPaddyBO.updateRawPaddy(dto);

                new Alert(Alert.AlertType.INFORMATION, "Updated Successfully!").show();
                clearFields();
                loadTable();
            } catch (NotFoundException | DuplicateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid numeric value for quantity, moisture, or price.").show();
            } catch (DateTimeParseException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid date format. Please select a valid date.").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    @FXML
    void tableColumnOnClicked(MouseEvent mouseEvent) {
        RawPaddydto selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            txtPaddy_id.setText(selectedItem.getPaddyId());
            cmbSupplier_id.setValue(selectedItem.getSupplierId());
            cmbFarmer_id.setValue(selectedItem.getFarmerId());
            txtQuantity_kg.setText(selectedItem.getQuantity() != null ? selectedItem.getQuantity().toPlainString() : "");
            txtMoisture_level.setText(selectedItem.getMoisture() != null ? selectedItem.getMoisture().toPlainString() : "");
            txtPurchase_price_per_kg.setText(selectedItem.getPurchasePrice() != null ? selectedItem.getPurchasePrice().toPlainString() : "");
            dpPurchase_date.setValue(selectedItem.getPurchaseDate() != null ? LocalDate.parse(selectedItem.getPurchaseDate().toLocaleString()) : null);
            updateButtonStatesAndStyles();
        }
    }

    private void clearFields() {
        txtMoisture_level.clear();
        txtPurchase_price_per_kg.clear();
        txtQuantity_kg.clear();
        cmbSupplier_id.getSelectionModel().clearSelection();
        cmbSupplier_id.setValue(null);
        cmbFarmer_id.getSelectionModel().clearSelection();
        cmbFarmer_id.setValue(null);
        txtSearch.clear();

        loadNextId();
        fillCurrentDate();
        table.getSelectionModel().clearSelection();
        updateButtonStatesAndStyles();
    }

    @FXML
    void searchRawPaddy(KeyEvent event) {
    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        table.setItems(rawPaddyMasterData);
        updateRawPaddyCount();
    }
}