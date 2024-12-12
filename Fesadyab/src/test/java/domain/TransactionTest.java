package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
    Transaction txn1, txn2;
    Object dummy;

    private Transaction createTransaction(int _txnId, int _acctId, int _amt, boolean _isDebit) {
        return new Transaction() {{transactionId = _txnId; accountId = _acctId; amount = _amt; isDebit = _isDebit;}};
    }

    @BeforeEach
    void setUp() {
        txn1 = createTransaction(1, 10, 1500, false);
        txn2 = createTransaction(2, 10, 1500, false);
        dummy = new Object();
    }

    @Test
    void testTxnEquals_WithDummyObject_ReturnsFalse() {
        assertFalse(txn1.equals(dummy));
    }

    @Test
    void testTxnEquals_WithSameTxn_ReturnsTrue() {
        assertTrue(txn1.equals(txn1));
    }

    @Test
    void testTxnEquals_WithDifferentTxn_ReturnsFalse() {
        assertFalse(txn1.equals(txn2));
    }
}