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
import lk.ijse.gdse74.mytest2.responsive.bo.custom.CustomerBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.FinishedProductBO;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.SalesOrderBO;
import lk.ijse.gdse74.mytest2.responsive.dto.*;
import lk.ijse.gdse74.mytest2.responsive.dto.tm.cartTM;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SalesOrderController implements Initializable {

    // BO instances
    private SalesOrderBO salesOrderBO = BOFactory.getInstance().getBO(BOTypes.SALES_ORDER);
    private CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);
    private FinishedProductBO finishedProductBO = BOFactory.getInstance().getBO(BOTypes.FINISHED_PRODUCT);

    // UI Components (FXML Injected)
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave; // This button's logic will be "Place Order" now
    @FXML private Button btnUpdate;
    @FXML private Button btnAddToCart;
    @FXML private Label lblCustomerName;
    @FXML private Label lblItemName;
    @FXML private Label lblItemQty;
    @FXML private Label lblOrder_Date;
    @FXML private Label lblUnitPrice;
    @FXML private TextField txtAddCartQuantity;
    @FXML private TextField txtOrdera; // This is Order ID TextField
    @FXML private Label lblGrandTotal; // Label to display Grand Total
    @FXML private TableColumn<?, ?> colAction;
    @FXML private TableColumn<cartTM, String> colProductID;
    @FXML private TableColumn<cartTM, String> colProductName;
    @FXML private TableColumn<cartTM, Integer> colQty;
    @FXML private TableColumn<cartTM, Integer> colTotalAmount;
    @FXML private TableColumn<cartTM, Integer> colUnitPrice;
    @FXML private TableView<cartTM> table;
    @FXML private ComboBox<String> cmbCustomerId;
    @FXML private ComboBox<String> cmbItemId;

    private final ObservableList<cartTM> cartData = FXCollections.observableArrayList();
    private int currentItemQtyOnHand = 0; // To keep track of the available quantity for the selected item

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        table.setItems(cartData);

        try {
            generateNextOrderId();
            lblOrder_Date.setText(LocalDate.now().toString());
            txtOrdera.setEditable(false); // Order ID should not be editable
            loadItemIds();
            loadCustomerIds();
            updateTotalAmount(); // Initialize total amount label to 0
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Initialization error: " + e.getMessage()).show();
        }
    }

    private void setCellValueFactories() {
        colProductID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
    }

    private void generateNextOrderId() {
        try {
            String nextId = salesOrderBO.getNextOrderId();
            txtOrdera.setText(nextId);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate Order ID: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void loadItemIds() {
        try {
            List<String> itemIds = finishedProductBO.getAllFinishedProductIds();
            cmbItemId.setItems(FXCollections.observableArrayList(itemIds));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load Item IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void loadCustomerIds() {
        try {
            List<Customersdto> customerList = customerBO.getAllCustomers();
            List<String> customerIds = new ArrayList<>();
            for (Customersdto customer : customerList) {
                customerIds.add(customer.getCustomerId());
            }
            cmbCustomerId.setItems(FXCollections.observableArrayList(customerIds));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load Customer IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent actionEvent) {
        try {
            String selectedItemId = cmbItemId.getValue();
            String cartQtyString = txtAddCartQuantity.getText();

            if (selectedItemId == null || selectedItemId.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select an item.").show();
                return;
            }
            if (cartQtyString.isEmpty() || !cartQtyString.matches("^\\d+$")) {
                new Alert(Alert.AlertType.WARNING, "Please enter a valid quantity.").show();
                return;
            }

            int cartQty = Integer.parseInt(cartQtyString);
            if (cartQty <= 0) {
                new Alert(Alert.AlertType.WARNING, "Quantity must be positive.").show();
                return;
            }

            String itemName = lblItemName.getText();
            // Ensure lblUnitPrice has a valid numeric value before parsing
            if (lblUnitPrice.getText().isEmpty() || !lblUnitPrice.getText().matches("^\\d+(\\.\\d+)?$")) {
                new Alert(Alert.AlertType.WARNING, "Unit price is not valid. Please select an item again.").show();
                return;
            }
            int unitPrice = Integer.parseInt(lblUnitPrice.getText().split("\\.")[0]); // Take only integer part if "0.00"
            int total = unitPrice * cartQty;

            // Check stock considering items already in cart
            int remainingStock = currentItemQtyOnHand;
            for (cartTM existingItemInCart : cartData) {
                if (existingItemInCart.getProductId().equals(selectedItemId)) {
                    remainingStock += existingItemInCart.getQty(); // Add back what's already in the cart for this item
                }
            }

            if (cartQty > remainingStock) {
                new Alert(Alert.AlertType.WARNING, "Not enough stock available. Available: " + remainingStock).show();
                return;
            }

            // Update existing item in cart or add new
            Optional<cartTM> existingItem = cartData.stream()
                    .filter(item -> item.getProductId().equals(selectedItemId))
                    .findFirst();

            if (existingItem.isPresent()) {
                cartTM item = existingItem.get();
                int newQty = item.getQty() + cartQty;
                item.setQty(newQty);
                item.setTotal(newQty * unitPrice);
            } else {
                Button removeBtn = new Button("Remove");
                cartTM newItem = new cartTM(
                        selectedItemId,
                        itemName,
                        unitPrice,
                        cartQty,
                        total,
                        removeBtn
                );
                removeBtn.setOnAction(event -> {
                    // When remove button is clicked, add back the quantity to lblItemQty (UI only)
                    int removedQty = newItem.getQty();
                    try {
                        FinishedProductdto product = finishedProductBO.findFinishedProductById(newItem.getProductId());
                        if (product != null) {
                            if (cmbItemId.getValue() != null && cmbItemId.getValue().equals(newItem.getProductId())) {
                                lblItemQty.setText(String.valueOf(product.getQuantityBags() - getTotalQuantityInCartForProduct(newItem.getProductId()) + removedQty));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Error updating quantity on remove: " + e.getMessage()).show();
                    }

                    cartData.remove(newItem);
                    table.refresh();
                    updateTotalAmount(); // Update total after removing
                });
                cartData.add(newItem);
            }

            // Update UI
            lblItemQty.setText(String.valueOf(remainingStock - cartQty)); // Update remaining stock on UI
            txtAddCartQuantity.clear();
            table.refresh();
            updateTotalAmount(); // Update total after adding

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format for quantity or price: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error adding to cart: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML void btnDeleteOnAction(ActionEvent event) {
        // This button's logic is for deleting an entire SalesOrder from the DB.
        new Alert(Alert.AlertType.INFORMATION, "Delete Sales Order functionality not fully implemented here. Proceed with caution if using.").show();
    }

    @FXML void btnSaveOnAction(ActionEvent event) {
        btnOnActionPlaceOrder(event); // Re-routing save to place order as per typical sales flow
    }

    @FXML void btnUpdateOnAction(ActionEvent event) {
        // This button's logic is for updating an existing SalesOrder in the DB.
        new Alert(Alert.AlertType.INFORMATION, "Update Sales Order functionality not fully implemented here. Proceed with caution if using.").show();
    }

    private void clearFields() {
        cmbItemId.getSelectionModel().clearSelection();
        lblUnitPrice.setText("0"); // Default to "0" for int parsing consistency
        lblItemName.setText("");
        txtAddCartQuantity.setText("");
        lblItemQty.setText("0"); // Clear item quantity label
        cartData.clear(); // Clear the cart
        updateTotalAmount(); // Reset total amount display to 0
        cmbCustomerId.getSelectionModel().clearSelection();
        lblCustomerName.setText("");
        generateNextOrderId(); // Generate new ID for next order
        lblOrder_Date.setText(LocalDate.now().toString()); // Set current date
    }

    @FXML void cmbCustomerOnAction(ActionEvent actionEvent) {
        String selectedCustomerId = cmbCustomerId.getValue();
        if (selectedCustomerId != null && !selectedCustomerId.isEmpty()) {
            try {
                // ***MODIFIED: Use getAllCustomers() and stream to find the customer***
                Customersdto customer = customerBO.getAllCustomers().stream()
                        .filter(c -> c.getCustomerId().equals(selectedCustomerId))
                        .findFirst()
                        .orElse(null);
                // ***END MODIFIED***

                if (customer != null) {
                    lblCustomerName.setText(customer.getName());
                } else {
                    lblCustomerName.setText("Customer not found.");
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching customer details: " + e.getMessage()).show();
                e.printStackTrace();
            }
        } else {
            lblCustomerName.setText("");
        }
    }

    @FXML void cmbItemIdOnAction(ActionEvent actionEvent) {
        String selectedItemId = cmbItemId.getValue();
        if (selectedItemId != null && !selectedItemId.isEmpty()) {
            try {
                FinishedProductdto product = finishedProductBO.findFinishedProductById(selectedItemId);
                if (product != null) {
                    lblItemName.setText(product.getProductType());
                    lblUnitPrice.setText(String.valueOf(product.getPricePerBag()));
                    currentItemQtyOnHand = product.getQuantityBags(); // Store original quantity

                    // Adjust lblItemQty based on what's already in the cart for this item
                    int qtyInCartForThisProduct = getTotalQuantityInCartForProduct(selectedItemId);
                    lblItemQty.setText(String.valueOf(currentItemQtyOnHand - qtyInCartForThisProduct));

                } else {
                    lblItemName.setText("N/A");
                    lblItemQty.setText("0");
                    lblUnitPrice.setText("0"); // Set to "0" for int parsing consistency
                    currentItemQtyOnHand = 0;
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching item details: " + e.getMessage()).show();
                e.printStackTrace();
            }
        } else {
            lblItemName.setText("");
            lblItemQty.setText("0");
            lblUnitPrice.setText("0"); // Set to "0" for int parsing consistency
            currentItemQtyOnHand = 0;
        }
    }

    // Helper method to get total quantity of a product currently in the cart
    private int getTotalQuantityInCartForProduct(String productId) {
        int totalQty = 0;
        for (cartTM item : cartData) {
            if (item.getProductId().equals(productId)) {
                totalQty += item.getQty();
            }
        }
        return totalQty;
    }

    @FXML void tableColumnOnClicked(MouseEvent mouseEvent) {
        // No specific action needed here for "Place Order" flow.
    }

    // This method now correctly updates the dedicated lblGrandTotal Label
    private void updateTotalAmount() {
        int grandTotal = 0;
        for (cartTM item : cartData) {
            grandTotal += item.getTotal();
        }
        lblGrandTotal.setText(String.valueOf(grandTotal)); // Set total to the new lblGrandTotal
    }

    @FXML
    public void btnOnActionPlaceOrder(ActionEvent actionEvent) {
        if (cmbCustomerId.getValue() == null || cmbCustomerId.getValue().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer.").show();
            return;
        }
        if (cartData.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty. Please add items to the cart.").show();
            return;
        }

        String orderId = txtOrdera.getText(); // This is correctly the generated Order ID
        String customerId = cmbCustomerId.getValue();
        Date orderDate = Date.valueOf(lblOrder_Date.getText()); // Get date from label

        ArrayList<SalesOrderDetailsdto> orderDetailsList = new ArrayList<>();
        int orderTotalAmount = Integer.parseInt(lblGrandTotal.getText()); // Get total from the new Label

        for (cartTM cartItem : cartData) {
            SalesOrderDetailsdto detailDto = new SalesOrderDetailsdto(
                    orderId,
                    cartItem.getProductId(),
                    cartItem.getQty(), // Qty from cartTM
                    cartItem.getUnitPrice(),
                    cartItem.getTotal()
            );
            orderDetailsList.add(detailDto);
        }

        SalesOrderdto salesOrderDto = new SalesOrderdto(
                orderId,
                customerId,
                orderDate,
                orderTotalAmount,
                orderDetailsList
        );

        try {
            boolean isPlaced = salesOrderBO.placeOrder(salesOrderDto);
            if (isPlaced) {
                new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!").show();
                clearFields(); // Clear UI for next order
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to place order. Please try again.").show();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                new Alert(Alert.AlertType.ERROR, "Failed to place order: Order ID already exists. Please try again. (Make sure you have cleaned up your database and restarted the app)").show();
            } else if (e.getMessage().contains("Insufficient stock")) { // Custom check for your BO exception
                new Alert(Alert.AlertType.ERROR, "Failed to place order: " + e.getMessage()).show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
            }
            e.printStackTrace();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "An unexpected error occurred: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}