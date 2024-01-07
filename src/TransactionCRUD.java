import java.sql.SQLException;

interface TransactionCRUD {
    void createTransaction(String type, String description, double amount) throws SQLException;
    void readTransactions(String filter) throws SQLException;
    void updateTransaction(String type, int transactionId, String newDescription, double newAmount) throws SQLException;
    void deleteTransaction(String type, int transactionId) throws SQLException;
}
