package com.iit.tutorials.taxdepartment;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    public void testItemCodeGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setItemCode("Cupcake99");
        assertEquals("Cupcake99", t.getItemCode());
    }

    @Test
    public void testInternalPriceGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setInternalPrice(150.0);
        assertEquals(150.0, t.getInternalPrice());
    }

    @Test
    public void testDiscountGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setDiscount(15.0);
        assertEquals(15.0, t.getDiscount());
    }

    @Test
    public void testSalePriceGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setSalePrice(200.0);
        assertEquals(200.0, t.getSalePrice());
    }

    @Test
    public void testQuantityGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setQuantity(5);
        assertEquals(5, t.getQuantity());
    }

    @Test
    public void testChecksumGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setChecksum(200);
        assertEquals(200, t.getChecksum());
    }

    @Test
    public void testValidGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setValid(false);
        assertFalse(t.isValid());
    }

    @Test
    public void testProfitGetterSetter() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setProfit(250.0);
        assertEquals(250.0, t.getProfit());
    }

    @Test
    public void testToStringFormat() {
        Transaction t = new Transaction("Cake01", 100.0, 10.0, 120.0, 2, 123);
        t.setProfit(50.0);
        String result = t.toString();
        assertTrue(result.contains("Cake01"));
        assertTrue(result.contains("Internal Price"));
        assertTrue(result.contains("Profit"));
    }
}
