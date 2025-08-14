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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.SupplierBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SuppliersController implements Initializable {
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<Suppliersdto, String> coladdress;
    @FXML private TableColumn<Suppliersdto, String> colcontatcnumber;
    @FXML private TableColumn<Suppliersdto, String> colemail;
    @FXML private TableColumn<Suppliersdto, String> colid;
    @FXML private TableColumn<Suppliersdto, String> colname;
    @FXML private TableView<Suppliersdto> tSuppliersTable;
    @FXML private TextField txtContact_number;
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtaddress;
    @FXML private TextField txtemail;
    @FXML private TextField txtSearch;
    @FXML private Label lblSupplierCount;

    private final String namePattern = "^[A-Za-z ]+$";
    private final String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final String phonePattern = "^0\\d{9}$";

    private ObservableList<Suppliersdto> supplierMasterData = FXCollections.observableArrayList();
    private final SupplierBO supplierBO = BOFactory.getInstance().getBO(BOTypes.SUPPLIER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        try {
            loadNextId();
            loadTable();
            setupSearchFilter();
            setupFieldListeners();
            updateButtonStates();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage()).show();
            throw new RuntimeException("Failed to initialize SuppliersController", e);
        }
    }

    private void setCellValueFactories() {
        colid.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colcontatcnumber.setCellValueFactory(new PropertyValueFactory<>("cotactNumber"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadTable() {
        try {
            List<Suppliersdto> suppliersdtos = supplierBO.getAllSuppliers();
            supplierMasterData.clear();
            supplierMasterData.addAll(suppliersdtos);
            tSuppliersTable.setItems(supplierMasterData);
            updateSupplierCount();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load supplier data: " + e.getMessage()).show();
        }
    }

    private void setupSearchFilter() {
        FilteredList<Suppliersdto> filteredData = new FilteredList<>(supplierMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(supplier -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return supplier.getSupplierId().toLowerCase().contains(lowerCaseFilter) ||
                        supplier.getName().toLowerCase().contains(lowerCaseFilter) ||
                        supplier.getCotactNumber().toLowerCase().contains(lowerCaseFilter) ||
                        supplier.getAddress().toLowerCase().contains(lowerCaseFilter) ||
                        supplier.getEmail().toLowerCase().contains(lowerCaseFilter);
            });

            SortedList<Suppliersdto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tSuppliersTable.comparatorProperty());
            tSuppliersTable.setItems(sortedData);
            updateSupplierCount();
        });
    }

    private void updateSupplierCount() {
        lblSupplierCount.setText("Suppliers: " + tSuppliersTable.getItems().size());
    }

    private void loadNextId() throws SQLException {
        try {
            String nextId = supplierBO.getNextId();
            txtId.setText(nextId);
            txtId.setEditable(false);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading next ID: " + e.getMessage()).show();
            txtId.setText("S001");
            txtId.setEditable(false);
        }
    }

    private void setupFieldListeners() {
        txtName.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtContact_number.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtaddress.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtemail.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        tSuppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        boolean isValidInputForSaveOrUpdate = validateInputFields(false);

        Suppliersdto selectedSupplier = tSuppliersTable.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            btnSave.setDisable(!isValidInputForSaveOrUpdate);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(true);
            btnUpdate.setDisable(!isValidInputForSaveOrUpdate);
            btnDelete.setDisable(false);
        }
    }

    private boolean validateInputFields(boolean showDialog) {
        String name = txtName.getText().trim();
        String contactNumber = txtContact_number.getText().trim();
        String email = txtemail.getText().trim();
        String address = txtaddress.getText().trim();

        if (name.isEmpty() || contactNumber.isEmpty() || address.isEmpty() || email.isEmpty()) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "All fields are required.").show();
            return false;
        }

        boolean isValidName = name.matches(namePattern);
        boolean isValidContact = contactNumber.matches(phonePattern);
        boolean isValidEmail = email.matches(emailPattern);

        txtName.setStyle(isValidName ? "-fx-border-color: blue" : "-fx-border-color: red");
        txtContact_number.setStyle(isValidContact ? "-fx-border-color: blue" : "-fx-border-color: red");
        txtemail.setStyle(isValidEmail ? "-fx-border-color: blue" : "-fx-border-color: red");

        if (!isValidName) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Name: Name should contain only letters and spaces.").show();
            return false;
        }
        if (!isValidContact) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Contact Number: Must be 10 digits starting with '0'.").show();
            return false;
        }
        if (!isValidEmail) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Email: Please enter a valid email address.").show();
            return false;
        }

        return true;
    }


    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
        loadNextId();
        tSuppliersTable.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    @FXML
    public void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Supplier");
        alert.setContentText("Are you sure you want to delete this supplier?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String id = txtId.getText();
            try {
                boolean isDeleted = supplierBO.deleteSupplier(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION,"Supplier deleted successfully").show();
                    clearFields();
                    loadTable();
                    loadNextId();
                    updateButtonStates();
                } else {
                    new Alert(Alert.AlertType.ERROR,"Failed to delete supplier").show();
                }
            } catch (NotFoundException | InUseException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,"An unexpected error occurred during delete: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (!validateInputFields(true)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Update Supplier");
        alert.setContentText("Are you sure you want to update this supplier?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Suppliersdto suppliersdto = new Suppliersdto(
                    txtId.getText(),
                    txtName.getText().trim(),
                    txtContact_number.getText().trim(),
                    txtaddress.getText().trim(),
                    txtemail.getText().trim()
            );

            try {
                supplierBO.updateSupplier(suppliersdto);
                new Alert(Alert.AlertType.INFORMATION, "Supplier updated successfully").show();
                clearFields();
                loadTable();
                loadNextId();
                updateButtonStates();
            } catch (NotFoundException | DuplicateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "An unexpected error occurred during update: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        if (!validateInputFields(true)) {
            return;
        }

        Suppliersdto suppliersdto = new Suppliersdto(
                txtId.getText(),
                txtName.getText().trim(),
                txtContact_number.getText().trim(),
                txtaddress.getText().trim(),
                txtemail.getText().trim()
        );

        try {
            supplierBO.saveSupplier(suppliersdto);
            new Alert(Alert.AlertType.INFORMATION, "Supplier saved successfully").show();
            clearFields();
            loadTable();
            loadNextId();
            updateButtonStates();
        } catch (DuplicateException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An unexpected error occurred during save: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtContact_number.clear();
        txtaddress.clear();
        txtemail.clear();
        txtName.setStyle("");
        txtContact_number.setStyle("");
        txtemail.setStyle("");
    }

    @FXML
    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        Suppliersdto suppliersdto = tSuppliersTable.getSelectionModel().getSelectedItem();
        if (suppliersdto != null) {
            txtId.setText(suppliersdto.getSupplierId());
            txtName.setText(suppliersdto.getName());
            txtContact_number.setText(suppliersdto.getCotactNumber());
            txtaddress.setText(suppliersdto.getAddress());
            txtemail.setText(suppliersdto.getEmail());
            txtNameChange(null);
            txtContactChange(null);
            txtEmailChange(null);
            updateButtonStates();
        }
    }

    @FXML
    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText().trim();
        boolean isValidName = name.matches(namePattern);
        txtName.setStyle(isValidName ? "-fx-border-color: blue" : "-fx-border-color: red");
        updateButtonStates();
    }

    @FXML
    public void txtContactChange(KeyEvent keyEvent) {
        String contactNumber = txtContact_number.getText().trim();
        boolean isValidContact = contactNumber.matches(phonePattern);
        txtContact_number.setStyle(isValidContact ? "-fx-border-color: blue" : "-fx-border-color: red");
        updateButtonStates();
    }

    @FXML
    public void txtEmailChange(KeyEvent keyEvent) {
        String email = txtemail.getText().trim();
        boolean isValidEmail = email.matches(emailPattern);
        txtemail.setStyle(isValidEmail ? "-fx-border-color: blue" : "-fx-border-color: red");
        updateButtonStates();
    }

    @FXML
    void searchSupplier(KeyEvent event) {

    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        updateSupplierCount();
    }
}