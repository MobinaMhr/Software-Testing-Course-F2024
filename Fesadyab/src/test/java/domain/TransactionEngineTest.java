package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionEngineTest {
    Transaction txn1;
    Transaction txn2;
    Transaction txn3;
    Transaction txn4;
    Transaction txn5;
    Transaction debitTxn1;
    Transaction debitTxn2;

    @BeforeEach
    void setUp() {
        txn1 = new Transaction(){{transactionId = 1; accountId = 10; amount = 150; isDebit = false;}};
        txn2 = new Transaction(){{transactionId = 2; accountId = 10; amount = 250; isDebit = false;}};
        txn3 = new Transaction(){{transactionId = 3; accountId = 10; amount = 350; isDebit = false;}};
        txn4 = new Transaction(){{transactionId = 4; accountId = 10; amount = 1100; isDebit = false;}};
        txn5 = new Transaction(){{transactionId = 4; accountId = 10; amount = 750; isDebit = false;}};

        debitTxn1 = new Transaction(){{transactionId = 5; accountId = 10; amount = 250; isDebit = true;}};
        debitTxn2 = new Transaction(){{transactionId = 6; accountId = 10; amount = 900; isDebit = true;}};
    }

    // ---------------------------------------- Average Transaction Amount ---------------------------------------- //

    @Test
    void testAverageTransactionAmountWithEmptyHistory() {
        TransactionEngine engine = new TransactionEngine();

        int accountId = 1;
        int averageAmount = engine.getAverageTransactionAmountByAccount(accountId);
        assertEquals(0, averageAmount,
                "Average amount should be 0 for empty transaction history");
    }

    @Test
    void testAverageTransactionAmountWithSingleTxn() {
        TransactionEngine engine = new TransactionEngine();
        engine.transactionHistory.add(txn1);

        int accountId = 10;
        int averageAmount = engine.getAverageTransactionAmountByAccount(accountId);
        assertEquals(150, averageAmount,
                "Average amount should be 150 for transaction history with single transaction");
    }

    @Test
    void testAverageTransactionAmountWithSingleTxnDifferentId() {
        TransactionEngine engine = new TransactionEngine();
        engine.transactionHistory.add(txn1);

        int accountId = 15;
        int averageAmount = engine.getAverageTransactionAmountByAccount(accountId);
        assertEquals(0, averageAmount,
                "Average amount should be 0 for transaction history with single transaction while giving wrong account id");
    }

    @Test
    void testAverageTransactionAmountWithMultipleTransactions() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int accountId = 10;
        int averageAmount = engine.getAverageTransactionAmountByAccount(accountId);
        assertEquals(200, averageAmount,
                "Average amount should be 200 for transaction history with single transaction");
    }

    // ---------------------------------------- Transaction Pattern Above Threshold ---------------------------------------- //

    @Test
    void testTransactionPatternAboveThresholdWithEmptyHistory() {
        TransactionEngine engine = new TransactionEngine();

        int threshold = 1200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }

    @Test
    void testTransactionPatternAboveThresholdWithSingleTxnDifferentId() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);

        int threshold = 1200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }

    // ---------------------------------------- Get Transaction Pattern Above Threshold ---------------------------------------- //

    @Test
    void testGetTransactionPatternAboveThresholdWithBellowThresholdTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int threshold = 1200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }

    @Test // Txn2 amount > threshold(200)
    void testGetTransactionPatternAboveThresholdWithSingleTxnAboveThreshold() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int threshold = 200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(100, txnPattern);
    }

    @Test // Txn2 amount > threshold(200)
    void testGetTransactionPatternAboveThresholdWithSingleTxnAboveThreshold_____() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int threshold = 250;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }

    // Txn2 and Txn3 amount > threshold(200)
    // Doesn't go to else if
    @Test
    void testGetTransactionPatternAboveThresholdWithMultipleTransactionsAboveThresholdWithNotSameDiffToPrevious() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int threshold = 200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(100, txnPattern);
    }
    @Test
    void testGetTransactionPatternAboveThresholdWithMultipleTransactionsAboveThresholdWithNotSameDiffToPrevious_____() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn4);

        int threshold = 200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }
    // Txn2 and Txn4 amount > threshold(200)
    // goes to else if
    @Test
    void testGetTransactionPatternAboveThresholdWithMultipleTransactionsAboveThresholdWithSameDiffToPrevious() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn4);

        int threshold = 200;
        int txnPattern = engine.getTransactionPatternAboveThreshold(threshold);
        assertEquals(0, txnPattern);
    }

    // ---------------------------------------- Detect Fraudulent Transaction ---------------------------------------- //

    @Test
    void testDetectFraudulentTransactionOnNonDebitTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);

        int fraudScore = engine.detectFraudulentTransaction(txn1);
        assertEquals(0, fraudScore);
    }

    @Test
    void testDetectFraudulentTransactionOnFraudulentTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(debitTxn1);

        int fraudScore = engine.detectFraudulentTransaction(debitTxn1);
        assertEquals(0, fraudScore);
    }

    @Test
    void testDetectFraudulentTransactionOnUnFraudulentTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int fraudScore = engine.detectFraudulentTransaction(debitTxn2);
        assertEquals(500, fraudScore);
    }
    
    // ---------------------------------------- Add Transaction And Detect Fraud ---------------------------------------- //

    @Test
    void testAddTransactionAndDetectFraudOnNonDebitTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);

        int fraudScore = engine.addTransactionAndDetectFraud(txn1);
        assertEquals(0, fraudScore);
    }

    @Test // Fraudulent score = 500
    void testAddTransactionAndDetectFraudOnFraudulentTxn() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int fraudScore = engine.addTransactionAndDetectFraud(debitTxn2);
        assertEquals(500, fraudScore);
    }

    @Test // txn2 is bellow threshold
    void testAddTransactionAndDetectFraudOnUnFraudulentTxnOtherTxnBellowThreshold() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2); // is less than threshold

        int fraudScore = engine.addTransactionAndDetectFraud(debitTxn1);
        assertEquals(0, fraudScore);
    }

    @Test // txn4 is above threshold
    void testAddTransactionAndDetectFraudOnUnFraudulentTxnOtherTxnAboveThreshold() {
        TransactionEngine engine = new TransactionEngine();

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn4); // is more than threshold

        int fraudScore = engine.addTransactionAndDetectFraud(debitTxn1);
        assertEquals(950, fraudScore);
    }
}
