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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.EmployeeBO; // New import
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Employeedto;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML private TableColumn<Employeedto, String> colEmployeeId;
    @FXML private TableColumn<Employeedto, String> colName;
    @FXML private TableColumn<Employeedto, String> colAddress;
    @FXML private TableColumn<Employeedto, String> colContactNumber;
    @FXML private TableColumn<Employeedto, String> colJobRole;
    @FXML private TableColumn<Employeedto, BigDecimal> colBasicSalary;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;

    @FXML private TableView<Employeedto> tblEmployees;
    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtName;
    @FXML private TextField txtAddress;
    @FXML private TextField txtContactNumber;
    @FXML private TextField txtJobRole;
    @FXML private TextField txtBasicSalary;
    @FXML private TextField txtSearch;
    @FXML private Label lblEmployeeCount;

    // Validation patterns
    private final String namePattern = "^[A-Za-z ]+$";
    private final String phonePattern = "^0\\d{9}$"; // Matches 0xxxxxxxxx (10 digits starting with 0)
    private final String salaryPattern = "^\\d+(\\.\\d{1,2})?$"; // Allows positive numbers with up to 2 decimal places

    private ObservableList<Employeedto> employeeMasterData = FXCollections.observableArrayList();
    private final EmployeeBO employeeBO = BOFactory.getInstance().getBO(BOTypes.EMPLOYEE); // Use BOFactory

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        try {
            loadNextId();
            loadTable();
            setupSearchFilter();
            setupFieldListeners(); // New method for real-time validation and button state
            //updateButtonStates(); // Set initial button states
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage()).show();
            throw new RuntimeException("Failed to initialize EmployeeController", e);
        }
    }

    private void setCellValueFactories() {
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colJobRole.setCellValueFactory(new PropertyValueFactory<>("jobRole"));
        colBasicSalary.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
    }

    private void setupSearchFilter() {
        FilteredList<Employeedto> filteredData = new FilteredList<>(employeeMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return employee.getEmployeeId().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getName().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getAddress().toLowerCase().contains(lowerCaseFilter) ||
                        employee.getJobRole().toLowerCase().contains(lowerCaseFilter);
            });

            SortedList<Employeedto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tblEmployees.comparatorProperty());
            tblEmployees.setItems(sortedData);
            updateEmployeeCount();
        });
    }

    private void updateEmployeeCount() {
        lblEmployeeCount.setText("Employees: " + tblEmployees.getItems().size());
    }

    // Removed the old disableButtons(boolean disable) method.
    // It's replaced by the more granular updateButtonStates().

    private void loadTable() throws SQLException {
        try {
            List<Employeedto> employeedtos = employeeBO.getAllEmployees(); // Use BO
            employeeMasterData.clear();
            employeeMasterData.addAll(employeedtos);
            tblEmployees.setItems(employeeMasterData);
            updateEmployeeCount();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load employee data: " + e.getMessage()).show();
        }
    }

    // New method to set up listeners for all input fields to update button states and styling
    private void setupFieldListeners() {
        txtName.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtAddress.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtContactNumber.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtJobRole.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        txtBasicSalary.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
        tblEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStatesAndStyles());
    }

    // Central method to control button states and apply styling
    private void updateButtonStatesAndStyles() {
        boolean isValidInputForSaveOrUpdate = validateInputFields(false); // Validate without showing alerts yet

        Employeedto selectedEmployee = tblEmployees.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) { // No item selected in table (implies new entry)
            btnSave.setDisable(!isValidInputForSaveOrUpdate); // Enable Save if valid
            btnUpdate.setDisable(true); // Disable Update
            btnDelete.setDisable(true); // Disable Delete
        } else { // An item is selected in the table (implies update or delete)
            btnSave.setDisable(true); // Disable Save
            btnUpdate.setDisable(!isValidInputForSaveOrUpdate); // Enable Update if valid
            btnDelete.setDisable(false); // Enable Delete
        }

        applyValidationStyles();
    }

    private boolean validateInputFields(boolean showDialog) {
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String contactNumber = txtContactNumber.getText().trim();
        String jobRole = txtJobRole.getText().trim();
        String basicSalaryStr = txtBasicSalary.getText().trim();

        if (name.isEmpty() || address.isEmpty() || contactNumber.isEmpty() || jobRole.isEmpty() || basicSalaryStr.isEmpty()) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "All fields are required.").show();
            return false;
        }

        boolean isValidName = name.matches(namePattern);
        boolean isValidContact = contactNumber.matches(phonePattern);
        boolean isValidSalary = basicSalaryStr.matches(salaryPattern);

        if (!isValidName) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Name: Name should contain only letters and spaces.").show();
            return false;
        }
        if (!isValidContact) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Contact Number: Must be 10 digits starting with '0'.").show();
            return false;
        }
        if (!isValidSalary) {
            if (showDialog) new Alert(Alert.AlertType.ERROR, "Invalid Basic Salary: Must be a positive number with up to 2 decimal places.").show();
            return false;
        }

        return true;
    }

    // Applies validation styles without showing alerts, for real-time feedback
    private void applyValidationStyles() {
        txtName.setStyle(txtName.getText().trim().matches(namePattern) ? "-fx-border-color: blue" : "-fx-border-color: red");
        txtContactNumber.setStyle(txtContactNumber.getText().trim().matches(phonePattern) ? "-fx-border-color: blue" : "-fx-border-color: red");
        txtBasicSalary.setStyle(txtBasicSalary.getText().trim().matches(salaryPattern) ? "-fx-border-color: blue" : "-fx-border-color: red");

        // Clear red border if field becomes empty (but required fields will still prevent save/update)
        if (txtName.getText().trim().isEmpty()) txtName.setStyle("");
        if (txtContactNumber.getText().trim().isEmpty()) txtContactNumber.setStyle("");
        if (txtBasicSalary.getText().trim().isEmpty()) txtBasicSalary.setStyle("");
    }


    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (!validateInputFields(true)) { // Show alerts for invalid input
            return;
        }

        try {
            BigDecimal basicSalary = new BigDecimal(txtBasicSalary.getText().trim());
            Employeedto employeedto = new Employeedto(
                    txtEmployeeId.getText(),
                    txtName.getText().trim(),
                    txtAddress.getText().trim(),
                    txtContactNumber.getText().trim(),
                    txtJobRole.getText().trim(),
                    basicSalary
            );
            employeeBO.saveEmployee(employeedto); // Use BO
            new Alert(Alert.AlertType.INFORMATION, "Employee saved successfully").show();
            clearFields();
            loadTable(); // Reload table after save
            loadNextId(); // Load next ID after save
            updateButtonStatesAndStyles(); // Re-evaluate button states
        } catch (DuplicateException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saving employee: " + e.getMessage()).show();
        }
    }

    private void clearFields() throws SQLException {
        txtName.clear();
        txtAddress.clear();
        txtContactNumber.clear();
        txtJobRole.clear();
        txtBasicSalary.clear();
        loadNextId();
        tblEmployees.getSelectionModel().clearSelection(); // Clear table selection
        updateButtonStatesAndStyles(); // Reset button states and styles
    }

    private void loadNextId() throws SQLException {
        try {
            String nextId = employeeBO.getNextId(); // Use BO
            txtEmployeeId.setText(nextId);
            txtEmployeeId.setEditable(false);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading next ID: " + e.getMessage()).show();
            txtEmployeeId.setText("E001"); // Fallback
            txtEmployeeId.setEditable(false);
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        if (!validateInputFields(true)) { // Show alerts for invalid input
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Update Employee");
        alert.setContentText("Are you sure you want to update this employee?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                BigDecimal basicSalary = new BigDecimal(txtBasicSalary.getText().trim());
                Employeedto employeedto = new Employeedto(
                        txtEmployeeId.getText(),
                        txtName.getText().trim(),
                        txtAddress.getText().trim(),
                        txtContactNumber.getText().trim(),
                        txtJobRole.getText().trim(),
                        basicSalary
                );
                employeeBO.updateEmployee(employeedto); // Use BO
                new Alert(Alert.AlertType.INFORMATION, "Employee updated successfully").show();
                clearFields();
                loadTable();
                loadNextId();
                updateButtonStatesAndStyles(); // Re-evaluate button states
            } catch (NotFoundException | DuplicateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error updating employee: " + e.getMessage()).show();
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Employee");
        alert.setContentText("Are you sure you want to delete this employee?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String id = txtEmployeeId.getText();
            try {
                boolean isDeleted = employeeBO.deleteEmployee(id); // Use BO
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION,"Employee deleted successfully").show();
                    clearFields();
                    loadTable();
                    loadNextId();
                    updateButtonStatesAndStyles(); // Re-evaluate button states
                } else {
                    new Alert(Alert.AlertType.INFORMATION,"Employee delete Failed").show();
                }
            } catch (NotFoundException | InUseException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,"An unexpected error occurred during delete: " + e.getMessage()).show();
            }
        }
    }

    public void btnClearOnAction(ActionEvent actionEvent) throws SQLException {
        clearFields();
    }

    @FXML
    void tableColumnOnClicked(MouseEvent event) {
        Employeedto employeedto = tblEmployees.getSelectionModel().getSelectedItem();
        if(employeedto != null) {
            txtEmployeeId.setText(employeedto.getEmployeeId());
            txtName.setText(employeedto.getName());
            txtAddress.setText(employeedto.getAddress());
            txtContactNumber.setText(employeedto.getContactNumber());
            txtJobRole.setText(employeedto.getJobRole());
            txtBasicSalary.setText(employeedto.getBasicSalary().toPlainString());
            updateButtonStatesAndStyles(); // Update button states based on selection
        }
    }

    // These methods now just call the central updateButtonStatesAndStyles()
    public void txtNameChange(KeyEvent keyEvent) { updateButtonStatesAndStyles(); }
    public void txtContactChange(KeyEvent keyEvent) { updateButtonStatesAndStyles(); }
    public void txtBasicSalaryChange(KeyEvent keyEvent) { updateButtonStatesAndStyles(); }
    public void txtAddressChange(KeyEvent keyEvent) { updateButtonStatesAndStyles(); } // Added for address
    public void txtJobRoleChange(KeyEvent keyEvent) { updateButtonStatesAndStyles(); } // Added for jobRole

    @FXML
    void searchEmployee(KeyEvent event) {
        // The search logic is handled by setupSearchFilter and its listener
    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        tblEmployees.setItems(employeeMasterData);
        updateEmployeeCount();
    }
}