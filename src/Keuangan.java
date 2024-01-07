import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Keuangan implements TransactionCRUD {
    private Map<String, String> users; // Menyimpan username dan password
    private String email;

    public Keuangan(String email, String password) {
        this.users = new HashMap<>();
        // Inisialisasi data pengguna
        this.users.put("raniashofi", "123456");
        this.email = email;
    }

    // Metode untuk mengecek autentikasi
    public boolean authenticate(String username, String enteredPassword) {
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(enteredPassword);
    }

    // Implementasi metode createTransaction dari interface
    @Override
    public void createTransaction(String type, String description, double amount) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/keuangan", "root", "");

        String query;
        if (type.equalsIgnoreCase("income")) {
            query = "INSERT INTO income (email, date, description, amount) VALUES (?, NOW(), ?, ?)";
        } else if (type.equalsIgnoreCase("expense")) {
            query = "INSERT INTO expense (email, date, description, amount) VALUES (?, NOW(), ?, ?)";
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, amount);
            preparedStatement.executeUpdate();
        }

        connection.close();
    }

    // Implementasi metode readTransactions dari interface
    @Override
    public void readTransactions(String filter) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/keuangan", "root", "");

        String incomeQuery = "SELECT SUM(amount) as total_income FROM income WHERE email = ? AND DATE(date) = ?";
        String expenseQuery = "SELECT SUM(amount) as total_expense FROM expense WHERE email = ? AND DATE(date) = ?";

        try (PreparedStatement incomeStatement = connection.prepareStatement(incomeQuery);
                PreparedStatement expenseStatement = connection.prepareStatement(expenseQuery)) {

            incomeStatement.setString(1, email);
            expenseStatement.setString(1, email);

            // Parsing the filter as a Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate;

            try {
                parsedDate = dateFormat.parse(filter);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                incomeStatement.setDate(2, sqlDate);
                expenseStatement.setDate(2, sqlDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try (ResultSet incomeResultSet = incomeStatement.executeQuery();
                    ResultSet expenseResultSet = expenseStatement.executeQuery()) {

                double totalIncome = 0;
                double totalExpense = 0;

                // Calculate total income
                if (incomeResultSet.next()) {
                    totalIncome = incomeResultSet.getDouble("total_income");
                }

                // Calculate total expense
                if (expenseResultSet.next()) {
                    totalExpense = expenseResultSet.getDouble("total_expense");
                }

                // Display results
                System.out.println("Total Income for " + filter + ": " + totalIncome);
                System.out.println("Total Expense for " + filter + ": " + totalExpense);
                System.out.println("Net Income for " + filter + ": " + (totalIncome - totalExpense));
            }
        }
        connection.close();
    }

    @Override
    public void updateTransaction(String type, int transactionId, String newDescription, double newAmount)
            throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/keuangan", "root", "");

        String query;
        if (transactionId < 0) {
            throw new IllegalArgumentException("Invalid transaction id");
        }

        if (newAmount < 0) {
            throw new IllegalArgumentException("Invalid transaction amount");
        }

        if (newDescription == null || newDescription.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        String tableName;
        String dateColumnName;

        if (type.equalsIgnoreCase("income")) {
            if (transactionId >= 1 && transactionId <= 100) {
                query = "UPDATE income SET description = ?, amount = ?, date = NOW() WHERE id = ?";
                tableName = "income";
                dateColumnName = "date";
            } else {
                throw new IllegalArgumentException("Invalid transaction id");
            }
        } else if (type.equalsIgnoreCase("expense")) {
            if (transactionId >= 101 && transactionId <= 200) {
                query = "UPDATE expense SET description = ?, amount = ?, date = NOW() WHERE id = ?";
                tableName = "expense";
                dateColumnName = "date";
            } else {
                throw new IllegalArgumentException("Invalid transaction id");
            }
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newDescription);
            preparedStatement.setDouble(2, newAmount);
            preparedStatement.setInt(3, transactionId);
            preparedStatement.executeUpdate();

            // Get the updated timestamp from the database
            String updatedTimestampQuery = "SELECT " + dateColumnName + " FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement timestampStatement = connection.prepareStatement(updatedTimestampQuery)) {
                timestampStatement.setInt(1, transactionId);
                try (ResultSet resultSet = timestampStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String updatedTimestamp = resultSet.getString(dateColumnName);
                        System.out.println("Transaction updated successfully at " + updatedTimestamp);
                    }
                }
            }
        }
        connection.close();
    }

    // Implementasi metode deleteTransaction dari interface
    @Override
    public void deleteTransaction(String type, int transactionId) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/keuangan", "root", "");

        String query;

        if (type.equalsIgnoreCase("income")) {
            if (transactionId >= 1 && transactionId <= 100) {
                query = "DELETE FROM income WHERE id = ?";
            } else {
                throw new IllegalArgumentException("Invalid transaction id");
            }
        } else if (type.equalsIgnoreCase("expense")) {
            if (transactionId >= 101 && transactionId <= 200) {
                query = "DELETE FROM expense WHERE id = ?";
            } else {
                throw new IllegalArgumentException("Invalid transaction id");
            }
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, transactionId);
            preparedStatement.executeUpdate();
        }
        connection.close();
    }
}