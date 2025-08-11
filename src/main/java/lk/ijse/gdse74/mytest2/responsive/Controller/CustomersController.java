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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.CustomerBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Customersdto;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    @FXML private TableColumn<Customersdto, String> coladdress;
    @FXML private TableColumn<Customersdto,String> colcontatcnumber;
    @FXML private TableColumn<Customersdto, String> colemail;
    @FXML private TableColumn<Customersdto, String> colid;
    @FXML private TableColumn<Customersdto, String> colname;
    @FXML private TableView<Customersdto> table;
    @FXML private TextField txtContact_number;
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtaddress;
    @FXML private TextField txtemail;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private TextField txtSearch;
    @FXML private Label lblCustomerCount;

    private final String namePattern = "^[A-Za-z ]+$";
    private final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.lk$";
    private final String phonePattern = "^(?:0|\\+94|0094)?(?:07\\d{8})$";

    private ObservableList<Customersdto> customerMasterData = FXCollections.observableArrayList();
    private final CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        try {
            loadTableData();
            loadNextId();
            setupSearchFilter();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load data").show();
        }
    }

    private void setupSearchFilter() {
        FilteredList<Customersdto> filteredData = new FilteredList<>(customerMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return customer.getCustomerId().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getName().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getAddress().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getEmail().toLowerCase().contains(lowerCaseFilter);
            });

            SortedList<Customersdto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);
            updateCustomerCount();
        });
    }

    private void updateCustomerCount() {
        lblCustomerCount.setText("Customers: " + table.getItems().size());
    }

    private void loadTableData() throws SQLException {
        colid.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colcontatcnumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));

        List<Customersdto> customers = customerBO.getAllCustomers();
        customerMasterData = FXCollections.observableArrayList(customers);
        table.setItems(customerMasterData);
        updateCustomerCount();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        String name = txtName.getText();
        String contactNumber = txtContact_number.getText();
        String email = txtemail.getText();
        String address = txtaddress.getText();

        boolean isValidName = name.matches(namePattern);
        boolean isValidContact = contactNumber.matches(phonePattern);
        boolean isValidEmail = email.matches(emailPattern);

        if (isValidName && isValidContact && isValidEmail) {
            Customersdto customersdto = new Customersdto(
                    txtId.getText(),
                    name,
                    contactNumber,
                    address,
                    email
            );

            try {
                customerBO.saveCustomer(customersdto);
                new Alert(Alert.AlertType.INFORMATION, "Customer saved successfully").show();
                clearFields();
            } catch (DuplicateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save customer").show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please fill in valid details").show();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Customer");
        alert.setContentText("Are you sure you want to update this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = txtName.getText();
            String contactNumber = txtContact_number.getText();
            String email = txtemail.getText();
            String address = txtaddress.getText();

            boolean isValidName = name.matches(namePattern);
            boolean isValidContact = contactNumber.matches(phonePattern);
            boolean isValidEmail = email.matches(emailPattern);

            if (isValidName && isValidContact && isValidEmail) {
                Customersdto customersdto = new Customersdto(
                        txtId.getText(),
                        name,
                        contactNumber,
                        address,
                        email
                );

                try {
                    customerBO.updateCustomer(customersdto);
                    new Alert(Alert.AlertType.INFORMATION, "Customer updated successfully").show();
                    clearFields();
                } catch (NotFoundException | DuplicateException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to update customer").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please fill in valid details").show();
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete this customer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String id = txtId.getText();
            try {
                boolean isDeleted = customerBO.deleteCustomer(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete customer").show();
                }
            } catch (InUseException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to delete customer").show();
            }
        }
    }

    public void btnClearOnAction(ActionEvent actionEvent) throws Exception {
        clearFields();
    }

    private void clearFields() throws Exception {
        txtName.clear();
        txtContact_number.clear();
        txtaddress.clear();
        txtemail.clear();
        loadNextId();
        loadTableData();
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(false);
    }

    private void loadNextId() throws SQLException {
        String nextId = customerBO.getNextId();
        txtId.setText(nextId);
        txtId.setEditable(false);
    }

    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        Customersdto customersdto = table.getSelectionModel().getSelectedItem();
        if (customersdto != null) {
            txtId.setText(customersdto.getCustomerId());
            txtName.setText(customersdto.getName());
            txtContact_number.setText(customersdto.getContactNumber());
            txtaddress.setText(customersdto.getAddress());
            txtemail.setText(customersdto.getEmail());

            btnSave.setDisable(true);
            btnUpdate.setDisable(false);
            btnDelete.setDisable(false);
        }
    }

    public void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValidName = name.matches(namePattern);
        txtName.setStyle(isValidName ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    public void txtContactChange(KeyEvent keyEvent) {
        String contactNumber = txtContact_number.getText();
        boolean isValidContact = contactNumber.matches(phonePattern);
        txtContact_number.setStyle(isValidContact ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    public void txtEmailChange(KeyEvent keyEvent) {
        String email = txtemail.getText();
        boolean isValidEmail = email.matches(emailPattern);
        txtemail.setStyle(isValidEmail ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    @FXML
    void searchCustomer(KeyEvent event) {

    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        table.setItems(customerMasterData);
        updateCustomerCount();
    }
}