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
//import lk.ijse.gdse74.mytest2.responsive.bo.custom.MachineMaintenanceBO; // Corrected import
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
//import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
//import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto; // Using the existing DTO name
//
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.List; // Use List instead of ArrayList
//import java.util.Optional;
//import java.util.ResourceBundle;
//
//public class MachineMaintenanceController implements Initializable {
//
//    @FXML private Button btnClear;
//    @FXML private Button btnDelete;
//    @FXML private Button btnSave;
//    @FXML private Button btnUpdate;
//    @FXML private TableColumn<MachineMaintenancedto, String> colMain_id;
//    @FXML private TableColumn<MachineMaintenancedto, Integer> col_cost;
//    @FXML private TableColumn<MachineMaintenancedto, String> coldescription;
//    @FXML private TableColumn<MachineMaintenancedto, String> colmachine_name;
//    @FXML private TableColumn<MachineMaintenancedto, String> colmain_date;
//    @FXML private TableView<MachineMaintenancedto> table;
//    @FXML private TextField txtcost;
//    @FXML private TextField txtdescription;
//    @FXML private TextField txtmachine_name;
//    @FXML private TextField txtmain_date;
//    @FXML private TextField txtmain_id;
//
//    // Use BOFactory to get BO instance
//    private final MachineMaintenanceBO machineMaintenanceBO = BOFactory.getInstance().getBO(BOTypes.MACHINE_MAINTENANCE);
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        setCellValueFactories();
//        loadTable();
//        // The original disableButtons() would re-enable them.
//        // We want them disabled on init if no item is selected for update/delete.
//        btnUpdate.setDisable(true);
//        btnDelete.setDisable(true);
//        btnSave.setDisable(false); // Enable save initially
//        loadNextId();
//        setupFieldListeners();
//    }
//
//    private void setCellValueFactories() {
//        colMain_id.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
//        colmachine_name.setCellValueFactory(new PropertyValueFactory<>("machineName"));
//        colmain_date.setCellValueFactory(new PropertyValueFactory<>("maintenanceDate"));
//        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
//        col_cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
//    }
//
//    private void loadTable() {
//        try {
//            List<MachineMaintenancedto> allMaintenance = machineMaintenanceBO.getAllMachineMaintenance(); // Use BO
//            table.setItems(FXCollections.observableArrayList(allMaintenance));
//        } catch (SQLException e) { // Catch SQLException from BO layer
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Failed to load data: " + e.getMessage()).show();
//        }
//    }
//
//    private void setupFieldListeners() {
//        // Disable save button if any field is empty
//        txtmachine_name.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
//        txtmain_date.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
//        txtdescription.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
//        txtcost.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
//    }
//
//    private void updateSaveButtonState() {
//        boolean anyFieldEmpty = txtmachine_name.getText().isEmpty() ||
//                txtmain_date.getText().isEmpty() ||
//                txtdescription.getText().isEmpty() ||
//                txtcost.getText().isEmpty();
//
//        // Only affect save button if an item is NOT selected for update/delete
//        if (table.getSelectionModel().getSelectedItem() == null) {
//            btnSave.setDisable(anyFieldEmpty);
//        }
//    }
//
//    private void loadNextId() {
//        try {
//            String nextId = machineMaintenanceBO.getNextId(); // Use BO
//            txtmain_id.setText(nextId);
//            txtmain_id.setDisable(true); // Keep it disabled as it's auto-generated
//        } catch (SQLException e) { // Catch SQLException from BO layer
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Failed to generate next ID: " + e.getMessage()).show();
//        }
//    }
//
//    private void clearFields() {
//        txtmachine_name.clear();
//        txtmain_date.clear();
//        txtdescription.clear();
//        txtcost.clear();
//
//        loadNextId(); // Generate new ID
//        loadTable(); // Refresh table
//        btnUpdate.setDisable(true); // After clear, disable update/delete
//        btnDelete.setDisable(true);
//        btnSave.setDisable(false); // Enable save
//        table.getSelectionModel().clearSelection(); // Clear table selection
//    }
//
//    @FXML
//    void btnClearOnAction(ActionEvent event) {
//        clearFields();
//    }
//
//    @FXML
//    void btnDeleteOnAction(ActionEvent event) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation Dialog");
//        alert.setHeaderText("Delete Maintenance Record");
//        alert.setContentText("Are you sure you want to delete this record?");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                boolean isDeleted = machineMaintenanceBO.deleteMachineMaintenance(txtmain_id.getText()); // Use BO
//
//                if (isDeleted) {
//                    new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully!").show();
//                    clearFields();
//                } else {
//                    new Alert(Alert.AlertType.ERROR, "Delete Failed!").show();
//                }
//            } catch (NotFoundException | InUseException e) {
//                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
//            } catch (Exception e) { // Catch generic exceptions from BO
//                new Alert(Alert.AlertType.ERROR, "Something went wrong during delete: " + e.getMessage()).show();
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @FXML
//    void btnSaveOnAction(ActionEvent event) {
//        try {
//            if (!validateFields()) {
//                return;
//            }
//
//            int cost = Integer.parseInt(txtcost.getText());
//
//            MachineMaintenancedto dto = new MachineMaintenancedto(
//                    txtmain_id.getText(),
//                    txtmachine_name.getText(),
//                    txtmain_date.getText(),
//                    txtdescription.getText(),
//                    cost
//            );
//
//            machineMaintenanceBO.saveMachineMaintenance(dto); // Use BO
//
//            new Alert(Alert.AlertType.INFORMATION, "Saved Successfully!").show();
//            clearFields();
//        } catch (NumberFormatException e) {
//            new Alert(Alert.AlertType.ERROR, "Invalid cost value. Please enter a number.").show();
//        } catch (DuplicateException e) { // Catch duplicate exception from BO
//            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
//        } catch (Exception e) { // Catch generic exceptions from BO
//            new Alert(Alert.AlertType.ERROR, "Something went wrong during save: " + e.getMessage()).show();
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    void btnUpdateOnAction(ActionEvent event) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmation Dialog");
//        alert.setHeaderText("Update Maintenance Record");
//        alert.setContentText("Are you sure you want to update this record?");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            try {
//                if (!validateFields()) {
//                    return;
//                }
//
//                int cost = Integer.parseInt(txtcost.getText());
//
//                MachineMaintenancedto dto = new MachineMaintenancedto(
//                        txtmain_id.getText(),
//                        txtmachine_name.getText(),
//                        txtmain_date.getText(),
//                        txtdescription.getText(),
//                        cost
//                );
//
//                machineMaintenanceBO.updateMachineMaintenance(dto); // Use BO
//
//                new Alert(Alert.AlertType.INFORMATION, "Updated Successfully!").show();
//                clearFields();
//            } catch (NumberFormatException e) {
//                new Alert(Alert.AlertType.ERROR, "Invalid cost value. Please enter a number.").show();
//            } catch (NotFoundException e) { // Catch NotFounException from BO
//                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
//            } catch (Exception e) { // Catch generic exceptions from BO
//                new Alert(Alert.AlertType.ERROR, "Something went wrong during update: " + e.getMessage()).show();
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @FXML
//    void tableColumnOnClicked(MouseEvent mouseEvent) {
//        MachineMaintenancedto selectedItem = table.getSelectionModel().getSelectedItem();
//        if (selectedItem != null) {
//            txtmain_id.setText(selectedItem.getMaintenanceId());
//            txtmachine_name.setText(selectedItem.getMachineName());
//            txtmain_date.setText(selectedItem.getMaintenanceDate());
//            txtdescription.setText(selectedItem.getDescription());
//            txtcost.setText(String.valueOf(selectedItem.getCost()));
//
//            btnSave.setDisable(true); // Disable save when an item is selected
//            btnUpdate.setDisable(false); // Enable update
//            btnDelete.setDisable(false); // Enable delete
//        }
//    }
//
//    private boolean validateFields() {
//        if (txtmachine_name.getText().isEmpty() || txtmain_date.getText().isEmpty() ||
//                txtdescription.getText().isEmpty() || txtcost.getText().isEmpty()) {
//            new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
//            return false;
//        }
//
//        try {
//            Integer.parseInt(txtcost.getText());
//        } catch (NumberFormatException e) {
//            new Alert(Alert.AlertType.ERROR, "Cost must be a number").show();
//            return false;
//        }
//
//        return true;
//    }
//}