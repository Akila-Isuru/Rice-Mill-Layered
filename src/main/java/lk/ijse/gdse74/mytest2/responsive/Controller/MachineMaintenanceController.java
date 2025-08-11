package lk.ijse.gdse74.mytest2.responsive.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.MachineMaintenanceBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.MachineMaintenancedto;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MachineMaintenanceController implements Initializable {

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<MachineMaintenancedto, String> colMain_id;
    @FXML private TableColumn<MachineMaintenancedto, Integer> col_cost;
    @FXML private TableColumn<MachineMaintenancedto, String> coldescription;
    @FXML private TableColumn<MachineMaintenancedto, String> colmachine_name;
    @FXML private TableColumn<MachineMaintenancedto, String> colmain_date;
    @FXML private TableView<MachineMaintenancedto> table;
    @FXML private TextField txtcost;
    @FXML private TextField txtdescription;
    @FXML private TextField txtmachine_name;
    @FXML private TextField txtmain_date;
    @FXML private TextField txtmain_id;


    private final MachineMaintenanceBO machineMaintenanceBO = BOFactory.getInstance().getBO(BOTypes.MACHINE_MAINTENANCE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        setupFieldListeners();

        try {
            loadNextId();
            txtmain_date.setText(LocalDate.now().toString());

            loadTable();
            updateButtonStates();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MachineMaintenanceController", e);
        }
    }

    private void setCellValueFactories() {
        colMain_id.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        colmachine_name.setCellValueFactory(new PropertyValueFactory<>("machineName"));
        colmain_date.setCellValueFactory(new PropertyValueFactory<>("maintenanceDate"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void loadTable() {
        try {
            List<MachineMaintenancedto> allMaintenance = machineMaintenanceBO.getAllMachineMaintenance();
            table.setItems(FXCollections.observableArrayList(allMaintenance));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFieldListeners() {
        txtmachine_name.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtmain_date.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtdescription.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtcost.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        boolean isAnyFieldEmpty = txtmachine_name.getText().isEmpty() ||
                txtmain_date.getText().isEmpty() ||
                txtdescription.getText().isEmpty() ||
                txtcost.getText().isEmpty();

        MachineMaintenancedto selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            btnSave.setDisable(isAnyFieldEmpty);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else {
            btnSave.setDisable(true);
            btnUpdate.setDisable(isAnyFieldEmpty);
            btnDelete.setDisable(false);
        }
    }

    private void loadNextId() throws SQLException {
        String nextId = machineMaintenanceBO.getNextMaintenanceId();
        txtmain_id.setText(nextId);
        txtmain_id.setDisable(true);
    }

    private void clearFields() throws SQLException {
        txtmachine_name.clear();
        txtmain_date.clear();
        txtdescription.clear();
        txtcost.clear();

        loadNextId();
        txtmain_date.setText(LocalDate.now().toString());
        loadTable();
        table.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private boolean validateFields(boolean showDialog) {
        if (txtmachine_name.getText().isEmpty() || txtmain_date.getText().isEmpty() ||
                txtdescription.getText().isEmpty() || txtcost.getText().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
            return false;
        }

        try {
            int cost = Integer.parseInt(txtcost.getText());
            if (cost < 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Cost cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Cost must be a valid number.");
            return false;
        }


        return true;
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(alertType.name().replace("_", " "));
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Maintenance Record");
        alert.setContentText("Are you sure you want to delete this record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String maintenanceId = txtmain_id.getText();
            try {
                boolean isDeleted = machineMaintenanceBO.deleteMachineMaintenance(maintenanceId);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed!");
                }
            } catch (NotFoundException | InUseException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong during delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateFields(true)) {
            return;
        }

        int cost = Integer.parseInt(txtcost.getText());

        MachineMaintenancedto dto = new MachineMaintenancedto(
                txtmain_id.getText(),
                txtmachine_name.getText(),
                txtmain_date.getText(),
                txtdescription.getText(),
                cost
        );

        try {
            machineMaintenanceBO.saveMachineMaintenance(dto);
            showAlert(Alert.AlertType.INFORMATION, "Saved Successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something went wrong during save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Maintenance Record");
        alert.setContentText("Are you sure you want to update this record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!validateFields(true)) {
                return;
            }

            int cost = Integer.parseInt(txtcost.getText());

            MachineMaintenancedto dto = new MachineMaintenancedto(
                    txtmain_id.getText(),
                    txtmachine_name.getText(),
                    txtmain_date.getText(),
                    txtdescription.getText(),
                    cost
            );

            try {
                machineMaintenanceBO.updateMachineMaintenance(dto);
                showAlert(Alert.AlertType.INFORMATION, "Updated Successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong during update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void tableColumnOnClicked(MouseEvent mouseEvent) {
        MachineMaintenancedto selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            txtmain_id.setText(selectedItem.getMaintenanceId());
            txtmachine_name.setText(selectedItem.getMachineName());
            txtmain_date.setText(selectedItem.getMaintenanceDate());
            txtdescription.setText(selectedItem.getDescription());
            txtcost.setText(String.valueOf(selectedItem.getCost()));

            updateButtonStates();
        }
    }
}