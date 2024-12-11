package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

// Just a small test on Github Pipeline =)))

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
    Transaction transaction;
    @BeforeEach
    void setUp() {
        transaction = new Transaction(){{transactionId=1; accountId=10; amount=1500; isDebit=false;}};
    }

    @Test
    void testTxnEqualsWithDummyObject() {
        Object dummy = new Object();
        assertEquals(false, transaction.equals(dummy));
    }

    @Test
    void testTxnEqualsWithSameTxnObject() {
        assertEquals(true, transaction.equals(transaction));
    }

    @Test
    void testTxnEqualsWithDifferentTxnObject() {
        Transaction txn = new Transaction(){{transactionId=2; accountId=10; amount=1500; isDebit=false;}};
        assertEquals(false, transaction.equals(txn));
    }

}