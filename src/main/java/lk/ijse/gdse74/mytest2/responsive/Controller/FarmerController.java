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
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.FarmerDTO;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FarmerController implements Initializable {

    @FXML private TableColumn<FarmerDTO, String> coladdress;
    @FXML private TableColumn<FarmerDTO, String> colcontatcnumber;
    @FXML private TableColumn<FarmerDTO, String> colid;
    @FXML private TableColumn<FarmerDTO, String> colname;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableView<FarmerDTO> tfarmersTable;
    @FXML private TextField txtContact_number;
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtaddress;
    @FXML private TextField txtSearch;
    @FXML private Label lblFarmerCount;

    private final String namePattern = "^[A-Za-z ]+$";
    private final String phonePattern = "^(?:0|\\+94|0094)?(?:07\\d{8})$";

    private ObservableList<FarmerDTO> farmerMasterData = FXCollections.observableArrayList();
    private final FarmerBO farmerBO = BOFactory.getInstance().getBO(BOTypes.FARMER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadTable();
            loadNextId();
            disableButtons(true);
            setupSearchFilter();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to initialize: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        FilteredList<FarmerDTO> filteredData = new FilteredList<>(farmerMasterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(farmer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return farmer.getFarmerId().toLowerCase().contains(lowerCaseFilter) ||
                        farmer.getName().toLowerCase().contains(lowerCaseFilter) ||
                        farmer.getContactNumber().toLowerCase().contains(lowerCaseFilter) ||
                        farmer.getAddress().toLowerCase().contains(lowerCaseFilter);
            });

            SortedList<FarmerDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tfarmersTable.comparatorProperty());
            tfarmersTable.setItems(sortedData);
            updateFarmerCount();
        });
    }

    private void updateFarmerCount() {
        lblFarmerCount.setText("Farmers: " + tfarmersTable.getItems().size());
    }

    private void disableButtons(boolean disable) {
        btnUpdate.setDisable(disable);
        btnDelete.setDisable(disable);
        btnSave.setDisable(!disable);
    }

    private void loadTable() throws SQLException {
        colid.setCellValueFactory(new PropertyValueFactory<>("farmerId"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colcontatcnumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));

        try {
            List<FarmerDTO> farmers = farmerBO.getAllFarmers();
            farmerMasterData = FXCollections.observableArrayList(farmers);
            tfarmersTable.setItems(farmerMasterData);
            updateFarmerCount();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load farmer data: " + e.getMessage()).show();
            throw e;
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        String name = txtName.getText();
        String contactNumber = txtContact_number.getText();
        String address = txtaddress.getText();

        boolean isValidName = name.matches(namePattern);
        boolean isValidContact = contactNumber.matches(phonePattern);

        if (isValidName && isValidContact) {
            FarmerDTO farmerDTO = new FarmerDTO(
                    txtId.getText(),
                    name,
                    contactNumber,
                    address
            );

            try {
                farmerBO.saveFarmer(farmerDTO);
                new Alert(Alert.AlertType.INFORMATION, "Farmer saved successfully").show();
                clearFields();
            } catch (DuplicateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save farmer: " + e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Please fill in valid details (Name, Contact Number)").show();
        }
    }

    private void clearFields() throws SQLException {
        txtName.clear();
        txtContact_number.clear();
        txtaddress.clear();
        loadNextId();
        disableButtons(true);
        loadTable();
    }

    private void loadNextId() throws SQLException {
        try {
            String nextId = farmerBO.getNextId();
            txtId.setText(nextId);
            txtId.setEditable(false);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading next ID: " + e.getMessage()).show();
            throw e;
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Farmer");
        alert.setContentText("Are you sure you want to update this farmer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = txtName.getText();
            String contactNumber = txtContact_number.getText();
            String address = txtaddress.getText();

            boolean isValidName = name.matches(namePattern);
            boolean isValidContact = contactNumber.matches(phonePattern);

            if (isValidName && isValidContact) {
                FarmerDTO farmerDTO = new FarmerDTO(
                        txtId.getText(),
                        name,
                        contactNumber,
                        address
                );

                try {
                    farmerBO.updateFarmer(farmerDTO);
                    new Alert(Alert.AlertType.INFORMATION, "Farmer updated successfully").show();
                    clearFields();
                } catch (NotFoundException | DuplicateException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to update farmer: " + e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please fill in valid details (Name, Contact Number)").show();
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Farmer");
        alert.setContentText("Are you sure you want to delete this farmer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String id = txtId.getText();
            try {
                boolean isDeleted = farmerBO.deleteFarmer(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Farmer deleted successfully").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete farmer").show();
                }
            } catch (InUseException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to delete farmer: " + e.getMessage()).show();
            }
        }
    }

    public void btnClearOnAction(ActionEvent actionEvent) throws SQLException {
        clearFields();
    }

    @FXML
    void tableColumnOnClicked(MouseEvent event) {
        FarmerDTO farmerDTO = tfarmersTable.getSelectionModel().getSelectedItem();
        if (farmerDTO != null) {
            txtId.setText(farmerDTO.getFarmerId());
            txtName.setText(farmerDTO.getName());
            txtContact_number.setText(farmerDTO.getContactNumber());
            txtaddress.setText(farmerDTO.getAddress());

            disableButtons(false);
            btnSave.setDisable(true);
        }
    }

    public void txtNamehange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValidName = name.matches(namePattern);
        txtName.setStyle(isValidName ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    public void txtContactChange(KeyEvent keyEvent) {
        String contactNumber = txtContact_number.getText();
        boolean isValidContact = contactNumber.matches(phonePattern);
        txtContact_number.setStyle(isValidContact ? "-fx-border-color: blue" : "-fx-border-color: red");
    }

    @FXML
    void searchFarmer(KeyEvent event) {

    }

    @FXML
    void clearSearch(ActionEvent event) {
        txtSearch.clear();
        tfarmersTable.setItems(farmerMasterData);
        updateFarmerCount();
    }
}