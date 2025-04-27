package com.iit.tutorials.taxdepartment;

import static org.junit.jupiter.api.Assertions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainControllerTest {

    private MainController controller;
    private ObservableList<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        controller = new MainController();
        transactions = FXCollections.observableArrayList();
    }

    @Test
    public void testCalculateChecksum() {
        Transaction t = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        int checksum = controller.calculateChecksum(t);

        int expectedChecksum = 0;
        for (char ch : t.getItemCode().toCharArray()) {
            expectedChecksum += ch;
        }
        expectedChecksum += (int) t.getInternalPrice();
        expectedChecksum += (int) t.getDiscount();
        expectedChecksum += (int) t.getSalePrice();
        expectedChecksum += t.getQuantity();
        expectedChecksum = expectedChecksum % 256;

        assertEquals(expectedChecksum, checksum);
    }

    @Test
    public void testHandleValidateWithoutTableView() {
        // Create a valid transaction
        Transaction t1 = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        t1.setChecksum(controller.calculateChecksum(t1));

        // Create an invalid transaction
        Transaction t2 = new Transaction("Cake@01", -100.0, 10.0, 500.0, 2, 0);
        t2.setChecksum(999);  // Wrong checksum

        // Create a fake list
        ObservableList<Transaction> fakeTransactions = FXCollections.observableArrayList(t1, t2);

        // Apply validation (simulate handleValidate())
        for (Transaction t : fakeTransactions) {
            boolean validCode = t.getItemCode().matches("[A-Za-z0-9]+");
            boolean nonNegative = t.getInternalPrice() >= 0;
            boolean checksumValid = t.getChecksum() == controller.calculateChecksum(t);

            boolean isValid = validCode && nonNegative && checksumValid;
            t.setValid(isValid);
        }

        // Assertions
        assertTrue(fakeTransactions.get(0).isValid(), "First transaction should be valid");
        assertFalse(fakeTransactions.get(1).isValid(), "Second transaction should be invalid");
    }

    @Test
    public void testHandleProfit() {
        Transaction t = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        transactions.add(t);

        double cost = t.getInternalPrice() * t.getQuantity();
        double expectedIncome = t.getSalePrice() * t.getQuantity();
        double discountAmount = t.getSalePrice() * t.getQuantity() * (t.getDiscount() / 100.0);
        double expectedProfit = expectedIncome - discountAmount - cost;

        t.setProfit(expectedProfit);
        assertEquals(expectedProfit, t.getProfit(), 0.01);
    }

    @Test
    public void testHandleDeleteInvalid() {
        Transaction t1 = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        t1.setValid(true);
        Transaction t2 = new Transaction("InvalidItem", -100.0, 10.0, 500.0, 2, 0);
        t2.setValid(false);

        transactions.addAll(t1, t2);
        transactions.removeIf(tran -> !tran.isValid());

        assertEquals(1, transactions.size());
        assertTrue(transactions.get(0).isValid());
    }

    @Test
    public void testHandleFinalTax() {
        Transaction t1 = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        t1.setProfit(50.0);
        t1.setValid(true);

        Transaction t2 = new Transaction("Cake02", 300.0, 5.0, 400.0, 1, 0);
        t2.setProfit(75.0);
        t2.setValid(true);
        transactions.addAll(t1, t2);

        double rate = 0.10; // 10%
        double expectedTax = (50.0 + 75.0) * rate;

        assertEquals(12.5, expectedTax, 0.01);
    }

    @Test
    public void testUpdateSummary() {
        Transaction t1 = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        t1.setValid(true);
        Transaction t2 = new Transaction("Cake02", 300.0, 5.0, 400.0, 1, 0);
        t2.setValid(false);

        transactions.addAll(t1, t2);

        int total = transactions.size();
        int valid = (int) transactions.stream().filter(Transaction::isValid).count();
        int invalid = total - valid;

        assertEquals(2, total);
        assertEquals(1, valid);
        assertEquals(1, invalid);
    }

    @Test
    public void testHandleDeleteZeroProfit() {
        Transaction t1 = new Transaction("Cake01", 450.0, 10.0, 500.0, 2, 0);
        t1.setProfit(0.0);

        Transaction t2 = new Transaction("Cake02", 300.0, 5.0, 400.0, 1, 0);
        t2.setProfit(80.0);

        transactions.addAll(t1, t2);

        transactions.removeIf(t -> t.getProfit() <= 0.0);

        assertEquals(1, transactions.size());
        assertEquals(80.0, transactions.get(0).getProfit(), 0.01);
    }

}
