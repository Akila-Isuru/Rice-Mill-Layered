package lk.ijse.gdse74.mytest2.responsive.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.gdse74.mytest2.responsive.dto.FinishedProductdto;
import lk.ijse.gdse74.mytest2.responsive.model.FinishedProductModel;
import lk.ijse.gdse74.mytest2.responsive.model.MillingProcessModel; // Import for MillingProcessModel

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.sql.SQLException;

public class FinishedProductController implements Initializable {

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private TableColumn<FinishedProductdto,String> colMilling_id;
    @FXML private TableColumn<FinishedProductdto, Double> colPackaging_size;
    @FXML private TableColumn<FinishedProductdto, Integer> colPricePer_bag;
    @FXML private TableColumn<FinishedProductdto,String> colProduct_id;
    @FXML private TableColumn<FinishedProductdto,String> colProduct_type;
    @FXML private TableColumn<FinishedProductdto,Integer> colTotal_quantity;
    @FXML private TableView<FinishedProductdto> table;
    @FXML private TextField txtProduct_id;
    @FXML private TextField txtPricePer_bag;
    @FXML private TextField txtQuantity_bags;

    @FXML private ComboBox<String> cmbMilling_id;
    @FXML private ComboBox<String> cmbProduct_type;
    // NEW: Changed from TextField to ComboBox for Packaging Size
    @FXML private ComboBox<Double> cmbPackaging_size;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        loadTable();
        loadNextId();
        loadMillingIds();
        loadProductTypes();
        loadPackagingSizes(); // NEW: Load packaging sizes into the ComboBox
        setupFieldListeners();
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
            ArrayList<FinishedProductdto> allProducts = FinishedProductModel.viewAllFinishedProduct();
            table.setItems(FXCollections.observableArrayList(allProducts));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load data").show();
        }
    }

    private void loadNextId() {
        try {
            String nextId = new FinishedProductModel().getNextId();
            txtProduct_id.setText(nextId);
            txtProduct_id.setDisable(true);
            btnSave.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to generate next ID").show();
        }
    }

    private void loadMillingIds() {
        try {
            ArrayList<String> millingIds = MillingProcessModel.getAllMillingIds();
            cmbMilling_id.setItems(FXCollections.observableArrayList(millingIds));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load Milling IDs").show();
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

    // NEW: Method to load predefined packaging sizes into the ComboBox
    private void loadPackagingSizes() {
        ObservableList<Double> sizes = FXCollections.observableArrayList(
                1.0,  // 1 kg
                5.0,  // 5 kg
                10.0, // 10 kg
                25.0  // 25 kg
        );
        cmbPackaging_size.setItems(sizes);
    }

    private void setupFieldListeners() {
        cmbMilling_id.valueProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
        cmbProduct_type.valueProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
        cmbPackaging_size.valueProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState()); // NEW: Listener for Packaging Size ComboBox
        txtPricePer_bag.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
        txtQuantity_bags.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButtonState());
    }

    private void updateSaveButtonState() {
        boolean allFieldsFilled = cmbMilling_id.getValue() != null &&
                cmbProduct_type.getValue() != null &&
                cmbPackaging_size.getValue() != null && // NEW: Check if a packaging size is selected
                !txtPricePer_bag.getText().isEmpty() &&
                !txtQuantity_bags.getText().isEmpty();

        btnSave.setDisable(!allFieldsFilled);
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
                boolean isDeleted = new FinishedProductModel().deleteFinishedProduct(
                        new FinishedProductdto(txtProduct_id.getText())
                );

                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully!").show();
                    clearFields();
                    loadTable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Delete Failed!").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            double packagingSize = cmbPackaging_size.getValue(); // NEW: Get value from Packaging Size ComboBox
            int quantityBags = Integer.parseInt(txtQuantity_bags.getText());
            int pricePerBag = Integer.parseInt(txtPricePer_bag.getText());

            FinishedProductdto dto = new FinishedProductdto(
                    txtProduct_id.getText(),
                    cmbMilling_id.getValue(),
                    cmbProduct_type.getValue(),
                    packagingSize,
                    quantityBags,
                    pricePerBag
            );

            boolean isSaved = new FinishedProductModel().saveFinishedProduct(dto);

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Saved Successfully!").show();
                clearFields();
                loadTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Save Failed!").show();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid numeric value in price/quantity").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
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
                if (!validateFields()) {
                    return;
                }

                double packagingSize = cmbPackaging_size.getValue(); // NEW: Get value from Packaging Size ComboBox
                int quantityBags = Integer.parseInt(txtQuantity_bags.getText());
                int pricePerBag = Integer.parseInt(txtPricePer_bag.getText());

                FinishedProductdto dto = new FinishedProductdto(
                        txtProduct_id.getText(),
                        cmbMilling_id.getValue(),
                        cmbProduct_type.getValue(),
                        packagingSize,
                        quantityBags,
                        pricePerBag
                );

                boolean isUpdated = new FinishedProductModel().updateFinishedProduct(dto);

                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "Updated Successfully!").show();
                    clearFields();
                    loadTable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Update Failed!").show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid numeric value in price/quantity").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
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
            cmbPackaging_size.setValue(selectedItem.getPackageSize()); // NEW: Set Packaging Size ComboBox value
            txtQuantity_bags.setText(String.valueOf(selectedItem.getQuantityBags()));
            txtPricePer_bag.setText(String.valueOf(selectedItem.getPricePerBag()));

            btnSave.setDisable(true);
        }
    }

    private boolean validateFields() {
        if (cmbMilling_id.getValue() == null || cmbMilling_id.getValue().isEmpty() ||
                cmbProduct_type.getValue() == null || cmbProduct_type.getValue().isEmpty() ||
                cmbPackaging_size.getValue() == null || // NEW: Validate Packaging Size ComboBox selection
                txtPricePer_bag.getText().isEmpty() ||
                txtQuantity_bags.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
            return false;
        }

        try {
            // No need to parse packaging size as it's already a Double from ComboBox
            Integer.parseInt(txtQuantity_bags.getText());
            Integer.parseInt(txtPricePer_bag.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantity and Price per Bag must contain valid numbers").show();
            return false;
        }

        return true;
    }

    private void clearFields() {
        cmbMilling_id.getSelectionModel().clearSelection();
        cmbProduct_type.getSelectionModel().clearSelection();
        cmbPackaging_size.getSelectionModel().clearSelection(); // NEW: Clear Packaging Size ComboBox selection
        txtPricePer_bag.clear();
        txtQuantity_bags.clear();

        loadNextId();
        loadTable();
        loadMillingIds();
        loadProductTypes();
        loadPackagingSizes(); // NEW: Reload packaging sizes
    }
}