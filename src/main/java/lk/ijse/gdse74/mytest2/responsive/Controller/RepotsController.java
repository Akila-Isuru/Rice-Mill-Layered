package lk.ijse.gdse74.mytest2.responsive.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.gdse74.mytest2.responsive.bo.BOFactory; // BOFactory import කරනවා
import lk.ijse.gdse74.mytest2.responsive.bo.BOTypes;
import lk.ijse.gdse74.mytest2.responsive.bo.custom.ReportsBO; // ReportsBO interface එක import කරනවා
import lk.ijse.gdse74.mytest2.responsive.dto.Reportsdto;

import java.net.URL;
import java.util.List; // ArrayList වෙනුවට List පාවිච්චි කරනවා
import java.util.ResourceBundle;

public class RepotsController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Reportsdto, String> colOrderdate;

    @FXML
    private TableColumn<Reportsdto, String> colReport_id;

    @FXML
    private TableColumn<Reportsdto, String> colReport_type;

    @FXML
    private TableView<Reportsdto> table;

    @FXML
    private TextField txtGenerated_date;

    @FXML
    private TextField txtReport_Type;

    @FXML
    private TextField txtReport_id;


    private ReportsBO reportsBO = BOFactory.getInstance().getBO(BOTypes.REPORTS);

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtReport_id.getText();
        try {
            boolean isDeleted = reportsBO.deleteReport(id);
            if (isDeleted) {
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "Deleted successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete report").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete report").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        Reportsdto reportsdto = new Reportsdto(txtReport_id.getText(), txtReport_Type.getText(), txtGenerated_date.getText());
        try {
            boolean isSaved = reportsBO.saveReport(reportsdto);
            if (isSaved) {
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "Report saved successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Report save failed").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Report save failed").show();
        }
    }

    private void clearFields() {
        txtReport_id.setText("");
        txtReport_Type.setText("");
        txtGenerated_date.setText("");
        loadTable(); // Table එක reload කරනවා
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Reportsdto reportsdto = new Reportsdto(txtReport_id.getText(), txtReport_Type.getText(), txtGenerated_date.getText());
        try {
            boolean isUpdated = reportsBO.updateReport(reportsdto); // BO layer එකෙන් update කරනවා
            if (isUpdated) {
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "Report update successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Report update failed").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Report update failed").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
    }

    private void loadTable() {
        colReport_id.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colReport_type.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colOrderdate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        try {
            List<Reportsdto> reportsdtos = reportsBO.getAllReports(); // BO layer එකෙන් all reports ගන්නවා
            if (reportsdtos != null) {
                ObservableList<Reportsdto> reportsdtoObservableList = FXCollections.observableArrayList(reportsdtos);
                table.setItems(reportsdtoObservableList);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to load reports").show(); // Error alert එකක් දැම්මා
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load reports").show();
        }
    }

    @FXML
    public void tableColumnOnClicked(MouseEvent mouseEvent) {
        Reportsdto reportsdto = table.getSelectionModel().getSelectedItem();
        if (reportsdto != null) {
            txtReport_id.setText(reportsdto.getReportId());
            txtReport_Type.setText(reportsdto.getReportType());
            txtGenerated_date.setText(reportsdto.getReportDate());
        }
    }
}