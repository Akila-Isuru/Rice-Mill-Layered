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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.InventoryBO;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.DuplicateException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.InUseException;
import lk.ijse.gdse74.mytest2.responsive.bo.exception.NotFoundException;
import lk.ijse.gdse74.mytest2.responsive.dto.Inventorydto;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int CRITICAL_STOCK_THRESHOLD = 5;

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<Inventorydto, Integer> colCurrentStockBags;
    @FXML private TableColumn<Inventorydto, String> colInventory_id;
    @FXML private TableColumn<Inventorydto, Date> colLastupdated;
    @FXML private TableColumn<Inventorydto, String> colProduct_id;
    @FXML private TableView<Inventorydto> table;
    @FXML private TextField txtId;
    @FXML private TextField txtLatUpdated;
    @FXML private ComboBox<String> cmbProductId;
    @FXML private TextField txt_CurrentStock;
    @FXML private Label lblStockStatus;

    // BO instance using BOFactory
    private final InventoryBO inventoryBO = BOFactory.getInstance().getBO(BOTypes.INVENTORY);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        setupFieldListeners(); // Set up listeners first

        try {
            loadNextId();
            txtLatUpdated.setText(LocalDate.now().toString());
            txtLatUpdated.setEditable(false);
            loadProductIds(); // Load product IDs from FinishedProductBO
            loadTable(); // Load table data
            updateButtonStates(); // Set initial button states
            lblStockStatus.setVisible(false); // Hide status label initially
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize InventoryController", e);
        }
    }

    private void setCellValueFactories() {
        colInventory_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProduct_id.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colCurrentStockBags.setCellValueFactory(new PropertyValueFactory<>("currentStockBags"));
        colLastupdated.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));
    }

    private void loadTable() {
        try {
            List<Inventorydto> inventorydtos = inventoryBO.getAllInventoryItems();
            if (inventorydtos != null) {
                ObservableList<Inventorydto> inventory = FXCollections.observableArrayList(inventorydtos);
                table.setItems(inventory);

                // Apply row styling for stock levels
                table.setRowFactory(tv -> new TableRow<Inventorydto>() {
                    @Override
                    protected void updateItem(Inventorydto item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(""); // Reset style
                        if (item == null || empty) {
                            // No item, no style
                        } else {
                            if (item.getCurrentStockBags() < CRITICAL_STOCK_THRESHOLD) {
                                setStyle("-fx-background-color: #ffdddd; -fx-font-weight: bold;"); // Light red
                            } else if (item.getCurrentStockBags() < LOW_STOCK_THRESHOLD) {
                                setStyle("-fx-background-color: #fff3cd;"); // Light yellow
                            }
                        }
                    }
                });

                checkCriticalStock(inventorydtos);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No inventory data found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load inventory data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNextId() throws SQLException {
        String nextId = inventoryBO.getNextInventoryId();
        txtId.setText(nextId);
        txtId.setDisable(true); // Always disabled as it's auto-generated
    }

    private void loadProductIds() throws SQLException {
        // Now getting product IDs directly from InventoryBO (which calls FinishedProductDAO)
        List<String> productIdList = inventoryBO.getAllFinishedProductIdsForInventory();
        ObservableList<String> productIds = FXCollections.observableArrayList(productIdList);
        cmbProductId.setItems(productIds);
    }

    private void setupFieldListeners() {
        cmbProductId.valueProperty().addListener((observable, oldValue, newValue) -> updateButtonStates());
        txt_CurrentStock.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonStates();
            validateStockInput(); // Re-check stock status when stock text changes
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateButtonStates() {
        boolean isValidInput = validateInputs(false); // Validate without showing dialogs
        Inventorydto selectedItem = table.getSelectionModel().getSelectedItem();

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

    private void checkCriticalStock(List<Inventorydto> inventoryList) {
        List<String> criticalItems = new ArrayList<>();
        List<String> lowItems = new ArrayList<>();

        for (Inventorydto item : inventoryList) {
            if (item.getCurrentStockBags() < CRITICAL_STOCK_THRESHOLD) {
                criticalItems.add(item.getId() + " (" + item.getProductId() + ") - Stock: " + item.getCurrentStockBags());
            } else if (item.getCurrentStockBags() < LOW_STOCK_THRESHOLD) {
                lowItems.add(item.getId() + " (" + item.getProductId() + ") - Stock: " + item.getCurrentStockBags());
            }
        }

        if (!criticalItems.isEmpty()) {
            showStockAlert("CRITICAL STOCK ALERT",
                    "The following items have critically low stock levels:\n\n" +
                            String.join("\n", criticalItems),
                    Alert.AlertType.ERROR);
        }

        if (!lowItems.isEmpty()) {
            showStockAlert("Low Stock Warning",
                    "The following items have low stock levels:\n\n" +
                            String.join("\n", lowItems),
                    Alert.AlertType.WARNING);
        }
    }

    private void clearFields() throws SQLException {
        txtId.clear();
        txtLatUpdated.clear();
        txt_CurrentStock.clear();
        cmbProductId.getSelectionModel().clearSelection();
        loadNextId(); // Generate new ID for next save
        txtLatUpdated.setText(LocalDate.now().toString()); // Set current date
        loadTable(); // Reload table to reflect changes
        table.getSelectionModel().clearSelection(); // Clear table selection
        updateButtonStates(); // Reset button states
        lblStockStatus.setVisible(false); // Hide status label
    }

    private boolean validateInputs(boolean showDialog) {
        if (cmbProductId.getValue() == null || cmbProductId.getValue().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Please select a Product ID.");
            return false;
        }

        if (txt_CurrentStock.getText().isEmpty()) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Current Stock cannot be empty.");
            return false;
        }

        try {
            int currentStock = Integer.parseInt(txt_CurrentStock.getText());
            if (currentStock < 0) {
                if (showDialog) showAlert(Alert.AlertType.ERROR, "Current Stock cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Invalid input for Current Stock. Please enter a valid whole number.");
            return false;
        }

        // txtLatUpdated is auto-set to LocalDate.now(), no need for format validation here unless user can edit it
        if (txtLatUpdated.getText().isEmpty()) { // Should not happen with auto-set date
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Last Updated date cannot be empty.");
            return false;
        }

        // Basic date format validation if it's user-editable, otherwise `Date.valueOf` will throw
        try {
            Date.valueOf(txtLatUpdated.getText());
        } catch (IllegalArgumentException e) {
            if (showDialog) showAlert(Alert.AlertType.ERROR, "Invalid date format for Last Updated. Please use YYYY-MM-DD.");
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
            alert.showAndWait(); // Use showAndWait for user to acknowledge
        });
    }

    private void showStockAlert(String title, String content, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.initOwner(table.getScene().getWindow()); // Attach to the window
            alert.show(); // Use show for non-blocking alerts
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
        alert.setHeaderText("Delete Inventory Item");
        alert.setContentText("Are you sure you want to delete this inventory item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String id = txtId.getText();
            try {
                boolean isDeleted = inventoryBO.deleteInventoryItem(id);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete failed!");
                }
            } catch (InUseException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong while deleting inventory item: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!validateInputs(true)) return; // Validate with dialogs

        int currentStock = Integer.parseInt(txt_CurrentStock.getText());
        Date lastUpdate = Date.valueOf(txtLatUpdated.getText());
        String selectedProductId = cmbProductId.getValue();

        Inventorydto inventorydto = new Inventorydto(
                txtId.getText(),
                selectedProductId,
                currentStock,
                lastUpdate
        );

        try {
            inventoryBO.saveInventoryItem(inventorydto);
            showAlert(Alert.AlertType.INFORMATION, "Inventory saved successfully!");
            clearFields();
        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something went wrong while saving inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Update Inventory Item");
        alert.setContentText("Are you sure you want to update this inventory item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (!validateInputs(true)) return; // Validate with dialogs

            int currentStock = Integer.parseInt(txt_CurrentStock.getText());
            Date lastUpdate = Date.valueOf(txtLatUpdated.getText());
            String selectedProductId = cmbProductId.getValue();

            Inventorydto inventorydto = new Inventorydto(
                    txtId.getText(),
                    selectedProductId,
                    currentStock,
                    lastUpdate
            );

            try {
                inventoryBO.updateInventoryItem(inventorydto);
                showAlert(Alert.AlertType.INFORMATION, "Inventory updated successfully!");
                clearFields();
            } catch (NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Something went wrong while updating inventory: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void validateStockInput() {
        try {
            int stock = Integer.parseInt(txt_CurrentStock.getText());
            if (stock < CRITICAL_STOCK_THRESHOLD) {
                lblStockStatus.setText("CRITICAL STOCK LEVEL!");
                lblStockStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Red, bold
                lblStockStatus.setVisible(true);
            } else if (stock < LOW_STOCK_THRESHOLD) {
                lblStockStatus.setText("LOW STOCK WARNING");
                lblStockStatus.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Orange, bold
                lblStockStatus.setVisible(true);
            } else {
                lblStockStatus.setVisible(false);
            }
        } catch (NumberFormatException e) {
            lblStockStatus.setVisible(false); // Hide if input is not a valid number
        }
    }

    @FXML
    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        Inventorydto selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        txtId.setText(selectedItem.getId());
        cmbProductId.setValue(selectedItem.getProductId());
        txt_CurrentStock.setText(String.valueOf(selectedItem.getCurrentStockBags()));
        txtLatUpdated.setText(String.valueOf(selectedItem.getLastUpdated()));

        updateButtonStates(); // Update button states based on selection
        validateStockInput(); // Re-validate stock status for selected item
    }

    @FXML
    public void cmbProductIdOnAction(ActionEvent actionEvent) {
        String selectedProductId = cmbProductId.getSelectionModel().getSelectedItem();
        if (selectedProductId != null && !selectedProductId.isEmpty()) {
            try {
                // Fetch the total_quantity_bags from FinishedProductBO
                int quantity = inventoryBO.getFinishedProductCurrentQuantity(selectedProductId);
                txt_CurrentStock.setText(String.valueOf(quantity));
                validateStockInput(); // Re-validate stock status after autofilling
            } catch (SQLException | NotFoundException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error fetching product quantity for " + selectedProductId + ": " + e.getMessage());
                txt_CurrentStock.clear(); // Clear if an error occurs
            }
        } else {
            txt_CurrentStock.clear(); // Clear if no product is selected
        }
        updateButtonStates(); // Update button states as combo box selection changes
    }
}