package lk.ijse.gdse74.mytest2.responsive.Controller;

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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.FinishedProductBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.MillingProcessBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.FinishedProductdto;
import lk.ijse.gdse74.mytest2.responsive.dto.MillingProcessdto;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FinishedProductController implements Initializable {

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<FinishedProductdto, String> colMilling_id;
    @FXML private TableColumn<FinishedProductdto, Double> colPackaging_size;
    @FXML private TableColumn<FinishedProductdto, Integer> colPricePer_bag;
    @FXML private TableColumn<FinishedProductdto, String> colProduct_id;
    @FXML private TableColumn<FinishedProductdto, String> colProduct_type;
    @FXML private TableColumn<FinishedProductdto, Integer> colTotal_quantity;
    @FXML private TableView<FinishedProductdto> table;
    @FXML private TextField txtProduct_id;
    @FXML private TextField txtPricePer_bag;
    @FXML private TextField txtQuantity_bags;

    @FXML private ComboBox<String> cmbMilling_id;
    @FXML private ComboBox<String> cmbProduct_type;
    @FXML private ComboBox<Double> cmbPackaging_size;

    // BO instances using BOFactory
    private final FinishedProductBO finishedProductBO = BOFactory.getInstance().getBO(BOTypes.FINISHED_PRODUCT);
    private final MillingProcessBO millingProcessBO = BOFactory.getInstance().getBO(BOTypes.MILLING_PROCESS);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        setupFieldListeners(); // Set up listeners before loading data

        loadNextId();
        loadTable();
        loadMillingIds();
        loadProductTypes();
        loadPackagingSizes();
        updateButtonStates(); // Set initial button states
    }

    private void setCellValueFactories() {
        colProduct_id.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colMilling_id.setCellValueFactory(new PropertyValueFactory<>("millingId"));
        colProduct_type.setCellValueFactory(new PropertyValueFactory<>("productType"));
        colPackaging_size.setCellValueFactory(new PropertyValueFactory<>("packageSize"));
        colTotal_quantity.setCellValueFactory(new PropertyValueFactory<>("quantityBags"));
        colPricePer_bag.setCellValueFactory(new PropertyValueFactory<>("pricePerBag"));
    }

    private void loadTable() {
        try {
            List<FinishedProductdto> allProducts = finishedProductBO.getAllFinishedProducts();
            table.setItems(FXCollections.observableArrayList(allProducts));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNextId() {
        try {
            String nextId = finishedProductBO.getNextFinishedProductId();
            txtProduct_id.setText(nextId);
            txtProduct_id.setDisable(true); // Should always be disabled
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to generate next ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMillingIds() {
        try {
            List<MillingProcessdto> allMillingProcesses = millingProcessBO.getAllMillingProcesses();
            ObservableList<String> millingIds = FXCollections.observableArrayList();
            for (MillingProcessdto dto : allMillingProcesses) {
                millingIds.add(dto.getMillingId());
            }
            cmbMilling_id.setItems(millingIds);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load Milling IDs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProductTypes() {
        ObservableList<String> riceTypes = FXCollections.observableArrayList(
                "Keeri Samba",
                "Samba",
                "Nadu",
                "Red Raw Rice (Rath Hal)",
                "Basmati"
        );
        cmbProduct_type.setItems(riceTypes);
    }

    private void loadPackagingSizes() {
        ObservableList<Double> sizes = FXCollections.observableArrayList(
                1.0,
                5.0,
                10.0,
                25.0
        );
        cmbPackaging_size.setItems(sizes);
    }

    private void setupFieldListeners() {
        cmbMilling_id.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        cmbProduct_type.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        cmbPackaging_size.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtPricePer_bag.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txtQuantity_bags.textProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        boolean isValidInput = validateFields(false); // Validate without showing dialogs
        FinishedProductdto selectedItem = table.getSelectionModel().getSelectedItem();

        if (selectedItem == null) { // No item selected (new record mode)
            btnSave.setDisable(!isValidInput);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } else { // Item selected (edit/delete mode)
            btnSave.setDisable(true);
            btnUpdate.setDisable(!isValidInput);
            btnDelete.setDisable(false);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Product Record");
        alert.setContentText("Are you sure you want to delete this product?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = finishedProductBO.deleteFinishedProduct(txtProduct_id.getText());

                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted Successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed! (Product not found or other issue)");
                }
            } catch (InUseException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong! " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateFields(true)) { // Validate with dialogs
            return;
        }

        try {
            FinishedProductdto dto = new FinishedProductdto(
                    txtProduct_id.getText(),
                    cmbMilling_id.getValue(),
                    cmbProduct_type.getValue(),
                    cmbPackaging_size.getValue(),
                    Integer.parseInt(txtQuantity_bags.getText()),
                    Integer.parseInt(txtPricePer_bag.getText())
            );

            finishedProductBO.saveFinishedProduct(dto);

            showAlert(Alert.AlertType.INFORMATION, "Saved Successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid numeric value in quantity or price. Please enter whole numbers.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something went wrong! " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Product Record");
        alert.setContentText("Are you sure you want to update this product?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (!validateFields(true)) { // Validate with dialogs
                    return;
                }

                FinishedProductdto dto = new FinishedProductdto(
                        txtProduct_id.getText(),
                        cmbMilling_id.getValue(),
                        cmbProduct_type.getValue(),
                        cmbPackaging_size.getValue(),
                        Integer.parseInt(txtQuantity_bags.getText()),
                        Integer.parseInt(txtPricePer_bag.getText())
                );

                finishedProductBO.updateFinishedProduct(dto);

                showAlert(Alert.AlertType.INFORMATION, "Updated Successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid numeric value in quantity or price. Please enter whole numbers.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong! " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void tableColumnOnClicked(MouseEvent mouseEvent) {
        FinishedProductdto selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            txtProduct_id.setText(selectedItem.getProductId());
            cmbMilling_id.setValue(selectedItem.getMillingId());
            cmbProduct_type.setValue(selectedItem.getProductType());
            cmbPackaging_size.setValue(selectedItem.getPackageSize());
            txtQuantity_bags.setText(String.valueOf(selectedItem.getQuantityBags()));
            txtPricePer_bag.setText(String.valueOf(selectedItem.getPricePerBag()));

            updateButtonStates(); // Update button states based on selection
        }
    }

    private boolean validateFields(boolean showDialog) {
        if (cmbMilling_id.getValue() == null || cmbMilling_id.getValue().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Milling ID.");
            return false;
        }
        if (cmbProduct_type.getValue() == null || cmbProduct_type.getValue().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Product Type.");
            return false;
        }
        if (cmbPackaging_size.getValue() == null) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Packaging Size.");
            return false;
        }
        if (txtQuantity_bags.getText().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Total Quantity (Bags) cannot be empty.");
            return false;
        }
        if (txtPricePer_bag.getText().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Price per Bag cannot be empty.");
            return false;
        }

        try {
            int quantityBags = Integer.parseInt(txtQuantity_bags.getText());
            int pricePerBag = Integer.parseInt(txtPricePer_bag.getText());
            if (quantityBags <= 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Quantity (Bags) must be a positive whole number.");
                return false;
            }
            if (pricePerBag <= 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Price per Bag must be a positive whole number.");
                return false;
            }
        } catch (NumberFormatException e) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Quantity and Price per Bag must contain valid whole numbers.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        cmbMilling_id.getSelectionModel().clearSelection();
        cmbProduct_type.getSelectionModel().clearSelection();
        cmbPackaging_size.getSelectionModel().clearSelection();
        txtPricePer_bag.clear();
        txtQuantity_bags.clear();

        loadNextId();
        loadTable();
        // No need to reload IDs/Types/Sizes unless their source data changes,
        // but for simplicity/consistency with your original, keeping them here.
        loadMillingIds();
        loadProductTypes();
        loadPackagingSizes();
        table.getSelectionModel().clearSelection(); // Clear selection after clearing fields
        updateButtonStates(); // Reset button states
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }
}