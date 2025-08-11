package lk.ijse.gdse74.mytest2.responsive.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory;
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.UserBO;
import lk.ijse.gdse74.mytest2.responsive.dto.Usersdto;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableView<Usersdto> table;

    @FXML
    private TextField txtContactNumber;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableColumn<Usersdto,String> colcontact_number;

    @FXML
    private TableColumn<Usersdto,String> colemail;

    @FXML
    private TableColumn<Usersdto,String> colid;

    @FXML
    private TableColumn<Usersdto,String> colname;

    @FXML
    private TableColumn<Usersdto,String> colpassword;

    @FXML
    private TableColumn<Usersdto,String> colrole;

    @FXML
    private Text txtPasswordStrength;

    @FXML
    private HBox passwordToggleContainer;

    private boolean passwordVisible = false;
    private final String[] ROLES = {"Admin", "Manager", "Cashier", "User"};

    private final String namePattern = "^[A-Za-z ]+$";
    private final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String phonePattern = "^(?:0|\\+94|0094)?(?:[0-9]{9})$";

    private UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmbRole.getItems().addAll(ROLES);

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);

            setupPasswordToggle();
            loadTable();
            loadNextId();
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Initialization Error: " + e.getMessage(), e);
        }
    }

    private void setupPasswordToggle() {
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
        });

        txtPasswordVisible.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordVisible) {
                updatePasswordStrength(newValue);
            }
        });

        Button toggleBtn = new Button("👁");
        toggleBtn.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        toggleBtn.setOnAction(e -> togglePasswordVisibility());
        passwordToggleContainer.getChildren().add(toggleBtn);
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            txtPasswordStrength.setText("");
            return;
        }

        int strength = calculatePasswordStrength(password);

        switch (strength) {
            case 0:
            case 1:
                txtPasswordStrength.setText("Weak");
                txtPasswordStrength.setFill(Color.RED);
                break;
            case 2:
                txtPasswordStrength.setText("Medium");
                txtPasswordStrength.setFill(Color.ORANGE);
                break;
            case 3:
                txtPasswordStrength.setText("Strong");
                txtPasswordStrength.setFill(Color.GREEN);
                break;
            case 4:
                txtPasswordStrength.setText("Very Strong");
                txtPasswordStrength.setFill(Color.DARKGREEN);
                break;
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        if (!password.equals(password.toLowerCase())) strength++;
        if (!password.equals(password.toUpperCase())) strength++;

        if (password.matches(".*\\d.*")) strength++;

        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        return Math.min(strength, 4);
    }

    private void loadNextId() throws SQLException, ClassNotFoundException {
        try {
            String nextId = userBO.getNextUserId();
            txtId.setText(nextId);
            txtId.setEditable(false);
            System.out.println("DEBUG: Next ID retrieved: " + nextId);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("ERROR in loadNextId: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading next ID").show();
            throw e;
        }
    }

    public void loadTable() throws SQLException, ClassNotFoundException {
        colid.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colrole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colcontact_number.setCellValueFactory(new PropertyValueFactory<>("contact_number"));
        try {
            List<Usersdto> usersdtos = userBO.getAllUsers();
            if (usersdtos != null) {
                ObservableList<Usersdto> users = FXCollections.observableArrayList(usersdtos);
                table.setItems(users);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        if (id.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to delete").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this user?");
        confirmation.setContentText("User ID: " + id + "\nThis action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = userBO.deleteUser(id);
                if (isDeleted) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "User deleted successfully").show();
                    btnDelete.setDisable(true);
                    btnUpdate.setDisable(true);
                    btnSave.setDisable(false);
                } else {
                    new Alert(Alert.AlertType.ERROR, "User delete not successful").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "User delete not successful: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtContactNumber.getText();
        String role = cmbRole.getValue();
        String password = passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role == null || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
            return;
        }

        boolean isValidName = name.matches(namePattern);
        boolean isValidEmail = email.matches(emailPattern);
        boolean isValidPhone = phone.matches(phonePattern);

        if (!isValidName || !isValidEmail || !isValidPhone) {
            new Alert(Alert.AlertType.ERROR, "Please check input formats for Name, Email, or Contact Number.").show();
            return;
        }


        Usersdto usersdto = new Usersdto(txtId.getText(), name, email, password, role, phone);

        try {
            boolean isSaved = userBO.saveUser(usersdto);
            if (isSaved) {
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "User saved successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "User could not be saved").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "User could not be saved: " + e.getMessage()).show();
        }
    }

    private void clearFields() throws SQLException, ClassNotFoundException {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtPasswordVisible.setText("");
        cmbRole.getSelectionModel().clearSelection();
        txtContactNumber.setText("");
        txtPasswordStrength.setText("");
        loadNextId();
        Platform.runLater(() -> {
            System.out.println("UI refreshed with ID: " + txtId.getText());
        });
        loadTable();
        btnSave.setDisable(false);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);

        if (passwordVisible) {
            togglePasswordVisibility();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();

        if (id.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to update").show();
            return;
        }

        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtContactNumber.getText();
        String role = cmbRole.getValue();
        String password = passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role == null || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
            return;
        }

        boolean isValidName = name.matches(namePattern);
        boolean isValidEmail = email.matches(emailPattern);
        boolean isValidPhone = phone.matches(phonePattern);

        if (!isValidName || !isValidEmail || !isValidPhone) {
            new Alert(Alert.AlertType.ERROR, "Please check input formats for Name, Email, or Contact Number.").show();
            return;
        }


        Usersdto usersdto = new Usersdto(id, name, email, password, role, phone);

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Update");
        confirmation.setHeaderText("Are you sure you want to update this user?");
        confirmation.setContentText("User ID: " + id);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isUpdated = userBO.updateUser(usersdto);
                if (isUpdated) {
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION, "User updated successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "User could not be updated").show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "User could not be updated: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void tableColumnOnClicked(MouseEvent mouseEvent) {
        Usersdto usersdto = table.getSelectionModel().getSelectedItem();
        if (usersdto != null) {
            btnSave.setDisable(true);
            btnDelete.setDisable(false);
            btnUpdate.setDisable(false);
            txtId.setText(usersdto.getUser_id());
            txtName.setText(usersdto.getName());
            txtEmail.setText(usersdto.getEmail());
            if (passwordVisible) {
                txtPasswordVisible.setText(usersdto.getPassword());
                txtPassword.setText("");
            } else {
                txtPassword.setText(usersdto.getPassword());
                txtPasswordVisible.setText("");
            }
            cmbRole.setValue(usersdto.getRole());
            txtContactNumber.setText(usersdto.getContact_number());
            updatePasswordStrength(usersdto.getPassword());
        }
    }

    @FXML
    void txtNameChange(KeyEvent keyEvent) {
        String name = txtName.getText();
        boolean isValidName = name.matches(namePattern);
        if (!isValidName) {
            txtName.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        } else {
            txtName.setStyle("-fx-border-color: blue; -fx-border-radius: 3;");
        }
    }

    @FXML
    void txtEmailChange(KeyEvent keyEvent) {
        String email = txtEmail.getText();
        boolean isValidEmail = email.matches(emailPattern);
        if(!isValidEmail) {
            txtEmail.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        } else {
            txtEmail.setStyle("-fx-border-color: blue; -fx-border-radius: 3;");
        }
    }

    @FXML
    void txtContactChange(KeyEvent keyEvent) {
        String contactNumber = txtContactNumber.getText();
        boolean isValidPhone = contactNumber.matches(phonePattern);
        if(!isValidPhone) {
            txtContactNumber.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        } else {
            txtContactNumber.setStyle("-fx-border-color: blue; -fx-border-radius: 3;");
        }
    }

    @FXML
    void txtSearchOnAction(KeyEvent event) {
        String searchText = txtSearch.getText().toLowerCase();
        if (searchText.isEmpty()) {
            try {
                loadTable();
                return;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        ObservableList<Usersdto> filteredList = FXCollections.observableArrayList();
        try {
            List<Usersdto> allUsers = userBO.getAllUsers();
            for (Usersdto user : allUsers) {
                if (user.getUser_id().toLowerCase().contains(searchText) ||
                        user.getName().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getRole().toLowerCase().contains(searchText)) {
                    filteredList.add(user);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error during search: " + e.getMessage()).show();
        }
        table.setItems(filteredList);
    }
}