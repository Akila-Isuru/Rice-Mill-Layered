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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.SupplierBO; // Corrected import
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Suppliersdto; // Using the existing DTO name

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
    private final String phonePattern = "^0\\d{9}$"; // Matches 0xxxxxxxxx (10 digits starting with 0)

    private ObservableList<Suppliersdto> supplierMasterData = FXCollections.observableArrayList();
    private final SupplierBO supplierBO = BOFactory.getInstance().getBO(BOTypes.SUPPLIER); // Use BOFactory

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadTable();
            disableButtons(true); // Initially disable update/delete
            loadNextId();
            setupSearchFilter();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage()).show();
            throw new RuntimeException(e);
        }
    }

    private void loadTable() {
        colid.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colcontatcnumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));

        try {
            List<Suppliersdto> suppliersdtos = supplierBO.getAllSuppliers(); // Use BO
            supplierMasterData = FXCollections.observableArrayList(suppliersdtos);
            tSuppliersTable.setItems(supplierMasterData);
            updateSupplierCount();
        } catch (SQLException e) { // Catch SQLException from BO layer
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
                        supplier.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
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
            String nextId = supplierBO.getNextId(); // Use BO
            txtId.setText(nextId);
            txtId.setEditable(false);
            System.out.println("DEBUG: Next ID retrieved: " + nextId);
        } catch (SQLException e) {
            System.err.println("ERROR in loadNextId: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading next ID: " + e.getMessage()).show();
            txtId.setText("S001"); // Fallback
            txtId.setEditable(false);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
        disableButtons(true);
        loadNextId();
        tSuppliersTable.getSelectionModel().clearSelection(); // Clear table selection
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
                boolean isDelete = supplierBO.deleteSupplier(id); // Use BO
                if (isDelete) {
                    new Alert(Alert.AlertType.INFORMATION,"Supplier deleted successfully").show();
                    clearFields();
                    loadTable();
                    disableButtons(true);
                    loadNextId();
                } else {
                    new Alert(Alert.AlertType.ERROR,"Failed to delete supplier").show();
                }
            } catch (NotFoundException | InUseException e) { // Catch specific BO exceptions
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,"Error deleting supplier: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Update Supplier");
        alert.setContentText("Are you sure you want to update this supplier?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = txtName.getText();
            String contactNumber = txtContact_number.getText();
            String email = txtemail.getText();

            boolean isValidName = name.matches(namePattern);
            boolean isValidContact = contactNumber.matches(phonePattern);
            boolean isValidEmail = email.matches(emailPattern);

            if(!isValidName || !isValidContact || !isValidEmail) {
                new Alert(Alert.AlertType.ERROR, "Invalid data. Please check fields (Name, Contact, Email)").show();
                return;
            }

            Suppliersdto suppliersdto = new Suppliersdto(
                    txtId.getText(),
                    name,
                    contactNumber,
                    txtaddress.getText(),
                    email
            );

            try {
                supplierBO.updateSupplier(suppliersdto); // Use BO
                new Alert(Alert.AlertType.INFORMATION, "Supplier updated successfully").show();
                clearFields();
                loadTable();
                disableButtons(true);
                loadNextId();
            } catch (NotFoundException | DuplicateException e) { // Catch specific BO exceptions
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error updating supplier: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    public void btnSaveOnAction(ActionEvent event) {
        String name = txtName.getText();
        String contactNumber = txtContact_number.getText();
        String email = txtemail.getText();

        boolean isValidName = name.matches(namePattern);
        boolean isValidContact = contactNumber.matches(phonePattern);
        boolean isValidEmail = email.matches(emailPattern);

        if(!isValidName || !isValidContact || !isValidEmail) {
            new Alert(Alert.AlertType.ERROR, "Invalid data. Please check fields (Name, Contact, Email)").show();
            return;
        }

        Suppliersdto suppliersdto = new Suppliersdto(
                txtId.getText(),
                name,
                contactNumber,
                txtaddress.getText(),
                email
        );

        try {
            supplierBO.saveSupplier(suppliersdto); // Use BO
            new Alert(Alert.AlertType.INFORMATION, "Supplier saved successfully").show();
            clearFields();
            loadTable();
            loadNextId();
        } catch (DuplicateException e) { // Catch specific BO exception
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saving supplier: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtContact_number.clear();
        txtaddress.clear();
        txtemail.clear();
        // Reset styles
        txtName.setStyle("");
        txtContact_number.setStyle("");
        txtemail.setStyle("");
    }

    private void disableButtons(boolean disable) {
        btnUpdate.setDisable(disable);
        btnDelete.setDisable(disable);
        btnSave.setDisable(!disable); // If update/delete are disabled, save is enabled, and vice-versa
    }

    @FXML
    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        Suppliersdto suppliersdto = tSuppliersTable.getSelectionModel().getSelectedItem();
        if (suppliersdto != null) {
            txtId.setText(suppliersdto.getSupplierId());
            txtName.setText(suppliersdto.getName());
            txtContact_number.setText(suppliersdto.getContactNumber());
            txtaddress.setText(suppliersdto.getAddress());
            txtemail.setText(suppliersdto.getEmail());
            disableButtons(false); // Enable update and delete
            btnSave.setDisable(true); // Disable save
        }
    }

    @FXML
    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValidName = name.matches(namePattern);
        txtName.setStyle(isValidName ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    @FXML
    public void txtContactChange(KeyEvent keyEvent) {
        String contactNumber = txtContact_number.getText();
        boolean isValidContact = contactNumber.matches(phonePattern);
        txtContact_number.setStyle(isValidContact ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    @FXML
    public void txtEmailChange(KeyEvent keyEvent) {
        String email = txtemail.getText();
        boolean isValidEmail = email.matches(emailPattern);
        txtemail.setStyle(isValidEmail ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    @FXML
    void searchSupplier(KeyEvent event) {
        // Handled by the setupSearchFilter method
    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        tSuppliersTable.setItems(supplierMasterData);
        updateSupplierCount();
    }
}