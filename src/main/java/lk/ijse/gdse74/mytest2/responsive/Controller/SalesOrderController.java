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

    private SalesOrderBO salesOrderBO = BOFactory.getInstance().getBO(BOTypes.SALES_ORDER);
    private CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);
    private FinishedProductBO finishedProductBO = BOFactory.getInstance().getBO(BOTypes.FINISHED_PRODUCT);

    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnAddToCart;
    @FXML private Label lblCustomerName;
    @FXML private Label lblItemName;
    @FXML private Label lblItemQty;
    @FXML private Label lblOrder_Date;
    @FXML private Label lblUnitPrice;
    @FXML private TextField txtAddCartQuantity;
    @FXML private TextField txtOrdera;
    @FXML private Label lblGrandTotal;
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
    private int currentItemQtyOnHand = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellValueFactories();
        table.setItems(cartData);

        try {
            generateNextOrderId();
            lblOrder_Date.setText(LocalDate.now().toString());
            txtOrdera.setEditable(false);
            loadItemIds();
            loadCustomerIds();
            updateTotalAmount();
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

            if (lblUnitPrice.getText().isEmpty() || !lblUnitPrice.getText().matches("^\\d+(\\.\\d+)?$")) {
                new Alert(Alert.AlertType.WARNING, "Unit price is not valid. Please select an item again.").show();
                return;
            }
            int unitPrice = Integer.parseInt(lblUnitPrice.getText().split("\\.")[0]);
            int total = unitPrice * cartQty;


            int remainingStock = currentItemQtyOnHand;
            for (cartTM existingItemInCart : cartData) {
                if (existingItemInCart.getProductId().equals(selectedItemId)) {
                    remainingStock += existingItemInCart.getQty();
                }
            }

            if (cartQty > remainingStock) {
                new Alert(Alert.AlertType.WARNING, "Not enough stock available. Available: " + remainingStock).show();
                return;
            }


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
                    updateTotalAmount();
                });
                cartData.add(newItem);
            }


            lblItemQty.setText(String.valueOf(remainingStock - cartQty));
            txtAddCartQuantity.clear();
            table.refresh();
            updateTotalAmount();

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

        new Alert(Alert.AlertType.INFORMATION, "Delete Sales Order functionality not fully implemented here. Proceed with caution if using.").show();
    }

    @FXML void btnSaveOnAction(ActionEvent event) {
        btnOnActionPlaceOrder(event);
    }

    @FXML void btnUpdateOnAction(ActionEvent event) {

        new Alert(Alert.AlertType.INFORMATION, "Update Sales Order functionality not fully implemented here. Proceed with caution if using.").show();
    }

    private void clearFields() {
        cmbItemId.getSelectionModel().clearSelection();
        lblUnitPrice.setText("0");
        lblItemName.setText("");
        txtAddCartQuantity.setText("");
        lblItemQty.setText("0");
        cartData.clear();
        updateTotalAmount();
        cmbCustomerId.getSelectionModel().clearSelection();
        lblCustomerName.setText("");
        generateNextOrderId();
        lblOrder_Date.setText(LocalDate.now().toString());
    }

    @FXML void cmbCustomerOnAction(ActionEvent actionEvent) {
        String selectedCustomerId = cmbCustomerId.getValue();
        if (selectedCustomerId != null && !selectedCustomerId.isEmpty()) {
            try {

                Customersdto customer = customerBO.getAllCustomers().stream()
                        .filter(c -> c.getCustomerId().equals(selectedCustomerId))
                        .findFirst()
                        .orElse(null);


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
                    currentItemQtyOnHand = product.getQuantityBags();

                    int qtyInCartForThisProduct = getTotalQuantityInCartForProduct(selectedItemId);
                    lblItemQty.setText(String.valueOf(currentItemQtyOnHand - qtyInCartForThisProduct));

                } else {
                    lblItemName.setText("N/A");
                    lblItemQty.setText("0");
                    lblUnitPrice.setText("0");
                    currentItemQtyOnHand = 0;
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching item details: " + e.getMessage()).show();
                e.printStackTrace();
            }
        } else {
            lblItemName.setText("");
            lblItemQty.setText("0");
            lblUnitPrice.setText("0");
            currentItemQtyOnHand = 0;
        }
    }

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
    }

    // This method now correctly updates the dedicated lblGrandTotal Label
    private void updateTotalAmount() {
        int grandTotal = 0;
        for (cartTM item : cartData) {
            grandTotal += item.getTotal();
        }
        lblGrandTotal.setText(String.valueOf(grandTotal));
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

        String orderId = txtOrdera.getText();
        String customerId = cmbCustomerId.getValue();
        Date orderDate = Date.valueOf(lblOrder_Date.getText());

        ArrayList<SalesOrderDetailsdto> orderDetailsList = new ArrayList<>();
        int orderTotalAmount = Integer.parseInt(lblGrandTotal.getText());

        for (cartTM cartItem : cartData) {
            SalesOrderDetailsdto detailDto = new SalesOrderDetailsdto(
                    orderId,
                    cartItem.getProductId(),
                    cartItem.getQty(),
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
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to place order. Please try again.").show();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                new Alert(Alert.AlertType.ERROR, "Failed to place order: Order ID already exists. Please try again. (Make sure you have cleaned up your database and restarted the app)").show();
            } else if (e.getMessage().contains("Insufficient stock")) {
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