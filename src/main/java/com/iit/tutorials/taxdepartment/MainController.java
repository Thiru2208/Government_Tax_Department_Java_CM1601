package com.iit.tutorials.taxdepartment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class MainController {

    @FXML private TextField filePathField;
    @FXML private Label importStatusLabel;

    @FXML private Label totalRecordsLabel;
    @FXML private Label validRecordsLabel;
    @FXML private Label invalidRecordsLabel;

    @FXML
    TextField taxRateField;
    @FXML
    Label finalTaxLabel;
    @FXML private Label profitStatusLabel;

    @FXML
    TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> itemCodeCol;
    @FXML private TableColumn<Transaction, Double> internalPriceCol;
    @FXML private TableColumn<Transaction, Double> discountCol;
    @FXML private TableColumn<Transaction, Double> salePriceCol;
    @FXML private TableColumn<Transaction, Integer> quantityCol;
    @FXML private TableColumn<Transaction, Integer> checksumCol;
    @FXML private TableColumn<Transaction, Boolean> validCol;
    @FXML private TableColumn<Transaction, Double> profitCol;

    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Link columns to properties
        itemCodeCol.setCellValueFactory(cellData -> cellData.getValue().itemCodeProperty());
        internalPriceCol.setCellValueFactory(cellData -> cellData.getValue().internalPriceProperty().asObject());
        discountCol.setCellValueFactory(cellData -> cellData.getValue().discountProperty().asObject());
        salePriceCol.setCellValueFactory(cellData -> cellData.getValue().salePriceProperty().asObject());
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        checksumCol.setCellValueFactory(cellData -> cellData.getValue().checksumProperty().asObject());
        validCol.setCellValueFactory(cellData -> cellData.getValue().validProperty());
        profitCol.setCellValueFactory(cellData -> cellData.getValue().profitProperty().asObject());

        transactionTable.setItems(transactions);

        transactionTable.setEditable(true);

        // Editable columns
        itemCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        itemCodeCol.setOnEditCommit(e -> e.getRowValue().setItemCode(e.getNewValue()));

        internalPriceCol.setCellFactory(TextFieldTableCell.<Transaction, Double>forTableColumn(new DoubleStringConverter()));
        internalPriceCol.setOnEditCommit(e -> e.getRowValue().setInternalPrice(e.getNewValue()));

        discountCol.setCellFactory(TextFieldTableCell.<Transaction, Double>forTableColumn(new DoubleStringConverter()));
        discountCol.setOnEditCommit(e -> e.getRowValue().setDiscount(e.getNewValue()));

        salePriceCol.setCellFactory(TextFieldTableCell.<Transaction, Double>forTableColumn(new DoubleStringConverter()));
        salePriceCol.setOnEditCommit(e -> e.getRowValue().setSalePrice(e.getNewValue()));

        quantityCol.setCellFactory(TextFieldTableCell.<Transaction, Integer>forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(e -> e.getRowValue().setQuantity(e.getNewValue()));

    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        Stage stage = (Stage) filePathField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
            importStatusLabel.setText("Status: Importing file...");

            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                if (!content.trim().startsWith("[") || !content.trim().endsWith("]")) {
                    throw new IllegalArgumentException("Invalid JSON format. Expected a JSON array.");
                }
                JSONArray array = new JSONArray(content);

                transactions.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    // Validate keys exist
                    if (!obj.has("itemCode") || !obj.has("internalPrice") || !obj.has("discount") ||
                            !obj.has("salePrice") || !obj.has("quantity") || !obj.has("checksum")) {
                        throw new IllegalArgumentException("Missing fields in JSON object at index " + i);
                    }
                    Transaction t = new Transaction(
                            obj.getString("itemCode"),
                            obj.getDouble("internalPrice"),
                            obj.getDouble("discount"),
                            obj.getDouble("salePrice"),
                            obj.getInt("quantity"),
                            obj.getInt("checksum")
                    );
                    transactions.add(t);
                }

                importStatusLabel.setText("Status: File imported successfully.");
                updateSummary();
                transactionTable.refresh();
            } catch (Exception e) {
                importStatusLabel.setText("Error: " + e.getMessage());
                showAlert("Invalid File", "The selected file is not valid for this system.\nError: " + e.getMessage());
            }
        } else {
            importStatusLabel.setText("Status: No file selected.");
        }
    }

    @FXML
    void handleValidate() {
        int valid = 0;
        for (Transaction t : transactions) {
            t.setChecksum(calculateChecksum(t));
            boolean validCode = t.getItemCode().matches("[A-Za-z0-9]+");
            boolean nonNegative = t.getInternalPrice() >= 0;
            boolean checksumValid = t.getChecksum() == calculateChecksum(t);

            boolean isValid = validCode && nonNegative && checksumValid;
            t.setValid(isValid);
            if (isValid) valid++;
        }
        transactionTable.refresh();
        updateSummary();
    }

    @FXML
    private void handleDeleteInvalid() {
        transactions.removeIf(t -> !t.isValid());
        transactionTable.refresh();
        updateSummary();
    }

    @FXML
    void handleProfit() {
        for (Transaction t : transactions) {
            double cost = t.getInternalPrice() * t.getQuantity();
            double expectedincome = t.getSalePrice() * t.getQuantity();
            double discountamount = t.getSalePrice() * t.getQuantity() * (t.getDiscount() / 100.0);
            double profit = expectedincome - discountamount - cost;
            t.setProfit(profit);
        }
        profitStatusLabel.setText("Profit calculated successfully.");
        transactionTable.refresh();
    }

    @FXML
    void handleFinalTax() {
        try {
            double rate = Double.parseDouble(taxRateField.getText()) / 100.0;
            double totalProfit = transactions.stream().filter(Transaction::isValid).mapToDouble(Transaction::getProfit).sum();
            double tax = totalProfit * rate;
            finalTaxLabel.setText("Final Tax: Rs. " + String.format("%.2f", tax));
        } catch (NumberFormatException e) {
            finalTaxLabel.setText("Invalid tax rate.");
        }
    }

    private void updateSummary() {
        int total = transactions.size();
        int valid = (int) transactions.stream().filter(Transaction::isValid).count();
        int invalid = total - valid;

        totalRecordsLabel.setText("Total Records: " + total);
        validRecordsLabel.setText("Valid Records: " + valid);
        invalidRecordsLabel.setText("Invalid Records: " + invalid);
    }

    int calculateChecksum(Transaction t) {
        int sum = 0;
        for (char ch : t.getItemCode().toCharArray()) {
            sum += ch;
        }
        sum += (int) t.getInternalPrice();
        sum += (int) t.getDiscount();
        sum += (int) t.getSalePrice();
        sum += t.getQuantity();
        return sum % 256;
    }

    @FXML
    private void handleDeleteZeroProfit() {
        transactions.removeIf(t -> t.getProfit() <= 0.0);
        transactionTable.refresh();
        updateSummary();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
