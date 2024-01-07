import java.sql.*;
import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input email dan password
        System.out.print("Enter email: ");
        String email = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        // Membuat objek Keuangan
        Keuangan keuangan = new Keuangan(email, password);

        // Autentikasi
        if (keuangan.authenticate(email, password)) {
            System.out.println("Authentication successful");

            // Menu utama
            int choice;
            do {
                System.out.println("\n1. Add Transaction");
                System.out.println("2. View Total Transactions");
                System.out.println("3. Update Transaction");
                System.out.println("4. Delete Transaction");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Input data transaksi baru
                        System.out.print("Enter type (income/expense): ");
                        String type = scanner.next();
                        scanner.nextLine(); // Consume the newline character

                        System.out.print("Enter description: ");
                        String description = scanner.nextLine();

                        System.out.print("Enter amount: ");
                        double amount = scanner.nextDouble();

                        try {
                            // Menambahkan transaksi baru
                            keuangan.createTransaction(type, description, amount);
                            System.out.println("Transaction added successfully");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        // Menampilkan transaksi
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        String filterDate = scanner.next();

                        try {
                            // Membaca dan menampilkan transaksi
                            keuangan.readTransactions(filterDate);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 3:
                        // Update transaksi
                        System.out.print("Enter transaction id to update: ");
                        int transactionId = scanner.nextInt();
                        System.out.print("Enter type (income/expense): ");
                        String updateType = scanner.next();

                        // Consume the newline character
                        scanner.nextLine();

                        System.out.print("Enter new description: ");
                        String newDescription = scanner.nextLine();
                        System.out.print("Enter new amount: ");
                        double newAmount = scanner.nextDouble();

                        try {
                            // Memperbarui transaksi
                            keuangan.updateTransaction(updateType, transactionId, newDescription, newAmount);
                            System.out.println("Transaction updated successfully");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 4:
                        // Hapus transaksi
                        System.out.print("Enter type (income/expense): ");
                        String deleteType = scanner.next();
                        System.out.print("Enter transaction id to delete: ");
                        int deleteTransactionId = scanner.nextInt();

                        try {
                            // Menghapus transaksi
                            keuangan.deleteTransaction(deleteType, deleteTransactionId);
                            System.out.println("Transaction deleted successfully");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 5:
                        System.out.println("Program is Exited");
                        break;

                    default:
                        System.out.println("Invalid choice, try again");
                        break;
                }

            } while (choice != 5);
        } else {
            System.out.println("Authentication failed");
        }

        scanner.close();
    }
}