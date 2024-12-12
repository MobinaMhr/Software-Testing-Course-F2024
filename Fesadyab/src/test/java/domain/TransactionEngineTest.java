package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionEngineTest {
    private static final String MESSAGE_EMPTY_HISTORY =
            "Average amount should be 0 for empty transaction history";
    private static final String MESSAGE_SINGLE_TRANSACTION =
            "Average amount should be 150 for transaction history with single transaction";
    private static final String MESSAGE_SINGLE_TRANSACTION_WRONG_ID =
            "Average amount should be 0 for transaction history with single transaction while giving wrong account id";
    private static final String MESSAGE_MULTIPLE_TRANSACTIONS =
            "Average amount should be 200 for transaction history with single transaction";

    Transaction txn1, txn2, txn3, txn4, txn5;
    Transaction debitTxn1, debitTxn2;

    private TransactionEngine setupTxnEngine(List<Transaction> transactions) {
        TransactionEngine engine = new TransactionEngine();
        engine.transactionHistory.addAll(transactions);
        return engine;
    }

    private Transaction createTransaction(int _txnId, int _acctId, int _amt, boolean _isDebit) {
        return new Transaction() {{transactionId = _txnId; accountId = _acctId; amount = _amt; isDebit = _isDebit;}};
    }

    @BeforeEach
    void setUp() {
        txn1 = createTransaction(1, 10, 150, false);
        txn2 = createTransaction(2, 10, 250, false);
        txn3 = createTransaction(3, 10, 350, false);
        txn4 = createTransaction(4, 10, 1100, false);
        txn5 = createTransaction(4, 10, 750, false);

        debitTxn1 = createTransaction(5, 10, 250, true);
        debitTxn2 = createTransaction(6, 10, 900, true);
    }

    // ---------- Average Transaction Amount ---------- //

    @Test
    void testAverageTransactionAmount_WithEmptyHistory() {
        TransactionEngine engine = setupTxnEngine(List.of());
        assertEquals(0, engine.getAverageTransactionAmountByAccount(1),
                MESSAGE_EMPTY_HISTORY);
    }

    @Test
    void testAverageTransactionAmount_WithSingleTxn() {
        TransactionEngine engine = setupTxnEngine(singletonList(txn1));
        assertEquals(150, engine.getAverageTransactionAmountByAccount(10),
                MESSAGE_SINGLE_TRANSACTION);
    }

    @Test
    void testAverageTransactionAmount_WithSingleTxnDifferentId() {
        TransactionEngine engine = setupTxnEngine(singletonList(txn1));
        assertEquals(0, engine.getAverageTransactionAmountByAccount(15),
                MESSAGE_SINGLE_TRANSACTION_WRONG_ID);
    }

    @Test
    void testAverageTransactionAmount_WithMultipleTxns() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(200, engine.getAverageTransactionAmountByAccount(10),
                MESSAGE_MULTIPLE_TRANSACTIONS);
    }

    // ---------- Transaction Pattern Above Threshold ---------- //

    @Test
    void testTransactionPatternAboveThreshold_WithEmptyHistory() {
        TransactionEngine engine = setupTxnEngine(List.of());
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1200));
    }

    @Test
    void testTransactionPatternAboveThreshold_WithSingleTxnDifferentId() {
        TransactionEngine engine = setupTxnEngine(singletonList(txn1));
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1200));
    }

    // ---------- Get Transaction Pattern Above Threshold ---------- //

    @Test
    void testGetTransactionPatternAboveThreshold_TxnBellowThreshold() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(0, engine.getTransactionPatternAboveThreshold(1200));
    }

    @Test
    void testGetTransactionPatternAboveThreshold_SingleTxnAboveThreshold() {
        // Txn2 amount > threshold(200)
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(0, engine.getTransactionPatternAboveThreshold(250));
    }

    @Test
    void testGetTransactionPatternAboveThreshold_MultipleTxnsAboveThreshold_NotSameDiffAmountToPrevious() {
        // Txn2 and Txn3 amount > threshold(200) | Doesn't go to else if
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2, txn3));
        assertEquals(100, engine.getTransactionPatternAboveThreshold(200));
    }

    @Test
    void testGetTransactionPatternAboveThreshold_MultipleTxnsAboveThreshold_SameDiffAmountToPrevious() {
        // Txn2 and Txn4 amount > threshold(200) | goes to else if
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2, txn4));
        assertEquals(0, engine.getTransactionPatternAboveThreshold(200));
    }

    // ---------- Detect Fraudulent Transaction ---------- //

    @Test
    void testDetectFraudulentTransaction_OnNonDebitTxn() {
        TransactionEngine engine = setupTxnEngine(singletonList(txn1));
        assertEquals(0, engine.detectFraudulentTransaction(txn1));
    }

    @Test
    void testDetectFraudulentTransaction_OnFraudulentTxn() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, debitTxn1));
        assertEquals(0, engine.detectFraudulentTransaction(debitTxn1));
    }

    @Test
    void testDetectFraudulentTransaction_OnUnFraudulentTxn() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(500, engine.detectFraudulentTransaction(debitTxn2));
    }

    // ---------- Add Transaction And Detect Fraud ---------- //

    @Test
    void testAddTransactionAndDetectFraud_OnNonDebitTxn() {
        TransactionEngine engine = setupTxnEngine(singletonList(txn1));
        assertEquals(0, engine.addTransactionAndDetectFraud(txn1));
    }

    @Test // Fraudulent score = 500
    void testAddTransactionAndDetectFraud_OnFraudulentTxn() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(500, engine.addTransactionAndDetectFraud(debitTxn2));
    }

    @Test // txn2 is bellow threshold
    void testAddTransactionAndDetectFraud_OnUnFraudulentTxnOtherTxnsBellowThreshold() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
        assertEquals(0, engine.addTransactionAndDetectFraud(debitTxn1));
    }

    @Test // txn4 is above threshold
    void testAddTransactionAndDetectFraud_OnUnFraudulentTxnOtherTxnsAboveThreshold() {
        TransactionEngine engine = setupTxnEngine(asList(txn1, txn4));
        assertEquals(950, engine.addTransactionAndDetectFraud(debitTxn1));
    }
}




//    @Test // Txn2 amount > threshold(200)
//    void testGetTransactionPatternAboveThreshold_SingleTxnAboveThreshold() {
//        TransactionEngine engine = setupTxnEngine(asList(txn1, txn2));
//        assertEquals(100, engine.getTransactionPatternAboveThreshold(200));
//    }
